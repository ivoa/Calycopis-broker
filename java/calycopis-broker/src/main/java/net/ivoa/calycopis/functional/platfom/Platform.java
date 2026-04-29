package net.ivoa.calycopis.functional.platfom;

import java.net.URI;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.component.AbstractLifecycleComponentEntityFactory;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;

/**
 * Platform is basically a factory of factories.
 * It provides a set of factories that provide platform specific implementations of the entities and validators.
 *
 * Extends {@link net.ivoa.calycopis.engine.platform.Platform} so that all concrete broker
 * platform implementations are valid engine {@code Platform} instances.
 * The {@code getProcessingRequestFactory()} method required by the engine interface is
 * satisfied by the broker-typed override defined here (covariant return type).
 * 
 */
public interface Platform
extends FactoryBase,
        net.ivoa.calycopis.engine.platform.Platform
    {

    /**
     * Initialize the platform.
     *
     */
    public void initialize();

    /**
     * Get a LifecycleComponentEntity using the appropriate factory for the kind.
     *  
     */
    public LifecycleComponentEntity select(final URI kind, final UUID uuid);
    
    /**
     * Get the ExecutionSessionEntityFactory for this platform.
     * TODO Do we need the <?> generic wildcard here?
     *
     */
    public AbstractExecutionSessionEntityFactory<?> getExecutionSessionFactory();

    /**
     * Get the ProcessingRequestFactory for this platform.
     *
     * Overrides (with covariant return type) the engine-level
     * {@code getProcessingRequestFactory()} method that returns the engine
     * {@code ProcessingRequestFactory} interface.
     *
     */
    @Override
    public ProcessingRequestFactory getProcessingRequestFactory();
    
    /**
     * Get the ComponentEntityFactory for this platform.
     *
     */
    public AbstractLifecycleComponentEntityFactory getLifecycleComponentEntityFactory();
    
    /**
     * Get the ComputeResourceOfferFactory for this platform.
     *
     */
    public ComputeResourceOfferFactory getComputeResourceOfferFactory();

    /**
     * Get the ComputeResourceValidatorFactory for this platform.
     *
     */
    public AbstractComputeResourceValidatorFactory getComputeResourceValidators();
    
    /**
     * Get the DataResourceEntityFactory for this platform.
     *
    public AbstractDataResourceEntityFactory getDataResourceEntityFactory();
     */

    /**
     * Get the DataResourceValidatorFactory for this platform.
     *
     */
    public AbstractDataResourceValidatorFactory getDataResourceValidators();

    /**
     * Get the ExecutableValidatorFactory for this platform.
     *
     */
    public AbstractExecutableValidatorFactory getExecutableValidators();

    /**
     * Get the StorageResourceValidatorFactory for this platform.
     *
     */
    public AbstractStorageResourceValidatorFactory getStorageResourceValidators();

    /**
     * Get the VolumeMountValidatorFactory for this platform.
     *
     */
    public AbstractVolumeMountValidatorFactory getVolumeMountValidators();

    /**
     * Get the DataStorageLinker for this platform.
     * 
     */
    public AbstractDataStorageLinker getDataStorageLinker();
    
    }
