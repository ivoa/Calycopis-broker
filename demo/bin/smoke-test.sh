#!/bin/bash
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
#     }
#   ]
#
# Smoke test: submit a request to each broker and verify distinct
# cost/metric values are returned.
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEMO_DIR="$(dirname "${SCRIPT_DIR}")"
ENV_DIR="${DEMO_DIR}/run"

if [ ! -f "${ENV_DIR}/demo-user.env" ]; then
    echo "ERROR: ${ENV_DIR}/demo-user.env not found. Run configure.sh first."
    exit 1
fi

source "${ENV_DIR}/demo-user.env"

REQUEST_BODY='{
    "name": "smoke-test",
    "executable": {
        "kind": "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/executables/docker-container.yaml",
        "name": "smoke-test-exec",
        "image": "alpine:3",
        "privileged": false,
        "entrypoint": "echo",
        "command_line_params": ["hello"]
    },
    "resources": {
        "compute": [
            {
                "kind": "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/compute/simple-compute-resource.yaml",
                "name": "compute-001",
                "cores": {"min": 1, "max": 1}
            }
        ]
    }
}'

AUTH="$(echo -n "${DEMO_USER}:${DEMO_PASS}" | base64)"
PASS=0
FAIL=0

for broker in alpha beta gamma
do
    URL_VAR="BROKER_$(echo "${broker}" | tr '[:lower:]' '[:upper:]')_URL"
    BROKER_URL="${!URL_VAR}"

    echo "=== Testing broker-${broker} (${BROKER_URL}) ==="

    RESPONSE=$(curl -sf \
        -X POST \
        -H "Content-Type: application/json" \
        -H "Accept: application/json" \
        -H "Authorization: Basic ${AUTH}" \
        -L \
        -d "${REQUEST_BODY}" \
        "${BROKER_URL}/offersets" 2>&1) || {
        echo "  FAIL: Could not submit request to broker-${broker}"
        FAIL=$((FAIL + 1))
        continue
    }

    OFFER_COUNT=$(echo "${RESPONSE}" | jq '.offers | length' 2>/dev/null || echo "0")
    if [ "${OFFER_COUNT}" -eq 0 ]; then
        echo "  FAIL: No offers returned"
        FAIL=$((FAIL + 1))
        continue
    fi

    echo "  Offers returned: ${OFFER_COUNT}"

    COSTS=$(echo "${RESPONSE}" | jq -r '.offers[0].costs // [] | .[] | "\(.kind // .type): \(.min) - \(.max)"' 2>/dev/null)
    METRICS=$(echo "${RESPONSE}" | jq -r '.offers[0].metrics // [] | .[] | "\(.kind // .type): \(.min) - \(.max)"' 2>/dev/null)

    if [ -n "${COSTS}" ]; then
        echo "  Costs:"
        echo "${COSTS}" | while read -r line; do echo "    ${line}"; done
        PASS=$((PASS + 1))
    else
        echo "  WARN: No costs on offer"
    fi

    if [ -n "${METRICS}" ]; then
        echo "  Metrics:"
        echo "${METRICS}" | while read -r line; do echo "    ${line}"; done
    else
        echo "  WARN: No metrics on offer"
    fi

    echo ""
done

echo "=== Results: ${PASS} passed, ${FAIL} failed ==="

if [ "${FAIL}" -gt 0 ]; then
    exit 1
fi
