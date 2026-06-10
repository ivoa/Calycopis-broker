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
#     "timestamp": "2026-06-09T21:42:00",
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
"""Build ExecutionRequest objects from YAML/JSON execution templates.

This module provides a programmatic API for loading execution templates,
detecting abstract placeholder elements, resolving them (interactively or
via a replacement dict), and building ExecutionRequest objects suitable
for submission to the broker.

Programmatic usage (e.g. from an AI agent)::

    from broker_tools.builder import (
        load_execution_template,
        find_abstract_elements,
        build_execution_request,
    )

    template = load_execution_template("path/to/template.yaml")
    abstracts = find_abstract_elements(template)
    replacements = {
        "data[0]": {
            "kind": "https://www.purl.org/ivoa.net/Calycopis-openapi"
                    "/schema/v1.0/kinds/data/simple-data-resource.yaml",
            "location": "https://example.com/data.fits",
        }
    }
    request = build_execution_request(template, replacements=replacements)
"""

import json
from collections import namedtuple
from pathlib import Path

import yaml

from calycopis_schema_client.models import (
    ComponentMetadata,
    DockerImageSpec,
    ExecutionRequest,
)
from calycopis_schema_client.wrappers import (
    DockerContainer,
    IvoaDataResource,
    JupyterNotebook,
    RucioDataResource,
    S3DataResource,
    SimpleComputeResource,
    SimpleDataResource,
    SimpleStorageResource,
    SimpleVolumeMount,
    SingularityContainer,
    SkaoDataResource,
)


# ---------------------------------------------------------------------------
# Kind URI registry
# ---------------------------------------------------------------------------

_KIND_BASE = "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds"

KindEntry = namedtuple("KindEntry", ["cls", "is_abstract", "family"])

KIND_REGISTRY = {
    # Executables
    f"{_KIND_BASE}/executable/abstract-executable.yaml":
        KindEntry(cls=None, is_abstract=True, family="executable"),
    f"{_KIND_BASE}/executable/docker-container.yaml":
        KindEntry(cls=DockerContainer, is_abstract=False, family="executable"),
    f"{_KIND_BASE}/executable/singularity-container.yaml":
        KindEntry(cls=SingularityContainer, is_abstract=False, family="executable"),
    f"{_KIND_BASE}/executable/jupyter-notebook.yaml":
        KindEntry(cls=JupyterNotebook, is_abstract=False, family="executable"),
    # Compute
    f"{_KIND_BASE}/compute/abstract-compute-resource.yaml":
        KindEntry(cls=None, is_abstract=True, family="compute"),
    f"{_KIND_BASE}/compute/simple-compute-resource.yaml":
        KindEntry(cls=SimpleComputeResource, is_abstract=False, family="compute"),
    # Storage
    f"{_KIND_BASE}/storage/abstract-storage-resource.yaml":
        KindEntry(cls=None, is_abstract=True, family="storage"),
    f"{_KIND_BASE}/storage/simple-storage-resource.yaml":
        KindEntry(cls=SimpleStorageResource, is_abstract=False, family="storage"),
    # Volumes
    f"{_KIND_BASE}/volume/abstract-volume-mount.yaml":
        KindEntry(cls=None, is_abstract=True, family="volume"),
    f"{_KIND_BASE}/volume/simple-volume-mount.yaml":
        KindEntry(cls=SimpleVolumeMount, is_abstract=False, family="volume"),
    # Data
    f"{_KIND_BASE}/data/abstract-data-resource.yaml":
        KindEntry(cls=None, is_abstract=True, family="data"),
    f"{_KIND_BASE}/data/simple-data-resource.yaml":
        KindEntry(cls=SimpleDataResource, is_abstract=False, family="data"),
    f"{_KIND_BASE}/data/S3-data-resource.yaml":
        KindEntry(cls=S3DataResource, is_abstract=False, family="data"),
    f"{_KIND_BASE}/data/rucio-data-resource.yaml":
        KindEntry(cls=RucioDataResource, is_abstract=False, family="data"),
    f"{_KIND_BASE}/data/ivoa-data-resource.yaml":
        KindEntry(cls=IvoaDataResource, is_abstract=False, family="data"),
    f"{_KIND_BASE}/data/skao-data-resource.yaml":
        KindEntry(cls=SkaoDataResource, is_abstract=False, family="data"),
}


