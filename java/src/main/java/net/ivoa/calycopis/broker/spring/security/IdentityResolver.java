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
 *     "timestamp": "2026-05-30T06:03:00",
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
package net.ivoa.calycopis.broker.spring.security;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntity;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntityFactory;

/**
 * Service that resolves a Spring Security Authentication into an IdentityEntity.
 * For JWT tokens: extracts issuer + subject, looks up or auto-provisions an IdentityEntity.
 * For Basic auth: the IdentityEntity UUID is stored as the authentication principal.
 * For anonymous access: returns null.
 *
 */
@Slf4j
@Service
public class IdentityResolver
    {

    private final IdentityEntityFactory identityFactory;

    public IdentityResolver(final IdentityEntityFactory identityFactory)
        {
        this.identityFactory = identityFactory;
        }

    /**
     * Resolve the given Authentication to an IdentityEntity.
     * Returns null if the authentication is null or anonymous.
     *
     */
    public IdentityEntity resolve(final Authentication authentication)
        {
        if (authentication == null || !authentication.isAuthenticated())
            {
            log.debug("No authentication present, returning null identity");
            return null;
            }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UUID)
            {
            return resolveFromUuid((UUID) principal);
            }
        else if (principal instanceof Jwt)
            {
            return resolveFromJwt((Jwt) principal);
            }
        else if (principal instanceof String && !"anonymousUser".equals(principal))
            {
            return resolveFromUsername((String) principal);
            }

        log.debug("Unrecognised principal type [{}], returning null identity",
            principal != null ? principal.getClass().getName() : "null");
        return null;
        }

    private IdentityEntity resolveFromUuid(final UUID uuid)
        {
        log.debug("Resolving identity from UUID [{}]", uuid);
        Optional<IdentityEntity> found = this.identityFactory.select(uuid);
        if (found.isPresent())
            {
            return found.get();
            }
        log.warn("No identity found for UUID [{}]", uuid);
        return null;
        }

    private IdentityEntity resolveFromJwt(final Jwt jwt)
        {
        String issuerStr = jwt.getIssuer().toString();
        String subject = jwt.getSubject();
        URI issuer = URI.create(issuerStr);

        log.debug("Resolving identity from JWT [{}][{}]", issuer, subject);

        Optional<IdentityEntity> found = this.identityFactory.findByIssuerAndUsername(issuer, subject);
        if (found.isPresent())
            {
            return found.get();
            }

        String displayName = jwt.getClaimAsString("name");
        if (displayName == null)
            {
            displayName = jwt.getClaimAsString("preferred_username");
            }
        if (displayName == null)
            {
            displayName = subject;
            }

        log.debug("Auto-provisioning identity for JWT [{}][{}]", issuer, subject);
        return this.identityFactory.create(issuer, subject, displayName);
        }

    private IdentityEntity resolveFromUsername(final String username)
        {
        log.debug("Resolving identity from username [{}]", username);
        Optional<IdentityEntity> found = this.identityFactory.findByUsername(username);
        if (found.isPresent())
            {
            return found.get();
            }
        log.warn("No identity found for username [{}]", username);
        return null;
        }

    }
