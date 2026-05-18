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

package net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.volume;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.DockerSimpleStorageResourceEntityFactoryImpl;

/**
 * 
 */
@Slf4j
public class DockerVolumeMountStorageEntityFactoryImpl
extends DockerSimpleStorageResourceEntityFactoryImpl
implements DockerVolumeMountStorageEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     *
     */
    public DockerVolumeMountStorageEntityFactoryImpl(
        final DockerVolumeMountStorageEntityRepository repository
        ){
        super(repository);
        }

    public DockerVolumeMountStorageEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        log.debug(
            "Creating DockerVolumeMountStorageEntity for [{}]",
            result.getObject()
            );
        DockerVolumeMountStorageEntity entity = this.repository.save(
            new DockerVolumeMountStorageEntity(
                session,
                result
                )
            );
        return entity ;
        }
    }
