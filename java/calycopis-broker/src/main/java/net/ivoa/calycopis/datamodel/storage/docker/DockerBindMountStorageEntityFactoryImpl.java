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

package net.ivoa.calycopis.datamodel.storage.docker;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Component
public class DockerBindMountStorageEntityFactoryImpl
extends FactoryBaseImpl
implements DockerBindMountStorageEntityFactory
    {
    @Override
    public URI getKind()
        {
        return null;
        }

    private final DockerBindMountStorageEntityRepository repository;

    @Autowired
    public DockerBindMountStorageEntityFactoryImpl(
        final DockerBindMountStorageEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<DockerBindMountStorageEntity> select(UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    public DockerBindMountStorageEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result,
        final String path
        ){
        DockerBindMountStorageEntity entity = this.repository.save(
            new DockerBindMountStorageEntity(
                session,
                result,
                path
                )
            );
        return entity ;
        }

    }
