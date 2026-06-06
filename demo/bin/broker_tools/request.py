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
"""Build ExecutionRequest objects from simple parameters."""

from calycopis_schema_client.models import ComponentMetadata, DockerImageSpec, ExecutionRequest
from calycopis_schema_client.wrappers import DockerContainer, SimpleComputeResource

from broker_tools.digest import resolve_digest


def parse_cores(spec: str | None) -> dict[str, int] | None:
    """Parse a cores spec like '1:2' into min/max dict."""
    if not spec:
        return None
    parts = spec.split(":")
    if len(parts) != 2:
        raise ValueError(f"Invalid cores spec '{spec}', expected min:max")
    return {"min": int(parts[0]), "max": int(parts[1])}


def build_docker_request(
    name: str,
    image: str,
    command: list[str],
    cores: dict[str, int] | None = None,
    digest: str | None = None,
    resolve: bool = True,
) -> ExecutionRequest:
    """Build a Docker-based ExecutionRequest."""
    image_digest = digest
    if image_digest is None and resolve:
        image_digest = resolve_digest(image)

    if not image_digest:
        raise RuntimeError(
            f"Image digest is required for [{image}]. "
            "Run 'bin/broker digest resolve' or pass --digest."
        )

    executable = DockerContainer(
        meta=ComponentMetadata(name=name),
        image=DockerImageSpec(locations=[image], digest=image_digest),
        command=command,
    )

    compute = None
    if cores:
        compute = SimpleComputeResource(
            meta=ComponentMetadata(name="compute-001"),
            cores=cores,
        )

    return ExecutionRequest(executable=executable, compute=compute)
