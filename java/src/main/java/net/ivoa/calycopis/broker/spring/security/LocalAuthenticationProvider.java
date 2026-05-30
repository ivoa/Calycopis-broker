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

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntity;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntityRepository;

/**
 * AuthenticationProvider that validates HTTP Basic credentials
 * against local IdentityEntity password hashes in the database.
 *
 */
@Slf4j
@Component
public class LocalAuthenticationProvider
implements AuthenticationProvider
    {

    private final IdentityEntityRepository identityRepository;
    private final PasswordEncoder passwordEncoder;

    public LocalAuthenticationProvider(
        final IdentityEntityRepository identityRepository,
        final PasswordEncoder passwordEncoder
        ){
        this.identityRepository = identityRepository;
        this.passwordEncoder = passwordEncoder;
        }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException
        {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        log.debug("Attempting local authentication for [{}]", username);

        Optional<IdentityEntity> found = this.identityRepository.findByUsername(username);
        if (found.isEmpty())
            {
            throw new BadCredentialsException("Unknown username");
            }

        IdentityEntity identity = found.get();
        if (identity.getPasswordHash() == null)
            {
            throw new BadCredentialsException("Local authentication not available for this account");
            }

        if (!this.passwordEncoder.matches(password, identity.getPasswordHash()))
            {
            throw new BadCredentialsException("Invalid password");
            }

        log.debug("Local authentication succeeded for [{}]", username);
        return new UsernamePasswordAuthenticationToken(
            identity.getUuid(),
            null,
            Collections.emptyList()
            );
        }

    @Override
    public boolean supports(final Class<?> authentication)
        {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
        }

    }
