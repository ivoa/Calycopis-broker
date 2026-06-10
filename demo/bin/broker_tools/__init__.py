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
#     },
#     {
#     "timestamp": "2026-06-09T21:42:00",
#     "name": "Cursor CLI",
#     "version": "2026.02.13-41ac335",
#     "model": "Claude 4.6 Opus (Thinking)",
#     "contribution": {
#       "value": 20,
#       "units": "%"
#       }
#     }
#   ]
#
"""Shared helpers for Calycopis Execution Broker demo clients."""

from broker_tools.builder import (
    build_execution_request,
    find_abstract_elements,
    format_request_as_dict,
    format_request_yaml,
    load_execution_template,
    resolve_abstract_interactively,
)
from broker_tools.client import BROKERS, BROKER_LABELS, get_brokers, get_env, make_client
from broker_tools.digest import resolve_digest
from broker_tools.offers import extract_summary, format_comparison_table, submit_to_all
from broker_tools.request import build_docker_request, parse_cores
from broker_tools.session import accept_and_monitor, session_summary
from broker_tools.state import DEFAULT_STATE_PATH, load_state, save_state

__all__ = [
    "BROKERS",
    "BROKER_LABELS",
    "get_brokers",
    "get_env",
    "make_client",
    "resolve_digest",
    "extract_summary",
    "format_comparison_table",
    "submit_to_all",
    "build_docker_request",
    "build_execution_request",
    "find_abstract_elements",
    "format_request_as_dict",
    "format_request_yaml",
    "load_execution_template",
    "resolve_abstract_interactively",
    "parse_cores",
    "accept_and_monitor",
    "session_summary",
    "DEFAULT_STATE_PATH",
    "load_state",
    "save_state",
]
