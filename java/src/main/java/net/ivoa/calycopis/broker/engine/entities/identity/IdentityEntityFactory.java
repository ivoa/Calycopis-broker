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

import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;

/**
 * Factory interface for creating and looking up IdentityEntity instances.
 *
 */
public interface IdentityEntityFactory
    extends FactoryBase
    {

    /**
     * Select an Identity by UUID.
     *
     */
    public Optional<IdentityEntity> select(final UUID uuid);

    /**
     * Find an Identity by username.
     *
     */
    public Optional<IdentityEntity> findByUsername(final String username);

    /**
     * Find an Identity by OIDC issuer and subject combination.
     *
     */
    public Optional<IdentityEntity> findByIssuerAndUsername(final URI issuer, final String subject);

    /**
     * Create a local Identity with username and password hash.
     *
     */
    public IdentityEntity create(final String username, final String passwordHash);

    /**
     * Create a federated Identity from an OIDC provider.
     *
     */
    public IdentityEntity create(final URI issuer, final String subject, final String displayName);

    }
