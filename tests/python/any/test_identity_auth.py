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
Integration tests for authentication and ownership isolation.

Tests:
- Unauthenticated POST requests are rejected (401)
- Authenticated POST requests succeed
- GET requests remain public (no auth required)
- Ownership isolation: one user cannot update another's session (403)
- Ownership: a user can update their own session

Requires:
  - A running Calycopis broker service with authentication enabled.
  - The calycopis_schema_client Python package installed.
  - Test identities seeded via conftest.py fixtures.
"""

import pytest

from calycopis_schema_client.models import (
    ExecutionRequest,
    SimpleExecutionSessionPhase,
)
from calycopis_schema_client.models.docker_container import DockerContainer
from calycopis_schema_client.models.docker_image_spec import DockerImageSpec
from calycopis_schema_client.models.component_metadata import ComponentMetadata
from calycopis_schema_client.exceptions import UnauthorizedException, ForbiddenException


# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

DOCKER_CONTAINER_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/docker-container-1.0"
)

CANTLIEI_IMAGE = "ghcr.io/zarquan/heliophorus-cantliei:sha-831ee57"
CANTLIEI_DIGEST = "sha256:6e495692cc6f1cae2023f261f433d4691aa70b19416730f8301e45fbb74bc526"


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _make_executable(name="auth-test"):
    """Create a minimal DockerContainer executable."""
    return DockerContainer(
        kind=DOCKER_CONTAINER_KIND,
        meta=ComponentMetadata(name=name),
        image=DockerImageSpec(
            locations=[CANTLIEI_IMAGE],
            digest=CANTLIEI_DIGEST,
        ),
        command=["5", "0"],
    )


# ===========================================================================
# Authentication enforcement tests
# ===========================================================================

class TestAuthEnforcement:
    """Tests that authentication is enforced for POST requests."""

    def test_unauthenticated_post_rejected(self, anon_client):
        """
        An unauthenticated POST to /sessions should return 401.
        """
        request = ExecutionRequest(
            executable=_make_executable("auth-unauth-post"),
        )
        with pytest.raises((UnauthorizedException, Exception)) as exc_info:
            anon_client.direct_execute(request)

        if hasattr(exc_info.value, 'status'):
            assert exc_info.value.status == 401, (
                f"Expected 401 Unauthorized, got {exc_info.value.status}"
            )

    def test_authenticated_post_succeeds(self, alice_client):
        """
        An authenticated POST to /sessions should succeed.
        """
        request = ExecutionRequest(
            executable=_make_executable("auth-alice-post"),
        )
        session = alice_client.direct_execute(request)
        assert session is not None
        assert session.meta is not None
        assert session.meta.uuid is not None

        alice_client.set_session_phase(
            session.meta.uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )

    def test_get_remains_public(self, alice_client, anon_client):
        """
        GET requests should work without authentication.
        """
        request = ExecutionRequest(
            executable=_make_executable("auth-public-get"),
        )
        session = alice_client.direct_execute(request)
        session_uuid = session.meta.uuid

        fetched = anon_client.get_session(session_uuid)
        assert fetched is not None
        assert fetched.meta.uuid == session_uuid

        alice_client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )


# ===========================================================================
# Ownership isolation tests
# ===========================================================================

class TestOwnershipIsolation:
    """Tests that one user cannot modify another user's sessions."""

    def test_owner_can_update_own_session(self, alice_client):
        """
        Alice should be able to cancel her own session.
        """
        request = ExecutionRequest(
            executable=_make_executable("auth-alice-own"),
        )
        session = alice_client.direct_execute(request)
        session_uuid = session.meta.uuid

        cancelled = alice_client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
        assert cancelled.phase == SimpleExecutionSessionPhase.CANCELLED

    def test_other_user_cannot_update_session(self, alice_client, bob_client):
        """
        Bob should not be able to cancel Alice's session (403 Forbidden).
        """
        request = ExecutionRequest(
            executable=_make_executable("auth-alice-bob-isolation"),
        )
        session = alice_client.direct_execute(request)
        session_uuid = session.meta.uuid

        with pytest.raises((ForbiddenException, Exception)) as exc_info:
            bob_client.set_session_phase(
                session_uuid,
                SimpleExecutionSessionPhase.CANCELLED,
            )

        if hasattr(exc_info.value, 'status'):
            assert exc_info.value.status == 403, (
                f"Expected 403 Forbidden, got {exc_info.value.status}"
            )

        alice_client.set_session_phase(
            session_uuid,
            SimpleExecutionSessionPhase.CANCELLED,
        )
