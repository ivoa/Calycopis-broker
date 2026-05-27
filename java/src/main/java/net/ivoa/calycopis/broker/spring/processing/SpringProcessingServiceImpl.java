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
 *       "value": 15,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-05-21T10:54:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.spring.processing;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.functional.platfom.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingService;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingServiceImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequest;

/**
 * 
 */
@Slf4j
@Service
public class SpringProcessingServiceImpl
extends ProcessingServiceImpl
implements ProcessingService
    {

    /**
     * Public constructor used by our Platform.
     *
     */
    @Autowired
    public SpringProcessingServiceImpl(final Platform platform)
        {
        super(
            platform,
            platform.getProcessingTransactionHandler()
            );
        }

    @Override
    public List<URI> getKinds()
        {
        return List.of(
            SessionProcessingRequest.KIND,
            ComponentProcessingRequest.KIND
            );
        }

    @Override
    @Scheduled(fixedDelay = 1000)
    public void loop()
        {
        super.loop();
        }
    
    }
