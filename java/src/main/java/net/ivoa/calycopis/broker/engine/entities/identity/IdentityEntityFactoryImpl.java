/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
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
 *     along with this software. If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-05-30T05:50:00",
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
package net.ivoa.calycopis.broker.engine.entities.identity;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;

/**
 * Implementation of IdentityEntityFactory.
 *
 */
@Slf4j
public class IdentityEntityFactoryImpl
    extends FactoryBaseImpl
    implements IdentityEntityFactory
    {

    private final IdentityEntityRepository repository;

    public IdentityEntityFactoryImpl(
        final IdentityEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<IdentityEntity> select(final UUID uuid)
        {
        return this.repository.findById(uuid);
        }

    @Override
    public Optional<IdentityEntity> findByUsername(final String username)
        {
        return this.repository.findByUsername(username);
        }

    @Override
    public Optional<IdentityEntity> findByIssuerAndUsername(final URI issuer, final String subject)
        {
        return this.repository.findByIssuerAndUsername(issuer, subject);
        }

    @Override
    public IdentityEntity create(final String username, final String passwordHash)
        {
        log.debug("Creating local identity [{}]", username);
        IdentityEntity entity = new IdentityEntity(
            username,
            passwordHash
            );
        this.repository.save(entity);
        entity.setSelfOwner();
        return this.repository.save(entity);
        }

    @Override
    public IdentityEntity create(final URI issuer, final String subject, final String displayName)
        {
        log.debug("Creating federated identity [{}][{}]", issuer, subject);
        IdentityEntity entity = new IdentityEntity(
            issuer,
            subject,
            displayName
            );
        this.repository.save(entity);
        entity.setSelfOwner();
        return this.repository.save(entity);
        }

    }
