# Bug report: compute container starts without data volume bind mount

**Date:** 2026-06-09  
**Reporter:** Calycopis multi-broker demo (Cursor agent session)  
**Severity:** High — template-based workloads with staged input data fail consistently  
**Component:** Calycopis Execution Broker — Docker platform prepare pipeline  
**Affected brokers observed:** Alpha, Beta (likely all Docker-based brokers)

---

## Summary

When executing a template-based workload that stages HTTP input data into a Docker volume and mounts it at `/input`, the broker prepares the **compute container before the storage volume is ready**. Bind-mount resolution therefore finds `volume ident [null]`, skips the mount, and starts the workload container with **zero bind mounts**. The executable then fails because `/input` does not exist.

Data staging itself succeeds; only the compute ↔ storage linkage is broken.

---

## Environment

| Item | Value |
|------|-------|
| Demo stack | Calycopis multi-broker Podman deployment |
| Container runtime | Podman (`unix:///run/podman/podman.sock`) |
| Brokers tested | Alpha (`broker-alpha:8082`), Beta (`broker-beta:8082`) |
| Client | `bin/broker` / `broker_tools.builder` |
| Template | [Heliophorus-androcles `ivoa-execution.yaml`](https://raw.githubusercontent.com/Zarquan/Heliophorus-androcles/refs/heads/main/ivoa-execution.yaml) |
| Executable image | `ghcr.io/zarquan/heliophorus-androcles:sha-9a2513b` |
| Input URL | `http://www.beespace.me/sites/www.beespace.me/files/styles/large/public/field/image/20250824_111206.jpg` |
| Command | `sha256sum json` |
| Compute constraints | Default (none) |

Local copies:

- Template: `/workspace/ivoa-execution.yaml`
- Broker state: `/workspace/run/.broker-state.json`
- Logs: `/workspace/run/alpha/logs/broker.log`, `/workspace/run/beta/logs/broker.log`

---

## Steps to reproduce

1. Load demo credentials: `source run/demo-user.env`
2. Build and submit the Heliophorus checksum task (or equivalent request with `data` + `compute.volumes`):

   ```bash
   # Programmatic submission used in the failing session:
   python3 -c "
   import sys; sys.path.insert(0, 'bin')
   from broker_tools.builder import load_execution_template, build_execution_request
   from broker_tools.offers import submit_to_all
   from broker_tools.state import save_state
   from datetime import datetime, timezone

   template = load_execution_template('/workspace/ivoa-execution.yaml')
   replacements = {
       'data[0]': {
           'kind': 'https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/data/simple-data-resource.yaml',
           'location': 'http://www.beespace.me/sites/www.beespace.me/files/styles/large/public/field/image/20250824_111206.jpg',
       },
       'executable': {'command': ['sha256sum', 'json']},
   }
   request = build_execution_request(template, replacements=replacements)
   summaries = submit_to_all(request)
   "
   ```

3. Accept an offer: `bin/broker accept --broker alpha` (or `beta`)
4. Observe session phase `FAILED`, compute phase `FAILED`, data phase `COMPLETED`

---

## Expected behaviour

1. Storage volume is created and assigned a Docker volume ID.
2. Input data is downloaded into that volume.
3. Compute container is created **with** the volume bind-mounted at `/input` (read-only).
4. Executable reads `/input`, computes SHA-256 checksum, emits JSON on stdout.
5. Session reaches terminal phase `COMPLETED`.

---

## Actual behaviour

1. All four prepare requests are scheduled concurrently (executable, compute, storage, data).
2. Compute prepare runs **before** storage has a volume ident.
3. Bind-mount resolution logs `Linker bean incomplete … skipping` and resolves **0 bind mounts**.
4. Compute container starts without `/input`.
5. Data download completes successfully into the (unmounted) volume.
6. Container stderr: `ERROR: INPUT '/input' does not exist` (exit code 2).
7. Session phase: `FAILED`.

---

## Failed sessions (evidence)

| Broker | Session UUID | Accepted at (UTC) | Data phase | Compute phase | Session phase |
|--------|--------------|-------------------|------------|---------------|---------------|
| Beta | `b5f44ef7-fea1-452e-9275-127689977ce8` | 2026-06-09 22:57:16 | COMPLETED | FAILED | FAILED |
| Alpha | `a99e0e66-3e0d-46a4-a752-521749fe5b81` | 2026-06-09 23:00:41 | COMPLETED | FAILED | FAILED |

Container stdout was empty in both cases. The failure message was captured in broker debug logs (stderr), not in the session API response.

---

## Log excerpts

### Beta — bind mount skipped (storage not ready)

```
2026-06-09 22:57:16 DEBUG ... beginPreparing() Scheduling [PREPARE] request for compute resource [86fe918b-...]
2026-06-09 22:57:16 DEBUG ... beginPreparing() Scheduling [PREPARE] request for storage resource [f2af8f6a-...]
2026-06-09 22:57:16 DEBUG ... beginPreparing() Scheduling [PREPARE] request for data resource [46d941d5-...]

2026-06-09 22:57:16 DEBUG ... getPrepareAction() Resolving volume mounts for compute resource [86fe918b-...]
2026-06-09 22:57:16 DEBUG ... DockerVolumeMountStorageEntity link() ... linking volume ident [null]
2026-06-09 22:57:16 WARN  ... getPrepareAction() Linker bean incomplete for volume mount [59dc8164-...], storage [AbstractStorageResourceEntity$HibernateProxy] - skipping
2026-06-09 22:57:16 DEBUG ... getPrepareAction() Resolved [0] bind mounts for compute resource [86fe918b-...]
```

### Beta — data download succeeds later

```
2026-06-09 22:57:17 DEBUG ... DockerVolumeMountStorageEntity process() Created Docker volume [be3db1fc...] for storage [f2af8f6a-...]
2026-06-09 22:57:17 DEBUG ... DockerSimpleDataHttpResourceEntity process() Starting helper container [...] to download [http://www.beespace.me/.../20250824_111206.jpg] into volume [be3db1fc...]
2026-06-09 22:57:19 DEBUG ... DockerSimpleDataHttpResourceEntity process() Helper container [...] completed successfully for data resource [46d941d5-...]
```

### Beta — container fails without mounted input

```
2026-06-09 22:57:19 DEBUG ... Container [...] stderr: [ERROR: INPUT '/input' does not exist
2026-06-09 22:57:19 DEBUG ... Post-processing component [86fe918b-...] next phase [FAILED] exit code [2]
```

### Alpha — same pattern

```
2026-06-09 23:00:42 DEBUG ... DockerVolumeMountStorageEntity link() ... linking volume ident [null]
2026-06-09 23:00:42 WARN  ... Linker bean incomplete for volume mount [6afd8e59-...] ... - skipping
2026-06-09 23:00:42 DEBUG ... Resolved [0] bind mounts for compute resource [56d222c2-...]
2026-06-09 23:00:44 DEBUG ... Helper container [...] completed successfully for data resource [02bc2f62-...]
2026-06-09 23:00:44 DEBUG ... Container [...] stderr: [ERROR: INPUT '/input' does not exist
```

---

## Root cause analysis

### Primary defect: prepare ordering / dependency handling

`PrepareSessionRequestEntity.beginPreparing()` schedules PREPARE for executable, compute, storage, and data **without enforcing dependencies**. The processing scheduler picks up compute prepare immediately.

Inside `DockerSimpleComputeResourceEntity.getPrepareAction()`:

1. Volume mount references a data resource.
2. Data resource references storage (`AbstractStorageResourceEntity$HibernateProxy`).
3. `DockerVolumeMountStorageEntity.link()` is called while `volume ident` is still **`null`** because storage prepare has not finished.
4. The linker is considered incomplete; the mount is **silently skipped** rather than deferred or retried.
5. Compute container is created and started with no bind mounts.

This is a **race condition**, not a request-validation error. Offer-set validation passes because the template structure is correct.

### Suggested dependency graph

```
storage (create volume)
    ↓
data (download into volume)
    ↓
compute (resolve bind mounts, start container with /input)
```

Executable prepare may proceed in parallel with storage/data, but **compute must not resolve mounts until storage has a volume ident and data staging is complete** (or at minimum until storage is AVAILABLE).

### Secondary observation: cgroup controller warning (non-fatal)

On both Alpha and Beta, the first container start attempt fails with:

```
crun: the requested cgroup controller `cpu` is not available: OCI runtime error
```

The broker retries without resource limits and the container then starts. This is **not** the cause of the checksum failure (the retry succeeds), but may warrant separate investigation in the demo Podman environment.

---

## Impact

- Any workload using `compute.volumes` backed by staged `data` resources is likely to fail intermittently or consistently, depending on scheduler ordering.
- Simple Docker workloads without volume mounts (e.g. `bin/broker compare` pi-calculator) are unaffected.
- Users see `FAILED` sessions with no useful stdout; the real error is only in broker debug logs.

---

## Suggested fixes

### Option A — enforce prepare dependencies (preferred)

In `PrepareSessionRequestEntity` or the processing scheduler:

- Do not schedule compute PREPARE until linked storage is at least `AVAILABLE` (volume ident assigned).
- Optionally wait until linked data resources are `AVAILABLE` before starting the compute container.

### Option B — defer bind-mount resolution

In `DockerSimpleComputeResourceEntity.getPrepareAction()`:

- If `DockerVolumeMountStorageEntity.link()` returns incomplete (null volume ident), **fail the prepare step with a retriable status** or re-queue compute prepare instead of skipping the mount and proceeding.

### Option C — two-phase compute prepare

1. **Phase 1:** wait for storage/data.
2. **Phase 2:** resolve bind mounts and create container.

### Option D — fail fast with a clear session message

If mounts cannot be resolved, mark compute FAILED during PREPARE with a user-visible message (e.g. `urn:volume-mount-unavailable`) rather than starting a container that cannot succeed.

---

## Verification / acceptance criteria

After fix, re-run the reproduction steps and confirm:

1. Log shows `Resolved [1] bind mounts` (or equivalent) for the compute resource.
2. No `Linker bean incomplete … skipping` warning during successful runs.
3. Container stdout contains JSON checksum output from `hashwrap`.
4. Session phase is `COMPLETED` (or `SUCCEEDED`, per broker terminology).
5. Regression: simple Docker workloads without volumes still work.

---

## Related files to inspect (broker codebase)

Likely Java classes (inferred from log class names):

| Class | Relevance |
|-------|-----------|
| `PrepareSessionRequestEntity` | Schedules concurrent PREPARE requests |
| `DockerSimpleComputeResourceEntity` | `getPrepareAction()`, bind-mount resolution |
| `DockerVolumeMountStorageEntity` | `link()`, `process()`, volume ident assignment |
| `DockerSimpleDataHttpResourceEntity` | HTTP download into volume |
| `UpdateSessionRequestEntity` | Session phase transitions on component failure |

---

## Workarounds (until fixed)

1. **Avoid volume mounts** — embed download logic in the container command (changes the execution model; not suitable for template fidelity).
2. **Manual re-accept** — unlikely to help; the race reproduces on every accept observed so far.
3. **Broker patch** — implement one of the suggested fixes above.

---

## References

- Execution template: https://github.com/Zarquan/Heliophorus-androcles/blob/main/ivoa-execution.yaml
- Heliophorus container expects `INPUT` default `/input`: https://github.com/Zarquan/Heliophorus-androcles/blob/main/bin/hashwrap.sh
- Demo broker skill: `agents/skills/calycopis-broker/SKILL.md`
