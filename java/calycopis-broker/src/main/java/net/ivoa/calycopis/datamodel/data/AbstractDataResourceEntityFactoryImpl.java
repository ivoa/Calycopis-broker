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
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 15,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.data;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResource;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 *
 */
@Slf4j
public abstract class AbstractDataResourceEntityFactoryImpl
extends FactoryBaseImpl
implements AbstractDataResourceEntityFactory
    {

    private AbstractDataResourceEntityRepository repository;

    @Override
    public URI getKind()
        {
        return SimpleDataResource.TYPE_DISCRIMINATOR;
        }

    @Autowired
    public AbstractDataResourceEntityFactoryImpl(final AbstractDataResourceEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractDataResourceEntity> select(UUID uuid)
        {
        return repository.findById(uuid);
        }

    }
