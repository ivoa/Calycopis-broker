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
#     "timestamp": "2026-02-17T06:00:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-02-17T07:30:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 1,
#       "units": "%"
#       }
#     }
#   ]
#

"""
Integration tests for all mock validation rules.

Each mock validator implements platform-specific validation rules
(excluded, resource limits, etc.) that can be tested via the
OfferSetRequest API. This module performs positive and negative
tests for every mock validation rule.

Mock validators tested:
  - MockDockerContainerValidatorImpl   (excluded port path/number)
  - MockJupyterNotebookValidatorImpl   (excluded location)
  - MockSimpleComputeResourceValidatorImpl (cores/memory limits)
  - MockSimpleStorageResourceValidatorImpl (size limit)
  - MockSimpleDataResourceValidatorImpl    (excluded location)
  - MockAmazonS3DataResourceValidatorImpl  (excluded endpoint)
  - MockIvoaDataResourceValidatorImpl      (excluded ivoid)
  - MockSkaoDataResourceValidatorImpl      (excluded namespace)

Requires:
  - A running Calycopis broker service (default: http://localhost:8082)
  - The calycopis_schema_client Python package installed

Usage:
  pytest tests/test_mock_validators.py -v
  CALYCOPIS_URL=http://host:port pytest tests/test_mock_validators.py -v
"""

import os

import pytest

from calycopis_schema_client.wrappers.execution_client import ExecutionBrokerClient
from calycopis_schema_client.models import (
    ExecutionRequest,
    OfferSetResponse,
)
from calycopis_schema_client.models.docker_container import DockerContainer
from calycopis_schema_client.models.docker_image_spec import DockerImageSpec
from calycopis_schema_client.models.docker_internal_port import DockerInternalPort
from calycopis_schema_client.models.docker_network_port import DockerNetworkPort
from calycopis_schema_client.models.docker_network_spec import DockerNetworkSpec
from calycopis_schema_client.models.jupyter_notebook import JupyterNotebook
from calycopis_schema_client.models.simple_compute_resource import SimpleComputeResource
from calycopis_schema_client.models.simple_compute_cores import SimpleComputeCores
from calycopis_schema_client.models.simple_compute_memory import SimpleComputeMemory
from calycopis_schema_client.models.simple_storage_resource import SimpleStorageResource
from calycopis_schema_client.models.simple_storage_size import SimpleStorageSize
from calycopis_schema_client.models.simple_data_resource import SimpleDataResource
from calycopis_schema_client.models.s3_data_resource import S3DataResource
from calycopis_schema_client.models.ivoa_data_resource import IvoaDataResource
from calycopis_schema_client.models.ivoa_data_resource_block import IvoaDataResourceBlock
from calycopis_schema_client.models.skao_data_resource import SkaoDataResource
from calycopis_schema_client.models.skao_data_resource_block import SkaoDataResourceBlock
from calycopis_schema_client.models.skao_replica_item import SkaoReplicaItem
from calycopis_schema_client.models.component_metadata import ComponentMetadata


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

CALYCOPIS_URL = os.environ.get("CALYCOPIS_URL", "http://localhost:8082")

# Kind discriminator URIs (must match the Java TYPE_DISCRIMINATOR constants)
DOCKER_CONTAINER_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/docker-container-1.0"
)
JUPYTER_NOTEBOOK_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/executable/jupyter-notebook-1.0"
)
SIMPLE_COMPUTE_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/compute/simple-compute-resource-1.0"
)
SIMPLE_STORAGE_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/storage/simple-storage-resource-1.0"
)
SIMPLE_DATA_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/data/simple-data-resource-1.0"
)
S3_DATA_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/data/S3-data-resource-1.0"
)
IVOA_DATA_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/data/ivoa-data-resource-1.0"
)
SKAO_DATA_KIND = (
    "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/data/skao-data-resource-1.0"
)


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------

