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

import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactory;

/**
 * 
 */
public interface ProcessingRequestFactory
extends FactoryBase
    {

    /**
     * Delete a request.
     * 
     */
    public void delete(final ProcessingRequest request);

    /**
     * Get the SessionProcessingRequestFactory for this Platform.
     * TODO Move this to Platform.
     *
     */
    public SessionProcessingRequestFactory getSessionProcessingRequestFactory();
    
    /**
     * Get the ComponentProcessingRequestFactory for this Platform.
     * TODO Move this to Platform.
     *
     */
    public ComponentProcessingRequestFactory getComponentProcessingRequestFactory();
    
    }
