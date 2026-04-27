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
 *       "value": 10,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 4,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.storage.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleStorageResource;

/**
 * A Validator implementation to handle simple storage resources.
 * 
 */
@Slf4j
public abstract class SimpleStorageResourceValidatorImpl
extends AbstractStorageResourceValidatorImpl
implements SimpleStorageResourceValidator
    {
    /**
     * Factory for creating Entities.
     *
     */
    final AbstractStorageResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     * 
     */
    public SimpleStorageResourceValidatorImpl(
        final AbstractStorageResourceEntityFactory entityFactory
        ){
        super();
        this.entityFactory = entityFactory;
        }
    
    @Override
    public ResultEnum validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractStorageResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaSimpleStorageResource simple:
                return validate(
                    simple,
                    context
                    );
            default:
                return ResultEnum.CONTINUE;
            }
        }

    /**
     * Validate an IvoaSimpleStorageResource.
     *
     */
    public ResultEnum validate(
        final IvoaSimpleStorageResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleStorageResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;
        IvoaSimpleStorageResource validated = new IvoaSimpleStorageResource()
            .kind(SimpleStorageResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );
        
        //
        // Check the requested size.
        success &= validateSize(
            requested,
            validated,
            context
            );
        
        //
        // Everything is good, create our Result.
        if (success)
            {
            context.addStorageValidatorResult(
                new AbstractStorageResourceValidator.ResultBean(
                    Validator.ResultEnum.ACCEPTED,
                    validated
                    ){
                    @Override
                    public AbstractStorageResourceEntity build(final SimpleExecutionSessionEntity session)
                        {
                        this.entity = SimpleStorageResourceValidatorImpl.this.entityFactory.create(
                            session,
                            this
                            );
                        return this.entity;
                        }
    
                    @Override
                    public Long getPrepareDuration()    
                        {
                        return SimpleStorageResourceValidatorImpl.this.getPrepareDuration(
                            validated
                            );
                        }
    
                    @Override
                    public Long getReleaseDuration()    
                        {
                        return SimpleStorageResourceValidatorImpl.this.getReleaseDuration(
                            validated
                            );
                        }
                    }
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
     * Predict the time to prepare the resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaSimpleStorageResource validated);

    /**
     * Predict the time to release the resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaSimpleStorageResource validated);

    /**
     * Validate the requested storage size.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract boolean validateSize(
            final IvoaSimpleStorageResource requested,
            final IvoaSimpleStorageResource validated,
            final OfferSetRequestParserContext context
            );
    
    }
