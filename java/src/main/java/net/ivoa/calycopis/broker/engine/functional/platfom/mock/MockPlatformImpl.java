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
 *     "timestamp": "2026-02-14T12:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 8,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-14T15:30:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 15,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T07:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     },
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
 *     "timestamp": "2026-03-26T16:30:00",
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

package net.ivoa.calycopis.broker.engine.functional.platfom.mock;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.mock.MockSimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.mock.MockSimpleComputeResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.mock.MockSimpleComputeResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.mock.MockSimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.mock.MockAmazonS3DataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.mock.MockAmazonS3DataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.mock.MockAmazonS3DataResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.mock.MockAmazonS3DataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.ivoa.mock.MockIvoaDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.ivoa.mock.MockIvoaDataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.ivoa.mock.MockIvoaDataResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.data.ivoa.mock.MockIvoaDataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.mock.MockDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.mock.MockDataStorageLinkerImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.mock.MockSimpleDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.simple.mock.MockSimpleDataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.mock.MockSimpleDataResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.data.simple.mock.MockSimpleDataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.skao.mock.MockSkaoDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.skao.mock.MockSkaoDataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.skao.mock.MockSkaoDataResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.data.skao.mock.MockSkaoDataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.mock.MockDockerContainerEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.mock.MockDockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.mock.MockDockerContainerValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock.MockJupyterNotebookEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock.MockJupyterNotebookEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock.MockJupyterNotebookValidatorImpl;
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
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.mock.MockSimpleStorageResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.mock.MockSimpleStorageResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.mock.MockSimpleStorageResourceEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.mock.MockSimpleStorageResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.mock.MockSimpleVolumeMountEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.mock.MockSimpleVolumeMountEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.mock.MockSimpleVolumeMountEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.mock.MockSimpleVolumeMountValidatorImpl;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestRepository;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestRepository;
import net.ivoa.calycopis.broker.engine.functional.processing.mock.MockEntitySettings;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestRepository;

/**
 * 
 */