def _server_reachable() -> bool:
    """Return True if the Calycopis broker is responding."""
    import urllib.request
    import urllib.error
    try:
        urllib.request.urlopen(CALYCOPIS_URL, timeout=5)
        return True
    except urllib.error.HTTPError:
        return True
    except Exception:
        return False


pytestmark = pytest.mark.skipif(
    not _server_reachable(),
    reason=f"Calycopis broker not reachable at {CALYCOPIS_URL}",
)


@pytest.fixture(scope="module")
def client() -> ExecutionBrokerClient:
    """Create a shared ExecutionBrokerClient for the test module."""
    return ExecutionBrokerClient(host=CALYCOPIS_URL)


# ---------------------------------------------------------------------------
# Helper functions
# ---------------------------------------------------------------------------

def _submit(client: ExecutionBrokerClient, request: ExecutionRequest) -> OfferSetResponse:
    """Submit a request and return the OfferSetResponse."""
    response = client.submit_execution(request, follow_redirect=True)
    assert isinstance(response, OfferSetResponse), (
        f"Expected OfferSetResponse, got {type(response)}"
    )
    return response



def _make_docker_executable(name: str = "test-container") -> DockerContainer:
    """Helper: create a minimal valid DockerContainer executable."""
    return DockerContainer(
        kind=DOCKER_CONTAINER_KIND,
        meta=ComponentMetadata(name=name),
        image=DockerImageSpec(
            locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
            digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
        ),
    )


def _make_jupyter_executable(
    name: str = "test-notebook",
    location: str = "http://example.com/valid-notebook.ipynb",
) -> JupyterNotebook:
    """Helper: create a minimal valid JupyterNotebook executable."""
    return JupyterNotebook(
        kind=JUPYTER_NOTEBOOK_KIND,
        meta=ComponentMetadata(name=name),
        location=location,
    )


def _assert_accepted(response: OfferSetResponse, msg: str = ""):
    """Assert that the response result is YES with at least one offer."""
    assert response.result == "YES", (
        f"Expected YES, got {response.result}. {msg} "
        f"Messages: {response.meta.messages if response.meta else 'none'}"
    )
    assert response.offers is not None and len(response.offers) > 0, (
        f"Expected at least one offer. {msg}"
    )


def _assert_rejected(response: OfferSetResponse, msg: str = ""):
    """Assert that the response result is NO (validation failed)."""
    assert response.result == "NO", (
        f"Expected NO, got {response.result}. {msg} "
        f"Messages: {response.meta.messages if response.meta else 'none'}"
    )


# ===========================================================================
# DockerContainer mock validation rules
# ===========================================================================

