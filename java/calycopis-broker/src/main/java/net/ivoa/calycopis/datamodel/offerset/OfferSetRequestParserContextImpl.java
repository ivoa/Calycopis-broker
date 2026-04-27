/**
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
 *       "value": 1,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.offerset;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.message.MessageEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidator;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.functional.validator.ValidatorBase;
import net.ivoa.calycopis.spring.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaExecutionRequest;
import net.ivoa.calycopis.spring.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

/**
 *
 */
@Slf4j
public class OfferSetRequestParserContextImpl
extends ValidatorBase
    implements OfferSetRequestParserContext
    {

    /**
     * Public constructor.
     *
     */
    public OfferSetRequestParserContextImpl(
        final IvoaExecutionRequest originalRequest
        ){
        this.originalRequest  = originalRequest;
        this.validatedRequest = new IvoaExecutionRequest();
        }

    private final IvoaExecutionRequest originalRequest;
    @Override
    public IvoaExecutionRequest getOriginalOfferSetRequest()
        {
        return this.originalRequest;
        }

    private IvoaExecutionRequest validatedRequest;
    @Override
    public IvoaExecutionRequest getValidatedOfferSetRequest()
        {
        return this.validatedRequest;
        }

    private boolean valid = true ;
    @Override
    public boolean valid()
        {
        return this.valid;
        }
    @Override
    public void valid(boolean value)
        {
        this.valid = value;
        }
    public void fail()
        {
        this.valid = false;
        }

    /**
     * Ensure that a ComponentMetadata block exists and has a UUID.
     *
     */
    private IvoaComponentMetadata ensureUuid(IvoaComponentMetadata meta)
        {
        if (meta == null)
            {
            meta = new IvoaComponentMetadata();
            }
        if (meta.getUuid() == null)
            {
            meta.uuid(UUID.randomUUID());
            }
        return meta;
        }

    @Override
    public void registerResources()
        {
        log.debug("registerResources()");
        //
        // Register storage resources.
        // Assigns UUIDs and pre-registers them in the lookup map
        // so that data resource validators can find them.
        if (originalRequest.getStorage() != null)
            {
            for (IvoaAbstractStorageResource resource : originalRequest.getStorage())
                {
                //
                // Save the original name before assigning a UUID.
                String name = null;
                if (resource.getMeta() != null)
                    {
                    name = notEmpty(resource.getMeta().getName());
                    }
                //
                // Ensure the resource has a UUID.
                resource.setMeta(
                    ensureUuid(resource.getMeta())
                    );
                //
                // Create a preliminary result for cross-reference lookups.
                AbstractStorageResourceValidator.ResultBean preliminary =
                    new AbstractStorageResourceValidator.ResultBean(
                        Validator.ResultEnum.ACCEPTED,
                        resource
                        ){
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
                //
                // Register by name if available.
                if (name != null)
                    {
                    storageValidatorResultMap.put(
                        name,
                        preliminary
                        );
                    }
                //
                // Register by UUID.
                storageValidatorResultMap.put(
                    resource.getMeta().getUuid().toString(),
                    preliminary
                    );
                }
            }
        //
        // Assign UUIDs to data resources.
        if (originalRequest.getData() != null)
            {
            for (IvoaAbstractDataResource resource : originalRequest.getData())
                {
                resource.setMeta(
                    ensureUuid(resource.getMeta())
                    );
                }
            }
        //
        // Assign UUID to compute resource and its nested volume mounts.
        if (originalRequest.getCompute() != null)
            {
            originalRequest.getCompute().setMeta(
                ensureUuid(
                    originalRequest.getCompute().getMeta()
                    )
                );
            if (originalRequest.getCompute() instanceof IvoaSimpleComputeResource simpleCompute)
                {
                if (simpleCompute.getVolumes() != null)
                    {
                    for (IvoaAbstractVolumeMount volume : simpleCompute.getVolumes())
                        {
                        volume.setMeta(
                            ensureUuid(volume.getMeta())
                            );
                        }
                    }
                }
            }
        //
        // Assign UUID to executable.
        if (originalRequest.getExecutable() != null)
            {
            originalRequest.getExecutable().setMeta(
                ensureUuid(
                    originalRequest.getExecutable().getMeta()
                    )
                );
            }
        }

    private AbstractExecutableValidator.Result executable;
    @Override
    public AbstractExecutableValidator.Result getExecutableResult()
        {
        return this.executable;
        }
    public void setExecutableResult(final AbstractExecutableValidator.Result executable)
        {
        this.executable = executable;
        }

    /**
     * Our List of DataValidator results.
     *
     */
    private List<AbstractDataResourceValidator.Result> dataValidatorResultList = new ArrayList<AbstractDataResourceValidator.Result> ();

    @Override
    public List<AbstractDataResourceValidator.Result> getDataResourceValidatorResults()
        {
        return dataValidatorResultList;
        }

    /**
     * Our Map of DataValidator results.
     *
     */
    private Map<String, AbstractDataResourceValidator.Result> dataValidatorResultMap = new HashMap<String, AbstractDataResourceValidator.Result>();

    @Override
    public String makeDataValidatorResultKey(final AbstractDataResourceValidator.Result result)
        {
        log.debug("makeDataValidatorResultKey(DataResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return makeDataValidatorResultKey(
            result.getObject()
            );
        }

    @Override
    public String makeDataValidatorResultKey(final IvoaAbstractDataResource resource)
        {
        log.debug("makeDataValidatorResultKey(IvoaAbstractDataResource)");
        log.debug("Resource [[{}]", resource.getMeta());
        String key = null ;
        if (resource != null)
            {
            IvoaComponentMetadata meta = resource.getMeta();
            if (null != meta)
                {
                UUID uuid = meta.getUuid();
                if (uuid != null)
                    {
                    key = uuid.toString();
                    }
                else {
                    key = meta.getName();
                    }
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }

    @Override
    public void addDataValidatorResult(final AbstractDataResourceValidator.Result result)
        {
        log.debug("addDataValidatorResult(DataResourceValidator.Result)");
        log.debug("Result [{}]", result);
        dataValidatorResultList.add(
            result
            );
        dataValidatorResultMap.put(
            makeDataValidatorResultKey(
                result
                ),
            result
            );
        //
        // Also register by name if the primary key is a UUID.
        // This ensures name-based lookups find the validated result
        // instead of the preliminary result from registration.
        if (result.getObject() != null && result.getObject().getMeta() != null)
            {
            String name = notEmpty(result.getObject().getMeta().getName());
            if (name != null)
                {
                dataValidatorResultMap.put(
                    name,
                    result
                    );
                }
            }
        }

    @Override
    public AbstractDataResourceValidator.Result findDataValidatorResult(final AbstractDataResourceValidator.Result result)
        {
        log.debug("findDataValidatorResult(DataResourceValidator.Result)");
        return findDataValidatorResult(
            makeDataValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractDataResourceValidator.Result findDataValidatorResult(final IvoaAbstractDataResource resource)
        {
        log.debug("findDataValidatorResult(DataResourceValidator.Result)");
        return findDataValidatorResult(
            makeDataValidatorResultKey(
                resource
                )
            );
        }

    @Override
    public AbstractDataResourceValidator.Result findDataValidatorResult(final String key)
        {
        log.debug("findDataValidatorResult(String)");
        log.debug("Key [{}]", key);
        return dataValidatorResultMap.get(key);
        }

    /**
     * Our List of ComputeValidator results.
     *
     */
    private List<AbstractComputeResourceValidator.Result> compValidatorResultList = new ArrayList<AbstractComputeResourceValidator.Result>();

    @Override
    public List<AbstractComputeResourceValidator.Result> getComputeValidatorResults()
        {
        return compValidatorResultList;
        }

    /**
     * Our Map of ComputeValidator results.
     *
     */
    private Map<String, AbstractComputeResourceValidator.Result> compValidatorResultMap = new HashMap<String, AbstractComputeResourceValidator.Result>();

    @Override
    public String makeComputeValidatorResultKey(final AbstractComputeResourceValidator.Result result)
        {
        log.debug("makeComputeValidatorResultKey(ComputeResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return makeComputeValidatorResultKey(
            result.getObject()
            );
        }

    @Override
    public String makeComputeValidatorResultKey(final IvoaAbstractComputeResource resource)
        {
        log.debug("makeComputeValidatorResultKey(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", resource);
        String key = null ;
        if (resource != null)
            {
            IvoaComponentMetadata meta = resource.getMeta();
            if (null != meta)
                {
                UUID uuid = meta.getUuid();
                if (uuid != null)
                    {
                    key = uuid.toString();
                    }
                else {
                    key = meta.getName();
                    }
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }

    @Override
    public void addComputeValidatorResult(final AbstractComputeResourceValidator.Result result)
        {
        log.debug("addComputeValidatorResult(ComputeResourceValidator.Result)");
        log.debug("Result [{}]", result);
        compValidatorResultList.add(
            result
            );
        compValidatorResultMap.put(
            makeComputeValidatorResultKey(
                result
                ),
            result
            );
        }

    @Override
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final AbstractComputeResourceValidator.Result result)
        {
        log.debug("findComputeValidatorResult(ComputeResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return findComputeValidatorResult(
            makeComputeValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(final IvoaAbstractComputeResource resource)
        {
        log.debug("findComputeValidatorResult(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", resource);
        return findComputeValidatorResult(
            makeComputeValidatorResultKey(
                resource
                )
            );
        }

    @Override
    public AbstractComputeResourceValidator.Result findComputeValidatorResult(String key)
        {
        log.debug("findComputeValidatorResult(String)");
        log.debug("Key [{}]", key);
        return compValidatorResultMap.get(key);
        }

    /**
     * Our List of StorageValidator results.
     *
     */
    private List<AbstractStorageResourceValidator.Result> storageValidatorResultList = new ArrayList<AbstractStorageResourceValidator.Result>();

    @Override
    public List<AbstractStorageResourceValidator.Result> getStorageValidatorResults()
        {
        return storageValidatorResultList;
        }

    /**
     * Our Map of StorageValidator results.
     *
     */
    private Map<String, AbstractStorageResourceValidator.Result> storageValidatorResultMap = new HashMap<String, AbstractStorageResourceValidator.Result>();

    @Override
    public String makeStorageValidatorResultKey(final AbstractStorageResourceValidator.Result result)
        {
        log.debug("makeStorageValidatorResultKey(StorageResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return makeStorageValidatorResultKey(
            result.getObject()
            );
        }

    @Override
    public String makeStorageValidatorResultKey(final IvoaAbstractStorageResource resource)
        {
        log.debug("makeStorageValidatorResultKey(IvoaAbstractStorageResource)");
        log.debug("Resource [{}]", resource);
        String key = null ;
        if (resource != null)
            {
            IvoaComponentMetadata meta = resource.getMeta();
            if (null != meta)
                {
                UUID uuid = meta.getUuid();
                if (uuid != null)
                    {
                    key = uuid.toString();
                    }
                else {
                    key = meta.getName();
                    }
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }

    @Override
    public void addStorageValidatorResult(final AbstractStorageResourceValidator.Result result)
        {
        log.debug("addStorageValidatorResult(String)");
        log.debug("Result [{}]", result);
        storageValidatorResultList.add(
            result
            );
        storageValidatorResultMap.put(
            makeStorageValidatorResultKey(
                result
                ),
            result
            );
        //
        // Also register by name if the primary key is a UUID.
        // This ensures name-based lookups find the validated result
        // instead of the preliminary result from registration.
        if (result.getObject() != null && result.getObject().getMeta() != null)
            {
            String name = notEmpty(result.getObject().getMeta().getName());
            if (name != null)
                {
                storageValidatorResultMap.put(
                    name,
                    result
                    );
                }
            }
        }

    @Override
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final AbstractStorageResourceValidator.Result result)
        {
        log.debug("findStorageValidatorResult(StorageResourceValidator.Result)");
        log.debug("Result [{}]", result);
        return findStorageValidatorResult(
            makeStorageValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final IvoaAbstractStorageResource resource)
        {
        log.debug("findStorageValidatorResult(IvoaAbstractStorageResource)");
        log.debug("Resource [{}]", resource);
        return findStorageValidatorResult(
            makeStorageValidatorResultKey(
                resource
                )
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findStorageValidatorResult(final String key)
        {
        log.debug("findStorageValidatorResult(String)");
        log.debug("Key [{}]", key);
        return storageValidatorResultMap.get(key);
        }

    /**
     * Our List of VolumeMountValidator results.
     *
     */
    private List<AbstractVolumeMountValidator.Result> volumeValidatorResultList = new ArrayList<AbstractVolumeMountValidator.Result>();

    @Override
    public List<AbstractVolumeMountValidator.Result> getVolumeValidatorResults()
        {
        return volumeValidatorResultList;
        }

    /**
     * Our Map of VolumeMountValidator results.
     *
     */
    private Map<String, AbstractVolumeMountValidator.Result> volumeValidatorResultMap = new HashMap<String, AbstractVolumeMountValidator.Result>();

    @Override
    public String makeVolumeValidatorResultKey(final AbstractVolumeMountValidator.Result result)
        {
        log.debug("makeVolumeValidatorResultKey(VolumeMountValidator.Result)");
        log.debug("Result [{}]", result);
        return makeVolumeValidatorResultKey(
            result.getObject()
            );
        }

    @Override
    public String makeVolumeValidatorResultKey(final IvoaAbstractVolumeMount resource)
        {
        log.debug("makeVolumeValidatorResultKey(IvoaAbstractVolumeMount)");
        log.debug("Resource [{}]", resource);
        String key = null ;
        if (resource != null)
            {
            IvoaComponentMetadata meta = resource.getMeta();
            if (null != meta)
                {
                UUID uuid = meta.getUuid();
                if (uuid != null)
                    {
                    key = uuid.toString();
                    }
                else {
                    key = meta.getName();
                    }
                }
            }
        log.debug("Key [{}]", key);
        return key ;
        }

    @Override
    public void addVolumeValidatorResult(final AbstractVolumeMountValidator.Result result)
        {
        log.debug("addVolumeValidatorResult(String)");
        log.debug("Result [{}]", result);
        volumeValidatorResultList.add(
            result
            );
        volumeValidatorResultMap.put(
            makeVolumeValidatorResultKey(
                result
                ),
            result
            );
        }

    @Override
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final AbstractVolumeMountValidator.Result result)
        {
        log.debug("findVolumeValidatorResult(VolumeMountValidator.Result)");
        log.debug("Result [{}]", result);
        return findVolumeValidatorResult(
            makeVolumeValidatorResultKey(
                result
                )
            );
        }

    @Override
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final IvoaAbstractVolumeMount resource)
        {
        log.debug("findVolumeValidatorResult(IvoaAbstractVolumeMount)");
        log.debug("Resource [{}]", resource);
        return findVolumeValidatorResult(
            makeVolumeValidatorResultKey(
                resource
                )
            );
        }

    @Override
    public AbstractVolumeMountValidator.Result findVolumeValidatorResult(final String key)
        {
        log.debug("findVolumeValidatorResult(String)");
        log.debug("Key [{}]", key);
        return volumeValidatorResultMap.get(key);
        }

    /*
     * A Map linking DataValidator results to StorageValidator results.
     *
    private Map<String, AbstractStorageResourceValidator.Result> dataStorageMap = new HashMap<String, AbstractStorageResourceValidator.Result>();

    @Override
    public void addDataStorageResult(
        final IvoaAbstractDataResource dataResource,
        final AbstractStorageResourceValidator.Result storageResult
        ){
        log.debug("addDataStorageResult(IvoaAbstractDataResource, StorageResourceValidator.Result)");
        log.debug("IvoaAbstractDataResource [{}]", dataResource);
        log.debug("StorageResult [{}]", storageResult);
        // TODO
        // Check for a duplicate already in the map ?
        dataStorageMap.put(
            makeDataValidatorResultKey(
                dataResource
                ),
            storageResult
            );
        }

    @Override
    public void addDataStorageResult(
        final AbstractDataResourceValidator.Result dataResult,
        final AbstractStorageResourceValidator.Result storageResult
        ){
        log.debug("addDataStorageResult(DataResourceValidator.Result, StorageResourceValidator.Result)");
        log.debug("DataResult [{}]", dataResult);
        log.debug("StorageResult [{}]", storageResult);
        // TODO
        // Check for a duplicate already in the map ?
        dataStorageMap.put(
            makeDataValidatorResultKey(
                dataResult
                ),
            storageResult
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findDataStorageResult(final AbstractDataResourceValidator.Result dataResult)
        {
        log.debug("findDataStorageResult(DataResourceValidator.Result)");
        log.debug("Result [{}]", dataResult);
        return dataStorageMap.get(
            makeDataValidatorResultKey(
                dataResult
                )
            );
        }

    @Override
    public AbstractStorageResourceValidator.Result findDataStorageResult(final IvoaAbstractDataResource dataResouce)
        {
        log.debug("findDataStorageResult(IvoaAbstractDataResource)");
        log.debug("Resouce [{}]", dataResouce);
        return dataStorageMap.get(
            makeDataValidatorResultKey(
                dataResouce
                )
            );
        }
     *
     */

    /**
     * The requested start Interval.
     *
     */
    private Interval startInterval = null;
    @Override
    public Interval getStartInterval()
        {
        return this.startInterval;
        }
    @Override
    public void setStartInterval(Interval interval)
        {
        this.startInterval = interval;
        }

    /**
     * The requested execution duration.
     *
     */
    private Duration executionDuration = null;
    @Override
    public Duration getExecutionDuration()
        {
        return this.executionDuration;
        }
    @Override
    public void setExecutionDuration(Duration duration)
        {
        this.executionDuration = duration;
        }

    private long totalMinCores;
    @Override
    public long getTotalMinCores()
        {
        return this.totalMinCores;
        }
    @Override
    public void addMinCores(long delta)
        {
        this.totalMinCores += delta;
        }

    private long totalMaxCores;
    @Override
    public long getTotalMaxCores()
        {
        return this.totalMaxCores;
        }
    @Override
    public void addMaxCores(long delta)
        {
        this.totalMaxCores += delta;
        }

    private long totalMinMemory;
    @Override
    public long getTotalMinMemory()
        {
        return this.totalMinMemory;
        }
    @Override
    public void addMinMemory(long delta)
        {
        this.totalMinMemory += delta;
        }

    private long totalMaxMemory;
    @Override
    public long getTotalMaxMemory()
        {
        return this.totalMaxMemory;
        }
    @Override
    public void addMaxMemory(long delta)
        {
        this.totalMaxMemory += delta;
        }

    private long totalStagingTime;
    @Override
    public Long getTotalStagingTime()
        {
        return totalStagingTime;
        }

    private long totalPrepareTime;
    @Override
    public Long getTotalPrepareTime()
        {
        return totalPrepareTime;
        }

    @Override
    public Long calculateTotalPrepareTime()
        {
        // TODO Split this into the method to calculate and the method to get the value.
        // The calculation method should only be called once, after all of the request has been validated.
        // Then the get method should just return the stored value.

        log.debug("OfferSetRequestParserContextImpl.getTotalPrepareTime()");
        //
        // Time needed to fetch the container image.
        Long executablePrepareTime = 0L ;
        if (this.executable != null)
            {
            executablePrepareTime = this.executable.getTotalPrepareDuration();
            log.debug("Executable prepare time [{}][{}]", this.executable.getName(), executablePrepareTime);
            }
        //
        // Time needed to create the storage space and stage the data.
        Long maxStoragePrepareTime = 0L ;
        for (AbstractStorageResourceValidator.Result storageResult : this.getStorageValidatorResults())
            {
            Long storagePrepareTime = storageResult.getTotalPrepareDuration();
            log.debug("Storage prepare time [{}][{}]", storageResult.getName(), storagePrepareTime);
            if (storagePrepareTime > maxStoragePrepareTime)
                {
                maxStoragePrepareTime = storagePrepareTime;
                }
            }

        //
        // Time needed to allocate the compute resources and mount the volumes.
        Long maxComputePrepareTime = 0L ;
        for (AbstractComputeResourceValidator.Result computeResult : this.getComputeValidatorResults())
            {
            Long computePrepareTime = computeResult.getTotalPrepareDuration();
            log.debug("Compute prepare time [{}][{}]", computeResult.getName(), computePrepareTime);
            if (computePrepareTime > maxComputePrepareTime)
                {
                maxComputePrepareTime = computePrepareTime;
                }
            }

        //
        // Assuming staging can happen in parallel.
        this.totalStagingTime = (executablePrepareTime > maxStoragePrepareTime) ? executablePrepareTime : maxStoragePrepareTime ;
        //
        // Total prepare time is staging time plus compute prepare time.
        this.totalPrepareTime = this.totalStagingTime + maxComputePrepareTime ;

        log.debug("Executable prepare time [{}]",  executablePrepareTime);
        log.debug("Max compute prepare time [{}]", maxComputePrepareTime);
        log.debug("Max storage prepare time [{}]", maxStoragePrepareTime);
        log.debug("Total staging time [{}]", this.totalStagingTime);
        log.debug("Total prepare time [{}]", this.totalPrepareTime);

        return this.totalPrepareTime ;
        }

    private List<MessageEntity> messages = new ArrayList<MessageEntity>();
    
    @Override
    public List<MessageEntity> getMessages()
        {
        return this.messages;
        }

    @Override
    public void addMessage(LevelEnum level, String type, String template, Map<String, Object> values)
        {
        this.messages.add(
            new MessageEntity(
                null,
                level,
                type,
                template,
                values
                )
            );
        }
    }
