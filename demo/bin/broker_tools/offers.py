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
#     "timestamp": "2026-06-06T01:36:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
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
"""Submit requests and format broker offer comparisons."""

from calycopis_schema_client.models import OfferSetResponse

from broker_tools.client import get_brokers, make_client

URN_LABELS = {
    "urn:ivoa:calycopis:cost:monetary": "Monetary cost",
    "urn:ivoa:calycopis:cost:energy": "Energy (kWh)",
    "urn:ivoa:calycopis:cost:carbon": "Carbon (gCO2)",
    "urn:ivoa:calycopis:metric:compute-performance": "Compute performance",
    "urn:ivoa:calycopis:metric:io-throughput": "IO throughput (MB/s)",
}


def _short_label(label: str) -> str:
    return URN_LABELS.get(label, label)


def extract_summary(response: OfferSetResponse) -> dict:
    """Extract costs, metrics, and session UUID from an offer-set response."""
    summary = {
        "result": response.result,
        "session_uuid": None,
        "costs": {},
        "metrics": {},
        "messages": [],
    }

    if response.meta and response.meta.messages:
        for message in response.meta.messages:
            summary["messages"].append(
                {
                    "kind": getattr(message, "kind", None),
                    "level": getattr(message, "level", None),
                    "template": getattr(message, "template", None),
                    "message": getattr(message, "message", None),
                }
            )

    if not response.offers:
        return summary

    offer = response.offers[0]
    if offer.meta:
        summary["session_uuid"] = str(offer.meta.uuid)

    if offer.costs:
        for cost in offer.costs:
            label = cost.type or cost.kind
            summary["costs"][label] = (cost.min, cost.max)

    if offer.metrics:
        for metric in offer.metrics:
            label = metric.type or metric.kind
            summary["metrics"][label] = (metric.min, metric.max)

    return summary


def submit_to_all(request) -> dict[str, dict]:
    """Submit a request to all brokers and return per-broker summaries."""
    results = {}
    for name, url in get_brokers().items():
        client = make_client(url)
        response = client.submit_execution(request, follow_redirect=True)
        if not isinstance(response, OfferSetResponse):
            raise RuntimeError(f"{name}: expected OfferSetResponse")
        results[name] = extract_summary(response)
    return results


def _format_range(values: tuple | None, monetary: bool = False) -> str:
    if not values:
        return "n/a"
    lo, hi = values
    if lo is None or hi is None:
        return "n/a"
    if monetary:
        return f"${lo:.2f} - ${hi:.2f}"
    if isinstance(lo, float) and lo < 10:
        return f"{lo:.2f} - {hi:.2f}"
    return f"{lo:.1f} - {hi:.1f}"


def format_comparison_table(summaries: dict[str, dict]) -> str:
    """Format broker summaries as a markdown comparison table."""
    rows: dict[str, dict[str, str]] = {}

    for broker, summary in summaries.items():
        for label, values in summary.get("costs", {}).items():
            key = _short_label(label)
            rows.setdefault(key, {})[broker] = _format_range(
                values, monetary="monetary" in label
            )
        for label, values in summary.get("metrics", {}).items():
            key = _short_label(label)
            rows.setdefault(key, {})[broker] = _format_range(values)

    lines = [
        "| Attribute | Alpha (Green HPC) | Beta (Cloud) | Gamma (Budget) |",
        "|-----------|-------------------|--------------|----------------|",
    ]

    for attribute in rows:
        alpha = rows[attribute].get("alpha", "n/a")
        beta = rows[attribute].get("beta", "n/a")
        gamma = rows[attribute].get("gamma", "n/a")
        lines.append(f"| {attribute} | {alpha} | {beta} | {gamma} |")

    return "\n".join(lines)
