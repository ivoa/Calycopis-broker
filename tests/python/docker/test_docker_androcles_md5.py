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
#     "timestamp": "2026-04-13T12:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-06-02T13:37:00",
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
Integration test for the Heliophorus-androcles checksum container.

Submits an execution request that runs the androcles container
against a local file:// data resource mounted at /input, then
captures the container's stdout via docker-py and verifies that
the reported MD5 matches the locally computed value.

Requires:
  - A running Calycopis broker service with the 'docker' profile active.
  - The calycopis_schema_client Python package installed.
  - The docker Python package installed (docker-py).
  - A local test file at BIND_MOUNT_TEST_FILE (default:
    /home/Zarquan/temp/random.txt) accessible to the broker.

Usage:
  pytest tests/python/test_docker_androcles_md5.py -v
  BIND_MOUNT_TEST_FILE=/path/to/file.txt pytest tests/python/test_docker_androcles_md5.py -v
"""

import json
import os
from datetime import datetime, timezone
from time import sleep as _sleep

import docker
import pytest

from calycopis_openapi_client.models import (
    ExecutionRequest,
    SimpleExecutionSessionPhase,
)
from calycopis_openapi_client.models.docker_image_spec import DockerImageSpec
from calycopis_openapi_client.models.component_metadata import ComponentMetadata
from calycopis_openapi_client.wrappers import (
    DockerContainer,
    SimpleComputeResource,
    SimpleDataResource,
    SimpleVolumeMount,
)


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

BIND_MOUNT_TEST_FILE = os.environ.get(
    "TESTFILE"
)

PHASE_TIMEOUT = float(os.environ.get("PHASE_TIMEOUT", "180"))

DOCKER_SOCKET = os.environ.get(
    "DOCKER_SOCKET",
    "unix:///run/podman/podman.sock",
)

ANDROCLES_IMAGE = "ghcr.io/zarquan/heliophorus-androcles:sha-9a2513b"
ANDROCLES_DIGEST = (
    "sha256:0dfeaad1f37ab8cd506f3a16d1ade56d035694a34e0ff28be51b97f1924c4df3"
)


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------

@pytest.fixture(scope="module")
def docker_client() -> docker.DockerClient:
    return docker.DockerClient(base_url=DOCKER_SOCKET)


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _compute_expected_md5(docker_client: docker.DockerClient, filepath: str) -> str:
    """Compute the MD5 of a file as seen through a Podman/Docker bind mount.

    Rootless Podman may see different file content from the host when
    running under a different user namespace, so we compute the
    reference hash through the same bind mount mechanism that the
    broker will use.
    """
    container = docker_client.containers.run(
        "alpine:3.23",
        command=["md5sum", "/input"],
        volumes={filepath: {"bind": "/input", "mode": "ro"}},
        remove=True,
        stdout=True,
        stderr=False,
    )
    output = container.decode("utf-8", errors="replace").strip()
    return output.split()[0]


def _find_container_by_image(
    docker_client: docker.DockerClient,
    image_substr: str,
    created_after: datetime,
):
    """Find the most recently created container whose image name
    contains *image_substr* and that was created after *created_after*.
    Searches running and exited containers.
    """
    for container in docker_client.containers.list(all=True):
        tags = container.image.tags if container.image.tags else []
        if not any(image_substr in t for t in tags):
            continue
        created_str = container.attrs.get("Created", "")
        try:
            created_dt = datetime.fromisoformat(
                created_str.replace("Z", "+00:00")
            )
        except (ValueError, TypeError):
            continue
        if created_dt >= created_after:
            return container
    return None


def _make_androcles_request(
    name: str = "androcles-md5",
    file_url: str = None,
) -> ExecutionRequest:
    """Build an ExecutionRequest that runs the androcles container
    with a file:// data resource bind-mounted at /input.

    The default CMD ["md5sum", "json"] tells androcles to compute
    the MD5 of /input and emit the result as JSON on stdout.
    """
    if file_url is None:
        file_url = f"file://{BIND_MOUNT_TEST_FILE}"

    return ExecutionRequest(
        executable=DockerContainer(
            meta=ComponentMetadata(name=f"{name}-exec"),
            image=DockerImageSpec(
                locations=[ANDROCLES_IMAGE],
                digest=ANDROCLES_DIGEST,
            ),
            command=["md5sum", "json"],
        ),
        data=[
            SimpleDataResource(
                meta=ComponentMetadata(name=f"{name}-data"),
                location=file_url,
            ),
        ],
        compute=SimpleComputeResource(
            meta=ComponentMetadata(name=f"{name}-compute"),
            volumes=[
                SimpleVolumeMount(
                    resource=f"{name}-data",
                    path="/input",
                    mode="READONLY",
                ),
            ],
        ),
    )


# ===========================================================================
# Tests
# ===========================================================================

class TestAndroclesMd5:
    """
    Run the Heliophorus-androcles container to compute the MD5 of a
    local file, then verify the result by capturing the container logs.
    """

    def test_session_completes(self, client):
        """The androcles session should reach COMPLETED."""
        request = _make_androcles_request("androcles-lifecycle")
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES", f"Expected YES, got {response.result}"
        assert len(response.offers) > 0

        offer = response.offers[0]
        offer_uuid = offer.meta.uuid

        client.set_session_phase(
            offer_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        result = client.wait_for_phase(
            offer_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=PHASE_TIMEOUT,
            interval=5.0,
        )
        assert result.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Session should reach COMPLETED, got {result.phase}"
        )

    def test_md5_matches_local(self, client, docker_client):
        """
        Compute the expected MD5 locally, run androcles via the broker,
        capture the container stdout, and verify the hash matches.

        The test polls for the container via docker-py so that it can
        read the logs before the broker's release action removes it.
        """
        expected_md5 = _compute_expected_md5(docker_client, BIND_MOUNT_TEST_FILE)

        test_start = datetime.now(timezone.utc)

        request = _make_androcles_request("androcles-md5")
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES", f"Expected YES, got {response.result}"
        assert len(response.offers) > 0

        offer = response.offers[0]
        offer_uuid = offer.meta.uuid

        client.set_session_phase(
            offer_uuid,
            SimpleExecutionSessionPhase.ACCEPTED,
        )

        # Poll for the androcles container and capture its stdout.
        # The container exits quickly after computing the hash, but
        # remains in the exited state until the broker's release
        # action removes it — giving us a window to read the logs.
        container_stdout = None
        deadline = datetime.now(timezone.utc).timestamp() + PHASE_TIMEOUT

        while datetime.now(timezone.utc).timestamp() < deadline:
            container = _find_container_by_image(
                docker_client,
                "heliophorus-androcles",
                test_start,
            )
            if container is not None:
                container.reload()
                status = container.status
                if status in ("exited", "dead", "stopped"):
                    container_stdout = container.logs(
                        stdout=True, stderr=False,
                    ).decode("utf-8", errors="replace")
                    break
                # Container exists but still running — grab logs anyway
                # in case we don't get another chance.
                container_stdout = container.logs(
                    stdout=True, stderr=False,
                ).decode("utf-8", errors="replace")
                if container_stdout.strip():
                    break
            _sleep(3.0)

        # Also wait for the session to complete.
        result = client.wait_for_phase(
            offer_uuid,
            target_phases=[
                SimpleExecutionSessionPhase.COMPLETED,
                SimpleExecutionSessionPhase.FAILED,
            ],
            timeout=PHASE_TIMEOUT,
            interval=5.0,
        )
        assert result.phase == SimpleExecutionSessionPhase.COMPLETED, (
            f"Session should reach COMPLETED, got {result.phase}"
        )

        # Verify we captured the container's stdout.
        assert container_stdout is not None and container_stdout.strip(), (
            "Failed to capture container stdout via docker-py. "
            "The container may have been removed before logs could be read."
        )

        # Parse the jc --hashsum JSON output.
        # Expected format: [{"hash": "<md5hex>", "filename": "/input"}]
        parsed = json.loads(container_stdout.strip())
        assert isinstance(parsed, list), (
            f"Expected JSON array, got {type(parsed).__name__}: "
            f"{container_stdout[:200]}"
        )
        assert len(parsed) > 0, (
            f"Expected at least one hash entry, got empty array"
        )

        actual_md5 = parsed[0].get("hash")
        assert actual_md5 is not None, (
            f"No 'hash' field in output: {parsed[0]}"
        )
        assert actual_md5 == expected_md5, (
            f"MD5 mismatch: container reported {actual_md5}, "
            f"local file has {expected_md5}"
        )

        actual_filename = parsed[0].get("filename")
        assert actual_filename == "/input", (
            f"Expected filename '/input', got '{actual_filename}'"
        )
