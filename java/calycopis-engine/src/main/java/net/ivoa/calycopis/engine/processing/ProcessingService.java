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

package net.ivoa.calycopis.engine.processing;

import java.net.URI;
import java.util.List;

import net.ivoa.calycopis.engine.factory.FactoryBase;

/**
 * Framework-independent interface for the processing service.
 *
 * Defines the contract that the scheduler must satisfy: knowing which request
 * kinds it handles ({@link #getKinds()}) and having a stable identity
 * ({@link #getUuid()} from {@link FactoryBase}).
 *
 * Spring-specific concerns (@Service, @Scheduled) are left to the broker-layer
 * concrete implementation ({@code ProcessingServiceImpl}).
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.ProcessingService} interface.
 * It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
public interface ProcessingService
extends FactoryBase
    {

    /**
     * Get the list of request kind URIs that this service processes.
     *
     */
    public List<URI> getKinds();

    }
