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

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetEntity;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceOffer;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * A Factory for execution sessions.
 *
 */
public interface SimpleExecutionSessionEntityFactory
extends FactoryBase
    {

    /**
     * Select an ExecutionSession based on UUID.
     *
     */
    public Optional<SimpleExecutionSessionEntity> select(final UUID uuid);

    /**
     * Select ExecutionSessions based on phase.
     *
     */
    public Iterable<SimpleExecutionSessionEntity> select(final IvoaSimpleExecutionSessionPhase phase);

    /**
     * Create a new ExecutionSession from a parser context and compute resource offer. 
     *
     */
    public SimpleExecutionSessionEntity create(
        final OfferSetEntity parent,
        final OfferSetRequestParserContext context,
        final SimpleComputeResourceOffer offer
        );
    
    }