class TestDockerContainerValidation:
    """
    Tests for MockDockerContainerValidatorImpl.

    Mock rules:
      - PORT_PATH_EXCLUDED: ["/badpath", "/alsobadpath"]
      - PORT_NUMBER_EXCLUDED: [1234, 5678]
    Also tests built-in validation:
      - privileged execution not supported
      - protocol must be UDP/TCP/HTTP/HTTPS
      - port number must be > 0
    """

    def test_valid_docker_with_network(self, client):
        """
        A DockerContainer with valid network port configuration
        should be accepted.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-valid-network"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                network=DockerNetworkSpec(
                    ports=[
                        DockerNetworkPort(
                            access=True,
                            protocol="HTTP",
                            path="/myapp",
                            internal=DockerInternalPort(port=8080),
                        ),
                    ]
                ),
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Valid Docker with network should be accepted")

    def test_excluded_port_path(self, client):
        """
        A DockerContainer with an excluded network port path
        ("/badpath") should be rejected.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-bad-path"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                network=DockerNetworkSpec(
                    ports=[
                        DockerNetworkPort(
                            access=True,
                            protocol="HTTP",
                            path="/badpath",
                            internal=DockerInternalPort(port=8080),
                        ),
                    ]
                ),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded port path '/badpath' should be rejected")

    def test_excluded_port_path_also(self, client):
        """
        A DockerContainer with the other excluded port path
        ("/alsobadpath") should also be rejected.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-also-bad-path"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                network=DockerNetworkSpec(
                    ports=[
                        DockerNetworkPort(
                            access=True,
                            protocol="HTTP",
                            path="/alsobadpath",
                            internal=DockerInternalPort(port=8080),
                        ),
                    ]
                ),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded port path '/alsobadpath' should be rejected")

    def test_excluded_port_number(self, client):
        """
        A DockerContainer with a excluded port number (1234)
        should be rejected.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-bad-port-1234"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                network=DockerNetworkSpec(
                    ports=[
                        DockerNetworkPort(
                            access=True,
                            protocol="HTTP",
                            path="/myapp",
                            internal=DockerInternalPort(port=1234),
                        ),
                    ]
                ),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded port number 1234 should be rejected")

    def test_excluded_port_number_5678(self, client):
        """
        A DockerContainer with the other excluded port number (5678)
        should also be rejected.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-bad-port-5678"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                network=DockerNetworkSpec(
                    ports=[
                        DockerNetworkPort(
                            access=True,
                            protocol="HTTP",
                            path="/myapp",
                            internal=DockerInternalPort(port=5678),
                        ),
                    ]
                ),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded port number 5678 should be rejected")

    def test_privileged_execution_rejected(self, client):
        """
        A DockerContainer requesting privileged execution
        should be rejected.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-privileged"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                privileged=True,
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Privileged execution should be rejected")

    def test_unsupported_protocol_rejected(self, client):
        """
        A DockerContainer with an unsupported network protocol
        should be rejected.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-bad-protocol"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                network=DockerNetworkSpec(
                    ports=[
                        DockerNetworkPort(
                            access=True,
                            protocol="FTP",
                            path="/myapp",
                            internal=DockerInternalPort(port=8080),
                        ),
                    ]
                ),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Unsupported protocol 'FTP' should be rejected")

    def test_negative_port_number_rejected(self, client):
        """
        A DockerContainer with a negative port number
        should be rejected.
        """
        request = ExecutionRequest(
            executable=DockerContainer(
                kind=DOCKER_CONTAINER_KIND,
                meta=ComponentMetadata(name="docker-negative-port"),
                image=DockerImageSpec(
                    locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
                    digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
                ),
                network=DockerNetworkSpec(
                    ports=[
                        DockerNetworkPort(
                            access=True,
                            protocol="HTTP",
                            path="/myapp",
                            internal=DockerInternalPort(port=-1),
                        ),
                    ]
                ),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Negative port number should be rejected")


# ===========================================================================
# JupyterNotebook mock validation rules
# ===========================================================================

class TestJupyterNotebookValidation:
    """
    Tests for MockJupyterNotebookValidatorImpl.

    Mock rules:
      - LOCATION_EXCLUDED: ["http://example.com/excluded-one.ipynb"]
    """

    def test_valid_notebook_location(self, client):
        """
        A JupyterNotebook with a valid location
        should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_jupyter_executable(
                name="notebook-valid",
                location="http://example.com/valid-notebook.ipynb",
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Valid notebook location should be accepted")

    def test_excluded_notebook_location(self, client):
        """
        A JupyterNotebook with an excluded location should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_jupyter_executable(
                name="notebook-excluded",
                location="http://example.com/excluded-one.ipynb",
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded notebook location should be rejected")

    def test_missing_notebook_location(self, client):
        """
        A JupyterNotebook with no location should be rejected
        (location is required).
        """
        request = ExecutionRequest(
            executable=JupyterNotebook(
                kind=JUPYTER_NOTEBOOK_KIND,
                meta=ComponentMetadata(name="notebook-no-location"),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Missing notebook location should be rejected")


# ===========================================================================
# SimpleComputeResource mock validation rules
# ===========================================================================

class TestSimpleComputeResourceValidation:
    """
    Tests for MockSimpleComputeResourceValidatorImpl.

    Mock rules:
      - MAX_CORES_LIMIT = 16  (min cores > 16 fails)
      - MAX_MEMORY_LIMIT = 16 (min memory > 16 fails)
    """

    def test_valid_compute_within_limits(self, client):
        """
        A compute resource with cores and memory within limits
        should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("compute-valid"),
            compute=SimpleComputeResource(
                kind=SIMPLE_COMPUTE_KIND,
                meta=ComponentMetadata(name="valid-compute"),
                cores=SimpleComputeCores(min=4, max=8),
                memory=SimpleComputeMemory(min=2, max=4),
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Compute within limits should be accepted")

    def test_default_compute_no_explicit(self, client):
        """
        A request with no explicit compute resource should be accepted
        (server creates a default).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("compute-default"),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Default compute should be accepted")

    def test_min_cores_exceeds_limit(self, client):
        """
        A compute resource requesting minimum cores above the limit (16)
        should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("compute-cores-over"),
            compute=SimpleComputeResource(
                kind=SIMPLE_COMPUTE_KIND,
                meta=ComponentMetadata(name="cores-over-limit"),
                cores=SimpleComputeCores(min=20, max=20),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Min cores > 16 should be rejected")

    def test_max_cores_exceeds_limit_capped(self, client):
        """
        A compute resource where max cores exceeds the limit but min cores
        is within the limit should still be accepted (max gets capped).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("compute-cores-capped"),
            compute=SimpleComputeResource(
                kind=SIMPLE_COMPUTE_KIND,
                meta=ComponentMetadata(name="cores-capped"),
                cores=SimpleComputeCores(min=4, max=32),
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Max cores > limit with valid min should be accepted (capped)")

    def test_min_memory_exceeds_limit(self, client):
        """
        A compute resource requesting minimum memory above the limit (16 GiB)
        should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("compute-memory-over"),
            compute=SimpleComputeResource(
                kind=SIMPLE_COMPUTE_KIND,
                meta=ComponentMetadata(name="memory-over-limit"),
                memory=SimpleComputeMemory(min=20, max=20),
            ),
        )
        response = _submit(client, request)
        _assert_rejected(response, "Min memory > 16 should be rejected")

    def test_max_memory_exceeds_limit_capped(self, client):
        """
        A compute resource where max memory exceeds the limit but min memory
        is within the limit should still be accepted (max gets capped).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("compute-memory-capped"),
            compute=SimpleComputeResource(
                kind=SIMPLE_COMPUTE_KIND,
                meta=ComponentMetadata(name="memory-capped"),
                memory=SimpleComputeMemory(min=4, max=32),
            ),
        )
        response = _submit(client, request)
        _assert_accepted(response, "Max memory > limit with valid min should be accepted (capped)")


# ===========================================================================
# SimpleStorageResource mock validation rules
# ===========================================================================

class TestSimpleStorageResourceValidation:
    """
    Tests for MockSimpleStorageResourceValidatorImpl.

    Mock rules:
      - MAX_SIZE_LIMIT = 1000 (min size > 1000 fails)
    """

    def test_valid_storage_within_limits(self, client):
        """
        A storage resource with size within the limit should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("storage-valid"),
            storage=[
                SimpleStorageResource(
                    kind=SIMPLE_STORAGE_KIND,
                    meta=ComponentMetadata(name="valid-storage"),
                    size=SimpleStorageSize(min=100, max=500),
                ),
            ],
            data=[
                SimpleDataResource(
                    kind=SIMPLE_DATA_KIND,
                    meta=ComponentMetadata(name="storage-test-data"),
                    storage="valid-storage",
                    location="https://example.org/data/test.fits",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_accepted(response, "Storage size within limit should be accepted")

    def test_min_size_exceeds_limit(self, client):
        """
        A storage resource requesting minimum size above the limit (1000)
        should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("storage-size-over"),
            storage=[
                SimpleStorageResource(
                    kind=SIMPLE_STORAGE_KIND,
                    meta=ComponentMetadata(name="big-storage"),
                    size=SimpleStorageSize(min=2000, max=2000),
                ),
            ],
            data=[
                SimpleDataResource(
                    kind=SIMPLE_DATA_KIND,
                    meta=ComponentMetadata(name="storage-size-data"),
                    storage="big-storage",
                    location="https://example.org/data/test.fits",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Min storage size > 1000 should be rejected")

    def test_max_size_exceeds_limit_capped(self, client):
        """
        A storage resource where max size exceeds the limit but min size
        is within the limit should still be accepted (max gets capped).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("storage-size-capped"),
            storage=[
                SimpleStorageResource(
                    kind=SIMPLE_STORAGE_KIND,
                    meta=ComponentMetadata(name="capped-storage"),
                    size=SimpleStorageSize(min=100, max=5000),
                ),
            ],
            data=[
                SimpleDataResource(
                    kind=SIMPLE_DATA_KIND,
                    meta=ComponentMetadata(name="storage-cap-data"),
                    storage="capped-storage",
                    location="https://example.org/data/test.fits",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_accepted(response, "Max size > limit with valid min should be accepted (capped)")


# ===========================================================================
# SimpleDataResource mock validation rules
# ===========================================================================

class TestSimpleDataResourceValidation:
    """
    Tests for MockSimpleDataResourceValidatorImpl.

    Mock rules:
      - EXCLUDED_LOCATIONS: [
            "http://example.com/excluded.dat",
            "http://example.com/forbidden.dat"
        ]
    """

    def test_valid_data_location(self, client):
        """
        A SimpleDataResource with a valid (non-excluded) location
        should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("simple-data-valid"),
            data=[
                SimpleDataResource(
                    kind=SIMPLE_DATA_KIND,
                    meta=ComponentMetadata(name="valid-data"),
                    location="https://example.org/data/good-file.fits",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_accepted(response, "Valid data location should be accepted")

    def test_excluded_data_location(self, client):
        """
        A SimpleDataResource with an excluded location should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("simple-data-excluded"),
            data=[
                SimpleDataResource(
                    kind=SIMPLE_DATA_KIND,
                    meta=ComponentMetadata(name="excluded-data"),
                    location="http://example.com/excluded.dat",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded data location should be rejected")

    def test_excluded_data_location_forbidden(self, client):
        """
        A SimpleDataResource with the other excluded location
        should also be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("simple-data-excluded"),
            data=[
                SimpleDataResource(
                    kind=SIMPLE_DATA_KIND,
                    meta=ComponentMetadata(name="excluded-data"),
                    location="http://example.com/excluded.vot",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded data location should be rejected")

    def test_missing_data_location(self, client):
        """
        A SimpleDataResource with no location should be rejected
        (location is required).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("simple-data-no-location"),
            data=[
                SimpleDataResource(
                    kind=SIMPLE_DATA_KIND,
                    meta=ComponentMetadata(name="no-location-data"),
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Missing data location should be rejected")


# ===========================================================================
# AmazonS3DataResource mock validation rules
# ===========================================================================

class TestAmazonS3DataResourceValidation:
    """
    Tests for MockAmazonS3DataResourceValidatorImpl.

    Mock rules:
      - EXCLUDED_ENDPOINTS: [
            "https://s3.excluded.example.com",
            "https://s3.forbidden.example.com"
        ]
    """

    def test_valid_s3_data_resource(self, client):
        """
        An S3DataResource with valid (non-excluded) endpoint
        should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("s3-data-valid"),
            data=[
                S3DataResource(
                    kind=S3_DATA_KIND,
                    meta=ComponentMetadata(name="valid-s3"),
                    endpoint="https://s3.valid.example.com",
                    template="https://s3.valid.example.com/{bucket}/{object}",
                    bucket="my-bucket",
                    object="my-object.fits",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_accepted(response, "Valid S3 data resource should be accepted")

    def test_excluded_s3_endpoint_one(self, client):
        """
        An S3DataResource with an excluded endpoint should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("s3-data-excluded"),
            data=[
                S3DataResource(
                    kind=S3_DATA_KIND,
                    meta=ComponentMetadata(name="excluded-s3"),
                    endpoint="https://s3.excluded-one.example.com",
                    template="https://s3.excluded-one.example.com/{bucket}/{object}",
                    bucket="my-bucket",
                    object="my-object.fits",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded S3 endpoint should be rejected")

    def test_excluded_s3_endpoint_two(self, client):
        """
        An S3DataResource with an excluded endpoint should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("s3-data-forbidden"),
            data=[
                S3DataResource(
                    kind=S3_DATA_KIND,
                    meta=ComponentMetadata(name="forbidden-s3"),
                    endpoint="https://s3.excluded-two.example.com",
                    template="https://s3.excluded-two.example.com/{bucket}/{object}",
                    bucket="my-bucket",
                    object="my-object.fits",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded S3 endpoint should be rejected")

    def test_missing_s3_endpoint(self, client):
        """
        An S3DataResource with no endpoint should be rejected
        (endpoint is required).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("s3-data-no-endpoint"),
            data=[
                S3DataResource(
                    kind=S3_DATA_KIND,
                    meta=ComponentMetadata(name="no-endpoint-s3"),
                    template="https://s3.example.com/{bucket}/{object}",
                    bucket="my-bucket",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Missing S3 endpoint should be rejected")

    def test_missing_s3_template(self, client):
        """
        An S3DataResource with no template should be rejected
        (template is required).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("s3-data-no-template"),
            data=[
                S3DataResource(
                    kind=S3_DATA_KIND,
                    meta=ComponentMetadata(name="no-template-s3"),
                    endpoint="https://s3.valid.example.com",
                    bucket="my-bucket",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Missing S3 template should be rejected")

    def test_missing_s3_bucket(self, client):
        """
        An S3DataResource with no bucket should be rejected
        (bucket is required).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("s3-data-no-bucket"),
            data=[
                S3DataResource(
                    kind=S3_DATA_KIND,
                    meta=ComponentMetadata(name="no-bucket-s3"),
                    endpoint="https://s3.valid.example.com",
                    template="https://s3.valid.example.com/{bucket}/{object}",
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Missing S3 bucket should be rejected")


# ===========================================================================
# IvoaDataResource mock validation rules
# ===========================================================================

class TestIvoaDataResourceValidation:
    """
    Tests for MockIvoaDataResourceValidatorImpl.

    Mock rules:
      - EXCLUDED_IVOIDS: [
            URI("ivo://example.com/excluded"),
            URI("ivo://example.com/forbidden")
        ]
    """

    def test_valid_ivoa_data_resource(self, client):
        """
        An IvoaDataResource with a valid ivoid
        should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("ivoa-data-valid"),
            data=[
                IvoaDataResource(
                    kind=IVOA_DATA_KIND,
                    meta=ComponentMetadata(name="valid-ivoa"),
                    ivoa=IvoaDataResourceBlock(
                        ivoid="ivo://example.com/valid-resource",
                    ),
                ),
            ],
        )
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES"
        assert response.offers is not None and len(response.offers) > 0

    def test_excluded_ivoid(self, client):
        """
        An IvoaDataResource with an excluded ivoid should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("ivoa-data-excluded"),
            data=[
                IvoaDataResource(
                    kind=IVOA_DATA_KIND,
                    meta=ComponentMetadata(name="excluded-ivoa"),
                    ivoa=IvoaDataResourceBlock(
                        ivoid="ivo://example.com/excluded",
                    ),
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Excluded ivoid should be rejected")

    def test_forbidden_ivoid(self, client):
        """
        An IvoaDataResource with a forbidden ivoid should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("ivoa-data-forbidden"),
            data=[
                IvoaDataResource(
                    kind=IVOA_DATA_KIND,
                    meta=ComponentMetadata(name="forbidden-ivoa"),
                    ivoa=IvoaDataResourceBlock(
                        ivoid="ivo://example.com/forbidden",
                    ),
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Forbidden ivoid should be rejected")

    def test_missing_ivoid(self, client):
        """
        An IvoaDataResource with no ivoid should be rejected
        (ivoid is required).
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("ivoa-data-no-ivoid"),
            data=[
                IvoaDataResource(
                    kind=IVOA_DATA_KIND,
                    meta=ComponentMetadata(name="no-ivoid-ivoa"),
                    ivoa=IvoaDataResourceBlock(),
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Missing ivoid should be rejected")

    def test_missing_ivoa_block(self, client):
        """
        An IvoaDataResource with no ivoa metadata block should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("ivoa-data-no-block"),
            data=[
                IvoaDataResource(
                    kind=IVOA_DATA_KIND,
                    meta=ComponentMetadata(name="no-block-ivoa"),
                ),
            ],
        )
        response = _submit(client, request)
        _assert_rejected(response, "Missing ivoa metadata block should be rejected")


# ===========================================================================
# SkaoDataResource mock validation rules
# ===========================================================================

class TestSkaoDataResourceValidation:
    """
    Tests for MockSkaoDataResourceValidatorImpl.

    Mock rules:
      - EXCLUDED_NAMESPACES: [
            "excluded-namespace",
            "forbidden-namespace"
        ]
    """

    def test_valid_skao_data_resource(self, client):
        """
        A SkaoDataResource with a valid namespace
        should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("skao-data-valid"),
            data=[
                SkaoDataResource(
                    kind=SKAO_DATA_KIND,
                    meta=ComponentMetadata(name="valid-skao"),
                    ivoa=IvoaDataResourceBlock(
                        ivoid="ivo://skao.int/valid-obs",
                    ),
                    skao=SkaoDataResourceBlock(
                        namespace="valid-namespace",
                        objectname="test-object",
                        objecttype="FILE",
                        datasize=1024,
                        replicas=[
                            SkaoReplicaItem(
                                rsename="TEST-RSE-1",
                                dataurl="https://rse1.example.com/data/test-object",
                            ),
                        ],
                    ),
                ),
            ],
        )
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "YES"
        assert response.offers is not None and len(response.offers) > 0

    def test_excluded_namespace_one(self, client):
        """
        A SkaoDataResource with an excluded namespace should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("skao-data-excluded"),
            data=[
                SkaoDataResource(
                    kind=SKAO_DATA_KIND,
                    meta=ComponentMetadata(name="skao-data-excluded-one"),
                    ivoa=IvoaDataResourceBlock(
                        ivoid="ivo://skao.int/skao-data-excluded-one",
                    ),
                    skao=SkaoDataResourceBlock(
                        namespace="excluded-namespace-one",
                        objectname="skao-data-excluded-one",
                        objecttype="FILE",
                        datasize=1024,
                        replicas=[
                            SkaoReplicaItem(
                                rsename="TEST-RSE-1",
                                dataurl="https://rse1.example.com/data/skao-data-excluded-one",
                            ),
                        ],
                    ),
                ),
            ],
        )
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "NO"

    def two(self, client):
        """
        A SkaoDataResource with an excluded namespace should be rejected.
        """
        request = ExecutionRequest(
            executable=_make_docker_executable("skao-data-excluded-two"),
            data=[
                SkaoDataResource(
                    kind=SKAO_DATA_KIND,
                    meta=ComponentMetadata(name="skao-data-excluded-two"),
                    ivoa=IvoaDataResourceBlock(
                        ivoid="ivo://skao.int/skao-data-excluded-two",
                    ),
                    skao=SkaoDataResourceBlock(
                        namespace="excluded-namespace-two",
                        objectname="skao-data-excluded-two",
                        objecttype="FILE",
                        datasize=1024,
                        replicas=[
                            SkaoReplicaItem(
                                rsename="TEST-RSE-1",
                                dataurl="https://rse1.example.com/data/skao-data-excluded-two",
                            ),
                        ],
                    ),
                ),
            ],
        )
        response = client.submit_execution(request, follow_redirect=True)
        assert response.result == "NO"
