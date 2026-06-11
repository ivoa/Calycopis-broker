<!--
<meta:header>
  <meta:licence>
    Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)

    This information is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This information is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  </meta:licence>
</meta:header>
-->

# Calycopis Broker Reference

## URN Label Map

| URN | Display label |
|-----|---------------|
| `urn:ivoa:calycopis:cost:monetary` | Monetary cost |
| `urn:ivoa:calycopis:cost:energy` | Energy (kWh) |
| `urn:ivoa:calycopis:cost:carbon` | Carbon (gCO2) |
| `urn:ivoa:calycopis:metric:compute-performance` | Compute performance |
| `urn:ivoa:calycopis:metric:io-throughput` | IO throughput (MB/s) |

## Common Error Kinds

| Kind | Meaning | Fix |
|------|---------|-----|
| `urn:image-digest-mismatch` | Requested digest does not match cached image | `bin/broker digest resolve <image>` |
| `urn:missing-value` | Required field missing (e.g. digest) | Pass `--digest` or let CLI auto-resolve |

## State File Schema

`run/.broker-state.json`:

```json
{
  "request": {
    "name": "pi-calculator",
    "image": "alpine:3",
    "command": ["sh", "-c", "..."],
    "cores": "1:2",
    "digest": "sha256:..."
  },
  "offers": {
    "alpha": {
      "result": "YES",
      "session_uuid": "...",
      "costs": {},
      "metrics": {},
      "messages": []
    }
  },
  "created_at": "2026-06-06T01:20:12+00:00"
}
```

## Broker Environment Variables

| Variable | Broker |
|----------|--------|
| `BROKER_ALPHA_URL` | Green HPC |
| `BROKER_BETA_URL` | Cloud |
| `BROKER_GAMMA_URL` | Budget |

## Kind URI Registry

All `kind` fields in execution templates use URIs from the OpenAPI schema. The base URI is `https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds`.

### Executables

| Short name | Kind URI suffix | Abstract |
|------------|-----------------|----------|
| `abstract-executable` | `/executable/abstract-executable.yaml` | Yes |
| `docker-container` | `/executable/docker-container.yaml` | No |
| `singularity-container` | `/executable/singularity-container.yaml` | No |
| `jupyter-notebook` | `/executable/jupyter-notebook.yaml` | No |

### Compute

| Short name | Kind URI suffix | Abstract |
|------------|-----------------|----------|
| `abstract-compute-resource` | `/compute/abstract-compute-resource.yaml` | Yes |
| `simple-compute-resource` | `/compute/simple-compute-resource.yaml` | No |

### Storage

| Short name | Kind URI suffix | Abstract |
|------------|-----------------|----------|
| `abstract-storage-resource` | `/storage/abstract-storage-resource.yaml` | Yes |
| `simple-storage-resource` | `/storage/simple-storage-resource.yaml` | No |

### Volumes

| Short name | Kind URI suffix | Abstract |
|------------|-----------------|----------|
| `abstract-volume-mount` | `/volume/abstract-volume-mount.yaml` | Yes |
| `simple-volume-mount` | `/volume/simple-volume-mount.yaml` | No |

### Data

| Short name | Kind URI suffix | Abstract |
|------------|-----------------|----------|
| `abstract-data-resource` | `/data/abstract-data-resource.yaml` | Yes |
| `simple-data-resource` | `/data/simple-data-resource.yaml` | No |
| `S3-data-resource` | `/data/S3-data-resource.yaml` | No |
| `rucio-data-resource` | `/data/rucio-data-resource.yaml` | No |
| `ivoa-data-resource` | `/data/ivoa-data-resource.yaml` | No |
| `skao-data-resource` | `/data/skao-data-resource.yaml` | No |

## Replacement Dict Keys for `build_execution_request`

When resolving abstract elements programmatically, the `replacements` dict is keyed by element path:

| Path pattern | Example | Meaning |
|--------------|---------|---------|
| `executable` | `"executable"` | The top-level executable |
| `compute` | `"compute"` | The top-level compute resource |
| `data[N]` | `"data[0]"` | The Nth entry in the data list |
| `storage[N]` | `"storage[1]"` | The Nth entry in the storage list |
| `compute.volumes[N]` | `"compute.volumes[0]"` | The Nth volume inside compute |

Each replacement value is a dict that is deep-merged into the original element.  At minimum, it must include a `kind` field with the concrete type's URI, plus any required fields for that type (e.g. `location` for `simple-data-resource`).
