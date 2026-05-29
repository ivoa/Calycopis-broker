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
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-05-27T06:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.session.simple;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 
 */
@Entity
@Table(
    name = "sessionconnectors"
    )
public class SimpleExecutionSessionConnectorEntity
implements SimpleExecutionSessionConnector
    {
    @Id
    @GeneratedValue
    protected UUID uuid;
    public UUID getUuid()
        {
        return this.uuid ;
        }

    /**
     * 
     */
    public SimpleExecutionSessionConnectorEntity()
        {
        super();
        }

    public SimpleExecutionSessionConnectorEntity(final SimpleExecutionSessionEntity session, final String type, final String protocol, final String location)
        {
        super();
        this.session = session;
        session.addConnector(
            this
            );
        this.type = type;
        this.protocol = protocol;
        this.location = location;
        }
    
    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntity session ;
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }
    
    private String type ; 
    @Override
    public String getType()
        {
        return this.type;
        }

    private String status ; 
    @Override
    public String getStatus()
        {
        return this.status;
        }

    private String protocol; 
    @Override
    public String getProtocol()
        {
        return this.protocol;
        }

    private String location;
    @Override
    public String getLocation()
        {
        return location;
        }
    }
