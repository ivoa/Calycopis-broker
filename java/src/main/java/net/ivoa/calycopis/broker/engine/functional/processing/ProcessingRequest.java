/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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
 *
 */

package net.ivoa.calycopis.broker.engine.functional.processing;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.functional.platform.Platform;

/**
 * 
 */
public interface ProcessingRequest
    {

    /**
     * URI identifier for the type of request.
     * 
     */
    public URI  getKind();
    
    /**
     * The unique identifier for this request.
     *
     */
    public UUID getUuid();

    /**
     * The unique identifier for the service handling this request.
     * This is used by a service to claim ownership of a request while it is processing it.
     * Preventing another service from processing the same request at the same time.
     *
     */
    public UUID getService();
    
    /**
     * The date/time when the request was created.
     *
     */
    public Instant getCreated();
    
    /**
     * The date/time when the request was modified.
     *
     */
    public Instant getModified();

    /**
     * The date/time when the request should be processed.
     *
     */
    public Instant getActivationTime();

    /**
     * Perform the pre-processing steps needed to create an action.
     * This pre-processing step is performed inside a database transaction, with giving it access to the database state.
     * @return A ProcessingAction that will be performed after the database transaction has been closed.
     * 
     */
    public ProcessingAction preProcess(final Platform platform);

    /**
     * Perform the post-processing steps needed to save the results of an action.
     * This post-processing step is performed inside a database transaction, with giving it access to the database state.
     *
     */
    public void postProcess(final Platform platform, final ProcessingAction action);
    
    }
