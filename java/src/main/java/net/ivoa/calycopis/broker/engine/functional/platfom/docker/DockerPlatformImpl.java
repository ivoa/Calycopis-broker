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

package net.ivoa.calycopis.broker.engine.functional.platfom.docker;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.docker.DockerSimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.docker.DockerSimpleComputeResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.docker.DockerSimpleComputeResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.docker.DockerSimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.file.DockerFileResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.file.DockerFileResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.file.DockerFileResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.file.DockerFileResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http.DockerHttpResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http.DockerHttpResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http.DockerHttpResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http.DockerHttpResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.link.DockerDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.link.DockerDataStorageLinkerImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.stop.DockerStopResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.docker.DockerDockerContainerEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.docker.DockerDockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.docker.DockerDockerContainerValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetFactory;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRepository;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParser;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserImpl;
import net.ivoa.calycopis.broker.engine.entities.session.AbstractExecutionSessionEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityUpdateHandler;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityUpdateHandlerImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.bind.DockerBindMountStorageEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.bind.DockerBindMountStorageEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.bind.DockerBindMountStorageEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.volume.DockerVolumeMountStorageEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.volume.DockerVolumeMountStorageEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.volume.DockerVolumeMountStorageEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.docker.DockerSimpleVolumeMountEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.docker.DockerSimpleVolumeMountEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.docker.DockerSimpleVolumeMountEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.docker.DockerSimpleVolumeMountValidatorImpl;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestRepository;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestRepository;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestRepository;

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
        this.dockerSimpleComputeResourceEntityFactory = new DockerSimpleComputeResourceEntityFactoryImpl(
            this.dockerSimpleComputeResourceEntityRepository
            );

        this.dockerContainerEntityFactory = new DockerDockerContainerEntityFactoryImpl(
            this.abstractExecutableEntityRepository
            );
        
        this.dockerFileResourceEntityFactory = new DockerFileResourceEntityFactoryImpl(
            this.dockerFileResourceEntityRepository
            );    

        this.dockerHttpResourceEntityFactory = new DockerHttpResourceEntityFactoryImpl(
            this.dockerHttpResourceEntityRepository
            );

        this.bindMountStorageResourceEntityFactory = new DockerBindMountStorageEntityFactoryImpl(
            this.bindMountStorageResourceEntityRepository
            );        
        
        this.volumeMountStorageResourceEntityFactory = new DockerVolumeMountStorageEntityFactoryImpl(   
            this.volumeMountStorageResourceEntityRepository
            );  

        this.volumeMountEntityFactory = new DockerSimpleVolumeMountEntityFactoryImpl(
            this.volumeMountEntityRepository
            );

        this.dataStorageLinker = new DockerDataStorageLinkerImpl(
            this.bindMountStorageResourceEntityFactory,
            this.volumeMountStorageResourceEntityFactory
            );

        this.sessionEntityFactory = new SimpleExecutionSessionEntityFactoryImpl(
            this.sessionEntityRepository
            );

        this.sessionUpdateHandler = new SimpleExecutionSessionEntityUpdateHandlerImpl(
            this
            );

        this.offerSetFactory = new OfferSetFactoryImpl(
            this,
            this.offerSetRepository,
            this.offerSetRequestParser
            );

        this.componentProcessingRequestFactory = new ComponentProcessingRequestFactoryImpl(
            componentProcessingRequestRepository
            );

        this.sessionProcessingRequestFactory = new SessionProcessingRequestFactoryImpl(
            sessionProcessingRequestRepository
            );

        this.processingRequestFactory = new ProcessingRequestFactoryImpl(
            this.processingRequestRepository,
            this.sessionProcessingRequestFactory,
            this.componentProcessingRequestFactory
            );
        
        //
        // Register validators with the most specific types first.
        // Each validator factory will iterate through it's list of
        // validators in registration order.
        //
        
        this.executableValidatorFactory.addValidator(
            new DockerDockerContainerValidatorImpl(
                this
                )
            );

        this.abstractComputeResourceValidatorFactory.addValidator(
            new DockerSimpleComputeResourceValidatorImpl(
                this.dockerSimpleComputeResourceEntityFactory,
                this.volumeMountValidatorFactory
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

        this.volumeMountValidatorFactory.addValidator(
            new DockerSimpleVolumeMountValidatorImpl(
                this.volumeMountEntityFactory,
                (AbstractDataResourceEntityFactory) this.dockerHttpResourceEntityFactory,
                (AbstractStorageResourceEntityFactory) this.volumeMountStorageResourceEntityFactory
                )
            );

        this.registerFactory(this.dockerSimpleComputeResourceEntityFactory);

        this.registerFactory(this.dockerContainerEntityFactory);
      //this.registerFactory(this.jupyterNotebookEntityFactory);
        
        // We probably only need to register one of these, because it searches the abstract base class repository.
        this.registerFactory(this.dockerFileResourceEntityFactory);
        this.registerFactory(this.dockerHttpResourceEntityFactory);

        // We probably only need to register one of these, because it searches the abstract base class repository.
        this.registerFactory(this.bindMountStorageResourceEntityFactory);
        this.registerFactory(this.volumeMountStorageResourceEntityFactory);
        
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
    private DockerSimpleComputeResourceEntityRepository dockerSimpleComputeResourceEntityRepository;
    private DockerSimpleComputeResourceEntityFactory dockerSimpleComputeResourceEntityFactory;

    private AbstractComputeResourceValidatorFactory abstractComputeResourceValidatorFactory = new AbstractComputeResourceValidatorFactoryImpl();
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.abstractComputeResourceValidatorFactory;
        }
    
// Data   
    
    @Autowired
    private DockerFileResourceEntityRepository dockerFileResourceEntityRepository;
    private DockerFileResourceEntityFactory dockerFileResourceEntityFactory ;

    @Autowired
    private DockerHttpResourceEntityRepository dockerHttpResourceEntityRepository;
    private DockerHttpResourceEntityFactory dockerHttpResourceEntityFactory ;

    private AbstractDataResourceValidatorFactory abstractDataResourceValidatorFactory = new AbstractDataResourceValidatorFactoryImpl();
    @Override
    public AbstractDataResourceValidatorFactory getDataResourceValidators()
        {
        return this.abstractDataResourceValidatorFactory;
        }

// Executable    
    
    @Autowired
    private AbstractExecutableEntityRepository abstractExecutableEntityRepository ;  
    private DockerDockerContainerEntityFactory dockerContainerEntityFactory;  
    @Override
    public DockerContainerEntityFactory getDockerContainerEntityFactory()
        {
        return this.dockerContainerEntityFactory;
        }

    // TODO 
    public JupyterNotebookEntityFactory getJupyterNotebookEntityFactory()
        {
        return null ;
        }
    
    private AbstractExecutableValidatorFactory executableValidatorFactory = new AbstractExecutableValidatorFactoryImpl() ;
    @Override
    public AbstractExecutableValidatorFactory getExecutableValidators()
        {
        return this.executableValidatorFactory;
        }
    
// Storage

    @Autowired
    private DockerBindMountStorageEntityRepository bindMountStorageResourceEntityRepository;
    private DockerBindMountStorageEntityFactory bindMountStorageResourceEntityFactory;

    @Autowired
    private DockerVolumeMountStorageEntityRepository volumeMountStorageResourceEntityRepository;
    private DockerVolumeMountStorageEntityFactory volumeMountStorageResourceEntityFactory;
    
    private AbstractStorageResourceValidatorFactory storageResourceValidatorFactory = new AbstractStorageResourceValidatorFactoryImpl() ;
    @Override
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators()
        {
        return this.storageResourceValidatorFactory;
        }

    private DockerDataStorageLinker dataStorageLinker;
    @Override
    public AbstractDataStorageLinker getDataStorageLinker()
        {
        return this.dataStorageLinker;
        }
    
// Volume

    @Autowired
    private DockerSimpleVolumeMountEntityRepository volumeMountEntityRepository;
    // This  has to be initialized in the initialize() method because the Autowired repository is not available at construction time.
    private DockerSimpleVolumeMountEntityFactory volumeMountEntityFactory;
    
    private AbstractVolumeMountValidatorFactory volumeMountValidatorFactory = new AbstractVolumeMountValidatorFactoryImpl();
    @Override
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators()
        {
        return this.volumeMountValidatorFactory;
        }

// Session
    
    @Autowired
    private SimpleExecutionSessionEntityRepository sessionEntityRepository;

    // This  has to be initialized in the initialize() method because the Autowired repository is not available at construction time.
    private SimpleExecutionSessionEntityFactory sessionEntityFactory;
    @Override
    public SimpleExecutionSessionEntityFactory getSessionEntityFactory()
        {
        return sessionEntityFactory;
        }
    @Override
    public AbstractExecutionSessionEntityFactory<?> getAbstractSessionFactory()
        {
        return this.sessionEntityFactory;
        }

    // This  has to be initialized in the initialize() method because the Autowired repository is not available at construction time.
    private SimpleExecutionSessionEntityUpdateHandler sessionUpdateHandler;
    @Override
    public SimpleExecutionSessionEntityUpdateHandler getSessionUpdateHandler()
        {
        return sessionUpdateHandler;
        }
    
// Processing

    @Autowired
    private ProcessingRequestRepository processingRequestRepository;
    @Autowired
    private ComponentProcessingRequestRepository componentProcessingRequestRepository;
    @Autowired
    private SessionProcessingRequestRepository sessionProcessingRequestRepository;

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
    private OfferSetRepository offerSetRepository;
    
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
