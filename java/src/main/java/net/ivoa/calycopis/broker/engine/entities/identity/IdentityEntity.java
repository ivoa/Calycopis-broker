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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.ComponentEntity;

/**
 * JPA Entity for an Identity.
 *
 */
@Slf4j
@Entity
@Table(name = "identities")
public class IdentityEntity
extends ComponentEntity
implements Identity
    {

    /**
     * Protected no-arg constructor required by JPA.
     *
     */
    protected IdentityEntity()
        {
        super();
        }

    /**
     * Constructor for a local account.
     *
     */
    public IdentityEntity(final String username, final String passwordHash)
        {
        super(username, null);
        this.username = username;
        this.issuer = null;
        this.passwordHash = passwordHash;
        this.displayName = username;
        }

    /**
     * Constructor for an OIDC (federated) account.
     *
     */
    public IdentityEntity(final URI issuer, final String subject, final String displayName)
        {
        super(subject, null);
        this.username = subject;
        this.issuer = issuer;
        this.passwordHash = null;
        this.displayName = displayName;
        }

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Override
    public String getUsername()
        {
        return this.username;
        }

    @Column(name = "issuer")
    private URI issuer;

    @Override
    public URI getIssuer()
        {
        return this.issuer;
        }

    @Column(name = "password_hash")
    private String passwordHash;

    public String getPasswordHash()
        {
        return this.passwordHash;
        }

    @Column(name = "display_name")
    private String displayName;

    @Override
    public String getDisplayName()
        {
        return this.displayName;
        }

    /**
     * Set this identity as its own owner (self-referencing).
     * Package-private, called by the factory after construction.
     *
     */
    void setSelfOwner()
        {
        this.setOwner(this);
        }

    @Override
    public URI getKind()
        {
        return URI.create("uri:identity");
        }

    @Override
    protected URI getWebappPath()
        {
        return URI.create("/identities/");
        }

    }
