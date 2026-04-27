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

package net.ivoa.calycopis.datamodel.executable.docker.docker;

import java.util.List;
import java.util.Map;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.platfom.docker.DockerPlatform;
import net.ivoa.calycopis.spring.model.IvoaDockerContainer;
import net.ivoa.calycopis.spring.model.IvoaDockerImageSpec;

/**
 * Docker platform specific validator for DockerContainer executables.
 * Checks the local Docker image cache to estimate preparation time
 * and validates image digest integrity.
 * 
 */
@Slf4j
public class DockerDockerContainerValidatorImpl
extends DockerContainerValidatorImpl
implements DockerDockerContainerValidator
    {

    private final DockerPlatform platform;

    public DockerDockerContainerValidatorImpl(
        final DockerPlatform platform
        ){
        super(platform.getDockerContainerEntityFactory());
        this.platform = platform;
        }

    public static final Long CACHED_IMAGE_PREPARE_TIME = 10L;

    public static final Long IMAGE_DOWNLOAD_PREPARE_TIME = 120L;

    public static final Long DEFAULT_RELEASE_TIME = 1L;

    /**
     * Tracks the result of the image cache check performed during validation.
     * CACHED: image is locally available with correct digest (or no digest check needed).
     * NOT_CACHED: image is not in the local Docker cache and needs downloading.
     * DIGEST_MISMATCH: image is locally available but the digest does not match.
     * 
     */
    private enum ImageCacheStatus
        {
        CACHED,
        NOT_CACHED,
        DIGEST_MISMATCH
        }

    private ImageCacheStatus imageCacheStatus = ImageCacheStatus.NOT_CACHED;

    @Override
    public ResultEnum validate(
        final IvoaDockerContainer requested,
        final OfferSetRequestParserContext context
        ){
        // Perform the image cache check before running the standard validation,
        // so that a digest mismatch can fail the whole validation.
        if (requested.getImage() != null)
            {
            this.imageCacheStatus = checkImageCache(
                requested.getImage()
                );
            if (this.imageCacheStatus == ImageCacheStatus.DIGEST_MISMATCH)
                {
                String imageName;
                if (requested.getImage().getLocations() != null
                    && !requested.getImage().getLocations().isEmpty())
                    {
                    imageName = requested.getImage().getLocations().get(0);
                    }
                else {
                    imageName = "unknown";
                    }
                context.addWarning(
                    "urn:image-digest-mismatch",
                    "DockerContainer - local image digest does not match requested digest for [{}]",
                    Map.of(
                        "value",
                        imageName
                        )
                    );
                context.valid(false);
                return ResultEnum.FAILED;
                }
            }

        return super.validate(
            requested,
            context
            );
        }

    /**
     * Check whether the requested image is available in the local Docker cache
     * and whether its digest matches.
     * 
     */
    private ImageCacheStatus checkImageCache(final IvoaDockerImageSpec imageSpec)
        {
        List<String> locations = imageSpec.getLocations();
        if (locations == null || locations.isEmpty())
            {
            return ImageCacheStatus.NOT_CACHED;
            }

        String imageName = locations.get(0);
        String requestedDigest = imageSpec.getDigest();

        try {
            DockerClient dockerClient = this.platform.getDockerClientFactory().getDockerClient();
            if (dockerClient == null)
                {
                log.warn("Unable to connect to Docker service to check image cache");
                return ImageCacheStatus.NOT_CACHED;
                }

            try {
                InspectImageResponse imageInfo = dockerClient.inspectImageCmd(imageName).exec();

                if (requestedDigest != null && !requestedDigest.isEmpty())
                    {
                    String localId = imageInfo.getId();
                    List<String> repoDigests = imageInfo.getRepoDigests();

                    boolean digestMatch = false;
                    if (localId != null && localId.contains(requestedDigest))
                        {
                        digestMatch = true;
                        }
                    if (!digestMatch && repoDigests != null)
                        {
                        for (String repoDigest : repoDigests)
                            {
                            if (repoDigest != null && repoDigest.contains(requestedDigest))
                                {
                                digestMatch = true;
                                break;
                                }
                            }
                        }

                    if (digestMatch)
                        {
                        log.debug(
                            "Image [{}] found in local cache with matching digest",
                            imageName
                            );
                        return ImageCacheStatus.CACHED;
                        }
                    else {
                        log.warn(
                            "Image [{}] found in local cache but digest does not match. "
                            + "Requested [{}], local id [{}], repo digests [{}]",
                            imageName,
                            requestedDigest,
                            localId,
                            repoDigests
                            );
                        return ImageCacheStatus.DIGEST_MISMATCH;
                        }
                    }
                else {
                    log.debug(
                        "Image [{}] found in local cache (no digest check requested)",
                        imageName
                        );
                    return ImageCacheStatus.CACHED;
                    }
                }
            catch (NotFoundException e)
                {
                log.debug(
                    "Image [{}] not found in local Docker cache",
                    imageName
                    );
                return ImageCacheStatus.NOT_CACHED;
                }
            }
        catch (Exception e)
            {
            log.warn(
                "Error checking Docker image cache for [{}]",
                imageName,
                e
                );
            return ImageCacheStatus.NOT_CACHED;
            }
        }

    @Override
    protected Long getPrepareDuration(final IvoaDockerContainer validated)
        {
        switch(this.imageCacheStatus)
            {
            case CACHED:
                return CACHED_IMAGE_PREPARE_TIME;
            case NOT_CACHED:
            default:
                return IMAGE_DOWNLOAD_PREPARE_TIME;
            }
        }

    @Override
    protected Long getReleaseDuration(final IvoaDockerContainer validated)
        {
        return DEFAULT_RELEASE_TIME;
        }

    @Override
    protected boolean validateNetworkPortPath(final String path, final OfferSetRequestParserContext context)
        {
        return true;
        }

    @Override
    protected boolean validateNetworkPortNumber(final Integer portnum, final OfferSetRequestParserContext context)
        {
        return true;
        }

    }
