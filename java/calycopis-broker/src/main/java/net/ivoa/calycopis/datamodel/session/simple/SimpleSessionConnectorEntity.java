/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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
 *
 */

package net.ivoa.calycopis.datamodel.session.simple;

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
public class SimpleSessionConnectorEntity
implements SimpleSessionConnector
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
    public SimpleSessionConnectorEntity()
        {
        super();
        }

    public SimpleSessionConnectorEntity(final SimpleExecutionSessionEntity session, final String type, final String protocol, final String location)
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