# ---------------------------------------------------------------------------
# Abstract element detection
# ---------------------------------------------------------------------------

AbstractElementInfo = namedtuple(
    "AbstractElementInfo",
    ["path", "kind", "name", "description", "concrete_alternatives"],
)


def _concrete_alternatives(family):
    """Return a dict mapping short type names to kind URIs for a family."""
    alternatives = {}
    for kind_uri, entry in KIND_REGISTRY.items():
        if entry.family == family and not entry.is_abstract:
            short_name = kind_uri.rsplit("/", 1)[-1].replace(".yaml", "")
            alternatives[short_name] = kind_uri
    return alternatives


# ---------------------------------------------------------------------------
# Interactive prompt definitions for each concrete type
# ---------------------------------------------------------------------------
# Each field tuple is (dotted_field_name, prompt_label, required, field_type).
# Supported field_type values: "str", "int", "str_list".

_FIELD_STR = "str"
_FIELD_INT = "int"
_FIELD_STR_LIST = "str_list"

CONCRETE_TYPE_PROMPTS = {
    "data": {
        "simple-data-resource": {
            "label": "Simple data resource (downloadable URL)",
            "kind": f"{_KIND_BASE}/data/simple-data-resource.yaml",
            "fields": [
                ("location", "Data URL", True, _FIELD_STR),
            ],
        },
        "S3-data-resource": {
            "label": "S3 data resource",
            "kind": f"{_KIND_BASE}/data/S3-data-resource.yaml",
            "fields": [
                ("endpoint", "S3 endpoint address", True, _FIELD_STR),
                ("template", "URL template", False, _FIELD_STR),
                ("bucket", "Bucket name", True, _FIELD_STR),
                ("object", "Object name", False, _FIELD_STR),
            ],
        },
        "rucio-data-resource": {
            "label": "Rucio data resource",
            "kind": f"{_KIND_BASE}/data/rucio-data-resource.yaml",
            "fields": [
                ("rucio.endpoint", "Rucio endpoint address", True, _FIELD_STR),
                ("rucio.scope", "Rucio scope", True, _FIELD_STR),
                ("rucio.object", "Object name", True, _FIELD_STR),
                ("rucio.type", "Object type (FILE/CONTAINER/DATASET)", True, _FIELD_STR),
            ],
        },
    },
    "executable": {
        "docker-container": {
            "label": "Docker/OCI container",
            "kind": f"{_KIND_BASE}/executable/docker-container.yaml",
            "fields": [
                ("image.locations", "Image location (e.g. ghcr.io/org/image:tag)", True, _FIELD_STR_LIST),
                ("image.digest", "Image digest (sha256:...)", False, _FIELD_STR),
            ],
        },
        "singularity-container": {
            "label": "Singularity container",
            "kind": f"{_KIND_BASE}/executable/singularity-container.yaml",
            "fields": [
                ("location", "Container location URL", True, _FIELD_STR),
            ],
        },
        "jupyter-notebook": {
            "label": "Jupyter notebook",
            "kind": f"{_KIND_BASE}/executable/jupyter-notebook.yaml",
            "fields": [
                ("location", "Notebook URL", True, _FIELD_STR),
            ],
        },
    },
    "compute": {
        "simple-compute-resource": {
            "label": "Simple compute resource",
            "kind": f"{_KIND_BASE}/compute/simple-compute-resource.yaml",
            "fields": [
                ("cores.min", "Minimum cores", False, _FIELD_INT),
                ("cores.max", "Maximum cores", False, _FIELD_INT),
                ("memory.min", "Minimum memory (GiB)", False, _FIELD_INT),
                ("memory.max", "Maximum memory (GiB)", False, _FIELD_INT),
            ],
        },
    },
    "storage": {
        "simple-storage-resource": {
            "label": "Simple storage resource",
            "kind": f"{_KIND_BASE}/storage/simple-storage-resource.yaml",
            "fields": [],
        },
    },
    "volume": {
        "simple-volume-mount": {
            "label": "Simple volume mount",
            "kind": f"{_KIND_BASE}/volume/simple-volume-mount.yaml",
            "fields": [
                ("path", "Mount path", True, _FIELD_STR),
                ("mode", "Access mode (READONLY/READWRITE)", True, _FIELD_STR),
                ("resource", "Resource name", True, _FIELD_STR),
            ],
        },
    },
}


