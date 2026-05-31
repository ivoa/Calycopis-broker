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
#     }
#   ]
#

"""
Stress test for the mock platform implementation.

Launches N concurrent execution requests via direct execution and
tracks their progress through the full mock lifecycle from ACCEPTED
to COMPLETED.

The mock platform processes sessions serially with 30-second delays
per component per phase transition. Each component has a lifecycle
loop count of 4, meaning the AVAILABLE phase is held for 4 monitoring
cycles before transitioning to RELEASING. With 2 components per
session (executable + compute), each session takes approximately
5-6 minutes to complete on the mock platform.

Because of serial processing, this test uses a much lower default
session count (STRESS_COUNT=5) and longer timeout than the docker
stress test.

Requires:
  - A running Calycopis broker service with the 'mock' profile active.
  - The calycopis_schema_client Python package installed.
  - A freshly started broker to avoid queue congestion.

Usage:
  pytest tests/python/test_mock_stress.py -v -s
  STRESS_COUNT=3 pytest tests/python/test_mock_stress.py -v -s
"""

import os
import sys
import time
from collections import Counter
from concurrent.futures import ThreadPoolExecutor, as_completed

import pytest

from calycopis_schema_client.models import (
    ExecutionRequest,
    SimpleExecutionSessionPhase,
)
from calycopis_schema_client.models.docker_container import DockerContainer
from calycopis_schema_client.models.docker_image_spec import DockerImageSpec
from calycopis_schema_client.models.component_metadata import ComponentMetadata


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

STRESS_COUNT = int(os.environ.get("STRESS_COUNT", "5"))
POLL_INTERVAL = float(os.environ.get("STRESS_POLL_INTERVAL", "10.0"))
TIMEOUT = float(os.environ.get("STRESS_TIMEOUT", "3600.0"))
POLL_WORKERS = int(os.environ.get("STRESS_POLL_WORKERS", "5"))

DOCKER_CONTAINER_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/docker-container-1.0"
)

CANTLIEI_IMAGE = "ghcr.io/zarquan/heliophorus-cantliei:sha-831ee57"
CANTLIEI_DIGEST = "sha256:6e495692cc6f1cae2023f261f433d4691aa70b19416730f8301e45fbb74bc526"

PHASE_ORDER = [
    "OFFERED",
    "ACCEPTED",
    "PREPARING",
    "WAITING",
    "AVAILABLE",
    "RUNNING",
    "RELEASING",
    "COMPLETED",
    "FAILED",
    "CANCELLED",
]

TERMINAL_PHASES = {
    SimpleExecutionSessionPhase.COMPLETED,
    SimpleExecutionSessionPhase.FAILED,
    SimpleExecutionSessionPhase.CANCELLED,
}



# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _make_executable(index: int) -> DockerContainer:
    return DockerContainer(
        kind=DOCKER_CONTAINER_KIND,
        meta=ComponentMetadata(name=f"mock-stress-{index:04d}"),
        image=DockerImageSpec(
            locations=[CANTLIEI_IMAGE],
            digest=CANTLIEI_DIGEST,
        ),
        command=["5"],
    )


def _format_phase_bar(counts: Counter, total: int) -> str:
    """Format a compact progress bar showing phase distribution."""
    parts = []
    for phase in PHASE_ORDER:
        n = counts.get(phase, 0)
        if n > 0:
            parts.append(f"{phase[:4]}:{n}")
    return " | ".join(parts)


# ---------------------------------------------------------------------------
# Stress test
# ---------------------------------------------------------------------------

