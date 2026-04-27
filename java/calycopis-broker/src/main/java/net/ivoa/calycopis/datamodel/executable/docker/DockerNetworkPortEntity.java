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

package net.ivoa.calycopis.datamodel.executable.docker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainer.DockerExternalPort;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainer.DockerInternalPort;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainer.DockerNetworkPort;
import net.ivoa.calycopis.spring.model.IvoaDockerNetworkPort;

/**
 * JPA Entity for DockerContainer ports.
 *  
 */
@Entity
@Table(
    name = "dockernetworkports"
    )
public class DockerNetworkPortEntity
implements DockerNetworkPort
    {
    @Id
    @GeneratedValue
    protected UUID uuid;
    public UUID getUuid()
        {
        return this.uuid ;
        }
    
    protected DockerNetworkPortEntity()
        {
        super();
        }
    
    protected DockerNetworkPortEntity(
        final DockerContainerEntity parent,
        final IvoaDockerNetworkPort template
        ){
        super();
        this.parent = parent;

        this.path     = template.getPath();
        this.access   = template.getAccess();
        this.protocol = template.getProtocol();

        if (template.getInternal() != null)
            {
            this.internalPortNumber = template.getInternal().getPort(); 
            }

        if (template.getExternal() != null)
            {
            this.externalPortNumber = template.getExternal().getPort(); 
            this.externalAddresses.addAll(
                template.getExternal().getAddresses()
                );
            }
        }

    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DockerContainerEntity parent ;
    public DockerContainerEntity getParent()
        {
        return this.parent;
        }

    private boolean access;
    @Override
    public boolean getAccess()
        {
        return this.access;
        }

    protected Integer internalPortNumber;
    @Override
    public DockerInternalPort getInternal()
        {
        return new DockerInternalPort()
            {
            @Override
            public Integer getPort()
                {
                return internalPortNumber;
                }
            };
        }

    private Integer externalPortNumber;
    
    @ElementCollection
    @CollectionTable(
        name="dockerexternaladdresses",
        joinColumns=@JoinColumn(
            name="parent",
            referencedColumnName = "uuid"
            )
        )
    private List<String> externalAddresses = new ArrayList<String>();
    
    @Override
    public DockerExternalPort getExternal()
        {
        if ((externalPortNumber != null) || (externalAddresses.isEmpty() == false))
            {
            return new DockerExternalPort()
                {
                @Override
                public Integer getPort()
                    {
                    return externalPortNumber;
                    }
    
                @Override
                public List<String> getAddresses()
                    {
                    return externalAddresses ;
                    }
                };
            }
        else {
            return null ;
            }
        }

    private String protocol;
    @Override
    public String getProtocol()
        {
        return this.protocol;
        }

    private String path;
    @Override
    public String getPath()
        {
        return this.path;
        }
    }
