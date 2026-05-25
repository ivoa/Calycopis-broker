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

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.SimpleComputeResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.compute.simple.SimpleComputeResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceOffer;

/**
 * A DockerSimpleComputeResourceEntityFactory implementation.
 *
 */
@Slf4j
public class DockerSimpleComputeResourceEntityFactoryImpl
extends SimpleComputeResourceEntityFactoryImpl
implements DockerSimpleComputeResourceEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     *
     */
    public DockerSimpleComputeResourceEntityFactoryImpl(
        final AbstractEntityRepository<AbstractComputeResourceEntity> repository
        ){
        super(
            repository
            );
        }

    @Override
    public DockerSimpleComputeResourceEntity create(
        final SimpleExecutionSessionEntity session,
        final SimpleComputeResourceValidator.Result result,
        final SimpleComputeResourceOffer offer
        ){
        return this.repository.save(
            new DockerSimpleComputeResourceEntity(
                session,
                result,
                offer
                )
            );
        }
    }
