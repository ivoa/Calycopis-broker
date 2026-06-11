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
 *
 */

package net.ivoa.calycopis.broker.engine.entities.data.simple.docker.stop;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.data.simple.SimpleDataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleDataResource;

/**
 * A STOP Validator to cap the end of the validator chain for SimpleDataResources.
 * This Validator fails the validation, adding a warning message to indicate that the platform is unable to handle the requested location .
 *  
 */
@Slf4j
public class DockerSimpleDataStopValidatorImpl
extends SimpleDataResourceValidatorImpl
implements DockerSimpleDataStopValidator
    {

    public DockerSimpleDataStopValidatorImpl()
        {
        super(null, null);
        }

    @Override
    public ResultEnum validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleDataResource, Context)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        String location = requested.getLocation();

        context.addWarning(
                "uri:unknown-url",
                "Unable to handle data location [${location}]",
                Map.of(
                    "location",
                    location
                    )
                );
            context.valid(false);
        
        return ResultEnum.FAILED;
        }
    
    @Override
    protected boolean validateLocation(final String location, final OfferSetRequestParserContext context)
        {
        return false;
        }

    @Override
    protected Long getPrepareDuration(final IvoaSimpleDataResource resource)
        {
        return 0L;
        }

    @Override
    protected Long getReleaseDuration(final IvoaSimpleDataResource resource)
        {
        return 0L;
        }
    }
