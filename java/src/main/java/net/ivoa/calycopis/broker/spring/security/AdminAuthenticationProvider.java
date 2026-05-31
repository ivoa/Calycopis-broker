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
 *     "timestamp": "2026-05-30T11:37:00",
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

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * AuthenticationProvider that validates HTTP Basic credentials
 * against the admin username and password configured in admin.yaml.
 * On success, grants the ADMIN authority.
 *
 */
@Slf4j
@Component
public class AdminAuthenticationProvider
implements AuthenticationProvider
    {

    @Value("${calycopis.admin.username:}")
    private String adminUsername;

    @Value("${calycopis.admin.password:}")
    private String adminPassword;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException
        {
        if (adminUsername == null || adminUsername.isEmpty())
            {
            throw new BadCredentialsException("Admin authentication not configured");
            }

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        if (!adminUsername.equals(username))
            {
            throw new BadCredentialsException("Not an admin user");
            }

        if (!adminPassword.equals(password))
            {
            throw new BadCredentialsException("Invalid admin password");
            }

        log.debug("Admin authentication succeeded for [{}]", username);
        return new UsernamePasswordAuthenticationToken(
            username,
            null,
            List.of(new SimpleGrantedAuthority("ADMIN"))
            );
        }

    @Override
    public boolean supports(final Class<?> authentication)
        {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
        }

    }
