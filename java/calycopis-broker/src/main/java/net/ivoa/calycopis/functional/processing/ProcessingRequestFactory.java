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

package net.ivoa.calycopis.functional.processing;

import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.session.SessionProcessingRequestFactory;

/**
 * Broker-layer processing request factory interface.
 *
 * Extends the engine's {@link net.ivoa.calycopis.engine.processing.ProcessingRequestFactory}
 * (which provides the basic {@code delete()} contract) and adds broker-specific
 * factory accessors for session and component request factories.
 *
 * The engine interface requires {@code delete(engine.ProcessingRequest)}.  The broker
 * types use the broker-layer {@code ProcessingRequest}.  A default bridge method is
 * provided so that concrete broker implementations only need to implement the
 * broker-typed {@code delete(ProcessingRequest)} method.
 *
 */
public interface ProcessingRequestFactory
extends net.ivoa.calycopis.engine.processing.ProcessingRequestFactory
    {

    /**
     * Delete a completed broker-layer processing request.
     *
     */
    public void delete(final ProcessingRequest request);

    /**
     * Bridge: satisfy the engine interface by delegating to the broker-typed
     * {@link #delete(ProcessingRequest)} method.
     *
     */
    @Override
    default void delete(final net.ivoa.calycopis.engine.processing.ProcessingRequest request)
        {
        if (request instanceof ProcessingRequest brokerRequest)
            {
            delete(brokerRequest);
            }
        else {
            throw new IllegalArgumentException(
                "Unexpected ProcessingRequest implementation [" + request.getClass().getName() + "]"
                );
            }
        }

    /**
     * Get the SessionProcessingRequestFactory for this platform.
     *
     */
    public SessionProcessingRequestFactory getSessionProcessingRequestFactory();
    
    /**
     * Get the ComponentProcessingRequestFactory for this platform.
     *
     */
    public ComponentProcessingRequestFactory getComponentProcessingRequestFactory();
    
    }
