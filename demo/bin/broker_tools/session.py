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
"""Accept offers and monitor execution sessions."""

from uuid import UUID

from calycopis_schema_client.models import SimpleExecutionSessionPhase

from broker_tools.client import get_brokers, make_client
from broker_tools.output import get_container_stdout


def accept_and_monitor(
    broker: str,
    session_uuid: str,
    timeout: float = 300.0,
    interval: float = 5.0,
) -> dict:
    """Accept an offer and wait until the session reaches a terminal phase."""
    brokers = get_brokers()
    if broker not in brokers:
        raise RuntimeError(f"Unknown broker: {broker}")

    client = make_client(brokers[broker])
    client.set_session_phase(UUID(session_uuid), SimpleExecutionSessionPhase.ACCEPTED)
    session = client.wait_until_terminal(UUID(session_uuid), timeout=timeout, interval=interval)

    summary = session_summary(session)
    summary["stdout"] = get_container_stdout(broker, session_uuid)
    return summary


def session_summary(session) -> dict:
    """Build a concise summary from a session object."""
    result = {
        "phase": str(getattr(session, "phase", None)),
        "session_uuid": str(session.meta.uuid) if session.meta else None,
        "messages": [],
        "connectors": [],
        "executable_phase": None,
    }

    if session.meta and session.meta.messages:
        for message in session.meta.messages:
            result["messages"].append(
                {
                    "kind": getattr(message, "kind", None),
                    "level": getattr(message, "level", None),
                    "message": getattr(message, "message", None)
                    or getattr(message, "template", None),
                }
            )

    if session.connectors:
        for connector in session.connectors:
            entry = {"kind": getattr(connector, "kind", None)}
            if hasattr(connector, "meta") and connector.meta:
                entry["name"] = getattr(connector.meta, "name", None)
                entry["url"] = getattr(connector.meta, "url", None)
            result["connectors"].append(entry)

    if session.executable:
        result["executable_phase"] = str(getattr(session.executable, "phase", None))

    return result
