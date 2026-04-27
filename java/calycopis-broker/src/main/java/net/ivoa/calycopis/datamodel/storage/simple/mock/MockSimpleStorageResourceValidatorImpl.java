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
 * AIMetrics: [
 *     {
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

package net.ivoa.calycopis.datamodel.storage.simple.mock;

import java.util.Map;

import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResourceValidatorImpl;
import net.ivoa.calycopis.spring.model.IvoaSimpleStorageResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleStorageSize;

/**
 * 
 */
public class MockSimpleStorageResourceValidatorImpl
extends SimpleStorageResourceValidatorImpl
implements MockSimpleStorageResourceValidator
    {

    public MockSimpleStorageResourceValidatorImpl(final AbstractStorageResourceEntityFactory entityFactory)
        {
        super(entityFactory);
        }

    public static final Long MIN_SIZE_DEFAULT =    1L ;
    public static final Long MAX_SIZE_LIMIT   = 1000L ;

    @Override
    protected boolean validateSize(
        IvoaSimpleStorageResource requested,
        IvoaSimpleStorageResource validated,
        OfferSetRequestParserContext context
        ){
        boolean success = true ;

        Long minsize = MIN_SIZE_DEFAULT;
        Long maxsize = MIN_SIZE_DEFAULT;

        if (requested.getSize() != null)
            {
            if (requested.getSize().getMin() != null)
                {
                minsize = requested.getSize().getMin();
                }
            if (requested.getSize().getMax() != null)
                {
                maxsize = requested.getSize().getMax();
                }
            }
        
        if (minsize > MAX_SIZE_LIMIT)
            {
            context.addWarning(
                "urn:resource-limit",
                "SimpleComputeResource - Requested minimum size exceeds available resources [${resource}][${size}][${limit}]",
                Map.of(
                    "resource",
                    requested.getMeta().getName(),
                    "size",
                    minsize,
                    "limit",
                    MAX_SIZE_LIMIT
                    )
                );
            success = false;
            }
        
        if (maxsize > MAX_SIZE_LIMIT)
            {
            maxsize = MAX_SIZE_LIMIT;
            context.addWarning(
                "urn:resource-limit",
                "SimpleComputeResource - Requested maximum size exceeds available resources [${resource}][${size}][${limit}]",
                Map.of(
                    "resource",
                    requested.getMeta().getName(),
                    "size",
                    maxsize,
                    "limit",
                    MAX_SIZE_LIMIT
                    )
                );
            // Not a fail condition.
            // success = false;
            }

        IvoaSimpleStorageSize size = new IvoaSimpleStorageSize();
        size.setMin(minsize);
        size.setMax(maxsize);
        validated.setSize(size);

        return success;
        }

    /**
     * Default prepare duration, 30 seconds.
     * 
     */
    public static final Long DEFAULT_PREPARE_ESTIMATE = 30L;

    /**
     * Get the prepare duration for a resource.
     * Returns DEFAULT_PREPARE_ESTIMATE if the request does not specify a value.
     * 
     */
    protected Long getPrepareDuration(final IvoaSimpleStorageResource validated)
        {
        Long duration = getPrepareDuration(
            validated.getSchedule()
            );
        if (duration != null)
            {
            return duration ;
            }
        else {
            return DEFAULT_PREPARE_ESTIMATE ;
            }
        }

    /**
     * Default release duration, 10 seconds.
     * 
     */
    public static final Long DEFAULT_RELEASE_ESTIMATE = 10L;

    /**
     * Get the release duration for a resource.
     * 
     */
    protected Long getReleaseDuration(final IvoaSimpleStorageResource validated)
        {
        return DEFAULT_RELEASE_ESTIMATE ;
        }
    
    }