class TestMockStress:

    def test_concurrent_sessions(self, client):
        """
        Launch STRESS_COUNT concurrent direct execution requests on
        the mock platform and track all sessions through the full
        lifecycle to completion.

        Uses direct execution (POST /sessions) to bypass the
        offer-set negotiation and start sessions at ACCEPTED.

        Reports progress every POLL_INTERVAL seconds showing the
        distribution of sessions across lifecycle phases.
        """
        total = STRESS_COUNT
        print(f"\n{'='*70}")
        print(f"Mock stress test: {total} concurrent sessions")
        print(f"{'='*70}")
        errors = []
        sessions = {}
        timestamps = {}

        # ------------------------------------------------------------------
        # Phase 1: Submit direct execution requests
        # ------------------------------------------------------------------
        print(f"\n[Phase 1] Submitting {total} direct execution requests...")
        submit_start = time.monotonic()

        for i in range(total):
            request = ExecutionRequest(
                executable=_make_executable(i),
            )
            try:
                session = client.direct_execute(request)
                uuid = session.meta.uuid
                sessions[i] = uuid
                timestamps[uuid] = {"accepted": time.monotonic()}
            except Exception as exc:
                errors.append(f"Submit {i}: {exc}")
            if (i + 1) % 5 == 0:
                print(f"    ... {i + 1}/{total} submitted")

        submit_elapsed = time.monotonic() - submit_start
        print(f"    Submitted {len(sessions)}/{total} sessions "
              f"in {submit_elapsed:.1f}s "
              f"({len(errors)} errors)")

        if not sessions:
            pytest.fail(f"No sessions were created. Errors: {errors}")

        # ------------------------------------------------------------------
        # Phase 2: Poll all sessions and track progress
        # ------------------------------------------------------------------
        total_sessions = len(sessions)
        print(f"\n[Phase 2] Tracking {total_sessions} sessions "
              f"(poll every {POLL_INTERVAL}s, timeout {TIMEOUT}s)...")
        print()

        track_start = time.monotonic()
        deadline = track_start + TIMEOUT
        active_uuids = set(sessions.values())
        completed_uuids = set()
        last_phases = {uuid: "ACCEPTED" for uuid in active_uuids}
        poll_count = 0

        header = (f"  {'Time':>6s}  {'Poll':>4s}  "
                  f"{'Done':>4s}/{total_sessions:<4d}  Progress")
        print(header)
        print(f"  {'-'*len(header)}")

        while active_uuids and time.monotonic() < deadline:
            poll_count += 1
            elapsed = time.monotonic() - track_start

            still_active = set()

            def _poll_one(uuid):
                session = client.get_session(uuid)
                return uuid, session.phase

            with ThreadPoolExecutor(max_workers=POLL_WORKERS) as pool:
                futures = {
                    pool.submit(_poll_one, uuid): uuid
                    for uuid in active_uuids
                }
                for future in as_completed(futures):
                    uuid = futures[future]
                    try:
                        _, phase = future.result()
                        phase_str = phase.value if phase else "UNKNOWN"
                        last_phases[uuid] = phase_str

                        if phase in TERMINAL_PHASES:
                            completed_uuids.add(uuid)
                            if uuid in timestamps:
                                timestamps[uuid]["terminal"] = time.monotonic()
                                timestamps[uuid]["final_phase"] = phase_str
                        else:
                            still_active.add(uuid)
                    except Exception as exc:
                        still_active.add(uuid)

            active_uuids = still_active
            done = len(completed_uuids)

            phase_counts = Counter(last_phases.values())
            bar = _format_phase_bar(phase_counts, total_sessions)

            print(f"  {elapsed:5.0f}s  {poll_count:4d}  "
                  f"{done:4d}/{total_sessions:<4d}  {bar}")
            sys.stdout.flush()

            if not active_uuids:
                break

            time.sleep(POLL_INTERVAL)

        total_elapsed = time.monotonic() - submit_start

        # ------------------------------------------------------------------
        # Results summary
        # ------------------------------------------------------------------
        print(f"\n{'='*70}")
        print(f"RESULTS")
        print(f"{'='*70}")

        final_counts = Counter()
        for uuid in sessions.values():
            final_counts[last_phases.get(uuid, "UNKNOWN")] += 1

        print(f"\n  Requested:         {total}")
        print(f"  Submitted:         {len(sessions)}")
        print(f"  Completed:         {final_counts.get('COMPLETED', 0)}")
        print(f"  Failed:            {final_counts.get('FAILED', 0)}")
        print(f"  Cancelled:         {final_counts.get('CANCELLED', 0)}")
        print(f"  Still active:      {len(active_uuids)}")
        print(f"  Submit errors:     {len(errors)}")

        print(f"\n  Timing:")
        print(f"    Submit phase:    {submit_elapsed:6.1f}s")
        track_elapsed = time.monotonic() - track_start
        print(f"    Tracking phase:  {track_elapsed:6.1f}s")
        print(f"    Total:           {total_elapsed:6.1f}s")

        lifecycle_times = []
        for uuid, ts in timestamps.items():
            if "accepted" in ts and "terminal" in ts:
                lifecycle_times.append(ts["terminal"] - ts["accepted"])

        if lifecycle_times:
            lifecycle_times.sort()
            print(f"\n  Lifecycle duration (ACCEPTED -> terminal):")
            print(f"    Min:             {min(lifecycle_times):6.1f}s")
            print(f"    Max:             {max(lifecycle_times):6.1f}s")
            print(f"    Mean:            "
                  f"{sum(lifecycle_times)/len(lifecycle_times):6.1f}s")
            p50 = lifecycle_times[len(lifecycle_times)//2]
            p90 = lifecycle_times[int(len(lifecycle_times)*0.9)]
            print(f"    P50:             {p50:6.1f}s")
            print(f"    P90:             {p90:6.1f}s")

        print(f"\n{'='*70}")

        if errors:
            print(f"\n  Errors ({len(errors)}):")
            for e in errors[:20]:
                print(f"    - {e}")
            if len(errors) > 20:
                print(f"    ... and {len(errors)-20} more")

        completed_count = final_counts.get("COMPLETED", 0)
        failed_count = final_counts.get("FAILED", 0)
        timed_out = len(active_uuids)

        assert len(errors) == 0, (
            f"{len(errors)}/{total} direct execution submissions failed"
        )
        assert timed_out == 0, (
            f"{timed_out} sessions did not reach a terminal phase "
            f"within {TIMEOUT}s"
        )
        assert failed_count == 0, (
            f"{failed_count}/{total_sessions} sessions FAILED"
        )
        assert completed_count == total_sessions, (
            f"Only {completed_count}/{total_sessions} sessions COMPLETED"
        )
