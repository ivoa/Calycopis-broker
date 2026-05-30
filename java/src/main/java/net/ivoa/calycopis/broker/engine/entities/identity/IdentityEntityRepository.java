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

import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;

/**
 * Repository interface for IdentityEntity with custom lookup methods.
 *
 */
public interface IdentityEntityRepository
    extends AbstractEntityRepository<IdentityEntity>
    {

    /**
     * Find an identity by its unique username.
     *
     */
    public Optional<IdentityEntity> findByUsername(final String username);

    /**
     * Find an identity by OIDC issuer and username (subject) combination.
     *
     */
    public Optional<IdentityEntity> findByIssuerAndUsername(final URI issuer, final String username);

    }
