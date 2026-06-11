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
package net.ivoa.calycopis.broker.spring.webapp;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntity;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntityFactory;

/**
 * Admin endpoint for managing user identities.
 * Requires ADMIN authority (authenticated via admin.yaml credentials).
 *
 */
@Slf4j
@RestController
@RequestMapping("/admin/identities")
public class AdminIdentityController
    {

    private final IdentityEntityFactory identityFactory;
    private final PasswordEncoder passwordEncoder;

    public AdminIdentityController(
        final IdentityEntityFactory identityFactory,
        final PasswordEncoder passwordEncoder
        ){
        this.identityFactory = identityFactory;
        this.passwordEncoder = passwordEncoder;
        }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<Map<String, String>> createIdentity(
        @RequestBody final Map<String, String> request
        ){
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || username.isBlank())
            {
            return ResponseEntity.badRequest().body(
                Map.of("error", "username is required")
                );
            }

        if (password == null || password.isBlank())
            {
            return ResponseEntity.badRequest().body(
                Map.of("error", "password is required")
                );
            }

        Optional<IdentityEntity> existing = this.identityFactory.findByUsername(username);
        if (existing.isPresent())
            {
            log.debug("Identity already exists for username [{}]", username);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                    "error", "username already exists",
                    "uuid", existing.get().getUuid().toString(),
                    "username", username
                    )
                );
            }

        String passwordHash = this.passwordEncoder.encode(password);
        IdentityEntity identity = this.identityFactory.create(username, passwordHash);

        log.info("Created identity [{}] for username [{}]", identity.getUuid(), username);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "uuid", identity.getUuid().toString(),
                "username", username
                )
            );
        }

    }
