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

import com.github.dockerjava.api.DockerClient;

/**
 * Factory interface for creating DockerClient instances
 * configured for the local Docker/Podman service.
 * 
 */
public interface DockerClientFactory
    {

    /**
     * Create a DockerClient configured to connect to the local
     * Docker/Podman service.
     * Returns null if the Docker host is not configured.
     * 
     */
    public DockerClient getDockerClient();

    }
