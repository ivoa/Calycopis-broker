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
 *       "value": 10,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.functional.platfom.docker;

import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.functional.platfom.Platform;

/**
 * Docker platform interface, extending the base Platform
 * with Docker-specific services.
 * 
 */
public interface DockerPlatform extends Platform
    {

    /**
     * Get the DockerClientFactory for this platform.
     *
     */
    public DockerClientFactory getDockerClientFactory();

    /**
     * Get the DockerContainerEntityFactory for this platform.
     *
     */
    public DockerContainerEntityFactory getDockerContainerEntityFactory();

    /**
     * Get the DockerSettings for this platform.
     *
     */
    public DockerSettings getDockerSettings();
    
    }