# ---------------------------------------------------------------------------
# Template loading
# ---------------------------------------------------------------------------

def load_execution_template(path):
    """Load a YAML or JSON execution template file.

    For YAML files, leading comment blocks (licence headers, AIMetrics)
    are stripped before parsing.  The file extension determines the format:
    ``.json`` is parsed as JSON, everything else as YAML.

    Returns:
        A plain dict whose top-level keys are the ``ExecutionRequest``
        component names (``executable``, ``compute``, ``storage``, ``data``).
    """
    filepath = Path(path)
    text = filepath.read_text(encoding="utf-8")

    suffix = filepath.suffix.lower()
    if suffix == ".json":
        return json.loads(text)

    lines = text.split("\n")
    content_lines = []
    in_header = True
    for line in lines:
        if in_header:
            stripped = line.strip()
            if stripped == "" or stripped.startswith("#"):
                continue
            in_header = False
        content_lines.append(line)

    return yaml.safe_load("\n".join(content_lines))


# ---------------------------------------------------------------------------
# Abstract element detection
# ---------------------------------------------------------------------------

def find_abstract_elements(template):
    """Walk an execution template and return info about abstract elements.

    Inspects the ``kind`` field of every component (and nested volumes)
    against the ``KIND_REGISTRY``.

    Returns:
        A list of :class:`AbstractElementInfo` named tuples, one for each
        component whose ``kind`` is registered as abstract.  Each entry
        includes the element path, kind URI, metadata (name/description),
        and a dict of concrete alternatives for that family.
    """
    abstracts = []

    for key in ("executable", "compute"):
        component = template.get(key)
        if component is None:
            continue
        info = _check_abstract(key, component)
        if info is not None:
            abstracts.append(info)

    for key in ("storage", "data"):
        items = template.get(key)
        if items is None:
            continue
        for index, component in enumerate(items):
            info = _check_abstract(f"{key}[{index}]", component)
            if info is not None:
                abstracts.append(info)

    compute = template.get("compute")
    if compute is not None:
        volumes = compute.get("volumes", [])
        for index, vol in enumerate(volumes):
            info = _check_abstract(f"compute.volumes[{index}]", vol)
            if info is not None:
                abstracts.append(info)

    return abstracts


def _check_abstract(path, component):
    """Return an AbstractElementInfo if the component is abstract, else None."""
    kind = component.get("kind", "")
    entry = KIND_REGISTRY.get(kind)
    if entry is None:
        return None
    if not entry.is_abstract:
        return None
    meta = component.get("meta", {})
    alternatives = _concrete_alternatives(entry.family)
    return AbstractElementInfo(
        path=path,
        kind=kind,
        name=meta.get("name", ""),
        description=meta.get("description", ""),
        concrete_alternatives=alternatives,
    )


# ---------------------------------------------------------------------------
# Interactive resolution
# ---------------------------------------------------------------------------

