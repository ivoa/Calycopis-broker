#
# AIMetrics: [
#     {
#     "timestamp": "2026-06-02T13:37:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 8,
#       "units": "%"
#       }
#     }
#   ]
#

"""
Integration tests for the resource registration fix (GitHub issue #340 follow-up).

These tests verify that cross-referencing resources (data <-> storage)
are correctly handled by the two-step validation process:
  Step 1: Register all resources and assign UUIDs.
  Step 2: Validate all resources, resolving cross-references.

The tests submit ExecutionRequests to a running Calycopis broker service
and verify the responses.

Requires:
  - A running Calycopis broker service (default: http://localhost:8082)
  - The calycopis_schema_client Python package installed

Usage:
  pytest tests/test_resource_registration.py -v
  CALYCOPIS_URL=http://host:port pytest tests/test_resource_registration.py -v
"""

import pytest

from calycopis_schema_client.models import (
    ExecutionRequest,
    OfferSetResponse,
)
from calycopis_schema_client.models.docker_image_spec import DockerImageSpec
from calycopis_schema_client.models.component_metadata import ComponentMetadata
from calycopis_schema_client.wrappers import (
    DockerContainer,
    SimpleDataResource,
    SimpleStorageResource,
    SimpleVolumeMount,
)



def _make_executable(name: str = "test-container") -> DockerContainer:
    """Helper: create a minimal DockerContainer executable."""
    return DockerContainer(
        meta=ComponentMetadata(name=name),
        image=DockerImageSpec(
            locations=["ghcr.io/ivoa/oligia-webtop:ubuntu-2022.01.13"],
            digest="sha256:abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890",
        ),
    )


# ---------------------------------------------------------------------------
# Helper to submit and return the OfferSetResponse
# ---------------------------------------------------------------------------

def _submit(client, request: ExecutionRequest) -> OfferSetResponse:
    """Submit a request and return the OfferSetResponse."""
    response = client.submit_execution(request, follow_redirect=True)
    assert isinstance(response, OfferSetResponse), (
        f"Expected OfferSetResponse, got {type(response)}"
    )
    return response


# ---------------------------------------------------------------------------
# Tests
# ---------------------------------------------------------------------------


class TestBaseline:
    """Baseline tests: executable only, no explicit data/storage."""

    def test_executable_only(self, client):
        """
        A request with just an executable should succeed.
        The server creates a default compute resource automatically.
        """
        request = ExecutionRequest(
            executable=_make_executable("baseline-exec-only"),
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )
        assert response.offers is not None
        assert len(response.offers) > 0


class TestDataWithDefaultStorage:
    """Data resources without explicit storage → server creates default storage."""

    def test_single_data_no_storage(self, client):
        """
        A data resource with no storage reference should get default storage
        auto-created by the server.
        """
        request = ExecutionRequest(
            executable=_make_executable("data-default-storage"),
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="my-data"),
                    location="https://example.org/data/test-file.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )
        assert response.offers is not None
        assert len(response.offers) > 0

        # Each offer should have both data and storage resources.
        offer = response.offers[0]
        assert offer.data is not None, "Offer should contain data resources"
        assert len(offer.data) > 0
        assert offer.storage is not None, "Offer should contain auto-created storage"
        assert len(offer.storage) > 0

    def test_multiple_data_no_storage(self, client):
        """
        Multiple data resources with no storage reference should each get
        their own default storage.
        """
        request = ExecutionRequest(
            executable=_make_executable("multi-data-default-storage"),
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-alpha"),
                    location="https://example.org/data/alpha.fits",
                ),
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-beta"),
                    location="https://example.org/data/beta.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )
        offer = response.offers[0]
        assert offer.data is not None
        assert len(offer.data) == 2, "Should have two data resources"
        assert offer.storage is not None
        assert len(offer.storage) >= 2, (
            f"Should have at least two storage resources (one per data), got {len(offer.storage)}"
        )


