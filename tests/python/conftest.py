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
#     "timestamp": "2026-05-30T11:37:00",
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
Shared pytest fixtures for Calycopis integration tests.

Provides session-scoped fixtures that:
- Seed test identities into the broker via the admin endpoint
- Create authenticated ExecutionBrokerClient instances for each test user
"""

import base64
import json
import os
import secrets
import urllib.error
import urllib.request
import uuid

import pytest

from calycopis_openapi_client import ApiClient, Configuration
from calycopis_openapi_client.wrappers.execution_client import ExecutionBrokerClient


# ---------------------------------------------------------------------------
# Configuration from environment
# ---------------------------------------------------------------------------

CALYCOPIS_URL = os.environ.get("CALYCOPIS_URL", "http://localhost:8082")
ADMIN_USERNAME = os.environ.get("CALYCOPIS_ADMIN_USERNAME", "admin")
ADMIN_PASSWORD = os.environ.get("CALYCOPIS_ADMIN_PASSWORD", "admin-secret")


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _random_username(prefix):
    """Generate a random username with a recognisable prefix."""
    return f"{prefix}-{uuid.uuid4().hex[:8]}"


def _random_password():
    """Generate a random password."""
    return secrets.token_urlsafe(16)


def _seed_user(username, password):
    """Create a user identity via the admin endpoint.

    Returns the UUID of the created identity.
    Handles 409 Conflict gracefully (user already exists).
    """
    url = f"{CALYCOPIS_URL}/admin/identities"
    data = json.dumps({"username": username, "password": password}).encode("utf-8")
    creds = base64.b64encode(
        f"{ADMIN_USERNAME}:{ADMIN_PASSWORD}".encode("utf-8")
    ).decode("utf-8")

    req = urllib.request.Request(url, data=data, method="POST")
    req.add_header("Content-Type", "application/json")
    req.add_header("Accept", "application/json")
    req.add_header("Authorization", f"Basic {creds}")

    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            body = json.loads(resp.read().decode("utf-8"))
            return body.get("uuid")
    except urllib.error.HTTPError as e:
        if e.code == 409:
            body = json.loads(e.read().decode("utf-8"))
            return body.get("uuid")
        raise RuntimeError(
            f"Failed to seed user '{username}': HTTP {e.code} - {e.read().decode('utf-8', errors='replace')}"
        ) from e


def _make_authenticated_client(host, username, password):
    """Create an ExecutionBrokerClient with HTTP Basic auth credentials."""
    cfg = Configuration(host=host, username=username, password=password)
    api_client = ApiClient(cfg)
    basic_creds = base64.b64encode(
        f"{username}:{password}".encode("utf-8")
    ).decode("utf-8")
    api_client.default_headers["Authorization"] = f"Basic {basic_creds}"
    return ExecutionBrokerClient(host=host, api_client=api_client)


def _server_reachable():
    """Return True if the Calycopis broker is responding."""
    try:
        urllib.request.urlopen(CALYCOPIS_URL, timeout=5)
        return True
    except urllib.error.HTTPError:
        return True
    except Exception:
        return False


# ---------------------------------------------------------------------------
# Session-scoped fixtures
# ---------------------------------------------------------------------------

@pytest.fixture(scope="session")
def alice_creds():
    """Random credentials for the 'alice' test user."""
    return {"username": _random_username("alice"), "password": _random_password()}


@pytest.fixture(scope="session")
def bob_creds():
    """Random credentials for the 'bob' test user."""
    return {"username": _random_username("bob"), "password": _random_password()}


@pytest.fixture(scope="session", autouse=True)
def seed_identities(alice_creds, bob_creds):
    """Seed test identities into the broker before any tests run."""
    if not _server_reachable():
        pytest.skip(f"Calycopis broker not reachable at {CALYCOPIS_URL}")
    _seed_user(alice_creds["username"], alice_creds["password"])
    _seed_user(bob_creds["username"], bob_creds["password"])


@pytest.fixture(scope="session")
def alice_client(seed_identities, alice_creds):
    """Authenticated ExecutionBrokerClient for the 'alice' test user."""
    return _make_authenticated_client(
        CALYCOPIS_URL,
        alice_creds["username"],
        alice_creds["password"],
    )


@pytest.fixture(scope="session")
def bob_client(seed_identities, bob_creds):
    """Authenticated ExecutionBrokerClient for the 'bob' test user."""
    return _make_authenticated_client(
        CALYCOPIS_URL,
        bob_creds["username"],
        bob_creds["password"],
    )


@pytest.fixture(scope="session")
def anon_client(seed_identities):
    """Unauthenticated ExecutionBrokerClient for testing public access."""
    return ExecutionBrokerClient(host=CALYCOPIS_URL)


@pytest.fixture(scope="session")
def client(alice_client):
    """Default authenticated client (aliases alice_client).

    Provides backward compatibility with existing tests that use
    a 'client' fixture parameter.
    """
    return alice_client
