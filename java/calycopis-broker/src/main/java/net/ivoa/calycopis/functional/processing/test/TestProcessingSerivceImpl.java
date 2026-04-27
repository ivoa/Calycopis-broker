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

package net.ivoa.calycopis.functional.processing.test;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.functional.processing.ProcessingService;
import net.ivoa.calycopis.functional.processing.ProcessingServiceImpl;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.functional.processing.session.SessionProcessingRequest;

/**
 * 
 */
@Slf4j
@Service
public class TestProcessingSerivceImpl
extends ProcessingServiceImpl
implements ProcessingService
    {

    @Autowired
    public TestProcessingSerivceImpl(final ProcessingRequestFactory processing, final Platform platform)
        {
        super(
            processing,
            platform
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
