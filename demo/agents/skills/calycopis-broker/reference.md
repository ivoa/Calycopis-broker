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
