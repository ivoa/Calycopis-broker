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
 *     "timestamp": "2026-04-29T10:00:00",
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

package net.ivoa.calycopis.engine.processing.mock;

import net.ivoa.calycopis.engine.lifecycle.LifecycleComponent;

/**
 * Interface that a lifecycle component must implement to support the
 * mock monitor action's loop-count mechanism.
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.mock.MockMonitorableComponent}
 * interface.  It carries no dependency on Spring, calycopis-spring, or any
 * other framework-specific type.
 */
public interface MockMonitorableComponent
extends LifecycleComponent
    {

    /**
     * Get the remaining number of monitor loops for this component.
     *
     */
    int getLifecycleLoopCount();

    /**
     * Set the remaining number of monitor loops for this component.
     *
     */
    void setLifecycleLoopCount(int count);

    }
