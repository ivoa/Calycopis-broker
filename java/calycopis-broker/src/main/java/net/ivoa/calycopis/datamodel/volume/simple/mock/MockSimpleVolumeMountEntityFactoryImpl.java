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

package net.ivoa.calycopis.datamodel.volume.simple.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.volume.simple.SimpleVolumeMountEntity;
import net.ivoa.calycopis.datamodel.volume.simple.SimpleVolumeMountEntityFactoryImpl;
import net.ivoa.calycopis.datamodel.volume.simple.SimpleVolumeMountValidator;

/**
 * A MockSimpleVolumeMount Factory implementation.
 *
 */
@Slf4j
@Component
public class MockSimpleVolumeMountEntityFactoryImpl
extends SimpleVolumeMountEntityFactoryImpl
implements MockSimpleVolumeMountEntityFactory
    {

    private final MockSimpleVolumeMountEntityRepository repository;

    @Autowired
    public MockSimpleVolumeMountEntityFactoryImpl(
        final MockSimpleVolumeMountEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    public MockSimpleVolumeMountEntity create(
        final AbstractComputeResourceEntity computeResource,
        final AbstractDataResourceEntity    dataResource,
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

    @Override
    public Optional<SimpleVolumeMountEntity> select(UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }
    }

