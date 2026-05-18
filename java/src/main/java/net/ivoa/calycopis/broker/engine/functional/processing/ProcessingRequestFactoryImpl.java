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

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactory;

/**
 * 
 */
@Slf4j
public class ProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements ProcessingRequestFactory
    {

    private final ProcessingRequestRepository processingRequestRepository;

    /**
     * Public constructor, used by our Platform.
     * 
     */
    public ProcessingRequestFactoryImpl(
        final ProcessingRequestRepository processingRequestRepository,
        final SessionProcessingRequestFactory sessionProcessingRequestFactory,
        final ComponentProcessingRequestFactory componentProcessingRequestFactory
        ){
        super();
        this.processingRequestRepository = processingRequestRepository;
        this.sessionProcessingRequestFactory = sessionProcessingRequestFactory;
        this.componentProcessingRequestFactory = componentProcessingRequestFactory;
        }

    // TODO Implement a two step deletion process, where we first mark a request as deleted, and then delete it after a delay.
    @Override
    public void delete(final ProcessingRequest request)
        {
        log.debug("Deleting ProcessingRequest [{}]", request.getUuid());
        if (request instanceof ProcessingRequestEntity)
            {
            processingRequestRepository.delete(
                (ProcessingRequestEntity)request
                );  
            }
        else {
            throw new IllegalArgumentException(
                "Unexpected request implementation [{" +request.getClass().getName() + "}]" 
                );
            }
        }

    private final SessionProcessingRequestFactory sessionProcessingRequestFactory;
    @Override
    public SessionProcessingRequestFactory getSessionProcessingRequestFactory()
        {
        return this.sessionProcessingRequestFactory;
        }
    
    private final ComponentProcessingRequestFactory componentProcessingRequestFactory;
    @Override
    public ComponentProcessingRequestFactory getComponentProcessingRequestFactory()
        {
        return this.componentProcessingRequestFactory;
        }
    }
