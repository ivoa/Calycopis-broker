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

package net.ivoa.calycopis.broker.engine.entities.volume.simple.mock;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.SimpleVolumeMountEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.simple.SimpleVolumeMountValidator;

/**
 *
 */
@Slf4j
public class MockSimpleVolumeMountEntityFactoryImpl
extends SimpleVolumeMountEntityFactoryImpl
implements MockSimpleVolumeMountEntityFactory
    {

    /**
     * Public constructor, used by our Platform.
     * 
     */
    public MockSimpleVolumeMountEntityFactoryImpl(
        final AbstractEntityRepository<AbstractVolumeMountEntity> repository
        ){
        super(
            repository
            );
        }

    @Override
    public MockSimpleVolumeMountEntity create(
        final AbstractComputeResourceEntity computeResource,
        final AbstractDataResourceEntity        dataResource,
        final SimpleVolumeMountValidator.Result result
        ){
        return this.repository.save(
            new MockSimpleVolumeMountEntity(
                computeResource,
                dataResource,
                result
                )
            );
        }

    @Override
    public MockSimpleVolumeMountEntity create(
        final AbstractComputeResourceEntity computeResource,
        final AbstractStorageResourceEntity storageResource,
        final SimpleVolumeMountValidator.Result result
        ){
        return this.repository.save(
            new MockSimpleVolumeMountEntity(
                computeResource,
                storageResource,
                result
                )
            );
        }
    }

