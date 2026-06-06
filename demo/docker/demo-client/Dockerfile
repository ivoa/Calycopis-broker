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
#     along with this program.  If not, see <http://www.gnu.org/licenses/>.
#   </meta:licence>
# </meta:header>
#
# AIMetrics: [
#     {
#     "timestamp": "2026-06-03T03:47:00",
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
# Demo client container with Python, calycopis_schema_client,
# and tools for interacting with the three demo brokers.

FROM docker.io/library/fedora:42

RUN dnf install -y \
    python3 \
    python3-pip \
    curl \
    jq \
    pwgen \
    && dnf clean all

COPY build/calycopis_schema_client-1.0.6-py3-none-any.whl /tmp/
RUN pip3 install /tmp/calycopis_schema_client-1.0.6-py3-none-any.whl \
    && rm /tmp/calycopis_schema_client-1.0.6-py3-none-any.whl

# Install Cursor CLI
RUN curl https://cursor.com/install -fsS | bash \
    && ln -sf "${HOME}/.local/bin/cursor-agent" /usr/local/bin/cursor-agent

WORKDIR /workspace

CMD ["/bin/bash"]
