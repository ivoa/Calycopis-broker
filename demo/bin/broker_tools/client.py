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
"""Broker client factory and environment helpers."""

import base64
import os

from calycopis_schema_client import ApiClient, Configuration
from calycopis_schema_client.wrappers import ExecutionBrokerClient

BROKERS = {
    "alpha": "BROKER_ALPHA_URL",
    "beta": "BROKER_BETA_URL",
    "gamma": "BROKER_GAMMA_URL",
}

BROKER_LABELS = {
    "alpha": "Alpha (Green HPC)",
    "beta": "Beta (Cloud)",
    "gamma": "Gamma (Budget)",
}


def get_env():
    """Return demo user credentials from the environment."""
    username = os.environ.get("DEMO_USER")
    password = os.environ.get("DEMO_PASS")
    if not username or not password:
        raise RuntimeError(
            "DEMO_USER and DEMO_PASS must be set. Run: source run/demo-user.env"
        )
    return username, password


def make_client(broker_url: str) -> ExecutionBrokerClient:
    """Create an authenticated ExecutionBrokerClient."""
    username, password = get_env()
    cfg = Configuration(host=broker_url, username=username, password=password)
    api_client = ApiClient(cfg)
    creds = base64.b64encode(f"{username}:{password}".encode()).decode()
    api_client.default_headers["Authorization"] = f"Basic {creds}"
    return ExecutionBrokerClient(host=broker_url, api_client=api_client)


def get_brokers() -> dict[str, str]:
    """Return broker name to URL mapping from environment variables."""
    brokers = {}
    for name, var in BROKERS.items():
        url = os.environ.get(var)
        if not url:
            raise RuntimeError(f"{var} environment variable not set.")
        brokers[name] = url
    return brokers
