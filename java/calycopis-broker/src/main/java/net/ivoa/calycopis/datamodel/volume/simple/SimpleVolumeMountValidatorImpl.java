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
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.volume.simple;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaSimpleVolumeMount;

/**
 * A Validator implementation to handle simple storage resources.
 *
 */
@Slf4j
public abstract class SimpleVolumeMountValidatorImpl
extends AbstractVolumeMountValidatorImpl
implements SimpleVolumeMountValidator
    {

    final SimpleVolumeMountEntityFactory volumeMountFactory;
    final AbstractDataResourceEntityFactory dataResourceFactory;
    final AbstractStorageResourceEntityFactory storageResourceFactory;

    /**
     * Public constructor.
     *
     */
    public SimpleVolumeMountValidatorImpl(
        final SimpleVolumeMountEntityFactory volumeMountFactory,
        final AbstractDataResourceEntityFactory dataResourceFactory,
        final AbstractStorageResourceEntityFactory storageResourceFactory
        ){
        super();
        this.volumeMountFactory = volumeMountFactory ;
        this.dataResourceFactory = dataResourceFactory ;
        this.storageResourceFactory = storageResourceFactory ;
        }

    @Override
    public ResultEnum validate(
        final IvoaAbstractVolumeMount requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractVolumeMount)");
        log.debug("Volume [{}][{}]", requested.getMeta(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaSimpleVolumeMount simple:
                return validate(
                    simple,
                    context
                    );
            default:
                return ResultEnum.CONTINUE;
            }
        }

    /**
     * Validate an IvoaSimpleVolumeMount.
     *
     */
    public ResultEnum validate(
        final IvoaSimpleVolumeMount requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleVolumeMount)");
        log.debug("Volume [{}][{}]", requested.getMeta(), requested.getClass().getName());

        IvoaSimpleVolumeMount validated = new IvoaSimpleVolumeMount()
           .kind(SimpleVolumeMount.TYPE_DISCRIMINATOR)
           .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
               );
        
        if (requested.getMode() == null)
            {
            validated.setMode(
                IvoaSimpleVolumeMount.ModeEnum.READONLY
                );
            }
        else {
            validated.setMode(
                requested.getMode()
                );
            }
        final String targetUuid = requested.getResource() ;
        log.debug(
            "Target resource [{}] for volume [{}]",
            targetUuid,
            validated.getMeta().getUuid()
            );

        if (targetUuid == null)
            {
            context.addError(
                "uri:resource-not-found",
                "Volume resource is null"
                );
            context.valid(
                false
                );
            return ResultEnum.FAILED;
            }
        
        final AbstractDataResourceValidator.Result dataResult = context.findDataValidatorResult(
                targetUuid 
                );
        final AbstractStorageResourceValidator.Result storageResult = context.findStorageValidatorResult(
                targetUuid
                );
        
        if ((dataResult == null) && (storageResult == null))
            {
            log.debug(
                "Unable to locate resource [{}] for volume [{}]",
                targetUuid,
                validated.getMeta().getUuid()
                );
            context.addError(
                "uri:resource-not-found",
                "Unable to locate resource [{}] for volume [{}]",
                Map.of(
                    "resource",
                    targetUuid,
                    "volume",
                    validated.getMeta().getUuid()
                    )
                );
            context.valid(
                false
                );
            return ResultEnum.FAILED;
            }
        
        if ((dataResult != null) && (storageResult != null))
            {
            log.debug(
                "Duplicate resource [{}][{}] for volume [{}]",
                dataResult.getName(),
                storageResult.getName(),
                validated.getMeta().getUuid()
                );
            context.addError(
                "uri:duplicate-resource",
                "Volume target must be EITHER data resource OR storage resource, not both [{}][{}]",
                Map.of(
                    "data",    dataResult.getEntity().getUuid(),
                    "storage", storageResult.getEntity().getUuid()
                    )
                );
            context.valid(
                false
                );
            return ResultEnum.FAILED;
            }

        if ((dataResult != null) && (storageResult == null))
            {
            log.debug(
                "Found data resource [{}] for volume [{}]",
                dataResult.getName(),
                validated.getMeta().getUuid()
                );

            ResultEnum pathResult = this.setPath(
                context,
                requested,
                validated,
                dataResult.getMeta()
                );
            
            if (pathResult == ResultEnum.FAILED)
                {
                return ResultEnum.FAILED;
                }
            
            context.addVolumeValidatorResult(
                new AbstractVolumeMountValidator.ResultBean(
                    Validator.ResultEnum.ACCEPTED,
                    validated
                    ){
                    @Override
                    public SimpleVolumeMountEntity build(
                        final AbstractComputeResourceEntity computeResource
                        ){
                        return volumeMountFactory.create(
                            computeResource,
                            dataResult.getEntity(),
                            this
                            );
                        }
                    @Override
                    public Long getPrepareDuration()    
                        {
                        return SimpleVolumeMountValidatorImpl.this.getPrepareDuration(
                            validated
                            );
                        }
                    @Override
                    public Long getReleaseDuration()    
                        {
                        return SimpleVolumeMountValidatorImpl.this.getReleaseDuration(
                            validated
                            );
                        }
                    }
                );
            return ResultEnum.ACCEPTED;
            }

        if ((dataResult == null) && (storageResult != null))
            {
            log.debug(
                "Found storage resource [{}] for volume [{}]",
                storageResult.getName(),
                validated.getMeta().getUuid()
                );
            context.addVolumeValidatorResult(
                new AbstractVolumeMountValidator.ResultBean(
                    Validator.ResultEnum.ACCEPTED,
                    validated
                    ){
                    @Override
                    public AbstractVolumeMountEntity build(
                        final AbstractComputeResourceEntity computeResource
                        ){
                        this.entity = SimpleVolumeMountValidatorImpl.this.volumeMountFactory.create(
                            computeResource,
                            storageResult.getEntity(),
                            this
                            );
                        return this.entity ;
                        }

                    @Override
                    public Long getPrepareDuration()    
                        {
                        return SimpleVolumeMountValidatorImpl.this.getPrepareDuration(
                            validated
                            );
                        }

                    @Override
                    public Long getReleaseDuration()    
                        {
                        return SimpleVolumeMountValidatorImpl.this.getReleaseDuration(
                            validated
                            );
                        }
                    }
                );
            return ResultEnum.ACCEPTED;
            }
        
        context.valid(false);
        return ResultEnum.FAILED;
        
        }

    public static final String DEFAULT_BASE_PATH = "/volumes";
    
    protected ResultEnum setPath(
        final OfferSetRequestParserContext context,
        final IvoaSimpleVolumeMount requested,
        final IvoaSimpleVolumeMount validated,
        final IvoaComponentMetadata meta
        ){

        if (requested.getPath() != null)
            {
            validated.setPath(
                requested.getPath()
                );
            }
        else {
            if (meta.getName() != null)
                {
                validated.setPath(
                    DEFAULT_BASE_PATH + "/" + meta.getName()
                    );
                }
            else {
                validated.setPath(
                    DEFAULT_BASE_PATH + "/" + meta.getUuid()
                    );
                }
            }
        return ResultEnum.ACCEPTED;
        }
    
    /**
     * Predict the time to prepare the volume.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * Note - volumes are not Lifecycle components, so they don't have prepare and release phases. 
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaSimpleVolumeMount validated);

    /**
     * Predict the time to release the volume.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * Note - volumes are not Lifecycle components, so they don't have prepare and release phases. 
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaSimpleVolumeMount validated);

    }
