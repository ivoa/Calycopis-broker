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
#     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
#     "timestamp": "2026-06-04T17:20:00",
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
Smoke test: submit an ExecutionRequest to each broker and verify
that distinct cost/metric values are returned on the offers.

Usage:
    source demo/run/demo-user.env
    python3 demo/bin/smoke-test.py
"""

import base64
import os
import sys

from calycopis_schema_client import ApiClient, Configuration
from calycopis_schema_client.models import (
    ComponentMetadata,
    DockerImageSpec,
    ExecutionRequest,
    OfferSetResponse,
)
from calycopis_schema_client.wrappers import (
    DockerContainer,
    ExecutionBrokerClient,
)


BROKERS = {
    "alpha": "BROKER_ALPHA_URL",
    "beta":  "BROKER_BETA_URL",
    "gamma": "BROKER_GAMMA_URL",
}


def make_client(broker_url, username, password):
    """Create an authenticated ExecutionBrokerClient."""
    cfg = Configuration(host=broker_url, username=username, password=password)
    api_client = ApiClient(cfg)
    basic_creds = base64.b64encode(
        f"{username}:{password}".encode("utf-8")
    ).decode("utf-8")
    api_client.default_headers["Authorization"] = f"Basic {basic_creds}"
    return ExecutionBrokerClient(host=broker_url, api_client=api_client)


def make_request():
    """Create a minimal ExecutionRequest for the smoke test."""
    return ExecutionRequest(
        executable=DockerContainer(
            meta=ComponentMetadata(name="smoke-test-exec"),
            image=DockerImageSpec(
                locations=["alpine:3"],
                digest="sha256:310c62b5e7ca5b08167e4384c68db0fd2905dd9c7493756d356e893909057601",
            ),
            command=["echo", "hello"],
        ),
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
    username = os.environ.get("DEMO_USER")
    password = os.environ.get("DEMO_PASS")

    if not username or not password:
        print("ERROR: DEMO_USER and DEMO_PASS environment variables must be set.")
        print("       Run: source demo/run/demo-user.env")
        sys.exit(1)

    request = make_request()
    passed = 0
    failed = 0

    for broker_name, url_var in BROKERS.items():
        broker_url = os.environ.get(url_var)
        if not broker_url:
            print(f"ERROR: {url_var} environment variable not set.")
            failed += 1
            continue

        print(f"=== Testing broker-{broker_name} ({broker_url}) ===")

        try:
            client = make_client(broker_url, username, password)
            response = client.submit_execution(request, follow_redirect=True)
        except Exception as e:
            print(f"  FAIL: Could not submit request: {e}")
            failed += 1
            continue

        if not isinstance(response, OfferSetResponse):
            print(f"  FAIL: Expected OfferSetResponse, got {type(response).__name__}")
            failed += 1
            continue

        if response.result != "YES":
            messages = ""
            if response.meta and response.meta.messages:
                messages = "; ".join(str(m) for m in response.meta.messages)
            print(f"  FAIL: result={response.result}. Messages: {messages}")
            failed += 1
            continue

        if not response.offers or len(response.offers) == 0:
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
