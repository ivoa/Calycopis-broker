/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package net.ivoa.calycopis.datamodel.compute.simple.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResourceEntityFactoryImpl;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;

/**
 * A SimpleComputeResource Factory implementation.
 *
 */
@Slf4j
@Component
public class MockSimpleComputeResourceEntityFactoryImpl
extends SimpleComputeResourceEntityFactoryImpl
implements MockSimpleComputeResourceEntityFactory
    {

    private final MockSimpleComputeResourceEntityRepository repository;

    @Autowired
    public MockSimpleComputeResourceEntityFactoryImpl(final MockSimpleComputeResourceEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractComputeResourceEntity> select(UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    @Override
    public MockSimpleComputeResourceEntity create(
        final SimpleExecutionSessionEntity session,
        final MockSimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer
        ){
        return this.repository.save(
            new MockSimpleComputeResourceEntity(
                session,
                result,
                offer
                )
            );
        }
    }

