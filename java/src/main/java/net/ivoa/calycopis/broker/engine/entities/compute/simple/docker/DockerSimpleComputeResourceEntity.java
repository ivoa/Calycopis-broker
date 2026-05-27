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
 *     "timestamp": "2026-04-14T17:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 40,
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
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.compute.simple.docker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponent;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.SimpleComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainer;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResource;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.DockerStorageLinkerImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.SimpleVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceOffer;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.engine.functional.platform.docker.DockerClientFactory;
import net.ivoa.calycopis.broker.engine.functional.platform.docker.DockerPlatform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleVolumeMount.ModeEnum;

/**
 * A Docker SimpleComputeResource entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "dockersimplecomputeresources"
    )
@DiscriminatorValue(
    value = "uri:docker-simple-compute-resources"
    )
public class DockerSimpleComputeResourceEntity
extends SimpleComputeResourceEntity
implements DockerSimpleComputeResource
    {

    /**
     * Protected constructor for JPA entities.
     *
     */
    protected DockerSimpleComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor used by our factory.
     *
     */
    protected DockerSimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final DockerSimpleComputeResourceValidator.Result result,
        final SimpleComputeResourceOffer offer
        ){
        super(
            session,
            result,
            offer
            );
        }
    
    @Column(name="dockercontainerid")
    private String dockerContainerId;
    public String getDockerContainerId()
        {
        return this.dockerContainerId;
        }

    @Column(name="dockercontainerexitcode")
    private Integer dockerContainerExitCode;
    @Override
    public Integer getDockerContainerExitCode()
        {
        return this.dockerContainerExitCode;
        }

    @Lob
    @Column(name="containerstdout")
    private String containerStdout;
    @Override
    public String getContainerStdout()
        {
        return this.containerStdout;
        }

    @Lob
    @Column(name="containerstderr")
    private String containerStderr;
    @Override
    public String getContainerStderr()
        {
        return this.containerStderr;
        }

    /**
     * Maximum number of characters to capture per stream.
     * Output beyond this limit is truncated from the beginning,
     * keeping only the last MAX_LOG_CHARS characters.
     */
    private static final int MAX_LOG_CHARS = 64 * 1024;

    /**
     * Capture stdout and stderr from a stopped Docker container
     * using the Docker log API.
     */
    private static void captureContainerLogs(
        final DockerClient dockerClient,
        final String containerId,
        final StringBuilder stdoutBuilder,
        final StringBuilder stderrBuilder
        ){
        try {
            dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(false)
                .withTailAll()
                .exec(new ResultCallback.Adapter<Frame>()
                    {
                    @Override
                    public void onNext(Frame frame)
                        {
                        String text = new String(
                            frame.getPayload(),
                            java.nio.charset.StandardCharsets.UTF_8
                            );
                        if (frame.getStreamType() == StreamType.STDOUT)
                            {
                            stdoutBuilder.append(text);
                            }
                        else if (frame.getStreamType() == StreamType.STDERR)
                            {
                            stderrBuilder.append(text);
                            }
                        }
                    })
                .awaitCompletion();
            }
        catch (Exception e)
            {
            log.warn(
                "Failed to capture logs for container [{}]: {}",
                containerId,
                e.getMessage()
                );
            }
        }

    /**
     * Truncate a string to at most maxChars characters,
     * keeping the tail (most recent output).
     */
    private static String truncateLog(final String text, final int maxChars)
        {
        if (text == null || text.length() <= maxChars)
            {
            return text;
            }
        return "...[truncated]...\n" + text.substring(text.length() - maxChars);
        }

    @Override
    public ProcessingAction getPrepareAction(final Platform platform, final ComponentProcessingRequest request)
        {
        // Eagerly resolve all data from the Hibernate session while still inside the transaction.
        final UUID resourceUuid = this.getUuid();
        final String resourceClassName = this.getClass().getSimpleName();
        final Long maxCores = this.getMaxOfferedCores();
        final Long maxMemory = this.getMaxOfferedMemory();

        final List<Bind> bindList = new ArrayList<Bind>();
        log.debug(
            "Resolving volume mounts for compute resource [{}]",
            resourceUuid
            );
        for (AbstractVolumeMountEntity volumeMount : this.getVolumeMountEntities())
            {
            log.debug(
                "Volume mount [{}] type [{}]",
                volumeMount.getUuid(),
                volumeMount.getClass().getSimpleName()
                );
            if (volumeMount instanceof SimpleVolumeMountEntity)
                {
                SimpleVolumeMountEntity simpleMount = (SimpleVolumeMountEntity) volumeMount;
                AbstractDataResourceEntity dataResource = simpleMount.getDataResource();
                log.debug(
                    "SimpleVolumeMount [{}] dataResource [{}]",
                    simpleMount.getUuid(),
                    dataResource != null ? dataResource.getUuid() + " " + dataResource.getClass().getSimpleName() : "null"
                    );
                if (dataResource != null)
                    {
                    AbstractStorageResource storage = dataResource.getStorage();
                    log.debug(
                        "DataResource [{}] storage [{}]",
                        dataResource.getUuid(),
                        storage != null ? storage.getClass().getSimpleName() : "null"
                        );
                    if (storage != null)
                        {
                        String containerPath = simpleMount.getPath();
                        AccessMode accessMode = AccessMode.rw;
                        if (simpleMount.getMode() == ModeEnum.READONLY)
                            {
                            accessMode = AccessMode.ro;
                            }
                        DockerStorageLinkerImpl linkerBean = new DockerStorageLinkerImpl(
                            containerPath,
                            accessMode
                            );
                        storage.link(linkerBean);
                        if (linkerBean.isComplete())
                            {
                            Bind bind = linkerBean.toBind();
                            log.debug(
                                "Adding bind [{}] for volume mount [{}]",
                                bind,
                                simpleMount.getUuid()
                                );
                            bindList.add(bind);
                            }
                        else {
                            log.warn(
                                "Linker bean incomplete for volume mount [{}], storage [{}] - skipping",
                                simpleMount.getUuid(),
                                storage.getClass().getSimpleName()
                                );
                            }
                        }
                    }
                }
            else {
                log.debug(
                    "Volume mount [{}] is not a SimpleVolumeMountEntity, skipping",
                    volumeMount.getUuid()
                    );
                }
            }
        log.debug(
            "Resolved [{}] bind mounts for compute resource [{}]",
            bindList.size(),
            resourceUuid
            );

        final AbstractExecutableEntity executable = this.session.getExecutable();
        final String imageName;
        final List<String> variablesList = new ArrayList<String>();
        final List<String> commandList = new ArrayList<String>();

        if (executable instanceof DockerContainerEntity)
            {
            DockerContainerEntity dockerExecutable = (DockerContainerEntity) executable;
            DockerContainer.DockerContainerImage image = dockerExecutable.getImage();
            // TODO We should iterate the list rather than just taking the first one.
            if (image != null && image.getLocations() != null && !image.getLocations().isEmpty())
                {
                imageName = image.getLocations().get(0);
                }
            else
                {
                imageName = null;
                // TODO fail the prepare step
                return ProcessingAction.NO_ACTION;
                }
            Map<String, String> environment = dockerExecutable.getEnvironment();
            if (environment != null)
                {
                for (Map.Entry<String, String> entry : environment.entrySet())
                    {
                    variablesList.add(entry.getKey() + "=" + entry.getValue());
                    }
                }
            List<String> command = dockerExecutable.getCommand();
            if (command != null)
                {
                commandList.addAll(command);
                }
            }
        else
            {
            imageName = null;
            // TODO fail the prepare step
            return ProcessingAction.NO_ACTION;
            }

        final DockerClientFactory clientFactory ;
        if (platform instanceof DockerPlatform)
            {
            clientFactory = ((DockerPlatform) platform).getDockerClientFactory();
            }
        else {
            clientFactory = null;
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform]",
                platform.getClass().getSimpleName()
                );
            // TODO fail the prepare step
            return ProcessingAction.NO_ACTION;
            }
        
        return new ComponentProcessingAction()
            {

            private String containerId;
            private IvoaLifecyclePhase nextPhase = IvoaLifecyclePhase.AVAILABLE;

            @Override
            public void preProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Pre-processing component [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                }
            
            @Override
            public void process()
                {
                log.debug(
                    "Preparing Docker container [{}][{}]",
                    resourceUuid,
                    resourceClassName
                    );

                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "Unable to create Docker client. CONTAINER_HOST / DOCKER_HOST environment variable may not be set"
                            );
                        nextPhase = IvoaLifecyclePhase.FAILED;
                        // TODO Add some messages to explain why.
                        return ;
                        }

                    //
                    // Verify the image is in the local cache.
                    // The image should already have been pulled by the
                    // DockerDockerContainerEntity prepare phase.
                    try {
                        dockerClient.inspectImageCmd(imageName).exec();
                        log.debug(
                            "Image [{}] found in local cache",
                            imageName
                            );
                        }
                    catch (NotFoundException e)
                        {
                        log.error(
                            "Image [{}] not found in local cache for compute resource [{}]. "
                            + "The executable prepare phase should have pulled it.",
                            imageName,
                            resourceUuid
                            );
                        nextPhase = IvoaLifecyclePhase.FAILED;
                        return;
                        }

                    //
                    // Configure host resource limits based on offered cores and memory.
                    // TODO also check the minCores and MinMemory.
                    HostConfig hostConfig = HostConfig.newHostConfig();
                    boolean hasResourceLimits = false;
                    if (maxCores != null)
                        {
                        long nanoCpus = maxCores * 1_000_000_000L;
                        hostConfig.withNanoCPUs(nanoCpus);
                        hasResourceLimits = true;
                        }
                    if (maxMemory != null)
                        {
                        long memoryBytes = maxMemory * 1_073_741_824L;
                        hostConfig.withMemory(memoryBytes);
                        hasResourceLimits = true;
                        }

                    //
                    // Add bind mounts to the host configuration.
                    if (!bindList.isEmpty())
                        {
                        hostConfig.withBinds(bindList);
                        }

                    //
                    // Create and start the container, retrying without resource limits
                    // if the cgroup controllers are not available (e.g. nested containers).
                    this.containerId = createAndStartContainer(
                        dockerClient,
                        imageName,
                        variablesList,
                        commandList,
                        hostConfig,
                        resourceUuid
                        );
                    if (this.containerId == null && hasResourceLimits)
                        {
                        log.warn(
                            "Retrying without resource limits for [{}]",
                            resourceUuid
                            );
                        HostConfig retryConfig = HostConfig.newHostConfig();
                        if (!bindList.isEmpty())
                            {
                            retryConfig.withBinds(bindList);
                            }
                        this.containerId = createAndStartContainer(
                            dockerClient,
                            imageName,
                            variablesList,
                            commandList,
                            retryConfig,
                            resourceUuid
                            );
                        }

                    if (this.containerId != null)
                        {
                        log.debug(
                            "Docker container started [{}] for resource [{}]",
                            this.containerId,
                            resourceUuid
                            );
                        nextPhase = IvoaLifecyclePhase.RUNNING;
                        }
                    else
                        {
                        nextPhase = IvoaLifecyclePhase.FAILED;
                        }
                    }
                catch (Exception e)
                    {
                    log.error(
                        "Failed to prepare Docker container [{}]",
                        resourceUuid,
                        e
                        );
                    nextPhase = IvoaLifecyclePhase.FAILED;
                    }
                }

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Post processing [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                if (component instanceof DockerSimpleComputeResourceEntity)
                    {
                    postProcess(
                        (DockerSimpleComputeResourceEntity) component
                        );
                    }
                else {
                    log.error(  
                        "Unexpected component type [{}] post processing [{}][{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    nextPhase = IvoaLifecyclePhase.FAILED;
                    // TODO Add some messages to explain why.
                    }
                }
                
            public void postProcess(final DockerSimpleComputeResourceEntity component)
                {
                log.debug(
                    "Post processing Docker compute resource [{}][{}] with container [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.containerId
                    );
                component.dockerContainerId = this.containerId;
                component.setPhase(
                    nextPhase
                    );
                }
            };
        }

    /**
     * Create and start a Docker container, returning the container ID on success or null on failure.
     */
    private String createAndStartContainer(
        final DockerClient dockerClient,
        final String imageName,
        final List<String> envList,
        final List<String> cmdList,
        final HostConfig hostConfig,
        final UUID resourceUuid
        )
        {
        String id = null;
        try {
            log.debug(
                "Creating Docker container from image [{}]",
                imageName
                );
            var createCmd = dockerClient.createContainerCmd(imageName)
                .withEnv(envList)
                .withHostConfig(hostConfig);
            if (!cmdList.isEmpty())
                {
                createCmd.withCmd(cmdList);
                }
            CreateContainerResponse container = createCmd.exec();
            id = container.getId();

            log.debug(
                "Starting Docker container [{}]",
                id
                );
            dockerClient.startContainerCmd(id).exec();
            return id;
            }
        catch (Exception e)
            {
            log.warn(
                "Failed to create/start Docker container for [{}]: {}",
                resourceUuid,
                e.getMessage()
                );
            if (id != null)
                {
                try {
                    log.debug(
                        "Removing failed Docker container [{}] for resource [{}]",
                        id,
                        resourceUuid
                        );
                    dockerClient.removeContainerCmd(id)
                        .withForce(true)
                        .exec();
                    }
                catch (Exception removeEx)
                    {
                    log.warn(
                        "Failed to remove Docker container [{}] for resource [{}]: {}",
                        id,
                        resourceUuid,
                        removeEx.getMessage()
                        );
                    }
                }
            return null;
            }
        }

    @Override
    public ProcessingAction getMonitorAction(final Platform platform, final ComponentProcessingRequest request)
        {
        final UUID resourceUuid = this.getUuid();
        final String resourceClassName = this.getClass().getSimpleName();
        final String containerId = this.dockerContainerId;

        if (containerId == null || containerId.isEmpty())
            {
            log.error(
                "No Docker container ID for resource [{}][{}]",
                resourceUuid,
                resourceClassName
                );
            // TODO fail the component.
            return ProcessingAction.NO_ACTION;
            }

        final DockerClientFactory clientFactory ;
        if (platform instanceof DockerPlatform)
            {
            clientFactory = ((DockerPlatform) platform).getDockerClientFactory();
            }
        else {
            clientFactory = null;
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform]",
                platform.getClass().getSimpleName()
                );
            // TODO fail the component.
            return ProcessingAction.NO_ACTION;
            }
        
        return new ComponentProcessingAction()
            {
            private IvoaLifecyclePhase nextPhase = IvoaLifecyclePhase.RUNNING;
            private Integer exitCode;
            private String capturedStdout;
            private String capturedStderr;

            @Override
            public void preProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Pre-processing component [{}][{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                }

            @Override
            public void process()
                {
                log.debug(
                    "Monitoring Docker container [{}] for resource [{}][{}]",
                    containerId,
                    resourceUuid,
                    resourceClassName
                    );
                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "CONTAINER_HOST / DOCKER_HOST environment variable is not set"
                            );
                        this.nextPhase = IvoaLifecyclePhase.FAILED;
                        return ;
                        }

                    InspectContainerResponse inspection = dockerClient.inspectContainerCmd(containerId).exec();
                    InspectContainerResponse.ContainerState state = inspection.getState();

                    log.debug(
                        "Container [{}] state: status=[{}] running=[{}] exitCode=[{}]",
                        containerId,
                        state.getStatus(),
                        state.getRunning(),
                        state.getExitCode()
                        );

                    if (Boolean.TRUE.equals(state.getRunning()))
                        {
                        this.nextPhase = IvoaLifecyclePhase.RUNNING;
                        }
                    else {
                        this.exitCode = state.getExitCode();

                        // Capture stdout and stderr before the container is removed.
                        log.debug(
                            "Capturing logs for exited container [{}]",
                            containerId
                            );
                        StringBuilder stdoutBuilder = new StringBuilder();
                        StringBuilder stderrBuilder = new StringBuilder();
                        captureContainerLogs(
                            dockerClient,
                            containerId,
                            stdoutBuilder,
                            stderrBuilder
                            );
                        this.capturedStdout = truncateLog(
                            stdoutBuilder.toString(),
                            MAX_LOG_CHARS
                            );
                        this.capturedStderr = truncateLog(
                            stderrBuilder.toString(),
                            MAX_LOG_CHARS
                            );
                        log.debug(
                            "Captured [{}] chars stdout, [{}] chars stderr for container [{}]",
                            this.capturedStdout.length(),
                            this.capturedStderr.length(),
                            containerId
                            );
                        log.debug(
                            "Container [{}] stdout: [{}]",
                            containerId,
                            this.capturedStdout.substring(0, Math.min(100, this.capturedStdout.length()))
                            );
                        log.debug(
                            "Container [{}] stderr: [{}]",
                            containerId,
                            this.capturedStderr.substring(0, Math.min(100, this.capturedStderr.length()))
                            );

                        if (this.exitCode != null && this.exitCode == 0)
                            {
                            this.nextPhase = IvoaLifecyclePhase.RELEASING;
                            }
                        else {
                            log.warn(
                                "Container [{}] exited with non-zero code [{}]",
                                containerId,
                                this.exitCode
                                );
                            this.nextPhase = IvoaLifecyclePhase.FAILED;
                            }
                        }
                    }
                catch (Exception e)
                    {
                    log.error(
                        "Failed to inspect Docker container [{}] for resource [{}]",
                        containerId,
                        resourceUuid,
                        e
                        );
                    this.nextPhase = IvoaLifecyclePhase.FAILED;
                    }
                }

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Post-processing component [{}][{}] next phase [{}] exit code [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.nextPhase,
                    this.exitCode
                    );
                if (component instanceof DockerSimpleComputeResourceEntity)
                    {
                    postProcess(
                        (DockerSimpleComputeResourceEntity) component
                        );
                    }
                else {
                    log.error(  
                        "Unexpected type [{}] for post processing component [{}][{}]",
                        component.getClass().getSimpleName(),
                        component.getUuid(),
                        component.getClass().getSimpleName()
                        );
                    component.addError(
                        "uri:internal-error",
                        "Unexpected component type, see logs for details"
                        );
                    }
                }

            public void postProcess(final DockerSimpleComputeResourceEntity component)
                {
                component.dockerContainerExitCode = this.exitCode;
                component.containerStdout = this.capturedStdout;
                component.containerStderr = this.capturedStderr;
                component.setPhase( 
                    this.nextPhase
                    );
                }
            };
        }

    @Override
    public ProcessingAction getReleaseAction(Platform platform, ComponentProcessingRequest request)
        {
        final UUID resourceUuid = this.getUuid();
        final String resourceClassName = this.getClass().getSimpleName();
        final String containerId = this.dockerContainerId;

        final DockerClientFactory clientFactory;
        if (platform instanceof DockerPlatform)
            {
            clientFactory = ((DockerPlatform) platform).getDockerClientFactory();
            }
        else {
            clientFactory = null;
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform]",
                platform.getClass().getSimpleName()
                );
            return ProcessingAction.NO_ACTION;
            }

        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Releasing Docker compute resource [{}][{}] container [{}]",
                    resourceUuid,
                    resourceClassName,
                    containerId
                    );
                component.setPhase(
                    IvoaLifecyclePhase.RELEASING
                    );
                }

            @Override
            public void process()
                {
                if (containerId == null || containerId.isEmpty())
                    {
                    log.warn(
                        "No Docker container ID to remove for resource [{}][{}]",
                        resourceUuid,
                        resourceClassName
                        );
                    return;
                    }
                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "Unable to create Docker client for release of [{}][{}]",
                            resourceUuid,
                            resourceClassName
                            );
                        return;
                        }
                    log.debug(
                        "Removing Docker container [{}] for resource [{}][{}]",
                        containerId,
                        resourceUuid,
                        resourceClassName
                        );
                    dockerClient.removeContainerCmd(containerId)
                        .withForce(true)
                        .exec();
                    log.debug(
                        "Docker container [{}] removed for resource [{}][{}]",
                        containerId,
                        resourceUuid,
                        resourceClassName
                        );
                    }
                catch (Exception e)
                    {
                    log.warn(
                        "Failed to remove Docker container [{}] for resource [{}][{}]: {}",
                        containerId,
                        resourceUuid,
                        resourceClassName,
                        e.getMessage()
                        );
                    }
                }

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Release complete for Docker compute resource [{}][{}], setting phase to COMPLETED",
                    resourceUuid,
                    resourceClassName
                    );
                component.setPhase(
                    IvoaLifecyclePhase.COMPLETED
                    );
                }
            };
        }
    }
