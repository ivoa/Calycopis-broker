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
package net.ivoa.calycopis.datamodel.data.amazon;

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
import net.ivoa.calycopis.spring.model.IvoaS3DataResource;

/**
 * A validator implementation to handle simple data resources.
 * TODO A lot of this should be inherited from SimpleDataResourceValidator.
 * TODO Create a common base class with methods that can be inherited.
 * 
 */
@Slf4j
public abstract class AmazonS3DataResourceValidatorImpl
extends AbstractDataResourceValidatorImpl
implements AmazonS3DataResourceValidator
    {
    /**
     * Factory for creating Entities.
     * 
     */
    final AmazonS3DataResourceEntityFactory entityFactory;

    /**
     * Public constructor.
     * 
     */
    public AmazonS3DataResourceValidatorImpl(
        final AmazonS3DataResourceEntityFactory entityFactory,
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
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta().getName(), requested.getClass().getName());
        //
        // Use exact class matching rather than instanceof to ensure each
        // validator only handles its specific type, not parent or sibling types.
        if (requested.getClass() == IvoaS3DataResource.class)
            {
            return validate(
                (IvoaS3DataResource) requested,
                context
                );
            }
        return ResultEnum.CONTINUE;
        }

    /**
     * Validate an S3 data resource.
     *
     */
    public ResultEnum validate(
        final IvoaS3DataResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaS3DataResource)");
        log.debug("Resource [{}][{}]", requested.getMeta().getName(), requested.getClass().getName());

        boolean success = true ;

        IvoaS3DataResource validated = new IvoaS3DataResource()
            .kind(AmazonS3DataResource.TYPE_DISCRIMINATOR)
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
        
        success &= validateS3Fields(
            requested,
            validated,
            context
            );

        //
        // Everything is good, create our Result.
        if (success)
            {
            AbstractDataResourceValidator.Result dataResult = new AbstractDataResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                ){
                @Override
                public AbstractDataResourceEntity build(final SimpleExecutionSessionEntity session)
                    {
                    this.entity = AmazonS3DataResourceValidatorImpl.this.entityFactory.create(
                        session,
                        storage.getEntity(),
                        this
                        );
                    return this.entity;
                    }

                @Override
                public Long getPrepareDuration()
                    {
                    return AmazonS3DataResourceValidatorImpl.this.getPrepareDuration(
                        validated
                        );
                    }

                @Override
                public Long getReleaseDuration()
                    {
                    return AmazonS3DataResourceValidatorImpl.this.getReleaseDuration(
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

    /**
     * Apply any platform specific validation rules to the S3 endpoint.
     * 
     */
    protected abstract boolean validateEndpoint(final String endpoint, final OfferSetRequestParserContext context);

    /**
     * Validate the S3 data resource fields.
     * 
     */
    public boolean validateS3Fields(
        final IvoaS3DataResource requested,
        final IvoaS3DataResource validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateS3Fields(IvoaS3DataResource ...)");

        boolean success = true ;

        String endpoint = trim(
            requested.getEndpoint()
            );
        String template = trim(
            requested.getTemplate()
            );
        String bucket = trim(
            requested.getBucket()
            );
        String object = trim(
            requested.getObject()
            );

        if ((endpoint == null) || (endpoint.isEmpty()))
            {
            context.addWarning(
                "urn:missing-required-value",
                "S3 service endpoint required"
                );
            success = false;
            }
        else {
            success &= validateEndpoint(
                endpoint,
                context
                );
            }

        if ((template == null) || (template.isEmpty()))
            {
            context.addWarning(
                "urn:missing-required-value",
                "S3 service template required"
                );
            success = false;
            }

        if ((bucket == null) || (bucket.isEmpty()))
            {
            context.addWarning(
                "urn:missing-required-value",
                "S3 bucket name required"
                );
            success = false;
            }

        if (success)
            {
            validated.setEndpoint(endpoint);
            validated.setTemplate(template);
            validated.setBucket(bucket);
            validated.setObject(object);
            }

        return success;
        }


    /**
     * Get the prepare duration for a resource.
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaS3DataResource validated);

    /**
     * Get the release duration for a resource.
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaS3DataResource validated);
    
    }
