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

package net.ivoa.calycopis.datamodel.compute.simple.docker;

import net.ivoa.calycopis.datamodel.compute.AbstractComputeResource;

/**
 * Public interface for a Docker SimpleComputeResource.
 *
 */
public interface DockerSimpleComputeResource
    extends AbstractComputeResource
    {

    /**
     * Get the Docker container ID.
     *
     */
    public String getDockerContainerId();

    /**
     * Get the Docker container exit code.
     *
     */
    public Integer getDockerContainerExitCode();

    /**
     * Get the captured stdout from the Docker container.
     * May be null if the container has not yet exited or
     * if log capture failed.
     *
     */
    public String getContainerStdout();

    /**
     * Get the captured stderr from the Docker container.
     * May be null if the container has not yet exited or
     * if log capture failed.
     *
     */
    public String getContainerStderr();

    }
