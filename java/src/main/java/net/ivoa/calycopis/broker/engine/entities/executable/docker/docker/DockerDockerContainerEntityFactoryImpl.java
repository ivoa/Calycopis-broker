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

package net.ivoa.calycopis.broker.engine.entities.executable.docker.docker;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;

/**
 * DockerDockerContainerEntity factory implementation for the Docker Platform.
 *
 */
@Slf4j
@Component
public class DockerDockerContainerEntityFactoryImpl
extends DockerContainerEntityFactoryImpl
implements DockerDockerContainerEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public DockerDockerContainerEntityFactoryImpl(
        final AbstractExecutableEntityRepository repository
        ){
        super(repository);
        }

    @Override
    public AbstractExecutableEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        DockerDockerContainerEntity entity = this.repository.save(
            new DockerDockerContainerEntity(
                session,
                result
                )
            );
        return entity;
        }
    }
