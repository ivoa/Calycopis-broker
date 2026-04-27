/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

package net.ivoa.calycopis.datamodel.storage;

import java.net.URI;
import java.util.List;

import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;

/**
 * 
 */
public interface AbstractStorageResource
extends LifecycleComponent
    {
    /**
     * The webapp path for storage resources.
     * 
     */
    public static final URI WEBAPP_PATH = URI.create("storage/"); 
    
    /**
     * Get a list of the data resources stored in this storage resource.
     *
     */
    public List<AbstractDataResourceEntity> getDataResources();

    /**
     * Get the list of volume mounts this resource is attached to.
     *
     */
    public List<AbstractVolumeMountEntity> getVolumeMounts();

    /**
     * Link this storage resource to a compute resource via
     * a platform-specific linker.
     * The storage entity uses the linker to contribute its
     * part of the volume mount specification.
     *
     */
    public void link(AbstractStorageLinker linker);

    }
