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
 *     },
 *     {
 *     "timestamp": "2026-05-20T14:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-05-20T14:41:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 2,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-05-21T10:54:00",
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

package net.ivoa.calycopis.broker.spring.platfom.docker;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.docker.DockerSimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.docker.DockerSimpleComputeResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.docker.DockerSimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.file.DockerFileResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.file.DockerFileResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.file.DockerFileResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http.DockerHttpResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http.DockerHttpResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http.DockerHttpResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.link.DockerDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.link.DockerDataStorageLinkerImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.stop.DockerStopResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.docker.DockerDockerContainerEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.docker.DockerDockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.docker.DockerDockerContainerValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetFactory;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParser;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityUpdateHandlerImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityUpdater;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.bind.DockerBindMountStorageEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.bind.DockerBindMountStorageEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.volume.DockerVolumeMountStorageEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.volume.DockerVolumeMountStorageEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.docker.DockerSimpleVolumeMountEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.docker.DockerSimpleVolumeMountEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.docker.DockerSimpleVolumeMountValidatorImpl;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.platfom.docker.DockerClientFactory;
import net.ivoa.calycopis.broker.engine.functional.platfom.docker.DockerPlatform;
import net.ivoa.calycopis.broker.engine.functional.platfom.docker.DockerPlatformSettings;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingTransactionHandler;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.spring.jpa.SpringAbstractEntityRepositoryWrapper;
import net.ivoa.calycopis.broker.spring.jpa.SpringComponentProcessingRequestRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringComputeResourceEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringDataResourceEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringExecutableEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringOfferSetRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringProcessingRequestRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringProcessingRequestRepositoryWrapper;
import net.ivoa.calycopis.broker.spring.jpa.SpringSessionEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringSessionEntityRepositoryWrapper;
import net.ivoa.calycopis.broker.spring.jpa.SpringSessionProcessingRequestRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringStorageResourceEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringVolumeMountEntityRepository;

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

    private boolean initialized = false;
    
    public void initialize()
        {
        log.debug("initialize()");

        if (this.initialized)
            {
            log.warn("Platform has already been initialized, skipping.");
            return;
            }
        else {
            this.initialized = true;
            }

        //
        // We need create these here because the Autowired Repositories are not available at construction time.

// Compute
        this.dockerSimpleComputeResourceEntityFactory = new DockerSimpleComputeResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractComputeResourceEntity>(
                this.springAbstractComputeResourceEntityRepository
                )
            );

// Data
        this.dockerFileResourceEntityFactory = new DockerFileResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractDataResourceEntity>(
                this.springAbstractDataResourceEntityRepository
                )
            );    

        this.dockerHttpResourceEntityFactory = new DockerHttpResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractDataResourceEntity>(
                this.springAbstractDataResourceEntityRepository
                )
            );

// Executable        
        this.dockerDockerContainerEntityFactory = new DockerDockerContainerEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractExecutableEntity>(
                this.springAbstractExecutableEntityRepository
                )
            );
        
// Storage        
        this.dockerBindMountStorageResourceEntityFactory = new DockerBindMountStorageEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractStorageResourceEntity>(
                this.springStorageResourceEntityRepository
                )
            );
        
        this.dockerVolumeMountStorageResourceEntityFactory = new DockerVolumeMountStorageEntityFactoryImpl(   
            new SpringAbstractEntityRepositoryWrapper<AbstractStorageResourceEntity>(
                this.springStorageResourceEntityRepository
                )
            );

// Volumes
        this.dockerVolumeMountEntityFactory = new DockerSimpleVolumeMountEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractVolumeMountEntity>(
                this.springVolumeMountEntityRepository
                )
            );

        this.dataStorageLinker = new DockerDataStorageLinkerImpl(
            this.dockerBindMountStorageResourceEntityFactory,
            this.dockerVolumeMountStorageResourceEntityFactory
            );

