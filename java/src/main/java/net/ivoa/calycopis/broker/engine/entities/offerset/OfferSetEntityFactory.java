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
 * Based on
 * https://www.geeksforgeeks.org/spring-boot-jparepository-with-example/
 * https://howtodoinjava.com/spring-boot/spring-boot-jparepository-example/
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-05-26T16:50:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 2,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-05-30T06:47:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 10,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.broker.engine.entities.offerset;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;
import net.ivoa.calycopis.schema.spring.model.IvoaExecutionRequest;

/**
 *
 */
public interface OfferSetEntityFactory
    extends FactoryBase
    {
    
    /**
     * The default expiry time for offers.
     *
     */
    public static final Long DEFAULT_EXPIRY_TIME_SECONDS = 5 * 60L ;

    /**
     * Select an OfferSet based on its identifier.
     *
     */
    public Optional<OfferSetEntity> select(final UUID uuid);

    /**
     * Create a new OfferSet based on an ExecutionRequest.
     *
     */
    public OfferSetEntity create(final IvoaExecutionRequest request, final IdentityEntity owner);

    /**
     * Create a new ExecutionSessionEntity based on a direct ExecutionRequest.
     *
     */
    public SimpleExecutionSessionEntity direct(final IvoaExecutionRequest request, final IdentityEntity owner);
    
    }

