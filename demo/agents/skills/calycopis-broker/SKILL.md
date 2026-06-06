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

Drive the three demo Execution Brokers using `bin/broker` CLI tools. Do not write ad-hoc Python unless the workload needs an executable type not covered by the CLI (Jupyter, Singularity, etc.).

## Prerequisites

```bash
source run/demo-user.env
```

Required env vars: `DEMO_USER`, `DEMO_PASS`, `BROKER_ALPHA_URL`, `BROKER_BETA_URL`, `BROKER_GAMMA_URL`.

## Standard Workflow

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

## CLI Reference

| Command | Purpose |
|---------|---------|
| `bin/broker compare` | Submit to all brokers; print markdown table; save state to `run/.broker-state.json` |
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
- **State persists in `run/.broker-state.json`** between compare and accept. Do not resubmit unless the state is stale.

## Comparison Table Format

Present the `compare` output directly to the user:

```
| Attribute | Alpha (Green HPC) | Beta (Cloud) | Gamma (Budget) |
```

Then explain trade-offs and ask which broker to select.

## Additional Resources

- URN labels and error kinds: [reference.md](reference.md)
- Low-level Python API: [AGENTS.md](../../../AGENTS.md) (fallback only)
