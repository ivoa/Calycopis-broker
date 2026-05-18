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

package net.ivoa.calycopis.broker.engine.entities.data.simple.docker.http;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.data.simple.docker.DockerSimpleDataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;

/**
 * 
 */
@Slf4j
@Component
public class DockerHttpResourceEntityFactoryImpl
extends DockerSimpleDataResourceEntityFactoryImpl
implements DockerHttpResourceEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public DockerHttpResourceEntityFactoryImpl(
        final DockerHttpResourceEntityRepository repository
        ){
        super(
            repository
            );
        }

    @Override
    public DockerHttpResourceEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        return this.repository.save(
            new DockerHttpResourceEntity(
                session,
                storage,
                result
                )
            );
        }
    }
