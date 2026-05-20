/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
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
 *     along with this software. If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-05-20T14:34:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.spring;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestEntity;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestRepository;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingService;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingServiceImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingTransactionHandler;

/**
 * Spring-specific implementation of ProcessingTransactionHandler.
 * Each method runs in its own independent transaction via @Transactional(REQUIRES_NEW).
 *
 */
@Slf4j
@Service
public class SpringProcessingTransactionHandlerImpl
implements ProcessingTransactionHandler
    {

    @Autowired
    private ProcessingRequestRepository requestRepository;

    /**
     * 
     */
    public SpringProcessingTransactionHandlerImpl()
        {
        super();
        }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getNext(final ProcessingService service)
        {
        log.debug("Finding next request for service [{}]", service.getUuid());
        UUID found = this.requestRepository.selectNextRequest(
            service.getUuid(),
            service.getKinds()
            );
        if (found == null)
            {
            log.debug("No requests found for service [{}]", service.getUuid());
            log.debug("Checking for requests for service [{}]", service.getUuid());
            int count = this.requestRepository.updateNextRequest(
                service.getUuid(),
                service.getKinds()
                );
            log.debug("[{}] requests claimed for service [{}]", count, service.getUuid());
            if (count > 0)
                {
                log.debug("Finding next request for service [{}]", service.getUuid());
                found = this.requestRepository.selectNextRequest(
                    service.getUuid(),
                    service.getKinds()
                    );
                }
            }

        if (found != null)
            {
            log.debug("Found request [{}] for service [{}]", found, service.getUuid());
            }
        else {
            log.debug("No requests found for service [{}]", service.getUuid());
            }

        return found;
        }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessingAction preProcess(final ProcessingServiceImpl outer, final UUID requestId)
        {
        ProcessingRequestEntity request = this.requestRepository.findById(requestId).orElseThrow();
        log.debug("Service [{}] inner pre-processing request [{}][{}]", outer.getUuid(), request.getUuid(), request.getClass().getSimpleName());
        return outer.preProcess(
            request
            );
        }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void postProcess(final ProcessingServiceImpl outer, final UUID requestId, final ProcessingAction action)
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
