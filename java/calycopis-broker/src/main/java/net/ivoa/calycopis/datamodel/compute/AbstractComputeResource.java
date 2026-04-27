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

package net.ivoa.calycopis.datamodel.compute;

import java.net.URI;
import java.util.List;

import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;

/**
 * 
 */
public interface AbstractComputeResource
    extends LifecycleComponent
    {
    /**
     * The webapp path for compute resources.
     * 
     */
    public static final URI WEBAPP_PATH = URI.create("compute/"); 

    /**
     * Get the list of volume mounts attached to this resource.
     *
     */
    public List<AbstractVolumeMountEntity> getVolumeMounts();

    }
