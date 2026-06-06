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
#     "timestamp": "2026-06-06T01:36:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 100,
#       "units": "%"
#       }
#     },
#     {
#     "timestamp": "2026-06-06T02:00:00",
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
"""Resolve Docker image digests for broker submissions."""

import re
from pathlib import Path

from calycopis_schema_client.models import ComponentMetadata, DockerImageSpec, ExecutionRequest
from calycopis_schema_client.wrappers import DockerContainer

from broker_tools.client import get_brokers, make_client
from broker_tools.state import DIGEST_CACHE_PATH, load_digest_cache, save_digest_cache

LOG_PATHS = {
    "alpha": Path("run/alpha/logs/broker.log"),
    "beta": Path("run/beta/logs/broker.log"),
    "gamma": Path("run/gamma/logs/broker.log"),
}

LOCAL_ID_RE = re.compile(
    r"Image \[([^\]]+)\] found in local cache but digest does not match\. "
    r"Requested \[[^\]]+\], local id \[(sha256:[0-9a-f]+)\]"
)
CACHE_HIT_RE = re.compile(
    r"Image \[([^\]]+)\] found in local cache, id \[(sha256:[0-9a-f]+)\]"
)


def _parse_digest_from_log(image: str, log_path: Path) -> str | None:
    if not log_path.exists():
        return None

    text = log_path.read_text(encoding="utf-8", errors="replace")
    for match in reversed(list(LOCAL_ID_RE.finditer(text))):
        if match.group(1) == image:
            return match.group(2)

    for match in reversed(list(CACHE_HIT_RE.finditer(text))):
        if match.group(1) == image:
            return match.group(2)

    return None


def _probe_digest_via_submit(image: str, broker: str) -> str | None:
    """Submit a minimal request to surface a digest-mismatch message."""
    brokers = get_brokers()
    if broker not in brokers:
        raise RuntimeError(f"Unknown broker: {broker}")

    request = ExecutionRequest(
        executable=DockerContainer(
            meta=ComponentMetadata(name="digest-probe"),
            image=DockerImageSpec(
                locations=[image],
                digest="sha256:0000000000000000000000000000000000000000000000000000000000000000",
            ),
            command=["echo", "probe"],
        ),
    )
    client = make_client(brokers[broker])
    response = client.submit_execution(request, follow_redirect=True)

    if response.meta and response.meta.messages:
        for message in response.meta.messages:
            template = getattr(message, "template", "") or ""
            values = getattr(message, "values", None)
            if "digest does not match" in template and values:
                for value in values:
                    if isinstance(value, str) and value.startswith("sha256:"):
                        return value

    return _parse_digest_from_log(image, LOG_PATHS.get(broker, LOG_PATHS["alpha"]))


def resolve_digest(image: str, broker: str = "alpha", cache_path: Path = DIGEST_CACHE_PATH) -> str:
    """Resolve the digest for an image tag cached on the brokers."""
    cache = load_digest_cache(cache_path)
    if image in cache:
        return cache[image]

    digest = _parse_digest_from_log(image, LOG_PATHS.get(broker, LOG_PATHS["alpha"]))
    if not digest:
        digest = _probe_digest_via_submit(image, broker)

    if not digest:
        raise RuntimeError(
            f"Could not resolve digest for image [{image}]. "
            f"Ensure brokers are deployed and the image is cached."
        )

    cache[image] = digest
    save_digest_cache(cache_path, cache)
    return digest
