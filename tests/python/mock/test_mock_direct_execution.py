#
# <meta:header>
#   <meta:licence>
#     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
#
#     This information is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     This information is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this software. If not, see <http://www.gnu.org/licenses/>.
#   </meta:licence>
# </meta:header>
#
# AIMetrics: [
#     {
#     "timestamp": "2026-03-26T16:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-06-02T13:37:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 3,
#       "units": "%"
#       }
#     }
#   ]
#

"""
Integration tests for the direct execution endpoint on the mock platform.

Direct execution bypasses the offer-set negotiation and creates an
ExecutionSession starting at the ACCEPTED phase. The session then
progresses through the normal lifecycle: PREPARING -> AVAILABLE ->
RELEASING -> COMPLETED.

The mock platform simulates the lifecycle using MockMonitorAction
with a configurable loop count and 30-second delays per component
per phase. Sessions are processed serially, so each test should be
run with a freshly started broker to avoid queue congestion from
earlier tests.

Requires:
  - A running Calycopis broker service with the 'mock' profile active.
  - The calycopis_schema_client Python package installed.

Usage:
  pytest tests/python/test_mock_direct_execution.py -v
  CALYCOPIS_URL=http://host:port pytest tests/python/test_mock_direct_execution.py -v
"""

import pytest

from calycopis_openapi_client.models import (
    ExecutionRequest,
    AbstractExecutionSession,
    SimpleExecutionSessionPhase,
)
from calycopis_openapi_client.models.docker_image_spec import DockerImageSpec
from calycopis_openapi_client.models.simple_compute_cores import SimpleComputeCores
from calycopis_openapi_client.models.simple_compute_memory import SimpleComputeMemory
from calycopis_openapi_client.models.component_metadata import ComponentMetadata
from calycopis_openapi_client.wrappers import (
    DockerContainer,
    SimpleComputeResource,
)


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------


# Heliophorus-cantliei test container: waits N seconds then exits
# with a configurable exit code.
CANTLIEI_IMAGE = "ghcr.io/zarquan/heliophorus-cantliei:sha-831ee57"
CANTLIEI_DIGEST = "sha256:6e495692cc6f1cae2023f261f433d4691aa70b19416730f8301e45fbb74bc526"



# ---------------------------------------------------------------------------
# Helper functions
# ---------------------------------------------------------------------------

def _make_cantliei_executable(name: str = "cantliei-direct", pause_seconds: int = 10, exit_code: int = 0) -> DockerContainer:
    """Create a Heliophorus-cantliei DockerContainer executable.

    The mock platform does not actually run the container, so the
    pause_seconds and exit_code parameters are ignored by the mock
    lifecycle. They are included to keep the request structure
    consistent with the docker platform tests.
    """
    return DockerContainer(
        meta=ComponentMetadata(name=name),
        image=DockerImageSpec(
            locations=[CANTLIEI_IMAGE],
            digest=CANTLIEI_DIGEST,
        ),
        command=[str(pause_seconds), str(exit_code)],
    )


# ===========================================================================
# Direct execution basic tests
# ===========================================================================

