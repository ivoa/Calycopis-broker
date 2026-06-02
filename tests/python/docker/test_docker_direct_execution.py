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
#     "timestamp": "2026-03-24T15:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-03-26T16:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 5,
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
Integration tests for the direct execution endpoint on the docker platform.

Direct execution bypasses the offer-set negotiation and creates an
ExecutionSession starting at the ACCEPTED phase. The session then
progresses through the normal lifecycle: PREPARING -> AVAILABLE ->
RUNNING -> COMPLETED (or FAILED for non-zero exit codes).

The test container is Heliophorus-cantliei, a simple Alpine container
that waits for a configurable number of seconds and then exits with
a configurable exit code.
https://github.com/Zarquan/Heliophorus-cantliei

Requires:
  - A running Calycopis broker service with the 'docker' profile active.
  - The CONTAINER_HOST environment variable set in the broker environment.
  - The calycopis_schema_client Python package installed.
  - Network access to ghcr.io to pull the test container image.

Usage:
  pytest tests/python/test_docker_direct_execution.py -v
  CALYCOPIS_URL=http://host:port pytest tests/python/test_docker_direct_execution.py -v
"""

import time

import pytest

from calycopis_schema_client.models import (
    ExecutionRequest,
    AbstractExecutionSession,
    SimpleExecutionSessionPhase,
)
from calycopis_schema_client.models.docker_image_spec import DockerImageSpec
from calycopis_schema_client.models.simple_compute_cores import SimpleComputeCores
from calycopis_schema_client.models.simple_compute_memory import SimpleComputeMemory
from calycopis_schema_client.models.component_metadata import ComponentMetadata
from calycopis_schema_client.wrappers import (
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

    Args:
        name: Human-readable name for the executable.
        pause_seconds: Number of seconds the container should pause before exiting.
        exit_code: Exit code the container should return (default: 0).
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
                "direct-basic",
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
                "direct-uuid",
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
                "direct-accepted",
                pause_seconds=30,
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
                "direct-executable",
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
                "direct-compute",
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
                "direct-explicit-compute",
                pause_seconds=5,
            ),
            compute=SimpleComputeResource(
                meta=ComponentMetadata(name="direct-compute"),
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
                "direct-retrievable",
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
    Tests for the full lifecycle of a direct execution session:
    ACCEPTED -> PREPARING -> AVAILABLE -> RUNNING -> COMPLETED

    Since direct execution starts at ACCEPTED, the session skips the
    OFFERED phase entirely — there is no offer to accept or reject.
    """

    def test_direct_execution_completes(self, client):
        """
        A direct execution with exit code 0 should progress through
        the lifecycle and reach COMPLETED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "direct-complete",
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
            f"Direct execution with exit code 0 should reach COMPLETED, "
            f"got {result.phase}"
        )

    def test_direct_execution_nonzero_exit_fails(self, client):
        """
        A direct execution with a non-zero exit code should progress
        through the lifecycle and reach FAILED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "direct-fail",
                pause_seconds=5,
                exit_code=1,
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
        assert result.phase == SimpleExecutionSessionPhase.FAILED, (
            f"Direct execution with non-zero exit code should reach "
            f"FAILED, got {result.phase}"
        )

    def test_direct_execution_timed_completion(self, client):
        """
        Run a direct execution for a known duration and verify that
        the elapsed time from AVAILABLE/RUNNING to COMPLETED is
        consistent with the requested pause duration.
        """
        pause_seconds = 15
        completion_overhead = 60.0

        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "direct-timed",
                pause_seconds=pause_seconds,
                exit_code=0,
            ),
        )
        session = client.direct_execute(request)
        session_uuid = session.meta.uuid

        # Wait for AVAILABLE/RUNNING first.
        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.AVAILABLE,
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.AVAILABLE,
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should reach AVAILABLE/RUNNING, got {session.phase}"
        )

        # Time the AVAILABLE/RUNNING -> COMPLETED transition.
        running_time = time.monotonic()

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=float(pause_seconds + completion_overhead),
            interval=5.0,
        )
        elapsed = time.monotonic() - running_time

        assert session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Session should reach COMPLETED, got {session.phase}"
        )
        assert elapsed <= pause_seconds + completion_overhead, (
            f"Session took too long ({elapsed:.1f}s) for a "
            f"{pause_seconds}s container"
        )


# ===========================================================================
# Direct execution vs offer-set comparison
# ===========================================================================

class TestDirectExecutionVsOfferSet:
    """
    Tests that compare direct execution with the offer-set flow
    to verify consistent behavior.
    """

    def test_direct_and_offerset_both_complete(self, client):
        """
        Submit the same container via both direct execution and the
        offer-set flow, and verify both reach COMPLETED.
        """
        # Direct execution path.
        direct_request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "direct-vs-offer-direct",
                pause_seconds=5,
                exit_code=0,
            ),
        )
        direct_session = client.direct_execute(direct_request)
        direct_uuid = direct_session.meta.uuid

        # Offer-set path.
        offer_request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "direct-vs-offer-offerset",
                pause_seconds=5,
                exit_code=0,
            ),
        )
        offer_response = client.submit_execution(
            offer_request, follow_redirect=True,
        )
        assert offer_response.result == "YES"
        assert offer_response.offers is not None
        assert len(offer_response.offers) > 0

        offer_uuid = offer_response.offers[0].meta.uuid
        client.set_session_phase(
            offer_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        # Wait for both to reach a terminal state.
        direct_result = client.wait_for_phase(
            direct_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        offer_result = client.wait_for_phase(
            offer_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )

        assert direct_result.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Direct session should COMPLETE, got {direct_result.phase}"
        )
        assert offer_result.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Offer session should COMPLETE, got {offer_result.phase}"
        )

    def test_direct_skips_offered_phase(self, client):
        """
        Direct execution should never pass through the OFFERED phase.
        The session should start at ACCEPTED or beyond.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "direct-no-offered",
                pause_seconds=30,
            ),
        )
        session = client.direct_execute(request)
        assert session.phase != SimpleExecutionSessionPhase.OFFERED, (
            f"Direct execution should not be in OFFERED phase, "
            f"got {session.phase}"
        )

        # Also verify via a GET — the session should not have
        # reverted to OFFERED.
        fetched = client.get_session(session.meta.uuid)
        assert fetched.phase != SimpleExecutionSessionPhase.OFFERED, (
            f"Direct execution session should not be OFFERED on "
            f"subsequent GET, got {fetched.phase}"
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
                "direct-cancel",
                pause_seconds=60,
            ),
        )
        session = client.direct_execute(request)
        session_uuid = session.meta.uuid

        cancelled = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"Cancelled session should be CANCELLED, "
            f"got {cancelled.phase}"
        )
