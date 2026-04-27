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
 *     "timestamp": "2026-04-11T06:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.storage.docker;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResource;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResourceEntityRepository;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 * A factory that can select any SimpleStorageResourceEntity subtype
 * by UUID, using the parent JPA repository to handle all subtypes
 * via JPA JOINED inheritance.
 *
 */
@Component
public class DockerSimpleStorageResourceEntityFactoryImpl
extends FactoryBaseImpl
implements DockerSimpleStorageResourceEntityFactory
    {

    private final SimpleStorageResourceEntityRepository repository;

    @Autowired
    public DockerSimpleStorageResourceEntityFactoryImpl(
        final SimpleStorageResourceEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public URI getKind()
        {
        return SimpleStorageResource.TYPE_DISCRIMINATOR;
        }

    @Override
    public Optional<SimpleStorageResourceEntity> select(final UUID uuid)
        {
        return this.repository.findById(uuid);
        }
    }
