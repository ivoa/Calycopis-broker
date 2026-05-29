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

package net.ivoa.calycopis.broker.engine.functional.processing;

import java.util.UUID;

/**
 * Framework-neutral interface for transactional processing of requests.
 * Each method is expected to execute within its own independent transaction.
 *
 */
public interface ProcessingTransactionHandler
    {

    /**
     * Find and claim the next available processing request for the given service.
     * 
     */
    public UUID getNext(ProcessingService service);

    /**
     * Pre-process a request within a transaction, returning an action to be
     * executed outside the transaction.
     * 
     */
    public ProcessingAction preProcess(ProcessingServiceImpl outer, UUID requestId);

    /**
     * Post-process a request within a transaction after the action has completed.
     * 
     */
    public void postProcess(ProcessingServiceImpl outer, UUID requestId, ProcessingAction action);

    }
