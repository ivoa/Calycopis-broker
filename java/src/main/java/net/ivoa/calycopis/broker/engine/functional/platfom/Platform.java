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
 *     "timestamp": "2026-05-20T14:41:00",
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

package net.ivoa.calycopis.broker.engine.functional.platfom;

import java.net.URI;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataStorageLinker;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetFactory;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityUpdater;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.ComputeResourceOfferFactory;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingTransactionHandler;

/**
 * Platform is basically a factory of factories.
 * It provides a set of factories that provide platform specific implementations of the entities and validators.
 * 
 */
public interface Platform
extends FactoryBase
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
     * Get the ProcessingRequestFactory for this platform.
     *
     */
    public ProcessingRequestFactory getProcessingRequestFactory();

    /**
     * Get the ProcessingTransactionHandler for this platform.
     *
     */
    public ProcessingTransactionHandler getProcessingTransactionHandler();
    
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

    /**
     * Get the OfferSetFactory for this platform.
     * 
     */
    public OfferSetFactory getOfferSetFactory();

    /**
     * Get the ExecutionSessionEntityFactory for this platform.
     * 
     */
    public SimpleExecutionSessionEntityFactory getExecutionSessionEntityFactory();

    /**
     * Get the ExecutionSessionEntityUpdateHandler for this platform.
     * 
     */
    public SimpleExecutionSessionEntityUpdater getExecutionSessionEntityUpdater();
    
    
    }
