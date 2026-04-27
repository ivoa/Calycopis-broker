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

package net.ivoa.calycopis.functional.platfom.docker;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;

import lombok.extern.slf4j.Slf4j;

/**
 * Factory implementation that creates DockerClient instances
 * configured using the CONTAINER_HOST or DOCKER_HOST environment
 * variable or system property.
 * 
 */
@Slf4j
@Component
@Profile("docker")
public class DockerClientFactoryImpl
implements DockerClientFactory
    {

    @Override
    public DockerClient getDockerClient()
        {
        String containerHost = System.getenv("CONTAINER_HOST");
        if (containerHost == null || containerHost.isEmpty())
            {
            containerHost = System.getProperty("CONTAINER_HOST");
            }
        if (containerHost == null || containerHost.isEmpty())
            {
            containerHost = System.getenv("DOCKER_HOST");
            }
        if (containerHost == null || containerHost.isEmpty())
            {
            log.error(
                "CONTAINER_HOST / DOCKER_HOST environment variable is not set"
                );
            return null;
            }
        log.debug("Using container host [{}]", containerHost);
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(containerHost)
            .build();
        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();
        return DockerClientImpl.getInstance(config, httpClient);
        }
    }
