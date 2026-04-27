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
 *     },
 *     {
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 10,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.compute.simple;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

/**
 * A validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public abstract class SimpleComputeResourceValidatorImpl
extends AbstractComputeResourceValidatorImpl
implements SimpleComputeResourceValidator
    {

    private final AbstractComputeResourceEntityFactory entityFactory;
    protected final AbstractVolumeMountValidatorFactory volumeMountValidatorFactory;

    /**
     * Public constructor.
     * 
     */
    public SimpleComputeResourceValidatorImpl(
        final AbstractComputeResourceEntityFactory entityFactory,
        final AbstractVolumeMountValidatorFactory volumeMountValidatorFactory
        ){
        super();
        this.entityFactory = entityFactory;
        this.volumeMountValidatorFactory = volumeMountValidatorFactory;
        }
    
    @Override
    public ResultEnum validate(
        final IvoaAbstractComputeResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", requested);
        //
        // Use exact class matching rather than instanceof to ensure each
        // validator only handles its specific type, not subclass types.
        // This prevents a parent type's validator from intercepting requests
        // that should be handled by a more specific subclass validator.
        if (requested.getClass() == IvoaSimpleComputeResource.class)
            {
            return validate(
                (IvoaSimpleComputeResource) requested,
                context
                );
            }
        return ResultEnum.CONTINUE;
        }

    protected abstract boolean validateCores(
        final IvoaSimpleComputeResource requested,
        final IvoaSimpleComputeResource validated,
        final OfferSetRequestParserContext context
        );

    protected abstract boolean validateMemory(
        final IvoaSimpleComputeResource requested,
        final IvoaSimpleComputeResource validated,
        final OfferSetRequestParserContext context
        );

    /**
     * Validate an IvoaAbstractComputeResource.
     *
     */
    public ResultEnum validate(
        final IvoaSimpleComputeResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleComputeResource)");
        log.debug("Resource [{}]", requested);

        boolean success = true ;

        IvoaSimpleComputeResource validated = new IvoaSimpleComputeResource()
            .kind(SimpleComputeResource.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );
        
        success &= validateCores(
            requested,
            validated,
            context
            );

        success &= validateMemory(
            requested,
            validated,
            context
            );
        
        //
        // Validate the volume mounts nested under this compute resource.
        log.debug("Validating the volume mounts");
        if (requested.getVolumes() != null)
            {
            for (IvoaAbstractVolumeMount volumeMount : requested.getVolumes())
                {
                log.debug("Validating volume mount [{}]", volumeMount);
                ResultEnum volumeResult = volumeMountValidatorFactory.validate(
                    volumeMount,
                    context
                    );
                success &= ResultEnum.ACCEPTED.equals(volumeResult);
                }
            }

        //
        // Everything is good, create our Result.
        if (success)
            {
            context.addComputeValidatorResult(
                new AbstractComputeResourceValidator.ResultBean(
                    Validator.ResultEnum.ACCEPTED,
                    validated
                    ){
                    @Override
                    public AbstractComputeResourceEntity build(final SimpleExecutionSessionEntity session, final ComputeResourceOffer offer)                
                        {
                        this.entity = SimpleComputeResourceValidatorImpl.this.entityFactory.create(
                            session,
                            this,
                            offer
                            );
                        return this.entity;
                        }
    
                    @Override
                    public Long getPrepareDuration()
                        {
                        return SimpleComputeResourceValidatorImpl.this.getPrepareDuration(
                            validated
                            );
                        }
    
                    @Override
                    public Long getReleaseDuration()
                        {
                        return SimpleComputeResourceValidatorImpl.this.getReleaseDuration(
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
     * Get the prepare duration for a resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaSimpleComputeResource validated);

    /**
     * Get the release duration for a resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaSimpleComputeResource validated);
    
    }
