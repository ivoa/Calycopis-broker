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

package net.ivoa.calycopis.broker.mapping;

import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;
import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;

/**
 * Mapping utilities between the engine's LifecyclePhase enum and the
 * calycopis-spring generated LifecyclePhase enum.
 *
 * The enum values have the same names, so conversion is done by name.
 */
public final class LifecyclePhaseMappings
    {
    private LifecyclePhaseMappings()
        {
        }

    /**
     * Convert an engine LifecyclePhase to the calycopis-spring LifecyclePhase.
     * Used when populating API response beans.
     *
     */
    public static LifecyclePhase toIvoa(final LifecyclePhase phase)
        {
        if (phase == null)
            {
            return null;
            }
        return LifecyclePhase.valueOf(
            phase.name()
            );
        }

    /**
     * Convert a calycopis-spring LifecyclePhase to the engine LifecyclePhase.
     * Used when processing incoming API requests.
     *
     */
    public static LifecyclePhase fromIvoa(final LifecyclePhase phase)
        {
        if (phase == null)
            {
            return null;
            }
        return LifecyclePhase.valueOf(
            phase.name()
            );
        }
    }
