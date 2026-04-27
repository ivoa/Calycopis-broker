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
package net.ivoa.calycopis.datamodel.data.ivoa;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataLinkItem;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataResource;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataResourceBlock;
import net.ivoa.calycopis.spring.model.IvoaIvoaObsCoreItem;

/**
 * A Validator implementation to handle IvoaDataResources.
 *
 */
@Slf4j
public abstract class IvoaDataResourceValidatorImpl
extends AbstractDataResourceValidatorImpl
implements IvoaDataResourceValidator
    {

    /**
     * Factory for creating Entities.
     *
     */
    final IvoaDataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     *
     */
    public IvoaDataResourceValidatorImpl(
        final IvoaDataResourceEntityFactory entityFactory,
        final AbstractDataStorageLinker storageLinker
        ){
        super(
            storageLinker
            );
        this.entityFactory = entityFactory ;
        }

    @Override
    public ResultEnum validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());
        //
        // Use exact class matching rather than instanceof to prevent this
        // validator from intercepting subclass types (e.g. IvoaSkaoDataResource
        // extends IvoaIvoaDataResource). Each subclass has its own validator
        // that should handle its specific type.
        if (requested.getClass() == IvoaIvoaDataResource.class)
            {
            return validate(
                (IvoaIvoaDataResource) requested,
                context
                );
            }
        return ResultEnum.CONTINUE;
        }

    public ResultEnum validate(
        final IvoaIvoaDataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaIvoaDataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        IvoaIvoaDataResource validated = new IvoaIvoaDataResource()
            .kind(IvoaDataResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );

        success &= duplicateCheck(
            requested,
            context
            );

        AbstractStorageResourceValidator.Result storage = linkStorage(
            requested,
            validated,
            context
            );
        success &= ResultEnum.ACCEPTED.equals(storage.getEnum());
        
        //
        // Validate the IvoaIvoaDataResourceBlock.
        success &= validate(
            requested.getIvoa(),
            validated,
            context
            );
        
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
                    this.entity = IvoaDataResourceValidatorImpl.this.entityFactory.create(
                        session,
                        storage.getEntity(),
                        this
                        );
                    return this.entity;
                    }

                @Override
                public Long getPrepareDuration()
                    {
                    return IvoaDataResourceValidatorImpl.this.getPrepareDuration(
                        validated
                        );
                    }

                @Override
                public Long getReleaseDuration()
                    {
                    return IvoaDataResourceValidatorImpl.this.getReleaseDuration(
                        validated
                        );
                    }
                };
            //
            // Add our Result to our context.
            context.addDataValidatorResult(
                dataResult
                );
            //
            // Add our Result to the StorageResource.
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

    public boolean validate(
        final IvoaIvoaDataResourceBlock requested,
        final IvoaIvoaDataResource validated,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        if (requested != null)
            {
            IvoaIvoaDataResourceBlock block = new IvoaIvoaDataResourceBlock();
            validated.setIvoa(block);
            URI ivoid = requested.getIvoid();
            if (null != ivoid)
                {
                if (validateIvoid(ivoid, context))
                    {
                    block.setIvoid(
                        ivoid
                        );
                    }
                else {
                    success = false ;
                    }
                }
            else {
                context.addWarning(
                    "urn:missing-required-value",
                    "Ivoa ID (ivoid) required"
                    );
                success = false ;
                }
            
            IvoaIvoaObsCoreItem obscore = requested.getObscore();
            if (null != obscore)
                {
                // TODO process the ObsCore data.
                block.setObscore(obscore);
                }

            IvoaIvoaDataLinkItem datalink = requested.getDatalink();
            if (null != datalink)
                {
                // TODO process the DataLink data.
                block.setDatalink(datalink);
                }
            }
        else {
            context.addWarning(
                "urn:missing-required-value",
                "Ivoa metadata required"
                );
            success = false ;
            }
        return success ;
        }
    
    /**
     * Apply platform specific validation rules to the ivoid.
     * 
     */
    protected abstract boolean validateIvoid(final URI ivoid, final OfferSetRequestParserContext context);

    /**
     * Get the prepare duration for a resource.
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaIvoaDataResource validated);

    /**
     * Get the release duration for a resource.
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaIvoaDataResource validated);
    
    }
