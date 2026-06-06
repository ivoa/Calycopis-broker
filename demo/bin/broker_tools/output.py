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
"""Extract container output from broker logs."""

import re
from pathlib import Path

LOG_PATHS = {
    "alpha": Path("run/alpha/logs/broker.log"),
    "beta": Path("run/beta/logs/broker.log"),
    "gamma": Path("run/gamma/logs/broker.log"),
}

STDOUT_RE = re.compile(r"stdout: \[(.*?)\]", re.DOTALL)


def get_container_stdout(broker: str, session_uuid: str) -> str | None:
    """Return container stdout captured in broker logs for a session."""
    log_path = LOG_PATHS.get(broker)
    if not log_path or not log_path.exists():
        return None

    text = log_path.read_text(encoding="utf-8", errors="replace")
    session_marker = f"session [{session_uuid}]"
    start = text.find(session_marker)
    if start == -1:
        return None

    chunk = text[start:]
    matches = list(STDOUT_RE.finditer(chunk))
    if not matches:
        return None

    raw = matches[-1].group(1)
    return raw.replace("\\\\\n", "").replace("\\\n", "").strip()