def resolve_abstract_interactively(element):
    """Prompt the user to replace an abstract element with a concrete type.

    Displays the element's path, name, and description, then presents
    a numbered menu of concrete alternatives.  After the user selects
    a type, prompts for each field defined in ``CONCRETE_TYPE_PROMPTS``.

    Args:
        element: An :class:`AbstractElementInfo` returned by
            :func:`find_abstract_elements`.

    Returns:
        A dict of fields to merge into the element (includes ``kind``
        and the concrete type's specific fields).
    """
    print(f"\n--- Resolve abstract element: {element.path} ---")
    if element.name:
        print(f"  Name: {element.name}")
    if element.description:
        for line in element.description.splitlines():
            print(f"  {line}")

    entry = KIND_REGISTRY.get(element.kind)
    family = entry.family
    type_prompts = CONCRETE_TYPE_PROMPTS.get(family, {})

    options = list(type_prompts.items())
    if not options:
        raise ValueError(f"No concrete types defined for family '{family}'")

    print("\n  Available concrete types:")
    for i, (_short_name, info) in enumerate(options, 1):
        print(f"    {i}) {info['label']}")

    while True:
        choice = input(f"\n  Select type [1-{len(options)}]: ").strip()
        try:
            choice_idx = int(choice) - 1
            if 0 <= choice_idx < len(options):
                break
        except ValueError:
            pass
        print(f"  Please enter a number between 1 and {len(options)}.")

    _selected_name, selected_info = options[choice_idx]
    result = {"kind": selected_info["kind"]}

    for field_name, prompt_label, required, field_type in selected_info["fields"]:
        if required:
            suffix = ""
        else:
            suffix = " (optional, press Enter to skip)"

        while True:
            value = input(f"  {prompt_label}{suffix}: ").strip()
            if value:
                if field_type == _FIELD_INT:
                    try:
                        value = int(value)
                    except ValueError:
                        print("  Please enter an integer value.")
                        continue
                if field_type == _FIELD_STR_LIST:
                    value = [value]
                _set_nested(result, field_name, value)
                break
            if not required:
                break
            print("  This field is required.")

    return result


def _set_nested(target, dotted_key, value):
    """Set a value in a nested dict using dot notation.

    For example, ``_set_nested(d, "image.locations", ["x"])`` creates
    ``d["image"]["locations"] = ["x"]``, creating intermediate dicts
    as needed.
    """
    parts = dotted_key.split(".")
    current = target
    for part in parts[:-1]:
        if part not in current:
            current[part] = {}
        current = current[part]
    current[parts[-1]] = value


# ---------------------------------------------------------------------------
# Replacement application
# ---------------------------------------------------------------------------

def _deep_merge(base, override):
    """Deep-merge *override* into *base*, returning a new dict.

    Nested dicts are merged recursively; all other values in *override*
    replace those in *base*.
    """
    result = dict(base)
    for key, value in override.items():
        if key in result and isinstance(result[key], dict) and isinstance(value, dict):
            result[key] = _deep_merge(result[key], value)
        else:
            result[key] = value
    return result


def _apply_replacements(template, replacements):
    """Apply replacement dicts to elements identified by path.

    Each key in *replacements* is an element path (e.g. ``"data[0]"``,
    ``"compute.volumes[1]"``, ``"executable"``).  The value is a dict
    of fields that are deep-merged into the original element.
    """
    result = dict(template)

    for path, fields in replacements.items():
        if "[" not in path:
            if path not in result:
                continue
            result[path] = _deep_merge(result[path], fields)
            continue

        parts = path.split(".")
        if len(parts) == 1:
            key = path.split("[")[0]
            index = int(path.split("[")[1].rstrip("]"))
            if key not in result:
                continue
            items = list(result[key])
            items[index] = _deep_merge(items[index], fields)
            result[key] = items
        else:
            parent_key = parts[0]
            rest = ".".join(parts[1:])
            child_key = rest.split("[")[0]
            index = int(rest.split("[")[1].rstrip("]"))
            if parent_key not in result:
                continue
            parent = dict(result[parent_key])
            if child_key not in parent:
                continue
            items = list(parent[child_key])
            items[index] = _deep_merge(items[index], fields)
            parent[child_key] = items
            result[parent_key] = parent

    return result


# ---------------------------------------------------------------------------
# Component construction
# ---------------------------------------------------------------------------