class TestDataReferencingNamedStorage:
    """
    Data resources referencing a named storage resource.
    This is the core cross-reference scenario that the registration fix addresses.
    """

    def test_data_references_named_storage(self, client):
        """
        A data resource that references a named storage resource should succeed.
        The storage is listed in the request, and the data resource references
        it by name.
        """
        request = ExecutionRequest(
            executable=_make_executable("data-refs-storage"),
            storage=[
                SimpleStorageResource(
                    meta=ComponentMetadata(name="my-storage"),
                ),
            ],
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="my-data"),
                    storage="my-storage",
                    location="https://example.org/data/test-file.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )
        assert response.offers is not None
        assert len(response.offers) > 0

        offer = response.offers[0]
        assert offer.data is not None and len(offer.data) > 0
        assert offer.storage is not None and len(offer.storage) > 0

        # Verify the data resource points to the storage resource.
        data_resource = offer.data[0]
        storage_resource = offer.storage[0]
        assert data_resource.storage is not None, (
            "Data resource should have a storage reference"
        )
        # The server resolves names to UUIDs, so the data's storage field
        # should match the storage resource's UUID.
        storage_uuid = str(storage_resource.meta.uuid)
        assert data_resource.storage == storage_uuid, (
            f"Data storage ref [{data_resource.storage}] "
            f"should match storage UUID [{storage_uuid}]"
        )

    def test_multiple_data_same_named_storage(self, client):
        """
        Multiple data resources referencing the same named storage.
        All should resolve to the same storage resource.
        """
        request = ExecutionRequest(
            executable=_make_executable("multi-data-same-storage"),
            storage=[
                SimpleStorageResource(
                    meta=ComponentMetadata(name="shared-storage"),
                ),
            ],
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-one"),
                    storage="shared-storage",
                    location="https://example.org/data/one.fits",
                ),
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-two"),
                    storage="shared-storage",
                    location="https://example.org/data/two.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )
        offer = response.offers[0]
        assert offer.data is not None
        assert len(offer.data) == 2

        # Both data resources should point to the same storage UUID.
        storage_refs = {d.storage for d in offer.data}
        assert len(storage_refs) == 1, (
            f"Both data resources should reference the same storage, "
            f"got refs: {storage_refs}"
        )

    def test_data_references_nonexistent_storage(self, client):
        """
        A data resource referencing a storage name that doesn't exist
        in the request should fail validation → result=NO.
        """
        request = ExecutionRequest(
            executable=_make_executable("data-bad-storage-ref"),
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="orphan-data"),
                    storage="nonexistent-storage",
                    location="https://example.org/data/orphan.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "NO", (
            f"Expected NO for non-existent storage reference, got {response.result}"
        )


