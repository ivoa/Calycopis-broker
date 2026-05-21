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

package net.ivoa.calycopis.broker.spring.jpa;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;

/**
 * 
 */
public class SpringAbstractEntityRepositoryWrapper<EntityType>
extends FactoryBaseImpl
implements FactoryBase, AbstractEntityRepository<EntityType>
    {
    
    private final SpringAbstractEntityRepository<EntityType> inner;

    /**
     * 
     */
    public SpringAbstractEntityRepositoryWrapper(final SpringAbstractEntityRepository<EntityType> inner)
        {
        this.inner = inner;
        }

    @Override
    public Optional<EntityType> findById(UUID uuid)
        {
        return inner.findById(uuid);
        }

    @Override
    public <ActualType extends EntityType> ActualType save(ActualType entity)
        {
        return inner.save(entity);
        }
    }
