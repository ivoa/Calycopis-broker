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
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.storage.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateVolumeResponse;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageLinker;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResourceEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.platfom.docker.DockerClientFactory;
import net.ivoa.calycopis.functional.platfom.docker.DockerPlatform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "dockervolumemountstorage"
    )
@DiscriminatorValue(
    value="uri:docker-volume-storage"
    )
public class DockerVolumeMountStorageEntity
extends SimpleStorageResourceEntity
implements DockerVolumeMountStorage
    {

    /**
     * 
     */
    public DockerVolumeMountStorageEntity()
        {
        super();
        }

    /**
     * 
     */
    public DockerVolumeMountStorageEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        super(
            session,
            result
            );
        }

    @Column(name = "volumeident")
    private String volumeIdent;
    @Override
    public String getMountPath()
        {
        return this.volumeIdent;
        }

    @Override
    public void link(final AbstractStorageLinker linker)
        {
        if (linker instanceof DockerStorageLinker)
            {
            DockerStorageLinker dockerLinker = (DockerStorageLinker) linker;
            log.debug(
                "DockerVolumeMountStorageEntity linking volume ident [{}]",
                this.volumeIdent
                );
            dockerLinker.setSourcePath(this.volumeIdent);
            }
        }

    @Override
    public ProcessingAction getPrepareAction(
        final Platform platform,
        final ComponentProcessingRequest request
        ){
        final String storageUuid = this.getUuid().toString();

        final DockerClientFactory clientFactory;
        if (platform instanceof DockerPlatform)
            {
            clientFactory = ((DockerPlatform) platform).getDockerClientFactory();
            }
        else {
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform] for storage [{}]",
                platform.getClass().getSimpleName(),
                storageUuid
                );
            return ProcessingAction.NO_ACTION;
            }

        return new ComponentProcessingAction()
            {
            private LifecyclePhase nextPhase = LifecyclePhase.AVAILABLE;
            private String createdVolumeName;

            @Override
            public void preProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Pre-processing volume storage [{}]",
                    storageUuid
                    );
                }

            @Override
            public void process()
                {
                log.debug(
                    "Creating Docker volume for storage [{}]",
                    storageUuid
                    );
                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "Unable to create Docker client for storage [{}]",
                            storageUuid
                            );
                        nextPhase = LifecyclePhase.FAILED;
                        return;
                        }
                    CreateVolumeResponse volume = dockerClient.createVolumeCmd().exec();
                    this.createdVolumeName = volume.getName();
                    log.debug(
                        "Created Docker volume [{}] for storage [{}]",
                        this.createdVolumeName,
                        storageUuid
                        );
                    }
                catch (Exception ex)
                    {
                    log.error(
                        "Failed to create Docker volume for storage [{}]",
                        storageUuid,
                        ex
                        );
                    nextPhase = LifecyclePhase.FAILED;
                    }
                }

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                if (nextPhase == LifecyclePhase.FAILED)
                    {
                    component.addError(
                        "uri:docker-volume-create-failed",
                        "Failed to create Docker volume, see logs for details"
                        );
                    }
                else if (component instanceof DockerVolumeMountStorageEntity)
                    {
                    ((DockerVolumeMountStorageEntity) component).volumeIdent = this.createdVolumeName;
                    log.debug(
                        "Stored volume ident [{}] on storage entity [{}]",
                        this.createdVolumeName,
                        storageUuid
                        );
                    }
                component.setPhase(nextPhase);
                }
            };
        }

    @Override
    public ProcessingAction getMonitorAction(
        final Platform platform,
        final ComponentProcessingRequest request
        ){
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(LifecyclePhase.COMPLETED);
                }
            };
        }

    @Override
    public ProcessingAction getReleaseAction(
        final Platform platform,
        final ComponentProcessingRequest request
        ){
        final String storageUuid = this.getUuid().toString();
        final String volumeName = this.volumeIdent;

        final DockerClientFactory clientFactory;
        if (platform instanceof DockerPlatform)
            {
            clientFactory = ((DockerPlatform) platform).getDockerClientFactory();
            }
        else {
            log.error(
                "Unexpected platform type [{}] expected [DockerPlatform] for storage [{}]",
                platform.getClass().getSimpleName(),
                storageUuid
                );
            return ProcessingAction.NO_ACTION;
            }

        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Pre-processing release for volume storage [{}]",
                    storageUuid
                    );
                }

            @Override
            public void process()
                {
                if (volumeName == null || volumeName.isEmpty())
                    {
                    log.warn(
                        "No volume identifier to remove for storage [{}]",
                        storageUuid
                        );
                    return;
                    }
                log.debug(
                    "Removing Docker volume [{}] for storage [{}]",
                    volumeName,
                    storageUuid
                    );
                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient != null)
                        {
                        dockerClient.removeVolumeCmd(volumeName).exec();
                        log.debug(
                            "Removed Docker volume [{}] for storage [{}]",
                            volumeName,
                            storageUuid
                            );
                        }
                    }
                catch (Exception ex)
                    {
                    log.warn(
                        "Failed to remove Docker volume [{}] for storage [{}]",
                        volumeName,
                        storageUuid,
                        ex
                        );
                    }
                }

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(LifecyclePhase.COMPLETED);
                }
            };
        }
    }
