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
# Tear down the demo deployment: stop and remove all pods and the network.
#

set -euo pipefail

NETWORK_NAME="calycopis-demo"

PODS=("demo-client" "broker-alpha" "broker-beta" "broker-gamma")

echo "=== Stopping and removing pods ==="
for pod in "${PODS[@]}"
do
    if podman pod exists "${pod}" 2>/dev/null; then
        echo "  Removing pod: ${pod}"
        podman pod stop "${pod}" 2>/dev/null || true
        podman pod rm -f "${pod}" 2>/dev/null || true
    else
        echo "  Pod ${pod} does not exist, skipping"
    fi
done

echo ""
echo "=== Removing network ==="
if podman network exists "${NETWORK_NAME}" 2>/dev/null; then
    podman network rm "${NETWORK_NAME}" 2>/dev/null || true
    echo "  Removed network: ${NETWORK_NAME}"
else
    echo "  Network ${NETWORK_NAME} does not exist, skipping"
fi

echo ""
echo "=== Teardown complete ==="
