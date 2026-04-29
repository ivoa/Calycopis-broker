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

import net.ivoa.calycopis.engine.factory.FactoryBase;

/**
 * Framework-independent interface for the processing request factory.
 *
 * Defines the minimum contract required by the engine's {@link ProcessingRequestEntity}
 * to delete a completed request.  The broker-layer extension adds session- and
 * component-specific factory methods that return broker entity types, and provides
 * a bridge default method that adapts the engine-typed {@code delete(ProcessingRequest)}
 * call to the broker's concrete repository-backed deletion logic.
 *
 * Concrete implementations live in the broker module
 * ({@code ProcessingRequestFactoryImpl}) and use a JPA repository to remove the
 * entity from the database.  The engine interface carries no dependency on Spring,
 * calycopis-spring, or any other framework-specific type.
 */
public interface ProcessingRequestFactory
extends FactoryBase
    {

    /**
     * Delete a completed processing request.
     *
     */
    public void delete(final ProcessingRequest request);

    }