class TestDirectExecutionBasic:
    """
    Tests that the direct execution endpoint creates a session
    that starts at ACCEPTED phase, bypassing the offer-set flow.
    """

    def test_direct_execution_returns_session(self, client):
        """
        A direct execution request should return an
        AbstractExecutionSession (not an OfferSetResponse).
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-basic",
                pause_seconds=5,
            ),
        )
        session = client.direct_execute(request)
        assert isinstance(session, AbstractExecutionSession), (
            f"Expected AbstractExecutionSession, got {type(session)}"
        )

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

    def test_direct_execution_session_has_uuid(self, client):
        """
        The returned session should have a UUID in its metadata.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-uuid",
                pause_seconds=5,
            ),
        )
        session = client.direct_execute(request)
        assert session.meta is not None, "Session should have metadata"
        assert session.meta.uuid is not None, "Session should have a UUID"

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

    def test_direct_execution_starts_at_accepted(self, client):
        """
        A direct execution session should start at ACCEPTED phase
        (or may have already progressed to PREPARING).
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-accepted",
                pause_seconds=5,
            ),
        )
        session = client.direct_execute(request)
        assert session.phase in (
            SimpleExecutionSessionPhase.ACCEPTED,
            SimpleExecutionSessionPhase.PREPARING,
        ), (
            f"Direct execution session should start at ACCEPTED or "
            f"PREPARING, got {session.phase}"
        )

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

    def test_direct_execution_has_executable(self, client):
        """
        The returned session should include the executable with the
        Docker image details from the request.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-executable",
                pause_seconds=5,
            ),
        )
        session = client.direct_execute(request)
        assert session.executable is not None, (
            "Session should include an executable"
        )
        assert session.executable.image is not None, (
            "Executable should have image info"
        )
        assert session.executable.image.locations is not None
        assert CANTLIEI_IMAGE in session.executable.image.locations

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

    def test_direct_execution_has_compute(self, client):
        """
        The returned session should include a default compute resource
        even when none was explicitly requested.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-compute",
                pause_seconds=5,
            ),
        )
        session = client.direct_execute(request)
        assert session.compute is not None, (
            "Session should include a compute resource"
        )

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

    def test_direct_execution_with_explicit_compute(self, client):
        """
        A direct execution with explicit compute resources should
        preserve them in the session.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-explicit-compute",
                pause_seconds=5,
            ),
            compute=SimpleComputeResource(
                meta=ComponentMetadata(name="mock-direct-compute"),
                cores=SimpleComputeCores(min=1, max=2),
                memory=SimpleComputeMemory(min=1, max=2),
            ),
        )
        session = client.direct_execute(request)
        assert session.compute is not None
        assert session.compute.cores is not None
        assert session.compute.memory is not None

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

    def test_direct_execution_session_retrievable_by_uuid(self, client):
        """
        After direct execution, the session should be retrievable
        via GET /sessions/{uuid}.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-retrievable",
                pause_seconds=5,
            ),
        )
        session = client.direct_execute(request)
        session_uuid = session.meta.uuid

        fetched = client.get_session(session_uuid)
        assert fetched is not None, "Should be able to fetch the session"
        assert fetched.meta.uuid == session_uuid, (
            "Fetched session UUID should match"
        )

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

# ===========================================================================
# Direct execution lifecycle tests
# ===========================================================================

class TestDirectExecutionLifecycle:
    """
    Tests for the full lifecycle of a direct execution session on the
    mock platform: ACCEPTED -> PREPARING -> AVAILABLE -> RELEASING -> COMPLETED

    The mock platform simulates the lifecycle using MockMonitorAction
    with a loop count of 4 and 30-second delays. Each component is
    processed serially, so a single session with 2 components (executable
    + compute) takes approximately 5-6 minutes to complete.

    The mock platform does not simulate exit codes, so all sessions
    reach COMPLETED regardless of the requested exit code.
    """

    def test_direct_execution_completes(self, client):
        """
        A direct execution should progress through the lifecycle
        and reach COMPLETED on the mock platform.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-complete",
                pause_seconds=5,
                exit_code=0,
            ),
        )
        session = client.direct_execute(request)
        session_uuid = session.meta.uuid

        result = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        assert result.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Direct execution should reach COMPLETED, "
            f"got {result.phase}"
        )


# ===========================================================================
# Direct execution vs offer-set comparison
# ===========================================================================

class TestDirectExecutionVsOfferSet:
    """
    Tests that compare direct execution with the offer-set flow
    to verify consistent behavior.
    """

    def test_direct_skips_offered_phase(self, client):
        """
        Direct execution should never pass through the OFFERED phase.
        The session should start at ACCEPTED or beyond.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-no-offered",
                pause_seconds=5,
            ),
        )
        session = client.direct_execute(request)
        assert session.phase != SimpleExecutionSessionPhase.OFFERED, (
            f"Direct execution should not be in OFFERED phase, "
            f"got {session.phase}"
        )

        fetched = client.get_session(session.meta.uuid)
        assert fetched.phase != SimpleExecutionSessionPhase.OFFERED, (
            f"Direct execution session should not be OFFERED on "
            f"subsequent GET, got {fetched.phase}"
        )

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )

# ===========================================================================
# Direct execution cancellation test
# ===========================================================================

class TestDirectExecutionCancellation:
    """
    Tests for cancelling a direct execution session.
    """

    def test_cancel_direct_execution(self, client):
        """
        A direct execution session should be cancellable before it
        reaches a terminal state.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "mock-direct-cancel",
                pause_seconds=60,
            ),
        )
        session = client.direct_execute(request)

        cancelled = client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )
