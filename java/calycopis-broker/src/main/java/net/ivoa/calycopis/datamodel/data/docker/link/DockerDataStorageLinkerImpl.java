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

package net.ivoa.calycopis.datamodel.data.docker.link;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator.Result;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator.ResultBean;
import net.ivoa.calycopis.datamodel.storage.docker.DockerBindMountStorageEntityFactory;
import net.ivoa.calycopis.datamodel.storage.docker.DockerVolumeMountStorageEntityFactory;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResource;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.functional.validator.Validator.ResultEnum;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;

/**
 * 
 */
@Slf4j
@Component
public class DockerDataStorageLinkerImpl
extends FactoryBaseImpl
implements DockerDataStorageLinker
    {

    private final DockerBindMountStorageEntityFactory   bindMountFactory;
    private final DockerVolumeMountStorageEntityFactory volumeMountFactory;
    
    public DockerDataStorageLinkerImpl(
        final DockerBindMountStorageEntityFactory   bindMountFactory,
        final DockerVolumeMountStorageEntityFactory volumeMountFactory
        ){
        super();
        this.bindMountFactory   = bindMountFactory;
        this.volumeMountFactory = volumeMountFactory;
        }

    @Override
    public AbstractStorageResourceValidator.Result linkStorage(
        final IvoaAbstractDataResource requested,
        final IvoaAbstractDataResource validated,
        final OfferSetRequestParserContext context
        ){
        
        // Check the resource isn't already linked to a storage resource.
        // There might be some cases where this is valid, but we don't support them yet.
        if (validated.getStorage() != null)
            {
            log.warn(
                "Data resource [{}] is already linked to storage resource [{}]",
                validated.getMeta().getUuid(),
                validated.getStorage()
                );
            context.addError(
                "uri:invalid-data",
                "Data resource [${uuid}] is already linked to storage resource [${storageUuid}]",
                Map.of(
                    "uuid", validated.getMeta().getUuid(),
                    "storageUuid", validated.getStorage()
                    )
                );
            return new ResultBean(ResultEnum.FAILED);
            }
        
        if ((requested instanceof IvoaSimpleDataResource) && (validated instanceof IvoaSimpleDataResource))
            {
            return linkSimple(
                (IvoaSimpleDataResource)requested,
                (IvoaSimpleDataResource)validated,
                context
                );
            }
        else {
            log.warn(
                "Unexpected data resource class [{}][{}]",
                requested.getClass().getSimpleName(),
                validated.getClass().getSimpleName()
                );
            context.addError(
                "uri:invalid-data",
                "Unexpected data resource class [${requested}][${validated}]",
                Map.of(
                    "requested", requested.getClass().getSimpleName(),
                    "validated", validated.getClass().getSimpleName()                        
                    )
                );
            return new ResultBean(ResultEnum.FAILED);
            }
        }
    
    protected AbstractStorageResourceValidator.Result linkSimple(
        final IvoaSimpleDataResource requested,
        final IvoaSimpleDataResource validated,
        final OfferSetRequestParserContext context
        ){

        String location = requested.getLocation();
        
        if ((location == null) || location.isBlank())
            {
            log.warn(
                "Missing location for data resource [{}]",
                requested.getMeta().getUuid()
                );
            context.addError(
                "uri:invalid-data",
                "Missing location [${location}] for data resource [${uuid}]",
                Map.of(
                    "location", location,
                    "uuid", requested.getMeta().getUuid()
                    )
                );
            return new ResultBean(ResultEnum.FAILED);
            }

        else if (location.startsWith("file://"))
            {
            return linkBindMount(
                requested,
                validated,
                context
                );
            }
        
        else if ((location.startsWith("http://")) || (location.startsWith("https://")))
            {
            return linkVolumeMount(
                requested,
                validated,
                context
                );
            }

        else {
            log.error(
                "Unsupported URL protocol [{}] for data resource [{}]",
                location,
                requested.getMeta().getUuid()
                );
            context.addError(
                "uri:invalid-data",
                "Unsupported location [${location}] for data resource [${uuid}]",
                Map.of(
                    "location", location,
                    "uuid", requested.getMeta().getUuid()
                    )
                );
            return new ResultBean(ResultEnum.FAILED);
            }
        }
    
    AbstractStorageResourceValidator.Result linkBindMount(
        final IvoaSimpleDataResource requested,
        final IvoaSimpleDataResource validated,
        final OfferSetRequestParserContext context
        ){

        IvoaAbstractStorageResource template = new IvoaAbstractStorageResource()
            .kind(SimpleStorageResource.TYPE_DISCRIMINATOR)
            .meta(
                new IvoaComponentMetadata()
                .name("BindMount")
                .description("Bind mount storage for data resource [" + validated.getMeta().getUuid() + "]")
                .uuid(UUID.randomUUID())
                );
        
        AbstractStorageResourceValidator.Result result = new AbstractStorageResourceValidator.ResultBean(
            Validator.ResultEnum.ACCEPTED,
            template
            ){
            @Override
            public AbstractStorageResourceEntity build(final SimpleExecutionSessionEntity session)
                {
                this.entity = DockerDataStorageLinkerImpl.this.bindMountFactory.create(
                    session,
                    this,
                    requested.getLocation()
                    );
                return this.entity;
                }
    
            @Override
            public Long getPrepareDuration()    
                {
                return 0L;
                }

            @Override
            public Long getReleaseDuration()    
                {
                return 0L;
                }
            };
                
        context.addStorageValidatorResult(  
            result
            );
        return result ;
        
        }

    Result linkVolumeMount(
        final IvoaSimpleDataResource requested,
        final IvoaSimpleDataResource validated,
        final OfferSetRequestParserContext context
        ){
        
        IvoaAbstractStorageResource template = new IvoaAbstractStorageResource()
            .kind(SimpleStorageResource.TYPE_DISCRIMINATOR)
            .meta(
                new IvoaComponentMetadata()
                .name("VolumeMount")
                .description("Volume mount storage for data resource [" + validated.getMeta().getUuid() + "]")
                .uuid(UUID.randomUUID())
                );
        
        log.debug(
            "Linking data resource [{}] to storage resource [{}]",
            validated,
            template
            );
        
        AbstractStorageResourceValidator.Result result = new AbstractStorageResourceValidator.ResultBean(
            Validator.ResultEnum.ACCEPTED,
            template
            ){
            @Override
            public AbstractStorageResourceEntity build(final SimpleExecutionSessionEntity session)
                {
                log.debug(
                    "Building storage resource [{}]",
                    this.getObject()
                    );
                this.entity = DockerDataStorageLinkerImpl.this.volumeMountFactory.create(
                    session,
                    this
                    );
                return this.entity;
                }
    
            @Override
            public Long getPrepareDuration()    
                {
                return 0L;
                }

            @Override
            public Long getReleaseDuration()    
                {
                return 0L;
                }
            };
                
        context.addStorageValidatorResult(  
            result
            );
        return result ;
        }
    }