// Session
        this.simpleExecutionSessionEntityFactory = new SimpleExecutionSessionEntityFactoryImpl(
            new SpringSessionEntityRepositoryWrapper(
                this.springSessionEntityRepository
                )
            );

        this.simpleExecutionSessionEntityUpdater = new SimpleExecutionSessionEntityUpdateHandlerImpl(
            this
            );

// OfferSet
        
        this.offerSetFactory = new OfferSetFactoryImpl(
            this,
            new SpringAbstractEntityRepositoryWrapper<>(
                this.springOfferSetRepository
                ),
            this.offerSetRequestParser
            );

// Processing
        
        this.componentProcessingRequestFactory = new ComponentProcessingRequestFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<>(
                this.springComponentProcessingRequestRepository
                )
            );

        this.sessionProcessingRequestFactory = new SessionProcessingRequestFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<>(
                this.springSessionProcessingRequestRepository
                )
            );

        this.processingRequestFactory = new ProcessingRequestFactoryImpl(
            new SpringProcessingRequestRepositoryWrapper(
                this.springProcessingRequestRepository
                ),
            this.sessionProcessingRequestFactory,
            this.componentProcessingRequestFactory
            );
        
        //
        // Register validators with the most specific types first.
        // Each validator factory will iterate through it's list of
        // validators in registration order.
        //
        
        this.abstractExecutableValidatorFactory.addValidator(
            new DockerDockerContainerValidatorImpl(
                this
                )
            );

        this.abstractComputeResourceValidatorFactory.addValidator(
            new DockerSimpleComputeResourceValidatorImpl(
                this.dockerSimpleComputeResourceEntityFactory,
                this.abstractVolumeMountValidatorFactory
                )
            );

        this.abstractDataResourceValidatorFactory.addValidator(
            new DockerFileResourceValidatorImpl(
                this.dockerFileResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        
        this.abstractDataResourceValidatorFactory.addValidator(
            new DockerHttpResourceValidatorImpl(
                this.dockerHttpResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        
        this.abstractDataResourceValidatorFactory.addValidator(
            new DockerStopResourceValidatorImpl()
            );

        this.abstractVolumeMountValidatorFactory.addValidator(
            new DockerSimpleVolumeMountValidatorImpl(
                this.dockerVolumeMountEntityFactory,
                (AbstractDataResourceEntityFactory) this.dockerHttpResourceEntityFactory,
                (AbstractStorageResourceEntityFactory) this.dockerVolumeMountStorageResourceEntityFactory
                )
            );

        this.registerFactory(this.dockerSimpleComputeResourceEntityFactory);

        this.registerFactory(this.dockerDockerContainerEntityFactory);
      //this.registerFactory(this.jupyterNotebookEntityFactory);
        
        // We probably only need to register one of these, because it searches the abstract base class repository.
        this.registerFactory(this.dockerFileResourceEntityFactory);
        this.registerFactory(this.dockerHttpResourceEntityFactory);

        // We probably only need to register one of these, because it searches the abstract base class repository.
        this.registerFactory(this.dockerBindMountStorageResourceEntityFactory);
        this.registerFactory(this.dockerVolumeMountStorageResourceEntityFactory);
        
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
    private DockerPlatformSettingsImpl dockerSettings;
    @Override
    public DockerPlatformSettings getDockerSettings()
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
    private SpringComputeResourceEntityRepository springAbstractComputeResourceEntityRepository;
    private DockerSimpleComputeResourceEntityFactory dockerSimpleComputeResourceEntityFactory;

    private AbstractComputeResourceValidatorFactory abstractComputeResourceValidatorFactory = new AbstractComputeResourceValidatorFactoryImpl();
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.abstractComputeResourceValidatorFactory;
        }
    
// Data   
    
    @Autowired
    private SpringDataResourceEntityRepository springAbstractDataResourceEntityRepository;

    private DockerFileResourceEntityFactory dockerFileResourceEntityFactory ;
    private DockerHttpResourceEntityFactory dockerHttpResourceEntityFactory ;

    private AbstractDataResourceValidatorFactory abstractDataResourceValidatorFactory = new AbstractDataResourceValidatorFactoryImpl();
    @Override
    public AbstractDataResourceValidatorFactory getDataResourceValidators()
        {
        return this.abstractDataResourceValidatorFactory;
        }

// Executable    
    
    @Autowired
    private SpringExecutableEntityRepository springAbstractExecutableEntityRepository;

    private DockerDockerContainerEntityFactory dockerDockerContainerEntityFactory;  
    @Override
    public DockerContainerEntityFactory getDockerContainerEntityFactory()
        {
        return this.dockerDockerContainerEntityFactory;
        }

    // TODO 
    public JupyterNotebookEntityFactory getJupyterNotebookEntityFactory()
        {
        return null ;
        }
    
    private AbstractExecutableValidatorFactory abstractExecutableValidatorFactory = new AbstractExecutableValidatorFactoryImpl() ;
    @Override
    public AbstractExecutableValidatorFactory getExecutableValidators()
        {
        return this.abstractExecutableValidatorFactory;
        }
    
// Storage

    @Autowired
    private SpringStorageResourceEntityRepository springStorageResourceEntityRepository;

    private DockerBindMountStorageEntityFactory   dockerBindMountStorageResourceEntityFactory;
    private DockerVolumeMountStorageEntityFactory dockerVolumeMountStorageResourceEntityFactory;
    
    private AbstractStorageResourceValidatorFactory abstractStorageResourceValidatorFactory = new AbstractStorageResourceValidatorFactoryImpl() ;
    @Override
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators()
        {
        return this.abstractStorageResourceValidatorFactory;
        }

    private DockerDataStorageLinker dataStorageLinker;
    @Override
    public AbstractDataStorageLinker getDataStorageLinker()
        {
        return this.dataStorageLinker;
        }
    
// Volume

    @Autowired
    private SpringVolumeMountEntityRepository springVolumeMountEntityRepository;
    private DockerSimpleVolumeMountEntityFactory dockerVolumeMountEntityFactory;
    
    private AbstractVolumeMountValidatorFactory abstractVolumeMountValidatorFactory = new AbstractVolumeMountValidatorFactoryImpl();
    @Override
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators()
        {
        return this.abstractVolumeMountValidatorFactory;
        }

// Session
    
    @Autowired
    private SpringSessionEntityRepository springSessionEntityRepository;
    private SimpleExecutionSessionEntityFactory simpleExecutionSessionEntityFactory;
    @Override
    public SimpleExecutionSessionEntityFactory getExecutionSessionEntityFactory()
        {
        return simpleExecutionSessionEntityFactory;
        }
    
    private SimpleExecutionSessionEntityUpdater simpleExecutionSessionEntityUpdater;
    @Override
    public SimpleExecutionSessionEntityUpdater getExecutionSessionEntityUpdater()
        {
        return simpleExecutionSessionEntityUpdater;
        }
    
// Processing

    @Autowired
    private ProcessingTransactionHandler processingTransactionHandler;
    @Override
    public ProcessingTransactionHandler getProcessingTransactionHandler()
        {
        return this.processingTransactionHandler;
        }

    @Autowired
    private SpringProcessingRequestRepository springProcessingRequestRepository;
    @Autowired
    private SpringComponentProcessingRequestRepository springComponentProcessingRequestRepository;
    @Autowired
    private SpringSessionProcessingRequestRepository springSessionProcessingRequestRepository;

    // These have to be initialized in the initialize() method because the Autowired repositories are not available at construction time.
    private ProcessingRequestFactory processingRequestFactory;
    private ComponentProcessingRequestFactory componentProcessingRequestFactory;
    private SessionProcessingRequestFactory sessionProcessingRequestFactory;
    
    @Override
    public ProcessingRequestFactory getProcessingRequestFactory()
        {
        return this.processingRequestFactory;
        }

// OfferSets
    
    @Autowired
    private SpringOfferSetRepository springOfferSetRepository;
    
    private OfferSetRequestParser offerSetRequestParser = new OfferSetRequestParserImpl();
   
    // This  has to be initialized in the initialize() method because the Autowired repository is not available at construction time.
    private OfferSetFactory offerSetFactory;
    @Override
    public OfferSetFactory getOfferSetFactory()
        {
        return this.offerSetFactory;
        }

// LifecycleComponent

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
