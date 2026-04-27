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
 *     },
 *     {
 *     "timestamp": "2026-04-14T17:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.storage.docker;

import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;

/**
 * A bean that collects the parts of a Docker Bind specification
 * from multiple participants.
 *
 * The container path and access mode are provided by the compute
 * resource (via the constructor). The source path is provided by
 * the storage entity (via setSourcePath).
 *
 */
public class DockerStorageLinkerBean
implements DockerStorageLinker
    {

    private final String containerPath;
    private final AccessMode accessMode;
    private String sourcePath;

    public DockerStorageLinkerBean(
        final String containerPath,
        final AccessMode accessMode
        ){
        this.containerPath = containerPath;
        this.accessMode = accessMode;
        }

    @Override
    public void setSourcePath(final String sourcePath)
        {
        this.sourcePath = sourcePath;
        }

    public String getSourcePath()
        {
        return this.sourcePath;
        }

    /**
     * Check whether all required fields have been populated.
     *
     */
    public boolean isComplete()
        {
        return (this.sourcePath != null)
            && (this.containerPath != null)
            && (this.accessMode != null);
        }

    /**
     * Build a Docker Bind from the collected information.
     * Returns null if the bean is not complete.
     *
     */
    public Bind toBind()
        {
        if (!isComplete())
            {
            return null;
            }
        return new Bind(
            this.sourcePath,
            new Volume(this.containerPath),
            this.accessMode
            );
        }
    }
