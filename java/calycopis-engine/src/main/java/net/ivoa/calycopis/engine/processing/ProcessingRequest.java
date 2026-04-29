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
import java.time.Instant;
import java.util.UUID;

import net.ivoa.calycopis.engine.platform.Platform;

/**
 * Framework-independent interface for a processing request.
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.ProcessingRequest} interface.
 * It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
public interface ProcessingRequest
    {

    /**
     * Get the kind URI that identifies the type of this request.
     *
     */
    public URI getKind();

    /**
     * Get the unique identifier for this request.
     *
     */
    public UUID getUuid();

    /**
     * Get the service identifier that has claimed this request, or {@code null} if unclaimed.
     *
     */
    public UUID getService();

    /**
     * Get the instant at which this request was created.
     *
     */
    public Instant getCreated();

    /**
     * Get the instant at which this request was last modified.
     *
     */
    public Instant getModified();

    /**
     * Get the instant at which this request should next be activated.
     *
     */
    public Instant getActivationTime();

    /**
     * Perform the pre-processing step for this request (called inside a transaction).
     *
     */
    public ProcessingAction preProcess(final ProcessingRequestFactory processing, final Platform platform);

    /**
     * Perform the post-processing step for this request (called inside a transaction).
     *
     */
    public void postProcess(final ProcessingRequestFactory processing, final Platform platform, final ProcessingAction action);

    }
