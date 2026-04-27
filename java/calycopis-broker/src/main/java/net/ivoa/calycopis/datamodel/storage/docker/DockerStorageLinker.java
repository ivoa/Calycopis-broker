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
 *     "timestamp": "2026-04-11T10:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 75,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.storage.docker;

import net.ivoa.calycopis.datamodel.storage.AbstractStorageLinker;

/**
 * Docker-specific storage linker interface.
 * Allows a storage entity to set the source path (host path
 * for a bind mount, or volume name for a volume mount) that
 * will be used to construct the Docker Bind specification.
 *
 */
public interface DockerStorageLinker
extends AbstractStorageLinker
    {

    /**
     * Set the source path for the Docker mount.
     * For a bind mount this is the host file path.
     * For a volume mount this is the Docker volume name.
     *
     */
    void setSourcePath(String sourcePath);

    }
