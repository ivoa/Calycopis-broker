#!/usr/bin/env python3
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
#     "timestamp": "2026-06-03T03:47:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-06-06T01:36:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 85,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-06-06T02:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 5,
#       "units": "%"
#       }
#     }
#   ]
#
"""
Smoke test: submit an ExecutionRequest to each broker and verify
that distinct cost/metric values are returned on the offers.

Usage:
    source run/demo-user.env
    python3 bin/smoke-test.py
"""

import sys
from pathlib import Path

BIN_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(BIN_DIR))

from broker_tools.client import BROKERS, get_env, make_client
from broker_tools.digest import resolve_digest
from broker_tools.offers import extract_summary
from broker_tools.request import build_docker_request

from calycopis_schema_client.models import OfferSetResponse


def make_request():
    """Create a minimal ExecutionRequest for the smoke test."""
    return build_docker_request(
        name="smoke-test-exec",
        image="alpine:3",
        command=["echo", "hello"],
        digest=resolve_digest("alpine:3"),
        resolve=False,
    )


def extract_costs(offer):
    """Extract cost summaries from an offer."""
    results = []
    if offer.costs:
        for cost in offer.costs:
            label = cost.type or cost.kind or "unknown"
            min_val = f"{cost.min:.4f}" if cost.min is not None else "n/a"
            max_val = f"{cost.max:.4f}" if cost.max is not None else "n/a"
            results.append(f"{label}: {min_val} - {max_val}")
    return results


def extract_metrics(offer):
    """Extract metric summaries from an offer."""
    results = []
    if offer.metrics:
        for metric in offer.metrics:
            label = metric.type or metric.kind or "unknown"
            min_val = f"{metric.min:.1f}" if metric.min is not None else "n/a"
            max_val = f"{metric.max:.1f}" if metric.max is not None else "n/a"
            results.append(f"{label}: {min_val} - {max_val}")
    return results


def main():
    get_env()
    request = make_request()
    passed = 0
    failed = 0

    for broker_name, url_var in BROKERS.items():
        import os
        broker_url = os.environ.get(url_var)
        if not broker_url:
            print(f"ERROR: {url_var} environment variable not set.")
            failed += 1
            continue

        print(f"=== Testing broker-{broker_name} ({broker_url}) ===")

        try:
            client = make_client(broker_url)
            response = client.submit_execution(request, follow_redirect=True)
        except Exception as e:
            print(f"  FAIL: Could not submit request: {e}")
            failed += 1
            continue

        if not isinstance(response, OfferSetResponse):
            print(f"  FAIL: Expected OfferSetResponse, got {type(response).__name__}")
            failed += 1
            continue

        summary = extract_summary(response)
        if summary["result"] != "YES":
            messages = "; ".join(
                str(m.get("template") or m.get("kind")) for m in summary.get("messages", [])
            )
            print(f"  FAIL: result={summary['result']}. Messages: {messages}")
            failed += 1
            continue

        if not response.offers:
            print("  FAIL: No offers returned")
            failed += 1
            continue

        offer = response.offers[0]
        print(f"  Offers returned: {len(response.offers)}")

        costs = extract_costs(offer)
        if costs:
            print("  Costs:")
            for line in costs:
                print(f"    {line}")
        else:
            print("  WARN: No costs on offer")

        metrics = extract_metrics(offer)
        if metrics:
            print("  Metrics:")
            for line in metrics:
                print(f"    {line}")
        else:
            print("  WARN: No metrics on offer")

        passed += 1
        print()

    print(f"=== Results: {passed} passed, {failed} failed ===")
    sys.exit(0 if failed == 0 else 1)


if __name__ == "__main__":
    main()
