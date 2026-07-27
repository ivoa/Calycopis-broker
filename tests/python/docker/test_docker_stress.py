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
#     "name":  "cursor",
#     "model": "Auto",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-03-25T17:30:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 3,
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
Stress test for the docker platform implementation.

Launches N concurrent execution requests via the offer-set flow
and tracks their progress through the full lifecycle from OFFERED
to COMPLETED using the Heliophorus-cantliei test container.

Requires:
  - A running Calycopis broker service with the 'docker' profile active.
  - The CONTAINER_HOST environment variable set in the broker environment.
  - The calycopis_schema_client Python package installed.
  - Network access to ghcr.io to pull the test container image.

Usage:
  pytest tests/python/test_docker_stress.py -v -s
  STRESS_COUNT=50 pytest tests/python/test_docker_stress.py -v -s
"""

import os
import sys
import time
from collections import Counter
from concurrent.futures import ThreadPoolExecutor, as_completed

import pytest

from calycopis_openapi_client.models import (
    ExecutionRequest,
    OfferSetResponse,
    SimpleExecutionSessionPhase,
)
from calycopis_openapi_client.models.docker_image_spec import DockerImageSpec
from calycopis_openapi_client.models.component_metadata import ComponentMetadata
from calycopis_openapi_client.wrappers import (
    DockerContainer,
    SimpleComputeResource,
)


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

STRESS_COUNT = int(os.environ.get("STRESS_COUNT", "100"))
PAUSE_SECONDS = int(os.environ.get("STRESS_PAUSE", "5"))
POLL_INTERVAL = float(os.environ.get("STRESS_POLL_INTERVAL", "5.0"))
TIMEOUT = float(os.environ.get("STRESS_TIMEOUT", "900.0"))
SUBMIT_WORKERS = int(os.environ.get("STRESS_SUBMIT_WORKERS", "10"))

CANTLIEI_IMAGE = "ghcr.io/zarquan/heliophorus-cantliei:sha-c9572b0"
CANTLIEI_DIGEST = "sha256:4911760109f78976d2a95a6491a8d8c77bfee9fd1498b9a4b7dd5b7515826689"

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
        meta=ComponentMetadata(name=f"stress-{index:04d}"),
        image=DockerImageSpec(
            locations=[CANTLIEI_IMAGE],
            digest=CANTLIEI_DIGEST,
        ),
        command=[str(PAUSE_SECONDS)],
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

class TestStress:

    def test_concurrent_sessions(self, client):
        """
        Launch STRESS_COUNT concurrent execution requests and track
        all sessions through the full lifecycle to completion.

        Reports progress every POLL_INTERVAL seconds showing the
        distribution of sessions across lifecycle phases.
        """
        total = STRESS_COUNT
        print(f"\n{'='*70}")
        print(f"Stress test: {total} concurrent sessions, "
              f"{PAUSE_SECONDS}s container pause")
        print(f"{'='*70}")
        errors = []
        sessions = {}
        timestamps = {}

        # ------------------------------------------------------------------
        # Phase 1: Submit offer requests sequentially
        # ------------------------------------------------------------------
        print(f"\n[Phase 1] Submitting {total} offer requests...")
        submit_start = time.monotonic()

        responses = {}
        for i in range(total):
            request = ExecutionRequest(
                executable=_make_executable(i),
            )
            try:
                resp = client.submit_execution(request, follow_redirect=True)
                responses[i] = resp
            except Exception as exc:
                errors.append(f"Submit {i}: {exc}")
            if (i + 1) % 25 == 0:
                print(f"    ... {i + 1}/{total} submitted")

        submit_elapsed = time.monotonic() - submit_start
        print(f"    Submitted {len(responses)}/{total} offers "
              f"in {submit_elapsed:.1f}s "
              f"({len(errors)} errors)")

        # ------------------------------------------------------------------
        # Phase 2: Accept all offers concurrently
        # ------------------------------------------------------------------
        print(f"\n[Phase 2] Accepting {len(responses)} offers...")
        accept_start = time.monotonic()

        def _accept_one(index, resp):
            if (resp.result != "YES"
                    or resp.offers is None
                    or len(resp.offers) == 0):
                return index, None, f"No valid offer for request {index}"
            offer = resp.offers[0]
            uuid = offer.meta.uuid
            client.set_session_phase(uuid, SimpleExecutionSessionPhase.ACCEPTED)
            return index, uuid, None

        with ThreadPoolExecutor(max_workers=SUBMIT_WORKERS) as pool:
            futures = {
                pool.submit(_accept_one, idx, resp): idx
                for idx, resp in responses.items()
            }
            for future in as_completed(futures):
                idx = futures[future]
                try:
                    _, uuid, err = future.result()
                    if err:
                        errors.append(err)
                    elif uuid:
                        sessions[idx] = uuid
                        timestamps[uuid] = {"accepted": time.monotonic()}
                except Exception as exc:
                    errors.append(f"Accept {idx}: {exc}")

        accept_elapsed = time.monotonic() - accept_start
        print(f"    Accepted {len(sessions)}/{len(responses)} sessions "
              f"in {accept_elapsed:.1f}s")

        if not sessions:
            pytest.fail(f"No sessions were accepted. Errors: {errors}")

        # ------------------------------------------------------------------
        # Phase 3: Poll all sessions and track progress
        # ------------------------------------------------------------------
        print(f"\n[Phase 3] Tracking {len(sessions)} sessions "
              f"(poll every {POLL_INTERVAL}s, timeout {TIMEOUT}s)...")
        print()

        total_sessions = len(sessions)
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

            with ThreadPoolExecutor(max_workers=SUBMIT_WORKERS) as pool:
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
        print(f"  Submitted:         {len(responses)}")
        print(f"  Accepted:          {total_sessions}")
        print(f"  Completed:         {final_counts.get('COMPLETED', 0)}")
        print(f"  Failed:            {final_counts.get('FAILED', 0)}")
        print(f"  Cancelled:         {final_counts.get('CANCELLED', 0)}")
        print(f"  Still active:      {len(active_uuids)}")
        print(f"  Submit errors:     {len(errors)}")

        print(f"\n  Timing:")
        print(f"    Submit phase:    {submit_elapsed:6.1f}s")
        print(f"    Accept phase:    {accept_elapsed:6.1f}s")
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
            p99 = lifecycle_times[int(len(lifecycle_times)*0.99)]
            print(f"    P50:             {p50:6.1f}s")
            print(f"    P90:             {p90:6.1f}s")
            print(f"    P99:             {p99:6.1f}s")

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
            f"{len(errors)}/{total} offer submissions failed"
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
