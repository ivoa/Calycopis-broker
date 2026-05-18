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

package net.ivoa.calycopis.broker.engine.entities.compute.simple.docker;

import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.SimpleComputeResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.ComputeResourceOffer;

/**
 * A DockerSimpleComputeResourceEntityFactory implementation.
 *
 */
@Slf4j
public class DockerSimpleComputeResourceEntityFactoryImpl
extends SimpleComputeResourceEntityFactoryImpl
implements DockerSimpleComputeResourceEntityFactory
    {

    private final DockerSimpleComputeResourceEntityRepository repository;

    /**
     * Public constructor used by our Platform.
     *
     */
    public DockerSimpleComputeResourceEntityFactoryImpl(
        final DockerSimpleComputeResourceEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractComputeResourceEntity> select(UUID uuid)
        {
        return Optional.of(
            repository.findById(uuid).get()
            );
        }

    @Override
    public DockerSimpleComputeResourceEntity create(
        final SimpleExecutionSessionEntity session,
        final DockerSimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer
        ){
        DockerSimpleComputeResourceEntity entity = this.repository.save(
            new DockerSimpleComputeResourceEntity(
                session,
                result,
                offer
                )
            );
        return entity;
        }
    }
