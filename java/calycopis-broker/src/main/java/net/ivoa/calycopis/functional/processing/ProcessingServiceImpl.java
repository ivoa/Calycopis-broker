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

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.platfom.Platform;

/**
 * 
 */
@Slf4j
@Service
public abstract class ProcessingServiceImpl
extends FactoryBaseImpl
implements ProcessingService
    {

    private final Platform platform;
    private final ProcessingRequestFactory processing;     

    // TODO Pass the inner component in via constructor.
    protected ProcessingServiceImpl(final ProcessingRequestFactory processing, final Platform platform)
        {
        super();
        this.processing = processing;
        this.platform = platform;
        }

    @Autowired
    private TransactionalInner inner ;

    public void loop()
        {
        log.info("++++++++");
        log.debug("Starting processing loop [{}]", this.getUuid());
        UUID requestId = inner.getNext(this) ;
        while (requestId != null)
            {
            log.debug("Pre-processing request [{}]", requestId);
            ProcessingAction action = inner.preProcess(    
                this,
                requestId
                );
            if (action != null)
                {
                log.debug("Processing action [{}] for request [{}]", action.getClass().getSimpleName(), requestId);
                action.process();
                }
            else {
                log.debug("No action for request [{}]", requestId);
                }

            log.debug("Post-processing request [{}]", requestId);
            inner.postProcess(    
                this,
                requestId,
                action
                );
            
            requestId = inner.getNext(this) ;
            }
        log.debug("Exiting processing loop [{}]", this.getUuid());
        log.info("--------");
        }
    
    /**
     * Inner class marked as @Service so that the @Transactional annotations are processed
     * when it is loaded by the Spring framework.
     * 
     */
    @Service
    public static class TransactionalInner
        {
        @Autowired
        private ProcessingRequestRepository requestRepository;

        TransactionalInner()
            {
            super();
            }
        
        /**
         * Inner method to get the next request in a transaction.
         * 
         */
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        protected UUID getNext(final ProcessingService service)
            {
            // Find the next request with this service ID.
            log.debug("Finding next request for service [{}]", service.getUuid());
            UUID found = this.requestRepository.selectNextRequest(
                service.getUuid(),
                service.getKinds()
                );
            // If we didn't find an active request, try to claim one.
            if (found == null)
                {
                log.debug("No requests found for service [{}]", service.getUuid());
                // Claim the next available request.
                log.debug("Checking for requests for service [{}]", service.getUuid());
                int count = this.requestRepository.updateNextRequest(
                    service.getUuid(),
                    service.getKinds()
                    ) ;
                // Load the claimed request.
                log.debug("[{}] requests claimed for service [{}]", count, service.getUuid());
                if (count > 0)
                    {
                    log.debug("Finding next request for service [{}]", service.getUuid());
                    found = this.requestRepository.selectNextRequest(
                        service.getUuid(),
                        service.getKinds()
                        ) ;
                    }
                }

            if (found != null)
                {
                log.debug("Found request [{}] for service [{}]", found, service.getUuid());
                }
            else {
                log.debug("No requests found for service [{}]", service.getUuid());
                }
            
            return found ;
            }
        
        /**
         * Inner method to pre-process the request in a transaction.
         * 
         */
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        protected ProcessingAction preProcess(final ProcessingServiceImpl outer, final UUID requestId)
            {
            ProcessingRequestEntity request = this.requestRepository.findById(requestId).orElseThrow();
            log.debug("Service [{}] inner pre-processing request [{}][{}]", outer.getUuid(), request.getUuid(), request.getClass().getSimpleName());
            return outer.preProcess(
                request
                );
            }

        /**
         * Inner method to post-process the request in a transaction.
         * 
         */
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        protected void postProcess(final ProcessingServiceImpl outer, final UUID requestId, final ProcessingAction action)
            {
            ProcessingRequestEntity request = this.requestRepository.findById(requestId).orElseThrow();
            log.debug("Service [{}] inner post-processing request [{}][{}]", outer.getUuid(), request.getUuid(), request.getClass().getSimpleName());
            outer.postProcess(
                request,
                action
                );
            request.setService(null);
            }
        }

    /**
     * Outer pre-process method that can be overridden if needed.
     * 
     */
    protected ProcessingAction preProcess(final ProcessingRequestEntity request)
        {
        log.debug("Service [{}] outer pre-processing request [{}][{}]", this.getUuid(), request.getUuid(), request.getClass().getSimpleName());
        return request.preProcess(
            this.processing,
            this.platform
            );
        }

    /**
     * Outer post-process method that can be overridden if needed.
     * 
     */
    protected void postProcess(final ProcessingRequestEntity request, ProcessingAction action)
        {
        log.debug("Service [{}] outer post-processing request [{}][{}]", this.getUuid(), request.getUuid(), request.getClass().getSimpleName());
        request.postProcess(
            this.processing,
            this.platform,
            action
            );
        }
    }
