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
 *       "value": 15,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.volume.simple.mock;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.SimpleVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.SimpleVolumeMountValidator;

/**
 *
 */
@Entity
@Table(
    name = "mocksimplevolumemounts"
    )
public class MockSimpleVolumeMountEntity
extends SimpleVolumeMountEntity
implements MockSimpleVolumeMount
    {

    /**
     * Protected constructor for JPA entities.
     *
     */
    protected MockSimpleVolumeMountEntity()
        {
        super();
        }

    /**
     * Protected constructor for a DataResource.
     *
     */
    protected MockSimpleVolumeMountEntity(
        final AbstractComputeResourceEntity computeResource,
        final AbstractDataResourceEntity    dataResource,
        final SimpleVolumeMountValidator.Result result
        ){
        super(
            computeResource,
            dataResource,
            result
            );
        }

    /**
     * Protected constructor for a StorageResource.
     *
     */
    public MockSimpleVolumeMountEntity(
        final AbstractComputeResourceEntity computeResource,
        final AbstractStorageResourceEntity storageResource,
        final SimpleVolumeMountValidator.Result result
        ){
        super(
            computeResource,
            storageResource,
            result
            );
        }
    }

