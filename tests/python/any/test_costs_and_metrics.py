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
#     "timestamp": "2026-06-03T01:33:00",
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
Integration tests for costs and metrics on the Execution Broker.

These tests verify that:
  - Offers include cost and metric data on session and component entities.
  - Cost and metric items have the expected structure (kind, type, min, max).
  - Request-level cost constraints are accepted without error.

Works on either the mock or docker platform.

Requires:
  - A running Calycopis broker service (default: http://localhost:8082)
  - The calycopis_schema_client Python package installed

Usage:
  pytest tests/python/any/test_costs_and_metrics.py -v
"""

import pytest

from calycopis_schema_client.models import (
    ExecutionRequest,
    OfferSetResponse,
    SimpleMinMaxFloatCost,
    SimpleMinMaxFloatMetric,
)
from calycopis_schema_client.models.docker_image_spec import DockerImageSpec
from calycopis_schema_client.models.component_metadata import ComponentMetadata
from calycopis_schema_client.wrappers import DockerContainer


COST_KIND_URI = (
    "https://www.purl.org/ivoa.net/Calycopis-openapi"
    "/schema/v1.0/kinds/costs/simple-minmax-float-cost.yaml"
)
METRIC_KIND_URI = (
    "https://www.purl.org/ivoa.net/Calycopis-openapi"
    "/schema/v1.0/kinds/metrics/simple-minmax-float-metric.yaml"
)


def _make_executable(name="test-container"):
    """Helper: create a minimal DockerContainer executable."""
    return DockerContainer(
        meta=ComponentMetadata(name=name),
        image=DockerImageSpec(
            locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
            digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
        ),
    )


def _submit(client, request):
    """Submit a request and return the OfferSetResponse."""
    response = client.submit_execution(request, follow_redirect=True)
    assert isinstance(response, OfferSetResponse), (
        f"Expected OfferSetResponse, got {type(response)}"
    )
    return response


def _assert_accepted(response, msg=""):
    """Assert that the response result is YES with at least one offer."""
    assert response.result == "YES", (
        f"Expected YES, got {response.result}. {msg} "
        f"Messages: {response.meta.messages if response.meta else 'none'}"
    )
    assert response.offers is not None and len(response.offers) > 0, (
        f"Expected at least one offer. {msg}"
    )


class TestCostsOnOffers:
    """Verify that offers include cost data."""

    def test_session_has_costs(self, client):
        """
        Submit a basic request and verify that the returned session
        offers include a non-empty costs list.
        """
        request = ExecutionRequest(
            executable=_make_executable("cost-test-session"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.costs is not None, "Session offer should have a costs list"
        assert len(offer.costs) > 0, "Session offer costs list should not be empty"

    def test_session_cost_structure(self, client):
        """
        Verify that each cost item on the session has the expected
        SimpleMinMaxFloatCost structure with kind, type, min, and max.
        """
        request = ExecutionRequest(
            executable=_make_executable("cost-structure-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.costs is not None and len(offer.costs) > 0

        for cost in offer.costs:
            assert cost.kind is not None, "Cost kind must not be null"
            assert cost.type is not None, "Cost type must not be null"
            assert cost.min is not None or cost.max is not None, (
                "Cost should have at least a min or max value"
            )

    def test_session_has_monetary_cost(self, client):
        """
        Verify that the session includes a monetary cost item.
        """
        request = ExecutionRequest(
            executable=_make_executable("monetary-cost-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.costs is not None

        monetary_costs = [
            c for c in offer.costs
            if c.type is not None and "monetary" in str(c.type)
        ]
        assert len(monetary_costs) > 0, (
            "Expected at least one monetary cost item on the session"
        )


class TestMetricsOnOffers:
    """Verify that offers include metric data."""

    def test_session_has_metrics(self, client):
        """
        Submit a basic request and verify that the returned session
        offers include a non-empty metrics list.
        """
        request = ExecutionRequest(
            executable=_make_executable("metric-test-session"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.metrics is not None, "Session offer should have a metrics list"
        assert len(offer.metrics) > 0, "Session offer metrics list should not be empty"

    def test_session_metric_structure(self, client):
        """
        Verify that each metric item on the session has the expected
        structure with kind, type, min, and max.
        """
        request = ExecutionRequest(
            executable=_make_executable("metric-structure-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.metrics is not None and len(offer.metrics) > 0

        for metric in offer.metrics:
            assert metric.kind is not None, "Metric kind must not be null"
            assert metric.type is not None, "Metric type must not be null"
            assert metric.min is not None or metric.max is not None, (
                "Metric should have at least a min or max value"
            )

    def test_session_has_compute_performance_metric(self, client):
        """
        Verify that the session includes a compute performance metric.
        """
        request = ExecutionRequest(
            executable=_make_executable("perf-metric-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.metrics is not None

        perf_metrics = [
            m for m in offer.metrics
            if m.type is not None and "compute-performance" in str(m.type)
        ]
        assert len(perf_metrics) > 0, (
            "Expected at least one compute-performance metric on the session"
        )


class TestComputeResourceCostsAndMetrics:
    """Verify that compute resource sub-components carry costs and metrics."""

    def test_compute_resource_has_costs(self, client):
        """
        Verify that the compute resource within an offer has costs.
        """
        request = ExecutionRequest(
            executable=_make_executable("compute-cost-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.compute is not None, "Offer should have a compute resource"
        assert offer.compute.costs is not None, (
            "Compute resource should have a costs list"
        )
        assert len(offer.compute.costs) > 0, (
            "Compute resource costs list should not be empty"
        )

    def test_compute_resource_has_metrics(self, client):
        """
        Verify that the compute resource within an offer has metrics.
        """
        request = ExecutionRequest(
            executable=_make_executable("compute-metric-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.compute is not None, "Offer should have a compute resource"
        assert offer.compute.metrics is not None, (
            "Compute resource should have a metrics list"
        )
        assert len(offer.compute.metrics) > 0, (
            "Compute resource metrics list should not be empty"
        )


class TestCostMinMaxValues:
    """Verify that cost min/max values are sensible."""

    def test_cost_min_less_than_max(self, client):
        """
        For costs that have both min and max, verify min <= max.
        """
        request = ExecutionRequest(
            executable=_make_executable("cost-range-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.costs is not None

        for cost in offer.costs:
            if cost.min is not None and cost.max is not None:
                assert cost.min <= cost.max, (
                    f"Cost min ({cost.min}) should be <= max ({cost.max}) "
                    f"for type {cost.type}"
                )

    def test_metric_min_less_than_max(self, client):
        """
        For metrics that have both min and max, verify min <= max.
        """
        request = ExecutionRequest(
            executable=_make_executable("metric-range-test"),
        )
        response = _submit(client, request)
        _assert_accepted(response)

        offer = response.offers[0]
        assert offer.metrics is not None

        for metric in offer.metrics:
            if metric.min is not None and metric.max is not None:
                assert metric.min <= metric.max, (
                    f"Metric min ({metric.min}) should be <= max ({metric.max}) "
                    f"for type {metric.type}"
                )


class TestRequestWithCostConstraints:
    """Verify that requests with cost constraints are accepted."""

    def test_request_with_cost_constraint(self, client):
        """
        Submit a request that includes a cost constraint.
        The broker should accept the request without error.
        """
        request = ExecutionRequest(
            executable=_make_executable("cost-constraint-test"),
            costs=[
                SimpleMinMaxFloatCost(
                    kind=COST_KIND_URI,
                    type="urn:ivoa:calycopis:cost:monetary",
                    description="Maximum acceptable monetary cost",
                    max=1.0,
                ),
            ],
        )
        response = _submit(client, request)
        _assert_accepted(response)

    def test_request_with_metric_requirement(self, client):
        """
        Submit a request that includes a metric requirement.
        The broker should accept the request without error.
        """
        request = ExecutionRequest(
            executable=_make_executable("metric-requirement-test"),
            metrics=[
                SimpleMinMaxFloatMetric(
                    kind=METRIC_KIND_URI,
                    type="urn:ivoa:calycopis:metric:compute-performance",
                    description="Minimum required compute performance",
                    min=50.0,
                ),
            ],
        )
        response = _submit(client, request)
        _assert_accepted(response)
