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
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-14T19:45:00",
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

package net.ivoa.calycopis.broker.engine.entities.data.amazon.mock;

import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.broker.engine.entities.data.amazon.AmazonS3DataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.AmazonS3DataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.mock.MockDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.schema.spring.model.IvoaS3DataResource;

/**
 * 
 */
public class MockAmazonS3DataResourceValidatorImpl
extends AmazonS3DataResourceValidatorImpl
implements MockAmazonS3DataResourceValidator
    {
    /**
     * Public constructor used by our Platform.
     * 
     */
    public MockAmazonS3DataResourceValidatorImpl(
        final AmazonS3DataResourceEntityFactory entityFactory,
        final MockDataStorageLinker storageLinker
        ){
        super(
            entityFactory,
            storageLinker
            );
        }

    public static final List<String> EXCLUDED_ENDPOINTS = List.of(
        "https://s3.excluded-one.example.com",
        "https://s3.excluded-two.example.com"
        );

    @Override
    protected boolean validateEndpoint(String endpoint, OfferSetRequestParserContext context)
        {
        if (EXCLUDED_ENDPOINTS.contains(endpoint))
            {
            context.addWarning(
                "urn:invalid-value",
                "S3DataResource - endpoint [${value}] is excluded",
                Map.of(
                    "value",
                    endpoint
                    )
                );
            return false;
            }
        else {
            return true;
            }
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
    protected Long getPrepareDuration(final IvoaS3DataResource validated)
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
     * Default release duration, 30 seconds.
     * 
     */
    public static final Long DEFAULT_RELEASE_ESTIMATE = 30L;

    /**
     * Get the release duration for a resource.
     * 
     */
    protected Long getReleaseDuration(final IvoaS3DataResource validated)
        {
        return DEFAULT_RELEASE_ESTIMATE ;
        }
    }
