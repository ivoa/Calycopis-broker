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
 *     },
 *     {
 *     "timestamp": "2026-05-26T16:50:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 2,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-05-27T06:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-06-03T01:33:00",
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

package net.ivoa.calycopis.broker.spring.platform.mock;

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
import net.ivoa.calycopis.broker.engine.entities.compute.simple.mock.MockSimpleComputeResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.mock.MockSimpleComputeResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.mock.MockSimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.mock.MockAmazonS3DataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.mock.MockAmazonS3DataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.amazon.mock.MockAmazonS3DataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.ivoa.mock.MockIvoaDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.ivoa.mock.MockIvoaDataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.ivoa.mock.MockIvoaDataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.data.mock.MockDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.data.mock.MockDataStorageLinkerImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.mock.MockSimpleDataResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.data.simple.mock.MockSimpleDataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.mock.MockSimpleDataResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.mock.MockDockerContainerEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.mock.MockDockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.mock.MockDockerContainerValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock.MockJupyterNotebookEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock.MockJupyterNotebookEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock.MockJupyterNotebookValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.cost.SimpleMinMaxFloatCostEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.CostsAndMetricsSettings;
import net.ivoa.calycopis.broker.engine.entities.metric.SimpleMinMaxFloatMetricEntity;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParser;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityUpdateHandlerImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityUpdater;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.mock.MockSimpleStorageResourceEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.mock.MockSimpleStorageResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.mock.MockSimpleStorageResourceValidatorImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.mock.MockSimpleVolumeMountEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.mock.MockSimpleVolumeMountEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.mock.MockSimpleVolumeMountValidatorImpl;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceOfferFactory;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.platform.mock.MockPlatform;
import net.ivoa.calycopis.broker.engine.functional.platform.mock.MockPlatformSettings;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingTransactionHandler;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.session.SessionProcessingRequestFactoryImpl;
import net.ivoa.calycopis.broker.spring.jpa.SpringAbstractEntityRepositoryWrapper;
import net.ivoa.calycopis.broker.spring.jpa.SpringComponentProcessingRequestEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringComputeResourceEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringDataResourceEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringExecutableEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringOfferSetEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringProcessingRequestEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringProcessingRequestEntityRepositoryWrapper;
import net.ivoa.calycopis.broker.spring.jpa.SpringSessionEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringSessionEntityRepositoryWrapper;
import net.ivoa.calycopis.broker.spring.jpa.SpringSessionProcessingRequestEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringStorageResourceEntityRepository;
import net.ivoa.calycopis.broker.spring.jpa.SpringVolumeMountEntityRepository;

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

    private boolean initialized = false;

    @Autowired
    private MockPlatformSettingsImpl mockEntitySettings;

    @Autowired
    private CostsAndMetricsSettings costsAndMetricsSettings;
    @Override
    public MockPlatformSettings getMockEntitySettings()
        {
        return this.mockEntitySettings;
        }

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
        this.mockSimpleComputeResourceEntityFactory = new MockSimpleComputeResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractComputeResourceEntity>(
                this.springAbstractComputeResourceEntityRepository
                )
            );

// Data
        this.mockSimpleDataResourceEntityFactory = new MockSimpleDataResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractDataResourceEntity>(
                this.springAbstractDataResourceEntityRepository
                )
            );

        this.mockAmazonS3DataResourceEntityFactory = new MockAmazonS3DataResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractDataResourceEntity>(
                this.springAbstractDataResourceEntityRepository
                )
            );

        this.mockIvoaDataResourceEntityFactory = new MockIvoaDataResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractDataResourceEntity>(
                this.springAbstractDataResourceEntityRepository
                )
            );  
        

        this.mockDataStorageLinker = new MockDataStorageLinkerImpl(
            this.abstractStorageResourceValidatorFactory
            );

// Executable        
        this.mockDockerContainerEntityFactory = new MockDockerContainerEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractExecutableEntity>(
                this.springAbstractExecutableEntityRepository
                )
            );

        this.mockJupyterNotebookEntityFactory = new MockJupyterNotebookEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractExecutableEntity>(
                this.springAbstractExecutableEntityRepository
                )
            );

// Storage        
        this.mockStorageResourceEntityFactory = new MockSimpleStorageResourceEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractStorageResourceEntity>(
                this.springAbstractStorageResourceEntityRepository
                )
            ); 

