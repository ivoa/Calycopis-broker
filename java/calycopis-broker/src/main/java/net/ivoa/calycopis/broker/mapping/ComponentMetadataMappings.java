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

import net.ivoa.calycopis.engine.component.ComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;

/**
 * Mapping utilities between the engine's ComponentMetadata POJO and the
 * calycopis-spring generated IvoaComponentMetadata.
 */
public final class ComponentMetadataMappings
    {
    private ComponentMetadataMappings()
        {
        }

    /**
     * Convert a calycopis-spring IvoaComponentMetadata to an engine
     * ComponentMetadata. Used when constructing entity objects from API request
     * data.
     *
     */
    public static ComponentMetadata fromIvoa(final IvoaComponentMetadata ivoa)
        {
        if (ivoa == null)
            {
            return new ComponentMetadata();
            }
        return new ComponentMetadata(
            ivoa.getName(),
            ivoa.getDescription()
            );
        }
    }
