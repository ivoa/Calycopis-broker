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

package net.ivoa.calycopis.datamodel.session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.engine.session.ExecutionSessionPhase;

/**
 * 
 */
public interface AbstractExecutionSessionEntityFactory<EntityType extends AbstractExecutionSessionEntity>
extends FactoryBase
    {

    /**
     * Select an ExecutionSessionEntity based on UUID.
     *
     */
    public Optional<EntityType> select(final UUID uuid);

    /**
     * Select ExecutionSessionEntities based on phase.
     *
     */
    public List<EntityType> select(final ExecutionSessionPhase phase);

    /**
     * Create a new ExecutionSessionEntity from a parser context and compute resource offer. 
     *
     */
    public EntityType create(final OfferSetEntity parent, final OfferSetRequestParserContext context, final ComputeResourceOffer offer);

    }
