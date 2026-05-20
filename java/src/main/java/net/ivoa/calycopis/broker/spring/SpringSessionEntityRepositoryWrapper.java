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

package net.ivoa.calycopis.broker.spring;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntityRepository;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * 
 */
@Component
public class SpringSessionEntityRepositoryWrapper
extends FactoryBaseImpl
implements FactoryBase, SimpleExecutionSessionEntityRepository
    {
    
    private final SpringSessionEntityRepository inner;

    /**
     * 
     */
    public SpringSessionEntityRepositoryWrapper(
        final SpringSessionEntityRepository repository
        ){
        this.inner = repository;
        }

    @Override
    public Optional<SimpleExecutionSessionEntity> findById(UUID uuid)
        {
        return inner.findById(uuid);
        }

    @Override
    @SuppressWarnings("unchecked")
    public SimpleExecutionSessionEntity save(SimpleExecutionSessionEntity entity)
        {
        return (SimpleExecutionSessionEntity) inner.save(entity);
        }

    @Override
    public Iterable<SimpleExecutionSessionEntity> findByPhase(final IvoaSimpleExecutionSessionPhase phase)
        {
        return inner.findByPhase(phase);
        }
    }
