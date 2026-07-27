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
 *       "value": 70,
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

package net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http;

import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponent;
import net.ivoa.calycopis.broker.engine.entities.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResource;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.DockerStorageLinkerImpl;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.engine.functional.platform.docker.DockerClientFactory;
import net.ivoa.calycopis.broker.engine.functional.platform.docker.DockerPlatform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.openapi.spring.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "dockerhttpdataresources"
    )
public class DockerSimpleDataHttpResourceEntity
extends SimpleDataResourceEntity
implements DockerSimpleDataHttpResource
    {
    
    /**
     * Protected constructor for JPA entities.
     * 
     */
    protected DockerSimpleDataHttpResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor used by our factory.
     * 
     */
    protected DockerSimpleDataHttpResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final DockerSimpleDataHttpResourceValidator.Result result
        ){
        super(
            session,
            storage,
            result
            );
        }

    private static final long DOWNLOAD_TIMEOUT_SECONDS = 300;

    @Override
    public ProcessingAction getPrepareAction(Platform platform, ComponentProcessingRequest request)
        {
        final String dataUuid = this.getUuid().toString();
        final AbstractStorageResource storage = this.getStorage();

        if (storage == null)
            {
            log.error(
                "No storage resource associated with data resource [{}]",
                dataUuid
                );
            return new ComponentProcessingAction()
                {
                @Override
                public void preProcess(final LifecycleComponent component) {}
                @Override
                public void process() {}
                @Override
                public void postProcess(final LifecycleComponent component)
                    {
                    component.addError(
                        "uri:missing-storage",
                        "No storage resource associated with this data resource"
                        );
                    component.setPhase(IvoaLifecyclePhase.FAILED);
                    }
                };
            }

        if (storage.getPhase() != IvoaLifecyclePhase.AVAILABLE)
            {
            log.debug(
                "Storage [{}] for data resource [{}] is not yet AVAILABLE (phase [{}]), will retry",
                storage.getUuid(),
                dataUuid,
                storage.getPhase()
                );
            return new ComponentProcessingAction()
                {
                @Override
                public void preProcess(final LifecycleComponent component) {}
                @Override
                public void process() {}
                @Override
                public void postProcess(final LifecycleComponent component)
                    {
                    // Leave phase as PREPARING — the processing loop will retry.
                    }
                };
            }

        final DockerStorageLinkerImpl volumeLinker = new DockerStorageLinkerImpl("/data", AccessMode.rw);
        ((AbstractStorageResourceEntity) storage).link(volumeLinker);
        final String volumeName = volumeLinker.getSourcePath();
        if (volumeName == null || volumeName.isEmpty())
            {
            log.error(
                "Storage [{}] for data resource [{}] did not provide a volume name via the linker (type [{}])",
                storage.getUuid(),
                dataUuid,
                storage.getClass().getSimpleName()
                );
            return new ComponentProcessingAction()
                {
                @Override
                public void preProcess(final LifecycleComponent component) {}
                @Override
                public void process() {}
                @Override
                public void postProcess(final LifecycleComponent component)
                    {
                    component.addError(
                        "uri:missing-volume-name",
                        "Storage resource did not provide a volume name"
                        );
                    component.setPhase(IvoaLifecyclePhase.FAILED);
                    }
                };
            }

        final String dataUrl = this.getLocation();

        final DockerClientFactory clientFactory;
        final String helperImage;
        if (platform instanceof DockerPlatform)
            {
            DockerPlatform dockerPlatform = (DockerPlatform) platform;
            clientFactory = dockerPlatform.getDockerClientFactory();
            helperImage = dockerPlatform.getDockerSettings().getHelperImage();
            }
        else {
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform] for data resource [{}]",
                platform.getClass().getSimpleName(),
                dataUuid
                );
            return ProcessingAction.NO_ACTION;
            }

        return new ComponentProcessingAction()
            {
            private IvoaLifecyclePhase nextPhase = IvoaLifecyclePhase.AVAILABLE;
            private String errorMessage = null;

            @Override
            public void preProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Pre-processing HTTP data resource [{}], downloading [{}] into volume [{}]",
                    dataUuid,
                    dataUrl,
                    volumeName
                    );
                }

            @Override
            public void process()
                {
                String helperContainerId = null;
                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "Unable to create Docker client for data resource [{}]",
                            dataUuid
                            );
                        nextPhase = IvoaLifecyclePhase.FAILED;
                        errorMessage = "Unable to create Docker client";
                        return;
                        }

                    try {
                        dockerClient.inspectImageCmd(helperImage).exec();
                        }
                    catch (NotFoundException ex)
                        {
                        log.debug(
                            "Helper image [{}] not found locally, pulling",
                            helperImage
                            );
                        try {
                            dockerClient.pullImageCmd(helperImage)
                                .start()
                                .awaitCompletion(120, TimeUnit.SECONDS);
                            }
                        catch (Exception pullEx)
                            {
                            log.error(
                                "Failed to pull helper image [{}] for data resource [{}]",
                                helperImage,
                                dataUuid,
                                pullEx
                                );
                            nextPhase = IvoaLifecyclePhase.FAILED;
                            errorMessage = "Failed to pull helper image " + helperImage;
                            return;
                            }
                        }

                    HostConfig hostConfig = HostConfig.newHostConfig()
                        .withBinds(
                            new Bind(
                                volumeName,
                                new Volume("/data"),
                                AccessMode.rw
                                )
                            );

                    CreateContainerResponse container = dockerClient.createContainerCmd(helperImage)
                        .withCmd("wget", "-O", "/data/content", dataUrl)
                        .withHostConfig(hostConfig)
                        .exec();
                    helperContainerId = container.getId();

                    log.debug(
                        "Starting helper container [{}] to download [{}] into volume [{}]",
                        helperContainerId,
                        dataUrl,
                        volumeName
                        );
                    dockerClient.startContainerCmd(helperContainerId).exec();

                    int exitCode = dockerClient.waitContainerCmd(helperContainerId)
                        .exec(new WaitContainerResultCallback())
                        .awaitStatusCode(
                            (int) DOWNLOAD_TIMEOUT_SECONDS,
                            TimeUnit.SECONDS
                            );

                    if (exitCode != 0)
                        {
                        log.error(
                            "Helper container [{}] exited with code [{}] for data resource [{}]",
                            helperContainerId,
                            exitCode,
                            dataUuid
                            );
                        nextPhase = IvoaLifecyclePhase.FAILED;
                        errorMessage = "Download failed with exit code " + exitCode;
                        }
                    else {
                        log.debug(
                            "Helper container [{}] completed successfully for data resource [{}]",
                            helperContainerId,
                            dataUuid
                            );
                        }

                    try {
                        dockerClient.removeContainerCmd(helperContainerId).exec();
                        }
                    catch (Exception removeEx)
                        {
                        log.warn(
                            "Failed to remove helper container [{}]",
                            helperContainerId,
                            removeEx
                            );
                        }
                    }
                catch (Exception ex)
                    {
                    log.error(
                        "Failed to download data for data resource [{}]",
                        dataUuid,
                        ex
                        );
                    nextPhase = IvoaLifecyclePhase.FAILED;
                    errorMessage = "Download failed: " + ex.getMessage();
                    }
                }

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                if (nextPhase == IvoaLifecyclePhase.FAILED && errorMessage != null)
                    {
                    component.addError(
                        "uri:http-download-failed",
                        errorMessage
                        );
                    }
                component.setPhase(nextPhase);
                }
            };
        }

    @Override
    public ProcessingAction getMonitorAction(Platform platform, ComponentProcessingRequest request)
        {
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(IvoaLifecyclePhase.COMPLETED);
                }
            };
        }

    @Override
    public ProcessingAction getReleaseAction(Platform platform, ComponentProcessingRequest request)
        {
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(IvoaLifecyclePhase.COMPLETED);
                }
            };
        }

    }
