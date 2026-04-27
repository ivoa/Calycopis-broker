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
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-04-14T17:00:00",
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

package net.ivoa.calycopis.functional.platfom.docker;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.compute.simple.docker.DockerSimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.simple.docker.DockerSimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.datamodel.data.docker.DockerSimpleDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.docker.file.DockerFileResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.docker.file.DockerFileResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.docker.http.DockerHttpResourceEntityFactory;
import net.ivoa.calycopis.datamodel.data.docker.http.DockerHttpResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.data.docker.link.DockerDataStorageLinker;
import net.ivoa.calycopis.datamodel.data.docker.stop.DockerStopResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.docker.docker.DockerDockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.docker.docker.DockerDockerContainerValidatorImpl;
import net.ivoa.calycopis.datamodel.executable.jupyter.mock.MockJupyterNotebookEntityFactory;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.storage.docker.DockerSimpleStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.docker.DockerVolumeMountStorageEntityFactory;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.datamodel.volume.simple.docker.DockerSimpleVolumeMountEntityFactory;
import net.ivoa.calycopis.datamodel.volume.simple.docker.DockerSimpleVolumeMountValidatorImpl;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;

/**
 * 
 */
@Slf4j
@Component
@Profile("docker")
public class DockerPlatformImpl
extends FactoryBaseImpl
implements DockerPlatform
    {

    public DockerPlatformImpl()
        {
        super();
        }

    public void initialize()
        {
        //
        // Register validators with the most specific types first.
        // Each validator factory will iterate through it's list of
        // validators in registration order.
        //
        
        /*
        this.executableValidatorFactory.addValidator(
            new MockJupyterNotebookValidatorImpl(
                this.jupyterNotebookEntityFactory
                )
            );
         */
        this.executableValidatorFactory.addValidator(
            new DockerDockerContainerValidatorImpl(
                this
                )
            );
        this.computeResourceValidatorFactory.addValidator(
            new DockerSimpleComputeResourceValidatorImpl(
                this.computeResourceEntityFactory,
                this.volumeMountValidatorFactory
                )
            );
        /*
        this.storageResourceValidatorFactory.addValidator(
            new DockerVolumeMountStorageValidatorImpl(
                this.storageResourceEntityFactory
                )
            );
         */

        /*
        this.dataResourceValidatorFactory.addValidator(
            new MockSkaoDataResourceValidatorImpl(
                this.jdbcTemplate,
                this.mockSkaoDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.dataResourceValidatorFactory.addValidator(
            new MockIvoaDataResourceValidatorImpl(
                this.mockIvoaDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.dataResourceValidatorFactory.addValidator(
            new MockAmazonS3DataResourceValidatorImpl(
                this.mockAmazonS3DataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
         */
        this.dataResourceValidatorFactory.addValidator(
            new DockerFileResourceValidatorImpl(
                this.dockerFileResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.dataResourceValidatorFactory.addValidator(
            new DockerHttpResourceValidatorImpl(
                this.dockerHttpResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.dataResourceValidatorFactory.addValidator(
            new DockerStopResourceValidatorImpl()
            );

        this.volumeMountValidatorFactory.addValidator(
            new DockerSimpleVolumeMountValidatorImpl(
                this.volumeMountEntityFactory,
                this.dockerHttpResourceEntityFactory,
                this.storageResourceEntityFactory
                )
            );

        this.registerFactory(this.dockerContainerEntityFactory);
        this.registerFactory(this.jupyterNotebookEntityFactory);
        this.registerFactory(this.computeResourceEntityFactory);
        this.registerFactory(this.simpleDataResourceEntityFactory);
        this.registerFactory(this.simpleStorageResourceEntityFactory);
        
        }

// Docker client

    @Autowired
    private DockerClientFactory dockerClientFactory;
    @Override
    public DockerClientFactory getDockerClientFactory()
        {
        return this.dockerClientFactory;
        }

    @Autowired
    private DockerSettings dockerSettings;
    @Override
    public DockerSettings getDockerSettings()
        {
        return this.dockerSettings;
        }

// Compute    
    
    @Autowired
    private ComputeResourceOfferFactory computeResourceOfferFactory;
    @Override
    public ComputeResourceOfferFactory getComputeResourceOfferFactory()
        {
        return this.computeResourceOfferFactory;
        }
    
    @Autowired
    private DockerSimpleComputeResourceEntityFactory computeResourceEntityFactory;

    @Autowired
    private AbstractComputeResourceValidatorFactory computeResourceValidatorFactory;
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.computeResourceValidatorFactory;
        }
    
// Data   

    @Autowired
    private DockerSimpleDataResourceEntityFactory simpleDataResourceEntityFactory;

    @Autowired
    private DockerFileResourceEntityFactory dockerFileResourceEntityFactory;    

    @Autowired
    private DockerHttpResourceEntityFactory dockerHttpResourceEntityFactory;

    @Autowired
    private AbstractDataResourceValidatorFactory dataResourceValidatorFactory;
    @Override
    public AbstractDataResourceValidatorFactory getDataResourceValidators()
        {
        return this.dataResourceValidatorFactory;
        }

// Executable    

    @Autowired
    private AbstractExecutableValidatorFactory executableValidatorFactory;
    @Override
    public AbstractExecutableValidatorFactory getExecutableValidators()
        {
        return this.executableValidatorFactory;
        }
    
    @Autowired
    private DockerDockerContainerEntityFactory dockerContainerEntityFactory;  
    @Override
    public DockerContainerEntityFactory getDockerContainerEntityFactory()
        {
        return this.dockerContainerEntityFactory;
        }

    @Autowired
    private MockJupyterNotebookEntityFactory jupyterNotebookEntityFactory;
    
// Storage

    @Autowired
    private DockerSimpleStorageResourceEntityFactory simpleStorageResourceEntityFactory;

    @Autowired
    private DockerVolumeMountStorageEntityFactory storageResourceEntityFactory;

    @Autowired
    private AbstractStorageResourceValidatorFactory storageResourceValidatorFactory;
    @Override
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators()
        {
        return this.storageResourceValidatorFactory;
        }

    @Autowired
    private DockerDataStorageLinker dataStorageLinker;
    @Override
    public AbstractDataStorageLinker getDataStorageLinker()
        {
        return this.dataStorageLinker;
        }
    
// Volume
    
    @Autowired
    private DockerSimpleVolumeMountEntityFactory volumeMountEntityFactory ;
    
    @Autowired
    private AbstractVolumeMountValidatorFactory volumeMountValidatorFactory;
    @Override
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators()
        {
        return this.volumeMountValidatorFactory;
        }

// Session
    
    @Autowired
    private SimpleExecutionSessionEntityFactory executionSessionEntityFactory;
    @Override
    public AbstractExecutionSessionEntityFactory<?> getExecutionSessionFactory()
        {
        return this.executionSessionEntityFactory;
        }

// Processing
    
    @Autowired
    private ProcessingRequestFactory processingRequestFactory;
    @Override
    public ProcessingRequestFactory getProcessingRequestFactory()
        {
        return this.processingRequestFactory;
        }

// LifecycleComponent

    @Autowired
    private AbstractLifecycleComponentEntityFactory lifecycleComponentEntityFactory;
    @Override
    public AbstractLifecycleComponentEntityFactory getLifecycleComponentEntityFactory()
        {
        return this.lifecycleComponentEntityFactory;
        }
    
    Map<URI, LifecycleComponentEntityFactory<?>> registry = new HashMap<URI, LifecycleComponentEntityFactory<?>>();
    
    void registerFactory(
        final LifecycleComponentEntityFactory<?> factory
        ){
        this.registry.put(
            factory.getKind(),
            factory
            );
        }
    
    @Override
    public LifecycleComponentEntity select(final URI kind, final UUID uuid)
        {
        LifecycleComponentEntityFactory<?> factory = this.registry.get(kind);
        if (factory != null)
            {
            return factory.select(uuid).orElse(null);
            }
        else {
            return null;
            }
        }
    }
