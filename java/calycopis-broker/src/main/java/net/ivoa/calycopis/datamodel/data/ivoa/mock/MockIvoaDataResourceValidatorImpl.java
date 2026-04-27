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

package net.ivoa.calycopis.datamodel.data.ivoa.mock;

import java.net.URI;
import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.datamodel.data.ivoa.IvoaDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.ivoa.IvoaDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.mock.MockDataStorageLinker;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataResource;

/**
 * 
 */
public class MockIvoaDataResourceValidatorImpl
extends IvoaDataResourceValidatorImpl
implements MockIvoaDataResourceValidator
    {

    public MockIvoaDataResourceValidatorImpl(
        final IvoaDataResourceEntityFactory entityFactory,
        final MockDataStorageLinker storageLinker
        ){
        super(
            entityFactory,
            storageLinker
            );
        }

    public static final List<URI> EXCLUDED_LOCATIONS = List.of(
        URI.create("ivo://example.com/excluded"),
        URI.create("ivo://example.com/forbidden")
        );

    @Override
    protected boolean validateIvoid(URI ivoid, OfferSetRequestParserContext context)
        {
        if (EXCLUDED_LOCATIONS.contains(ivoid))
            {
            context.addWarning(
                "urn:invalid-value",
                "IvoaDataResource - ivoid is excluded [${value}]",
                Map.of(
                    "value",
                    ivoid.toString()
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
    protected Long getPrepareDuration(final IvoaIvoaDataResource validated)
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
    protected Long getReleaseDuration(final IvoaIvoaDataResource validated)
        {
        return DEFAULT_RELEASE_ESTIMATE ;
        }
    }
