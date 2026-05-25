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
 *     "name": "ChatGPT",
 *     "contribution": {
 *       "value": 90,
 *       "units": "%"
 *       }
*     }
 *   ]
 *
 */
package net.ivoa.calycopis.broker.engine.functional.booking.compute.simple;

import java.time.Duration;

import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;

/**
 *
 *
 */
@Slf4j
public class SimpleComputeResourceOfferFactoryImpl
extends FactoryBaseImpl
implements SimpleComputeResourceOfferFactory
    {

    private final SimpleComputeResourceQueryHandler queryHandler ;
    
    /**
     * Public constructor.
     *
     */
    public SimpleComputeResourceOfferFactoryImpl(
        final SimpleComputeResourceQueryHandler queryHandler
        ){
        this.queryHandler = queryHandler ;
        }

    @Override
    public Iterable<SimpleComputeResourceOffer> generate(Interval requestStart, Duration requestDuration, Long requestMinCores, Long requestMemory, int requestLimit)
        {
        String query = SimpleComputeResourceQuery.build(
            requestStart,
            requestDuration,
            requestMinCores,
            requestMemory,
            requestLimit
            );
        
        return queryHandler.query(
            query
            );
        }
    }