// Volumes        
        this.mockVolumeMountEntityFactory = new MockSimpleVolumeMountEntityFactoryImpl(
            new SpringAbstractEntityRepositoryWrapper<AbstractVolumeMountEntity>(
                this.springAbstractVolumeMountEntityRepository
                )
            );

// Session        
        this.simpleExecutionSessionEntityFactory = new SimpleExecutionSessionEntityFactoryImpl(
            new SpringSessionEntityRepositoryWrapper(
                this.springSessionEntityRepository
                )
            );

        this.sessionUpdateHandler = new SimpleExecutionSessionEntityUpdateHandlerImpl(
            this
            );

        this.offerSetFactory = new OfferSetEntityFactoryImpl(
            this,
            new SpringAbstractEntityRepositoryWrapper<>(
                this.springOfferSetRepository
                ),
            this.offerSetRequestParser
            );

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
            new SpringProcessingRequestEntityRepositoryWrapper(
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
            new MockJupyterNotebookValidatorImpl(
                this.mockJupyterNotebookEntityFactory
                )
            );
        this.abstractExecutableValidatorFactory.addValidator(
            new MockDockerContainerValidatorImpl(
                this.mockDockerContainerEntityFactory
                )
            );
        this.abstractComputeResourceValidatorFactory.addValidator(
            new MockSimpleComputeResourceValidatorImpl(
                this.mockSimpleComputeResourceEntityFactory,
                this.abstractVolumeMountValidatorFactory
                )
            );
        this.abstractStorageResourceValidatorFactory.addValidator(
            new MockSimpleStorageResourceValidatorImpl(
                this.mockStorageResourceEntityFactory
                )
            );

        //
        // Register data resource validators with the most specific types first.
        // The validator factory iterates through validators in registration order
        // and stops at the first one that returns ACCEPTED or FAILED. Although
        // each validator now uses exact class matching (getClass() ==) rather
        // than instanceof, registering specific subtypes before their parent
        // types provides defence in depth against future regressions.
        this.abstractDataResourceValidatorFactory.addValidator(
            new MockIvoaDataResourceValidatorImpl(
                this.mockIvoaDataResourceEntityFactory,
                this.mockDataStorageLinker
                )
            );
        this.abstractDataResourceValidatorFactory.addValidator(
            new MockAmazonS3DataResourceValidatorImpl(
                this.mockAmazonS3DataResourceEntityFactory,
                this.mockDataStorageLinker
                )
            );
        this.abstractDataResourceValidatorFactory.addValidator(
            new MockSimpleDataResourceValidatorImpl(
                this.mockSimpleDataResourceEntityFactory,
                this.mockDataStorageLinker
                )
            );
        this.abstractVolumeMountValidatorFactory.addValidator(
            new MockSimpleVolumeMountValidatorImpl(
                this.mockVolumeMountEntityFactory,
                this.mockSimpleDataResourceEntityFactory,
                this.mockStorageResourceEntityFactory
                )
            );

        this.registerFactory(this.mockDockerContainerEntityFactory);
        this.registerFactory(this.mockJupyterNotebookEntityFactory);
        this.registerFactory(this.mockSimpleComputeResourceEntityFactory);
        //this.registerFactory(this.dataResourceEntityFactory);
        this.registerFactory(this.mockStorageResourceEntityFactory);
        
        }

// Compute    
    
    @Autowired
    private SimpleComputeResourceOfferFactory simpleComputeResourceOfferFactory;
    @Override
    public SimpleComputeResourceOfferFactory getComputeResourceOfferFactory()
        {
        return this.simpleComputeResourceOfferFactory;
        }
    
    @Autowired
    private SpringComputeResourceEntityRepository springAbstractComputeResourceEntityRepository;
    private MockSimpleComputeResourceEntityFactory mockSimpleComputeResourceEntityFactory;

    private AbstractComputeResourceValidatorFactory abstractComputeResourceValidatorFactory = new AbstractComputeResourceValidatorFactoryImpl();
    @Override
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators()
        {
        return this.abstractComputeResourceValidatorFactory;
        }
    
// Data   


    @Autowired
    private SpringDataResourceEntityRepository springAbstractDataResourceEntityRepository;
    private MockSimpleDataResourceEntityFactory   mockSimpleDataResourceEntityFactory;
    private MockAmazonS3DataResourceEntityFactory mockAmazonS3DataResourceEntityFactory;
    private MockIvoaDataResourceEntityFactory     mockIvoaDataResourceEntityFactory;

    private AbstractDataResourceValidatorFactory abstractDataResourceValidatorFactory = new AbstractDataResourceValidatorFactoryImpl();
    @Override
    public AbstractDataResourceValidatorFactory getDataResourceValidators()
        {
        return this.abstractDataResourceValidatorFactory;
        }

