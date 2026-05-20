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
 *     "timestamp": "2026-05-20T14:34:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 40,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.functional.processing;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.platfom.Platform;

/**
 * 
 */
@Slf4j
public abstract class ProcessingServiceImpl
extends FactoryBaseImpl
implements ProcessingService
    {

    private final Platform platform;
    private final ProcessingTransactionHandler transactionHandler;

    /**
     * 
     */
    protected ProcessingServiceImpl(
        final Platform platform,
        final ProcessingTransactionHandler transactionHandler
        ){
        super();
        this.platform = platform;
        this.transactionHandler = transactionHandler;
        }

    public void loop()
        {
        log.info("++++++++");
        log.debug("Starting processing loop [{}]", this.getUuid());
        UUID requestId = transactionHandler.getNext(this) ;
        while (requestId != null)
            {
            log.debug("Pre-processing request [{}]", requestId);
            ProcessingAction action = transactionHandler.preProcess(    
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
            transactionHandler.postProcess(    
                this,
                requestId,
                action
                );
            
            requestId = transactionHandler.getNext(this) ;
            }
        log.debug("Exiting processing loop [{}]", this.getUuid());
        log.info("--------");
        }

    /**
     * Pre-process method that can be overridden if needed.
     * Called by the ProcessingTransactionHandler within a transaction.
     * 
     */
    public ProcessingAction preProcess(final ProcessingRequestEntity request)
        {
        log.debug("Service [{}] outer pre-processing request [{}][{}]", this.getUuid(), request.getUuid(), request.getClass().getSimpleName());
        return request.preProcess(
            this.platform
            );
        }

    /**
     * Post-process method that can be overridden if needed.
     * Called by the ProcessingTransactionHandler within a transaction.
     * 
     */
    public void postProcess(final ProcessingRequestEntity request, ProcessingAction action)
        {
        log.debug("Service [{}] outer post-processing request [{}][{}]", this.getUuid(), request.getUuid(), request.getClass().getSimpleName());
        request.postProcess(
            this.platform,
            action
            );
        }
    }
