/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
 *
 *     This information is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This information is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-04-27T13:46:34",
 *     "name": "Copilot",
 *     "version": "unknown",
 *     "model": "claude-sonnet-4.5",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.engine.session;

/**
 * Lifecycle phase for an execution session.
 *
 * This enum replaces IvoaSimpleExecutionSessionPhase from the calycopis-spring
 * generated schema package, removing the dependency on that external artefact
 * from the engine layer.
 *
 * Values must remain string-identical to those in
 * IvoaSimpleExecutionSessionPhase so that the JPA
 * {@code @Enumerated(EnumType.STRING)} column stores the same values as the
 * previous schema type and existing database rows remain valid.
 */
public enum ExecutionSessionPhase
    {
    INITIAL,
    WAITING,
    OFFERED,
    ACCEPTED,
    REJECTED,
    EXPIRED,
    PREPARING,
    AVAILABLE,
    RUNNING,
    RELEASING,
    COMPLETED,
    CANCELLED,
    FAILED;

    /**
     * Parse a phase value from its string representation.
     * Accepts the same string values as those returned by {@link #name()}.
     *
     * @throws IllegalArgumentException if the value does not match any phase.
     */
    public static ExecutionSessionPhase fromValue(final String value)
        {
        return ExecutionSessionPhase.valueOf(value);
        }
    }
