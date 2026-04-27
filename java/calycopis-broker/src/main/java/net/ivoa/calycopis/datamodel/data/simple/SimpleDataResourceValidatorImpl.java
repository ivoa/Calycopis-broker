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
 *     "timestamp": "2026-02-14T15:30:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 10,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T07:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 3,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 3,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.data.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;

/**
 * A Validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public abstract class SimpleDataResourceValidatorImpl
extends AbstractDataResourceValidatorImpl
implements SimpleDataResourceValidator
    {

    /**
     * Factory for creating Entities.
     * 
     */
    final AbstractDataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     * 
     */
    public SimpleDataResourceValidatorImpl(
        final AbstractDataResourceEntityFactory entityFactory,
        final AbstractDataStorageLinker storageLinker
        ){
        super(storageLinker);
        this.entityFactory = entityFactory ;
        }
    
    @Override
    public ResultEnum validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource, Context)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
        //
        // Use exact class matching rather than instanceof to ensure each
        // validator only handles its specific type, not parent or sibling types.
        if (requested.getClass() == IvoaSimpleDataResource.class)
            {
            return validate(
                (IvoaSimpleDataResource) requested,
                context
                );
            }
        return ResultEnum.CONTINUE;
        }

    public ResultEnum validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleDataResource, Context)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        success &= duplicateCheck(
            requested,
            context
            );
        
        IvoaSimpleDataResource validated = new IvoaSimpleDataResource()
            .kind(SimpleDataResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );

        success &= validateLocation(
                requested,
                validated,
                context
                );

        AbstractStorageResourceValidator.Result storage = linkStorage(
            requested,
            validated,
            context
            );
        success &= ResultEnum.ACCEPTED.equals(storage.getEnum());

        //
        // Everything is good, create a validator Result.
        if (success)
            {
            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public AbstractDataResourceEntity build(final SimpleExecutionSessionEntity session)
                    {
                    this.entity = SimpleDataResourceValidatorImpl.this.entityFactory.create(
                        session,
                        storage.getEntity(),
                        this
                        );
                    return this.entity ;
                    }

                @Override
                public Long getPrepareDuration()
                    {
                    return SimpleDataResourceValidatorImpl.this.getPrepareDuration(
                        validated
                        );
                    }

                @Override
                public Long getReleaseDuration()
                    {
                    return SimpleDataResourceValidatorImpl.this.getReleaseDuration(
                        validated
                        );
                    }
                };
            context.addDataValidatorResult(
                dataResult
                );
            storage.addDataResourceResult(
                dataResult
                );
            return ResultEnum.ACCEPTED;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return ResultEnum.FAILED;
            }
        }

    /**
     * Validate the data resource location.
     * 
     */
    public boolean validateLocation(
        final IvoaSimpleDataResource requested,
        final IvoaSimpleDataResource validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateLocation(IvoaSimpleDataResource, IvoaSimpleDataResource, Context)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        String location = trim(
            requested.getLocation()
            );
        if ((location == null) || (location.isEmpty()))
            {
            context.addWarning(
                "urn:missing-required-value",
                "Data location required"
                );
            success = false ;
            }
        else {
            success &= validateLocation(
                location,
                context
                );
            }

        if (success)
            {
            validated.setLocation(location);
            }
        
        return success;
        }

    /**
     * Apply platform specific validation rules to the data location.
     * 
     */
    protected abstract boolean validateLocation(final String location, final OfferSetRequestParserContext context);
    
    /**
     * Get the prepare duration for a resource.
     * TODO Is this get or calculate ?
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaSimpleDataResource resource);

    /**
     * Get the release duration for a resource.
     * TODO Is this get or calculate ?
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaSimpleDataResource resource);

    }
