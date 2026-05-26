/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

package net.ivoa.calycopis.broker.engine.entities.data;

import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
@Deprecated
public abstract class AbstractDataResourceFactoryImpl
extends FactoryBaseImpl
implements AbstractDataResourceEntityFactory
    {

    protected final AbstractEntityRepository<AbstractDataResourceEntity> repository;

    /**
     * Protected constructor.
     * 
     */
    protected AbstractDataResourceFactoryImpl(
        final AbstractEntityRepository<AbstractDataResourceEntity> repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractDataResourceEntity> select(final UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }
    }
