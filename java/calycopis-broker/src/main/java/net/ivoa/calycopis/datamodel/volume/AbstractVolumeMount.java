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
 *     "timestamp": "2026-03-25T14:45:00",
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

package net.ivoa.calycopis.datamodel.volume;

import java.net.URI;

import net.ivoa.calycopis.datamodel.component.Component;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResource;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResource;

/**
 *
 */
public interface AbstractVolumeMount
    extends Component
    {
    /**
     * The webapp path for volume mounts.
     * 
     */
    public static final URI WEBAPP_PATH = URI.create("volumes/"); 

    /**
     * Get the parent ComputeResource.
     *
     */
    public AbstractComputeResourceEntity getComputeResource();

    /**
     * Reference to the DataResource mounted in this volume.
     *
     */
    public AbstractDataResource getDataResource();
    
    /**
     * Reference to the StorageResource mounted in this volume.
     *
     */
    public AbstractStorageResource getStorageResource();
    
    }
