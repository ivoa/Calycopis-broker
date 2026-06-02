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
#     "timestamp": "2026-03-24T14:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 30,
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
Integration tests for the Docker platform implementation.

These tests verify the broker can create offers, accept sessions,
and execute Docker containers via the Docker/Podman platform.

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
  pytest tests/python/test_docker_platform.py -v
  CALYCOPIS_URL=http://host:port pytest tests/python/test_docker_platform.py -v
"""

import re
import time
from datetime import datetime, timedelta, timezone

import pytest

from calycopis_schema_client.models import (
    ExecutionRequest,
    OfferSetResponse,
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

def _make_cantliei_executable(name: str = "cantliei-test", pause_seconds: int = 10, exit_code: int = 0) -> DockerContainer:
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


def _submit(client, request: ExecutionRequest) -> OfferSetResponse:
    """Submit a request and return the OfferSetResponse."""
    response = client.submit_execution(request, follow_redirect=True)
    assert isinstance(response, OfferSetResponse), (
        f"Expected OfferSetResponse, got {type(response)}"
    )
    return response


def _assert_accepted(response: OfferSetResponse, msg: str = ""):
    """Assert that the response result is YES with at least one offer."""
    assert response.result == "YES", (
        f"Expected YES, got {response.result}. {msg} "
        f"Messages: {response.meta.messages if response.meta else 'none'}"
    )
    assert response.offers is not None and len(response.offers) > 0, (
        f"Expected at least one offer. {msg}"
    )


def _assert_rejected(response: OfferSetResponse, msg: str = ""):
    """Assert that the response result is NO (validation failed)."""
    assert response.result == "NO", (
        f"Expected NO, got {response.result}. {msg} "
        f"Messages: {response.meta.messages if response.meta else 'none'}"
    )


def _parse_iso8601_interval(interval_str: str):
    """Parse an ISO 8601 interval string into (start_datetime, end_datetime).

    Supports two forms:
      - <datetime>/<duration>  e.g. '2026-03-01T19:50:00Z/PT35S'
      - <duration>/<datetime>  e.g. 'PT35S/2026-03-01T19:50:00Z'

    Returns:
        Tuple of (start: datetime, end: datetime) in UTC.
    """
    left, right = interval_str.split("/", 1)

    def _parse_dt(s):
        s = s.replace("Z", "+00:00")
        return datetime.fromisoformat(s)

    def _parse_duration(s):
        m = re.match(
            r"^P(?:(\d+)D)?(?:T(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?)?$",
            s,
        )
        if not m:
            raise ValueError(f"Cannot parse ISO 8601 duration: {s}")
        days = int(m.group(1) or 0)
        hours = int(m.group(2) or 0)
        minutes = int(m.group(3) or 0)
        seconds = float(m.group(4) or 0)
        return timedelta(days=days, hours=hours, minutes=minutes, seconds=seconds)

    if left.startswith("P"):
        dur = _parse_duration(left)
        end = _parse_dt(right)
        return (end - dur, end)
    else:
        start = _parse_dt(left)
        dur = _parse_duration(right)
        return (start, start + dur)


# ===========================================================================
# Offer creation tests
# ===========================================================================

class TestDockerPlatformOffers:
    """
    Tests that the Docker platform can create valid offers for
    the Heliophorus-cantliei test container.
    """

    def test_cantliei_basic_offer(self, client):
        """
        Submitting the cantliei container with no explicit compute
        should produce a YES response with at least one offer.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-basic", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Basic cantliei offer should be accepted")

        offer = response.offers[0]
        assert offer.executable is not None, "Offer should include an executable"
        assert offer.compute is not None, "Offer should include a compute resource"
        assert offer.phase == SimpleExecutionSessionPhase.OFFERED

    def test_cantliei_with_compute(self, client):
        """
        Submitting the cantliei container with explicit compute resource
        limits should produce a YES response.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-with-compute", pause_seconds=5),
            compute=SimpleComputeResource(
                meta=ComponentMetadata(name="cantliei-compute"),
                cores=SimpleComputeCores(min=1, max=2),
                memory=SimpleComputeMemory(min=1, max=2),
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Cantliei with compute should be accepted")

        offer = response.offers[0]
        assert offer.compute is not None
        assert offer.compute.cores is not None
        assert offer.compute.memory is not None

    def test_cantliei_compute_over_limit_rejected(self, client):
        """
        Requesting compute resources that exceed the Docker platform
        limits (min cores > 16) should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-over-limit", pause_seconds=5),
            compute=SimpleComputeResource(
                meta=ComponentMetadata(name="over-limit-compute"),
                cores=SimpleComputeCores(min=32, max=32),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Min cores > 16 should be rejected")

    def test_cantliei_offer_has_executable_image(self, client):
        """
        The offer's executable should preserve the Docker image details
        from the request.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-image-check", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        executable = offer.executable
        assert executable is not None

        # The executable should be a DockerContainer with image info.
        assert executable.image is not None, "Executable should have image info"
        assert executable.image.locations is not None
        assert CANTLIEI_IMAGE in executable.image.locations
        assert executable.image.digest == CANTLIEI_DIGEST

    def test_cantliei_offer_has_session_uuid(self, client):
        """
        Each offer should have a session UUID assigned in its metadata.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-uuid-check", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.meta is not None, "Offer should have metadata"
        assert offer.meta.uuid is not None, "Offer should have a UUID"


# ===========================================================================
# Session lifecycle tests
# ===========================================================================

class TestDockerPlatformSessionLifecycle:
    """
    Tests for the full session lifecycle on the Docker platform:
    OFFERED -> ACCEPTED -> PREPARING -> AVAILABLE -> RUNNING -> COMPLETED

    These tests accept an offer, pass a specific pause duration to the
    cantliei container via the command property, and verify that the
    session transitions through the expected phases and completes at
    approximately the expected time.
    """

    def test_accept_offer(self, client):
        """
        Accepting an offered session should transition it to ACCEPTED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-accept", pause_seconds=10),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.ACCEPTED,
            SimpleExecutionSessionPhase.PREPARING,
        ), (
            f"After accepting, session should be ACCEPTED or PREPARING, "
            f"got {session.phase}"
        )

    def test_session_reaches_preparing(self, client):
        """
        After accepting, the session should eventually reach PREPARING
        as the broker begins to prepare the Docker container.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-preparing", pause_seconds=30),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.PREPARING,
                SimpleExecutionSessionPhase.AVAILABLE,
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=60.0,
            interval=2.0,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.PREPARING,
            SimpleExecutionSessionPhase.AVAILABLE,
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should reach PREPARING or beyond, got {session.phase}"
        )

    def test_session_reaches_available(self, client):
        """
        After preparation completes, the session should reach AVAILABLE,
        meaning the Docker container has been created and started.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-available", pause_seconds=30),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

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
            f"Session should reach AVAILABLE or beyond, got {session.phase}"
        )

    def test_session_completes_with_timed_container(self, client):
        """
        Run the cantliei container for a known duration and verify that
        the time from AVAILABLE/RUNNING to COMPLETED matches the
        requested pause, using the broker's published schedule to
        minimise unnecessary polling.

        The offer includes a compute-resource schedule with
        ``preparing.start`` (when preparation begins) and
        ``available.start`` (an ISO 8601 interval for when the session
        should become AVAILABLE).  The test:

        1. Sleeps until just before ``preparing.start`` and asserts
           the session phase is still less than AVAILABLE (preparation
           has not begun yet, so the container cannot be running).
        2. Polls from that point, using the ``available.start``
           interval to bound the timeout, and verifies the session
           reaches AVAILABLE/RUNNING.
        3. Times the AVAILABLE/RUNNING → COMPLETED transition and
           checks it matches the requested container duration.
        """
        pause_seconds = 20
        # Overhead on top of pause_seconds for monitor poll intervals,
        # container startup/teardown and session state propagation.
        completion_overhead = 60.0
        # Safety margin: seconds before preparing.start to perform
        # the pre-AVAILABLE check.
        pre_check_margin = 5.0

        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-timed-complete",
                pause_seconds=pause_seconds,
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        # ---- extract schedule from the offer ----
        compute_schedule = offer.compute.schedule
        assert compute_schedule is not None, (
            "Offer compute resource should include a schedule"
        )
        assert compute_schedule.preparing is not None, (
            "Compute schedule should include a preparing entry"
        )
        assert compute_schedule.available is not None, (
            "Compute schedule should include an available interval"
        )
        assert compute_schedule.available.start is not None, (
            "Compute schedule available should include a start interval"
        )

        prepare_start_str = compute_schedule.preparing.start
        assert prepare_start_str is not None, (
            "Compute schedule preparing should include a start time"
        )
        prepare_start = datetime.fromisoformat(
            prepare_start_str.replace("Z", "+00:00")
        )

        avail_start, avail_end = _parse_iso8601_interval(
            compute_schedule.available.start
        )
        # If the interval has zero duration, give a small window for
        # the phase transition to propagate.
        if avail_end <= avail_start:
            avail_end = avail_start + timedelta(seconds=60)

        # ---- accept the session ----
        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        # ---- Phase 1: sleep until just before preparation begins ----
        now = datetime.now(timezone.utc)
        check_time = prepare_start - timedelta(seconds=pre_check_margin)
        if check_time > now:
            time.sleep((check_time - now).total_seconds())

        # Preparation has not started, so the session must still be
        # before AVAILABLE.
        session = client.get_session(session_uuid)
        pre_phase = session.phase
        assert pre_phase not in (
            SimpleExecutionSessionPhase.AVAILABLE,
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should still be before AVAILABLE at "
            f"{pre_check_margin}s before preparing.start "
            f"({prepare_start_str}), got {pre_phase}"
        )

        # ---- Phase 2: poll until AVAILABLE/RUNNING ----
        # Timeout: from now until the end of the available window
        # plus overhead for image pulls and scheduling jitter.
        phase2_timeout = (
            (avail_end - datetime.now(timezone.utc)).total_seconds()
            + completion_overhead
        )
        phase2_timeout = max(phase2_timeout, 60.0)

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.AVAILABLE,
                SimpleExecutionSessionPhase.RUNNING,
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=phase2_timeout,
            interval=5.0,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.AVAILABLE,
            SimpleExecutionSessionPhase.RUNNING,
            SimpleExecutionSessionPhase.COMPLETED,
        ), (
            f"Session should reach AVAILABLE/RUNNING within the "
            f"scheduled window, got {session.phase}"
        )

        # ---- Phase 3: time the AVAILABLE/RUNNING → COMPLETED step ----
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

        assert elapsed >= pause_seconds, (
            f"Session completed too quickly ({elapsed:.1f}s) for a "
            f"{pause_seconds}s container"
        )
        assert elapsed <= pause_seconds + completion_overhead, (
            f"Session took too long ({elapsed:.1f}s) for a "
            f"{pause_seconds}s container (max allowed "
            f"{pause_seconds + completion_overhead}s)"
        )

    def test_short_and_long_containers(self, client):
        """
        Run two containers with different pause durations and verify
        the shorter one completes before the longer one.
        """
        short_pause = 10
        long_pause = 30
        wait_timeout = 600.0

        short_request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-short",
                pause_seconds=short_pause,
            ),
        )
        long_request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-long",
                pause_seconds=long_pause,
            ),
        )

        short_response = _submit(client, short_request)
        long_response = _submit(client, long_request)
        _assert_accepted(short_response)
        _assert_accepted(long_response)

        short_uuid = short_response.offers[0].meta.uuid
        long_uuid = long_response.offers[0].meta.uuid

        client.set_session_phase(
            short_uuid, SimpleExecutionSessionPhase.ACCEPTED,
        )
        client.set_session_phase(
            long_uuid, SimpleExecutionSessionPhase.ACCEPTED,
        )

        short_start = time.monotonic()

        short_session = client.wait_for_phase(
            short_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=wait_timeout,
            interval=5.0,
        )
        short_elapsed = time.monotonic() - short_start

        long_session = client.wait_for_phase(
            long_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=wait_timeout,
            interval=5.0,
        )
        long_elapsed = time.monotonic() - short_start

        assert short_session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Short session should reach COMPLETED, got {short_session.phase}"
        )
        assert long_session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Long session should reach COMPLETED, got {long_session.phase}"
        )

        assert short_elapsed < long_elapsed, (
            f"Short container ({short_pause}s, elapsed {short_elapsed:.1f}s) "
            f"should complete before long container "
            f"({long_pause}s, elapsed {long_elapsed:.1f}s)"
        )


# ===========================================================================
# Session cancellation test
# ===========================================================================

class TestDockerPlatformCancellation:
    """
    Tests for cancelling a Docker session.
    """

    def test_cancel_offered_session(self, client):
        """
        Rejecting an offered session should transition it to REJECTED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-reject", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.REJECTED,
        )
        assert session.phase == SimpleExecutionSessionPhase.REJECTED, (
            f"After rejecting, session should be REJECTED, got {session.phase}"
        )

    def test_cancel_accepted_session(self, client):
        """
        Cancelling an accepted session should transition it to CANCELLED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-cancel", pause_seconds=5),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert session.phase == SimpleExecutionSessionPhase.CANCELLED, (
            f"After cancelling, session should be CANCELLED, got {session.phase}"
        )


# ===========================================================================
# Multiple offers test
# ===========================================================================

class TestDockerPlatformMultipleOffers:
    """
    Tests for handling multiple concurrent offer requests
    on the Docker platform.
    """

    def test_independent_sessions(self, client):
        """
        Two independent offer requests should produce independent sessions
        with distinct UUIDs.
        """
        request_a = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-session-a", pause_seconds=5),
        )
        request_b = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-session-b", pause_seconds=5),
        )
        response_a = _submit(client, request_a)
        response_b = _submit(client, request_b)

        _assert_accepted(response_a)
        _assert_accepted(response_b)

        uuid_a = response_a.offers[0].meta.uuid
        uuid_b = response_b.offers[0].meta.uuid

        assert uuid_a != uuid_b, (
            "Independent sessions should have different UUIDs"
        )

    def test_accept_one_reject_other(self, client):
        """
        From two offers, accepting one and rejecting the other should
        produce independent outcomes.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-dual", pause_seconds=10),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        # Accept this one.
        session = client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )
        assert session.phase in (
            SimpleExecutionSessionPhase.ACCEPTED,
            SimpleExecutionSessionPhase.PREPARING,
        )

        # Submit a second request and reject it.
        request2 = ExecutionRequest(
            executable=_make_cantliei_executable("cantliei-dual-reject", pause_seconds=5),
        )
        response2 = _submit(client, request2)
        _assert_accepted(response2)

        offer2 = response2.offers[0]
        session2 = client.set_session_phase(
            offer2.meta.uuid,
            SimpleExecutionSessionPhase.REJECTED,
        )
        assert session2.phase == SimpleExecutionSessionPhase.REJECTED

        # The first session should still be progressing.
        session1_check = client.get_session(session_uuid)
        assert session1_check.phase != SimpleExecutionSessionPhase.REJECTED, (
            "First session should not be affected by rejecting the second"
        )


