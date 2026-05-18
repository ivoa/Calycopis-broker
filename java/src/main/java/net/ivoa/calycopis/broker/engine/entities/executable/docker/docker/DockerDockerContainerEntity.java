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
 *
 */

package net.ivoa.calycopis.broker.engine.entities.executable.docker.docker;

import java.util.List;
import java.util.UUID;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponent;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.platfom.Platform;
import net.ivoa.calycopis.broker.engine.functional.platfom.docker.DockerClientFactory;
import net.ivoa.calycopis.broker.engine.functional.platfom.docker.DockerPlatform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.broker.engine.functional.processing.mock.MockDelayAction;
import net.ivoa.calycopis.broker.engine.functional.processing.mock.MockReleaseAction;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;

/**
 * Docker platform specific DockerContainer entity.
 * Handles image pulling and digest verification when preparing the executable.
 * 
 */
@Slf4j
@Entity
@Table(
    name = "dockerdockercontainers"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class DockerDockerContainerEntity
extends DockerContainerEntity
implements DockerDockerContainer
    {

    /**
     * Protected constructor for JPA entities.
     * 
     */
    protected DockerDockerContainerEntity()
        {
        super();
        }

    /**
     * Protected constructor used by our factory.
     *
     */
    protected DockerDockerContainerEntity(
        final SimpleExecutionSessionEntity session,
        final DockerDockerContainerValidator.Result result
        ){
        super(
            session,
            result
            );
        }

    @Column(name="imagedownloadmillis")
    private Long imageDownloadMillis;

    @Override
    public Long getImageDownloadMillis()
        {
        return this.imageDownloadMillis;
        }

    @Override
    public ProcessingAction getPrepareAction(final Platform platform, final ComponentProcessingRequest request)
        {
        // Eagerly resolve data from the Hibernate session while still in a transaction.
        final UUID entityUuid = this.getUuid();
        final String entityClassName = this.getClass().getSimpleName();

        final DockerContainerImage dockerContainerImage = this.getImage();
        final String imageName;
        final String requestedDigest;

        if (dockerContainerImage == null)
            {
            // TODO fail the prepare step
            return ProcessingAction.NO_ACTION;
            }
        else {
            // TODO We should iterate the list rather than just taking the first one.
            List<String> locations = dockerContainerImage.getLocations();
            if (locations != null && !locations.isEmpty())
                {
                imageName = locations.get(0);
                }
            else {
                imageName = null;
                }
            requestedDigest = dockerContainerImage.getDigest();
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
            private IvoaLifecyclePhase nextPhase = IvoaLifecyclePhase.PREPARING;
            private long downloadTimeMillis = 0L;

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
                    "Preparing DockerDockerContainer [{}][{}] image [{}]",
                    entityUuid,
                    entityClassName,
                    imageName
                    );

                if (imageName == null)
                    {
                    log.error(
                        "No image location for DockerDockerContainer [{}]",
                        entityUuid
                        );
                    // TODO Add a message explaining why it failed.
                    this.nextPhase = IvoaLifecyclePhase.FAILED;
                    return;
                    }

                try {
                    DockerClient dockerClient = clientFactory.getDockerClient();
                    if (dockerClient == null)
                        {
                        log.error(
                            "CONTAINER_HOST / DOCKER_HOST environment variable is not set"
                            );
                        // TODO Add a message explaining why it failed.
                        this.nextPhase = IvoaLifecyclePhase.FAILED;
                        return;
                        }

                    // Check if the image is already in the local cache.
                    boolean imageAvailable = false;
                    try {
                        InspectImageResponse imageInfo = dockerClient.inspectImageCmd(imageName).exec();
                        log.debug(
                            "Image [{}] found in local cache, id [{}]",
                            imageName,
                            imageInfo.getId()
                            );

                        // Verify the digest if one was requested.
                        if (requestedDigest != null && !requestedDigest.isEmpty())
                            {
                            boolean digestMatch = checkDigest(
                                imageInfo,
                                requestedDigest
                                );
                            if (digestMatch)
                                {
                                log.debug(
                                    "Image [{}] digest matches, no download needed",
                                    imageName
                                    );
                                imageAvailable = true;
                                }
                            else {
                                log.error(
                                    "Image [{}] found in local cache but digest does not match [{}][{}]",
                                    imageName,
                                    requestedDigest,
                                    imageInfo.getId()
                                    );
                                // TODO Add a message explaining why it failed.
                                this.nextPhase = IvoaLifecyclePhase.FAILED;
                                return;
                                }
                            }
                        else {
                            imageAvailable = true;
                            }
                        }
                    catch (NotFoundException e)
                        {
                        log.debug(
                            "Image [{}] not in local cache, will pull",
                            imageName
                            );
                        }

                    // Pull the image if it's not already available.
                    if (!imageAvailable)
                        {
                        log.debug(
                            "Pulling Docker image [{}]",
                            imageName
                            );
                        long pullStart = System.currentTimeMillis();
                        dockerClient.pullImageCmd(imageName)
                            .exec(new PullImageResultCallback())
                            .awaitCompletion();
                        this.downloadTimeMillis = System.currentTimeMillis() - pullStart;
                        log.debug(
                            "Image [{}] pulled in [{}] ms",
                            imageName,
                            this.downloadTimeMillis
                            );

                        // Verify the digest after download if requested.
                        if (requestedDigest != null && !requestedDigest.isEmpty())
                            {
                            InspectImageResponse downloadedImage = dockerClient.inspectImageCmd(imageName).exec();
                            boolean digestMatch = checkDigest(
                                downloadedImage,
                                requestedDigest
                                );
                            if (!digestMatch)
                                {
                                log.error(
                                    "Downloaded image [{}] digest does not match. "
                                    + "Requested [{}], downloaded id [{}]",
                                    imageName,
                                    requestedDigest,
                                    downloadedImage.getId()
                                    );
                                // TODO Add a message explaining why it failed.
                                this.nextPhase = IvoaLifecyclePhase.FAILED;
                                return;
                                }
                            }
                        }
                    }
                catch (Exception e)
                    {
                    log.error(
                        "Failed to prepare Docker image [{}] for [{}]",
                        imageName,
                        entityUuid,
                        e
                        );
                    // TODO Add a message explaining why it failed.
                    this.nextPhase = IvoaLifecyclePhase.FAILED;
                    return;
                    }
                // If we got this far, the image is available.
                this.nextPhase = IvoaLifecyclePhase.AVAILABLE;
                }

            private boolean checkDigest(
                final InspectImageResponse imageInfo,
                final String digest
                ){
                String localId = imageInfo.getId();
                if (localId != null && localId.contains(digest))
                    {
                    return true;
                    }
                List<String> repoDigests = imageInfo.getRepoDigests();
                if (repoDigests != null)
                    {
                    for (String repoDigest : repoDigests)
                        {
                        if (repoDigest != null && repoDigest.contains(digest))
                            {
                            return true;
                            }
                        }
                    }
                return false;
                }

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Post-processing component [{}][{}] next phase [{}]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    this.nextPhase
                    );
                if (component instanceof DockerDockerContainerEntity)
                    {
                    this.postProcess(
                        (DockerDockerContainerEntity) component
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
                    component.setPhase(
                        IvoaLifecyclePhase.FAILED
                        );
                    }
                }

            public void postProcess(final DockerDockerContainerEntity component)
                {
                component.imageDownloadMillis = this.downloadTimeMillis;
                component.setPhase(
                    this.nextPhase
                    );
                }
            };
        }

    @Override
    public ProcessingAction getReleaseAction(final Platform platform, final ComponentProcessingRequest request)
        {
        return new MockReleaseAction(
            this,
            0
            );
        }

    @Override
    public ProcessingAction getMonitorAction(Platform platform, ComponentProcessingRequest request)
        {
        return new MockDelayAction(
            this,
            0
            );
        }
    }