class TestMixedResources:
    """Tests combining storage, data, and other resource types."""

    def test_storage_and_data_with_names(self, client):
        """
        Named storage and named data with cross-reference.
        Verifies the full registration + validation pipeline.
        """
        request = ExecutionRequest(
            executable=_make_executable("named-storage-data"),
            storage=[
                SimpleStorageResource(
                    meta=ComponentMetadata(name="named-store"),
                ),
            ],
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="named-data"),
                    storage="named-store",
                    location="https://example.org/data/named.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )

        offer = response.offers[0]
        # Verify names are preserved.
        assert offer.storage[0].meta.name == "named-store"
        assert offer.data[0].meta.name == "named-data"
        # Verify the cross-reference.
        assert offer.data[0].storage == str(offer.storage[0].meta.uuid)

    def test_multiple_storage_multiple_data(self, client):
        """
        Two storage resources, each with a data resource pointing to it.
        Verifies that multiple independent cross-references resolve correctly.
        """
        request = ExecutionRequest(
            executable=_make_executable("multi-storage-multi-data"),
            storage=[
                SimpleStorageResource(
                    meta=ComponentMetadata(name="store-A"),
                ),
                SimpleStorageResource(
                    meta=ComponentMetadata(name="store-B"),
                ),
            ],
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-for-A"),
                    storage="store-A",
                    location="https://example.org/data/a.fits",
                ),
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-for-B"),
                    storage="store-B",
                    location="https://example.org/data/b.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )
        offer = response.offers[0]
        assert offer.storage is not None
        assert len(offer.storage) == 2
        assert offer.data is not None
        assert len(offer.data) == 2

        # Build a name -> uuid map for storage.
        storage_map = {s.meta.name: str(s.meta.uuid) for s in offer.storage}

        # Each data resource should point to its corresponding storage.
        data_map = {d.meta.name: d.storage for d in offer.data}
        assert data_map["data-for-A"] == storage_map["store-A"], (
            f"data-for-A should reference store-A UUID"
        )
        assert data_map["data-for-B"] == storage_map["store-B"], (
            f"data-for-B should reference store-B UUID"
        )

    def test_mixed_data_with_and_without_storage_ref(self, client):
        """
        One data resource references named storage, another gets default.
        Both should be accepted.
        """
        request = ExecutionRequest(
            executable=_make_executable("mixed-data-storage"),
            storage=[
                SimpleStorageResource(
                    meta=ComponentMetadata(name="explicit-store"),
                ),
            ],
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-with-storage"),
                    storage="explicit-store",
                    location="https://example.org/data/with-storage.fits",
                ),
                SimpleDataResource(
                    meta=ComponentMetadata(name="data-auto-storage"),
                    location="https://example.org/data/auto-storage.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES", (
            f"Expected YES, got {response.result}. "
            f"Messages: {response.meta.messages if response.meta else 'none'}"
        )
        offer = response.offers[0]
        assert len(offer.data) == 2
        # Should have at least 2 storage: the explicit one + auto-created one.
        assert len(offer.storage) >= 2, (
            f"Expected at least 2 storage resources, got {len(offer.storage)}"
        )


class TestUuidAssignment:
    """Tests verifying that UUIDs are assigned to all resources."""

    def test_all_resources_get_uuids(self, client):
        """
        All resources in the response should have UUIDs assigned,
        even if the request didn't specify them.
        """
        request = ExecutionRequest(
            executable=_make_executable("uuid-test"),
            storage=[
                SimpleStorageResource(
                    meta=ComponentMetadata(name="uuid-storage"),
                ),
            ],
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="uuid-data"),
                    storage="uuid-storage",
                    location="https://example.org/data/uuid-test.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES"
        offer = response.offers[0]

        # Executable should have a UUID.
        assert offer.executable is not None
        assert offer.executable.meta is not None
        assert offer.executable.meta.uuid is not None, "Executable should have a UUID"

        # Compute should have a UUID.
        assert offer.compute is not None
        assert offer.compute.meta is not None
        assert offer.compute.meta.uuid is not None, "Compute should have a UUID"

        # Storage should have a UUID.
        for storage in offer.storage:
            assert storage.meta is not None
            assert storage.meta.uuid is not None, (
                f"Storage '{storage.meta.name}' should have a UUID"
            )

        # Data should have a UUID.
        for data in offer.data:
            assert data.meta is not None
            assert data.meta.uuid is not None, (
                f"Data '{data.meta.name}' should have a UUID"
            )

    def test_uuids_are_unique(self, client):
        """
        All resource UUIDs within a single offer should be unique.
        """
        request = ExecutionRequest(
            executable=_make_executable("uuid-unique-test"),
            storage=[
                SimpleStorageResource(
                    meta=ComponentMetadata(name="uniq-store-1"),
                ),
                SimpleStorageResource(
                    meta=ComponentMetadata(name="uniq-store-2"),
                ),
            ],
            data=[
                SimpleDataResource(
                    meta=ComponentMetadata(name="uniq-data-1"),
                    storage="uniq-store-1",
                    location="https://example.org/data/uniq1.fits",
                ),
                SimpleDataResource(
                    meta=ComponentMetadata(name="uniq-data-2"),
                    storage="uniq-store-2",
                    location="https://example.org/data/uniq2.fits",
                ),
            ],
        )
        response = _submit(client, request)

        assert response.result == "YES"
        offer = response.offers[0]

        all_uuids = []
        if offer.executable and offer.executable.meta:
            all_uuids.append(str(offer.executable.meta.uuid))
        if offer.compute and offer.compute.meta:
            all_uuids.append(str(offer.compute.meta.uuid))
        for s in (offer.storage or []):
            if s.meta:
                all_uuids.append(str(s.meta.uuid))
        for d in (offer.data or []):
            if d.meta:
                all_uuids.append(str(d.meta.uuid))

        assert len(all_uuids) == len(set(all_uuids)), (
            f"All resource UUIDs should be unique, got duplicates in: {all_uuids}"
        )