// Executable    

    @Autowired
    private SpringExecutableEntityRepository springAbstractExecutableEntityRepository;
    private MockDockerContainerEntityFactory mockDockerContainerEntityFactory;  
    private MockJupyterNotebookEntityFactory mockJupyterNotebookEntityFactory;
    
    private AbstractExecutableValidatorFactory abstractExecutableValidatorFactory = new AbstractExecutableValidatorFactoryImpl();
    @Override
    public AbstractExecutableValidatorFactory getExecutableValidators()
        {
        return this.abstractExecutableValidatorFactory;
        }
    
// Storage

    @Autowired
    private SpringStorageResourceEntityRepository springAbstractStorageResourceEntityRepository;
    private MockSimpleStorageResourceEntityFactory mockStorageResourceEntityFactory; 
    
    private AbstractStorageResourceValidatorFactory abstractStorageResourceValidatorFactory = new AbstractStorageResourceValidatorFactoryImpl();
    @Override
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators()
        {
        return this.abstractStorageResourceValidatorFactory;
        }

    private MockDataStorageLinker mockDataStorageLinker;
    @Override
    public AbstractDataStorageLinker getDataStorageLinker()
        {
        return this.mockDataStorageLinker;
        }
    
// Volume
    
    @Autowired
    private SpringVolumeMountEntityRepository springAbstractVolumeMountEntityRepository;
    private MockSimpleVolumeMountEntityFactory mockVolumeMountEntityFactory;

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

    private SimpleExecutionSessionEntityUpdater sessionUpdateHandler;
    @Override
    public SimpleExecutionSessionEntityUpdater getExecutionSessionEntityUpdater()
        {
        return sessionUpdateHandler;
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
    private SpringProcessingRequestEntityRepository springProcessingRequestRepository;
    @Autowired
    private SpringComponentProcessingRequestEntityRepository springComponentProcessingRequestRepository;
    @Autowired
    private SpringSessionProcessingRequestEntityRepository springSessionProcessingRequestRepository;

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
    private SpringOfferSetEntityRepository springOfferSetRepository;
    
    private OfferSetRequestParser offerSetRequestParser = new OfferSetRequestParserImpl();
   
    // This  has to be initialized in the initialize() method because the Autowired repository is not available at construction time.
    private OfferSetEntityFactory offerSetFactory;
    @Override
    public OfferSetEntityFactory getOfferSetEntityFactory()
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

    @Override
    public void populateCostsAndMetrics(
        final SimpleExecutionSessionEntity sessionEntity,
        final AbstractComputeResourceEntity computeResourceEntity,
        final OfferSetRequestParserContext context
        ){
        log.debug("populateCostsAndMetrics(mock)");

        CostsAndMetricsSettings.ComponentConfig sessionConfig = costsAndMetricsSettings.getSession();
        for (CostsAndMetricsSettings.Item item : sessionConfig.getCosts())
            {
            sessionEntity.addCost(
                new SimpleMinMaxFloatCostEntity(
                    sessionEntity,
                    item.getType(),
                    item.getDescription(),
                    item.getMin(),
                    item.getMax()
                    )
                );
            }
        for (CostsAndMetricsSettings.Item item : sessionConfig.getMetrics())
            {
            sessionEntity.addMetric(
                new SimpleMinMaxFloatMetricEntity(
                    sessionEntity,
                    item.getType(),
                    item.getDescription(),
                    item.getMin(),
                    item.getMax()
                    )
                );
            }

        if (computeResourceEntity != null)
            {
            CostsAndMetricsSettings.ComponentConfig computeConfig = costsAndMetricsSettings.getCompute();
            for (CostsAndMetricsSettings.Item item : computeConfig.getCosts())
                {
                computeResourceEntity.addCost(
                    new SimpleMinMaxFloatCostEntity(
                        computeResourceEntity,
                        item.getType(),
                        item.getDescription(),
                        item.getMin(),
                        item.getMax()
                        )
                    );
                }
            for (CostsAndMetricsSettings.Item item : computeConfig.getMetrics())
                {
                computeResourceEntity.addMetric(
                    new SimpleMinMaxFloatMetricEntity(
                        computeResourceEntity,
                        item.getType(),
                        item.getDescription(),
                        item.getMin(),
                        item.getMax()
                        )
                    );
                }
            }
        }

    }
