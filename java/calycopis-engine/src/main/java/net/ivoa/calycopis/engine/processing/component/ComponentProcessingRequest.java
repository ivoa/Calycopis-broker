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

package net.ivoa.calycopis.engine.processing.component;

import java.net.URI;

import net.ivoa.calycopis.engine.lifecycle.LifecycleComponent;
import net.ivoa.calycopis.engine.platform.Platform;
import net.ivoa.calycopis.engine.processing.ProcessingRequest;
import net.ivoa.calycopis.engine.processing.ProcessingRequestFactory;

/**
 * Framework-independent interface for a processing request targeting a single
 * lifecycle component (executable, compute, storage, data, volume).
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest}
 * interface.  It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
public interface ComponentProcessingRequest
extends ProcessingRequest
    {

    /**
     * Kind URI for component-level processing requests.
     *
     */
    public static final URI KIND = URI.create("urn:ivoa.calycopis.processing.component-processing-request");

    /**
     * Get the lifecycle component targeted by this request.
     *
     */
    public LifecycleComponent getComponent(final Platform platform);

    /**
     * Perform component-specific post-processing after the action completes.
     *
     */
    public void postProcess(final ProcessingRequestFactory processing, final Platform platform, final ComponentProcessingAction action);

    }
