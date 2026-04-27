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

package net.ivoa.calycopis.datamodel.data.docker.file;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;

/**
 * 
 */
@Slf4j
public class DockerFileResourceValidatorImpl 
extends SimpleDataResourceValidatorImpl
implements DockerFileResourceValidator
    {

    public DockerFileResourceValidatorImpl(
        final AbstractDataResourceEntityFactory entityFactory,
        final AbstractDataStorageLinker storageLinker)
        {
        super(
            entityFactory,
            storageLinker
            );
        }

    @Override
    public ResultEnum validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleDataResource, Context)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
    
        var location = requested.getLocation();
        if ((location != null) && (location.startsWith("file://")))
            {
            return super.validate(
                requested,
                context
                );
            }
        else {
            return ResultEnum.CONTINUE;
            }
        }
    
    @Override
    protected boolean validateLocation(
        final String location,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(String, Context)");
        log.debug("Location [{}]", location);
        // TODO Auto-generated method stub
        return true;
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