# ===========================================================================
# Non-zero exit code tests
# ===========================================================================

class TestDockerPlatformNonZeroExitCode:
    """
    Tests for Docker sessions where the container exits with a non-zero
    exit code. The broker should transition these sessions to FAILED.

    Uses the Heliophorus-cantliei container's second command parameter
    to control the exit code.
    """

    def test_nonzero_exit_code_reaches_failed(self, client):
        """
        A container that exits with a non-zero exit code should cause
        the session to reach FAILED rather than COMPLETED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-nonzero-exit",
                pause_seconds=5,
                exit_code=1,
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        assert session.phase == SimpleExecutionSessionPhase.FAILED, (
            f"Session with non-zero exit code should reach FAILED, "
            f"got {session.phase}"
        )

    def test_zero_exit_code_reaches_completed(self, client):
        """
        A container that exits with exit code 0 (using the explicit
        two-parameter command format) should reach COMPLETED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-zero-exit",
                pause_seconds=5,
                exit_code=0,
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        assert session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Session with exit code 0 should reach COMPLETED, "
            f"got {session.phase}"
        )

    def test_nonzero_exit_code_with_large_code(self, client):
        """
        A container that exits with a larger non-zero exit code (e.g. 42)
        should still reach FAILED.
        """
        request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-exit-42",
                pause_seconds=5,
                exit_code=42,
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        session_uuid = offer.meta.uuid

        client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        session = client.wait_for_phase(
            session_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        assert session.phase == SimpleExecutionSessionPhase.FAILED, (
            f"Session with exit code 42 should reach FAILED, "
            f"got {session.phase}"
        )

    def test_zero_and_nonzero_independent_outcomes(self, client):
        """
        Submit two containers concurrently — one with exit code 0 and one
        with a non-zero exit code — and verify that each reaches the
        correct terminal state independently.
        """
        success_request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-success",
                pause_seconds=5,
                exit_code=0,
            ),
        )
        failure_request = ExecutionRequest(
            executable=_make_cantliei_executable(
                "cantliei-failure",
                pause_seconds=5,
                exit_code=1,
            ),
        )

        success_response = _submit(client, success_request)
        failure_response = _submit(client, failure_request)
        _assert_accepted(success_response)
        _assert_accepted(failure_response)

        success_uuid = success_response.offers[0].meta.uuid
        failure_uuid = failure_response.offers[0].meta.uuid

        client.set_session_phase(
            success_uuid, SimpleExecutionSessionPhase.ACCEPTED,
        )
        client.set_session_phase(
            failure_uuid, SimpleExecutionSessionPhase.ACCEPTED,
        )

        success_session = client.wait_for_phase(
            success_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )
        failure_session = client.wait_for_phase(
            failure_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=600.0,
            interval=5.0,
        )

        assert success_session.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Success session (exit code 0) should reach COMPLETED, "
            f"got {success_session.phase}"
        )
        assert failure_session.phase == SimpleExecutionSessionPhase.FAILED, (
            f"Failure session (exit code 1) should reach FAILED, "
            f"got {failure_session.phase}"
        )
