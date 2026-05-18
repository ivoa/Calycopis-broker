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
 * AIMetrics: [
 *     {
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
package net.ivoa.calycopis.broker.engine.entities.data.simple.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.data.simple.SimpleDataResourceEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;

/**
 *
 */
@Slf4j
@Component
public class MockSimpleDataResourceEntityFactoryImpl
extends SimpleDataResourceEntityFactoryImpl
implements MockSimpleDataResourceEntityFactory
    {

    private final MockSimpleDataResourceEntityRepository simpleDataResourceEntityRepository;

    /**
     * Public constructor used by our Platform.
     * 
     */
    public MockSimpleDataResourceEntityFactoryImpl(
        final MockSimpleDataResourceEntityRepository simpleDataResourceEntityRepository
        ){
        super();
        this.simpleDataResourceEntityRepository = simpleDataResourceEntityRepository;
        }

    @Override
    public Optional<AbstractDataResourceEntity> select(final UUID uuid)
        {
        return Optional.of(
            this.simpleDataResourceEntityRepository.findById(uuid).get()
            );
        }

    @Override
    public SimpleDataResourceEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        return this.simpleDataResourceEntityRepository.save(
            new MockSimpleDataResourceEntity(
                session,
                storage,
                result
                )
            );
        }
    }
