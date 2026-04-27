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

import java.time.Instant;

import net.ivoa.calycopis.engine.component.Component;

/**
 * Framework-independent interface for an execution session.
 *
 * This is the engine-layer foundation for the broker's
 * {@code net.ivoa.calycopis.datamodel.session.AbstractExecutionSession}.
 * It carries no dependency on Spring, calycopis-spring, or broker entity classes.
 */
public interface AbstractExecutionSession
    extends Component
    {
    /**
     * Get the expiry time for an OFFERED session.
     *
     */
    public Instant getExpires();
    }