@Slf4j
@Component
@Profile("mock")
public class MockPlatformImpl
extends FactoryBaseImpl
implements MockPlatform
    {

    public MockPlatformImpl()
        {
        super();
        }

    @Autowired
    private MockEntitySettings mockEntitySettings;
    @Override
    public MockEntitySettings getMockEntitySettings()
        {
        return this.mockEntitySettings;
        }

    public void initialize()
        {
        log.debug("initialize()");
        //
        // We need create these here because the Autowired Repositories are not available at construction time.
        this.simpleComputeResourceEntityFactory = new MockSimpleComputeResourceEntityFactoryImpl(
            this.simpleComputeResourceEntityRepository
            );

        this.simpleDataResourceEntityFactory = new MockSimpleDataResourceEntityFactoryImpl(
            this.simpleDataResourceEntityRepository
            );

        this.amazonS3DataResourceEntityFactory = new MockAmazonS3DataResourceEntityFactoryImpl(
            this.amazonS3DataResourceEntityRepository
            );

        this.ivoaDataResourceEntityFactory = new MockIvoaDataResourceEntityFactoryImpl(
            this.ivoaDataResourceEntityRepository
            );  
        
        this.skaoDataResourceEntityFactory = new MockSkaoDataResourceEntityFactoryImpl(
            this.skaoDataResourceEntityRepository
            );

        this.dataStorageLinker = new MockDataStorageLinkerImpl(
            this.storageResourceValidatorFactory
            );

        this.dockerContainerEntityFactory = new MockDockerContainerEntityFactoryImpl(
            this.abstractExecutableEntityRepository
            );

        this.jupyterNotebookEntityFactory = new MockJupyterNotebookEntityFactoryImpl(
            this.abstractExecutableEntityRepository
            );

        this.storageResourceEntityFactory = new MockSimpleStorageResourceEntityFactoryImpl(
            this.storageResourceEntityRepository
            ); 
        
        this.volumeMountEntityFactory = new MockSimpleVolumeMountEntityFactoryImpl(
            this.volumeMountEntityRepository
            );
        
        this.sessionEntityFactory = new SimpleExecutionSessionEntityFactoryImpl(
            this.sessionEntityRepository
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
            new MockJupyterNotebookValidatorImpl(
                this.jupyterNotebookEntityFactory
                )
            );
        this.executableValidatorFactory.addValidator(
            new MockDockerContainerValidatorImpl(
                this.dockerContainerEntityFactory
                )
            );
        this.abstractComputeResourceValidatorFactory.addValidator(
            new MockSimpleComputeResourceValidatorImpl(
                this.simpleComputeResourceEntityFactory,
                this.volumeMountValidatorFactory
                )
            );
        this.storageResourceValidatorFactory.addValidator(
            new MockSimpleStorageResourceValidatorImpl(
                this.storageResourceEntityFactory
                )
            );

        //
        // Register data resource validators with the most specific types first.
        // The validator factory iterates through validators in registration order
        // and stops at the first one that returns ACCEPTED or FAILED. Although
        // each validator now uses exact class matching (getClass() ==) rather
        // than instanceof, registering specific subtypes before their parent
        // types provides defence in depth against future regressions.
        //
        // SkaoDataResource extends IvoaDataResource in the type hierarchy,
        // so the SKAO validator must be registered before the IVOA validator.
        //
        this.abstractDataResourceValidatorFactory.addValidator(
            new MockSkaoDataResourceValidatorImpl(
                this.jdbcTemplate,
                this.skaoDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.abstractDataResourceValidatorFactory.addValidator(
            new MockIvoaDataResourceValidatorImpl(
                this.ivoaDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.abstractDataResourceValidatorFactory.addValidator(
            new MockAmazonS3DataResourceValidatorImpl(
                this.amazonS3DataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.abstractDataResourceValidatorFactory.addValidator(
            new MockSimpleDataResourceValidatorImpl(
                this.simpleDataResourceEntityFactory,
                this.dataStorageLinker
                )
            );
        this.volumeMountValidatorFactory.addValidator(
            new MockSimpleVolumeMountValidatorImpl(
                this.volumeMountEntityFactory,
                this.simpleDataResourceEntityFactory,
                this.storageResourceEntityFactory
                )
            );

        this.registerFactory(this.dockerContainerEntityFactory);
        this.registerFactory(this.jupyterNotebookEntityFactory);
        this.registerFactory(this.simpleComputeResourceEntityFactory);
        //this.registerFactory(this.dataResourceEntityFactory);
        this.registerFactory(this.storageResourceEntityFactory);
        
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
    private MockSimpleComputeResourceEntityRepository simpleComputeResourceEntityRepository;
    private MockSimpleComputeResourceEntityFactory    simpleComputeResourceEntityFactory;

    private AbstractComputeResourceValidatorFactory abstractComputeResourceValidatorFactory = new AbstractComputeResourceValidatorFactoryImpl();
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.abstractComputeResourceValidatorFactory;
        }
    
// Data   

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockSimpleDataResourceEntityRepository simpleDataResourceEntityRepository;
    private MockSimpleDataResourceEntityFactory    simpleDataResourceEntityFactory;

    @Autowired
    private MockAmazonS3DataResourceEntityRepository amazonS3DataResourceEntityRepository;
    private MockAmazonS3DataResourceEntityFactory    amazonS3DataResourceEntityFactory;

    @Autowired
    private MockIvoaDataResourceEntityRepository ivoaDataResourceEntityRepository;
    private MockIvoaDataResourceEntityFactory    ivoaDataResourceEntityFactory;

    @Autowired
    private MockSkaoDataResourceEntityRepository skaoDataResourceEntityRepository;
    private MockSkaoDataResourceEntityFactory    skaoDataResourceEntityFactory;

    private AbstractDataResourceValidatorFactory abstractDataResourceValidatorFactory = new AbstractDataResourceValidatorFactoryImpl();
    @Override
    public AbstractDataResourceValidatorFactory getDataResourceValidators()
        {
        return this.abstractDataResourceValidatorFactory;
        }

// Executable    

    @Autowired
    private AbstractExecutableEntityRepository abstractExecutableEntityRepository ;  

    private MockDockerContainerEntityFactory dockerContainerEntityFactory;  
    private MockJupyterNotebookEntityFactory jupyterNotebookEntityFactory;
    
    private AbstractExecutableValidatorFactory executableValidatorFactory = new AbstractExecutableValidatorFactoryImpl();
    @Override
    public AbstractExecutableValidatorFactory getExecutableValidators()
        {
        return this.executableValidatorFactory;
        }
    
// Storage

    @Autowired
    private MockSimpleStorageResourceEntityRepository storageResourceEntityRepository;
    private MockSimpleStorageResourceEntityFactory    storageResourceEntityFactory; 
    
    private AbstractStorageResourceValidatorFactory storageResourceValidatorFactory = new AbstractStorageResourceValidatorFactoryImpl();
    @Override
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators()
        {
        return this.storageResourceValidatorFactory;
        }

    private MockDataStorageLinker dataStorageLinker;
    @Override
    public AbstractDataStorageLinker getDataStorageLinker()
        {
        return this.dataStorageLinker;
        }
    
// Volume
    
    @Autowired
    private MockSimpleVolumeMountEntityRepository volumeMountEntityRepository;
    private MockSimpleVolumeMountEntityFactory    volumeMountEntityFactory;

    private AbstractVolumeMountValidatorFactory volumeMountValidatorFactory = new AbstractVolumeMountValidatorFactoryImpl();
    @Override
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators()
        {
        return this.volumeMountValidatorFactory;
        }

// Session
    
    @Autowired
    private SimpleExecutionSessionEntityRepository sessionEntityRepository;
    private SimpleExecutionSessionEntityFactory    sessionEntityFactory;
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
