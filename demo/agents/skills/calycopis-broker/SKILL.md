---
name: calycopis-broker
description: >-
  Submit, compare, accept, and monitor IVOA Calycopis Execution Broker tasks
  across Alpha/Beta/Gamma. Use when the user wants to run computational
  workloads, compare broker offers, or manage execution sessions.
---

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

# Calycopis Broker Client

Drive the three demo Execution Brokers using `bin/broker` CLI tools. Do not write ad-hoc Python unless the workload needs something not covered by the CLI.

## Prerequisites

```bash
source run/demo-user.env
```

Required env vars: `DEMO_USER`, `DEMO_PASS`, `BROKER_ALPHA_URL`, `BROKER_BETA_URL`, `BROKER_GAMMA_URL`.

## Workflows

There are two ways to submit tasks: **direct CLI flags** for simple Docker workloads, and **execution templates** for structured requests (including data resources, volume mounts, and non-Docker executables).

### Direct CLI workflow (simple Docker tasks)

1. **Compare** — submit the task to all brokers and show the comparison table
2. **Ask** — let the user pick Alpha, Beta, or Gamma based on priorities
3. **Accept** — accept the saved offer and monitor to completion
4. **Report** — show phase, stdout, and any errors

```bash
bin/broker compare \
  --name pi-calculator \
  --image alpine:3 \
  --command "sh -c \"echo 'scale=1000; 4*a(1)' | bc -l\"" \
  --cores 1:2

bin/broker accept --broker alpha
```

For automated flows (no user choice): `bin/broker run --broker alpha --name ... --image ... --command ...`

### Template-based workflow (structured requests)

Use `bin/broker build` when the task is described in a YAML or JSON execution template file. Templates can include executables, compute resources, volume mounts, and data resources. Abstract placeholders in the template (e.g. `abstract-data-resource`) are resolved interactively.

```bash
# Review the built request (prints YAML)
bin/broker build --template /path/to/ivoa-execution.yaml

# Build and submit to all brokers in one step
bin/broker build --template /path/to/ivoa-execution.yaml --submit

# Accept from the saved state
bin/broker accept --broker alpha
```

Example template file structure:

```yaml
executable:
  kind: "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/executable/docker-container.yaml"
  meta:
    description: "A checksum calculator container"
  image:
    locations:
      - "ghcr.io/zarquan/heliophorus-androcles:sha-9a2513b"
    digest: "sha256:..."
compute:
  kind: "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/compute/simple-compute-resource.yaml"
  volumes:
    - kind: "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/volume/simple-volume-mount.yaml"
      path: "/input"
      mode: "READONLY"
      resource: "input-data"
data:
  - kind: "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/data/abstract-data-resource.yaml"
    meta:
      name: "input-data"
      description: "Replace with a concrete data resource"
```

When the template contains abstract elements, `bin/broker build` will prompt the user to select a concrete type and provide the required fields.

### Programmatic API (for AI agents and scripts)

The builder can be used programmatically without interactive prompts by providing a `replacements` dict:

```python
import sys
sys.path.insert(0, "bin")

from broker_tools.builder import (
    load_execution_template,
    find_abstract_elements,
    build_execution_request,
    format_request_yaml,
)
from broker_tools.offers import submit_to_all

template = load_execution_template("/path/to/template.yaml")

# Inspect abstract elements that need resolution
abstracts = find_abstract_elements(template)
for a in abstracts:
    print(f"{a.path}: {a.name} — alternatives: {list(a.concrete_alternatives.keys())}")

# Provide concrete replacements
replacements = {
    "data[0]": {
        "kind": "https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/data/simple-data-resource.yaml",
        "location": "https://example.com/data.fits",
    }
}

request = build_execution_request(template, replacements=replacements)

# Review the built request
print(format_request_yaml(request))

# Submit to all brokers
summaries = submit_to_all(request)
```

Key functions in `broker_tools.builder`:

| Function | Purpose |
|----------|---------|
| `load_execution_template(path)` | Load YAML/JSON template, strip licence headers |
| `find_abstract_elements(template)` | Detect abstract placeholders, return their paths and concrete alternatives |
| `resolve_abstract_interactively(element)` | Prompt user to choose a concrete type and fill in fields |
| `build_execution_request(template, replacements)` | Convert template to `ExecutionRequest`, applying replacements |
| `format_request_yaml(request)` | Serialize request back to YAML for review |
| `format_request_as_dict(request)` | Serialize request to a plain dict |

## CLI Reference

| Command | Purpose |
|---------|---------|
| `bin/broker compare` | Submit to all brokers; print markdown table; save state to `run/.broker-state.json` |
| `bin/broker build --template <path>` | Build request from YAML/JSON template; resolves abstract elements interactively; prints result |
| `bin/broker build --template <path> --submit` | Build from template and submit to all brokers; save state |
| `bin/broker accept --broker <name>` | Accept offer from state file; monitor until terminal |
| `bin/broker monitor --broker <name> --uuid <uuid>` | Poll session; add `--accept` to accept first |
| `bin/broker run --broker <name> ...` | Compare + accept + monitor in one step |
| `bin/broker digest resolve <image>` | Resolve cached image digest (e.g. `alpine:3`) |
| `bin/broker status` | Health check all brokers |

All commands support `--json` for machine-readable output.

## Broker Profiles

| Broker | Best for | Trade-off |
|--------|----------|-----------|
| **Alpha** | Speed, greenest | Highest cost |
| **Beta** | Balanced | Moderate on all axes |
| **Gamma** | Cheapest | Slowest, highest carbon |

## Gotchas

- **Image digest is required.** The CLI auto-resolves via `run/image-digests.json` or broker logs. If submission fails with `urn:image-digest-mismatch`, run `bin/broker digest resolve alpine:3`.
- **Offer UUID is at `offer.meta.uuid`**, not `offer.uuid`.
- **Container stdout is not in the session API.** `bin/broker accept` and `bin/broker monitor` read it from broker logs automatically.
- **State persists in `run/.broker-state.json`** between compare/build and accept. Do not resubmit unless the state is stale.
- **Template abstract elements must be resolved.** If a template contains `abstract-data-resource` or other abstract kinds, they must be replaced with concrete types before submission. Use `bin/broker build` interactively, or pass a `replacements` dict to `build_execution_request()`.
- **Template `kind` URIs must match the registry.** Each component's `kind` field must be a recognized URI from the OpenAPI schema. See [reference.md](reference.md) for the full list.

## Comparison Table Format

Present the `compare` output directly to the user:

```
| Attribute | Alpha (Green HPC) | Beta (Cloud) | Gamma (Budget) |
```

Then explain trade-offs and ask which broker to select.

## Additional Resources

- URN labels and error kinds: [reference.md](reference.md)
- Low-level Python API: [AGENTS.md](../../../AGENTS.md) (fallback only)
