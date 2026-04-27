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

import java.net.URI;

/**
 * Framework-independent interface for a simple execution session.
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSession}.
 * It carries no dependency on Spring, calycopis-spring, or broker entity classes.
 *
 * The lifecycle timing methods are inherited from
 * {@link net.ivoa.calycopis.engine.lifecycle.LifecycleComponent}; only
 * session-specific members are declared here.
 */
public interface SimpleExecutionSession
    extends AbstractExecutionSession
    {
    /**
     * The type discriminator URI for a simple execution session.
     *
     */
    public static final URI TYPE_DISCRIMINATOR = URI.create(
        "https://www.purl.org/ivoa.net/EB/schema/v1.0/types/session/simple-execution-session-1.0"
        );

    /**
     * Get the current execution session phase.
     *
     */
    public ExecutionSessionPhase getPhase();

    /**
     * Set the execution session phase.
     *
     */
    public void setPhase(final ExecutionSessionPhase phase);
    }
