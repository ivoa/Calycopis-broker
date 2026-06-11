/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
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
 *     along with this software. If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-06-03T03:47:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.functional.platform;

import java.util.List;

/**
 * Framework-neutral interface for costs and metrics configuration.
 * Provides externally configurable cost/metric values that platforms
 * attach to session and compute resource entities during offer building.
 *
 */
public interface CostsAndMetricsSettings
    {

    /**
     * A single cost or metric item with type URI, description, and min/max values.
     *
     */
    public interface Item
        {
        public String getType();
        public String getDescription();
        public Float getMin();
        public Float getMax();
        }

    /**
     * A group of costs and metrics for a particular component type.
     *
     */
    public interface ComponentConfig
        {
        public List<? extends Item> getCosts();
        public List<? extends Item> getMetrics();
        }

    /**
     * Get the session-level cost/metric configuration.
     *
     */
    public ComponentConfig getSession();

    /**
     * Get the compute-resource-level cost/metric configuration.
     *
     */
    public ComponentConfig getCompute();

    }
