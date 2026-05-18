/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package net.ivoa.calycopis.broker.engine.entities.session.simple;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetEntity;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * An ExecutionSessionFactory implementation.
 *
 */
@Slf4j
public class SimpleExecutionSessionEntityFactoryImpl
    extends FactoryBaseImpl
    implements SimpleExecutionSessionEntityFactory
    {

    private final SimpleExecutionSessionEntityRepository sessionEntityRepository;

    /**
     * Public constructor used by our Platform.
     * 
     */
    public SimpleExecutionSessionEntityFactoryImpl(
        final SimpleExecutionSessionEntityRepository sessionEntityRepository
        ){
        super();
        this.sessionEntityRepository = sessionEntityRepository;
        }

    @Override
    public Optional<SimpleExecutionSessionEntity> select(UUID uuid)
        {
        return this.sessionEntityRepository.findById(
            uuid
            );
        }

    @Override
    public SimpleExecutionSessionEntity create(final OfferSetEntity parent, final OfferSetRequestParserContext context, final ComputeResourceOffer offer)
        {
        return this.sessionEntityRepository.save(
            new SimpleExecutionSessionEntity(
                parent,
                context,
                offer
                )
            );
        }

    @Override
    public List<SimpleExecutionSessionEntity> select(final IvoaSimpleExecutionSessionPhase phase)
        {
        return sessionEntityRepository.findByPhase(
            phase
            );
        }

    @Override
    public SimpleExecutionSessionEntity save(final SimpleExecutionSessionEntity entity)
        {
        return sessionEntityRepository.save(
            entity
            );
        }
    }