def _build_component(component_dict):
    """Convert a raw dict to the appropriate model instance based on its kind.

    Looks up the ``kind`` URI in ``KIND_REGISTRY`` to select the wrapper
    class, pre-processes known nested structures (``meta``, ``image``,
    ``volumes``), and constructs the model instance.

    Raises:
        ValueError: If the kind URI is unknown or still abstract.
    """
    if component_dict is None:
        return None

    kind = component_dict.get("kind", "")
    entry = KIND_REGISTRY.get(kind)
    if entry is None:
        raise ValueError(f"Unknown kind URI: {kind}")
    if entry.is_abstract:
        raise ValueError(
            f"Abstract element has not been resolved to a concrete type: {kind}"
        )

    data = dict(component_dict)

    if "meta" in data and isinstance(data["meta"], dict):
        data["meta"] = ComponentMetadata(**data["meta"])

    if "image" in data and isinstance(data["image"], dict):
        data["image"] = DockerImageSpec(**data["image"])

    if "volumes" in data and isinstance(data["volumes"], list):
        data["volumes"] = [_build_component(v) for v in data["volumes"]]

    return entry.cls(**data)


# ---------------------------------------------------------------------------
# Public API – build and format
# ---------------------------------------------------------------------------

def build_execution_request(template, replacements=None):
    """Convert a template dict into an ExecutionRequest.

    Args:
        template: Raw dict from :func:`load_execution_template`.
        replacements: Optional dict keyed by element path (e.g.
            ``"data[0]"``) with values being dicts of fields to
            deep-merge into the element.  Used to resolve abstract
            elements programmatically (e.g. from an AI agent).

    Returns:
        An ``ExecutionRequest`` instance ready for submission.

    Raises:
        ValueError: If any component has an unknown or unresolved
            abstract kind URI.
    """
    resolved = _apply_replacements(template, replacements or {})

    executable = _build_component(resolved.get("executable"))
    compute = _build_component(resolved.get("compute"))

    storage_list = resolved.get("storage")
    storage = None
    if storage_list:
        storage = [_build_component(s) for s in storage_list]

    data_list = resolved.get("data")
    data = None
    if data_list:
        data = [_build_component(d) for d in data_list]

    return ExecutionRequest(
        executable=executable,
        compute=compute,
        storage=storage,
        data=data,
    )


def _clean_dict(data):
    """Remove None values and empty collections from a nested structure."""
    if isinstance(data, dict):
        cleaned = {}
        for key, value in data.items():
            cleaned_value = _clean_dict(value)
            if cleaned_value is not None:
                cleaned[key] = cleaned_value
        if not cleaned:
            return None
        return cleaned
    if isinstance(data, list):
        cleaned = [_clean_dict(item) for item in data]
        cleaned = [item for item in cleaned if item is not None]
        if not cleaned:
            return None
        return cleaned
    return data


def _dump_component(component):
    """Dump a single component using its concrete class serializer.

    Pydantic's ``model_dump`` on the parent ``ExecutionRequest`` serializes
    polymorphic fields using the base class schema, which drops
    subclass-specific fields.  Dumping each component individually
    preserves all fields.
    """
    if component is None:
        return None
    return component.model_dump(exclude_none=True, mode="json")


def format_request_as_dict(request):
    """Convert an ExecutionRequest to a plain dict preserving all fields.

    Each component is serialized via its concrete class so that
    subclass-specific fields (e.g. ``image`` on ``DockerContainer``,
    ``location`` on ``SimpleDataResource``) are included.
    """
    result = {}

    if request.executable is not None:
        result["executable"] = _dump_component(request.executable)

    if request.compute is not None:
        compute_dict = _dump_component(request.compute)
        if compute_dict and "volumes" in compute_dict and request.compute.volumes:
            compute_dict["volumes"] = [
                _dump_component(v) for v in request.compute.volumes
            ]
        result["compute"] = compute_dict

    if request.storage:
        result["storage"] = [_dump_component(s) for s in request.storage]

    if request.data:
        result["data"] = [_dump_component(d) for d in request.data]

    return result


def format_request_yaml(request):
    """Serialize an ExecutionRequest to YAML for review.

    None-valued fields and empty collections are omitted for readability.
    """
    data = format_request_as_dict(request)
    cleaned = _clean_dict(data)
    if cleaned is None:
        cleaned = {}
    return yaml.dump(
        cleaned,
        default_flow_style=False,
        sort_keys=False,
        allow_unicode=True,
    )
