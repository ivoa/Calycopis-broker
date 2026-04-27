/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaDockerContainer;
import net.ivoa.calycopis.spring.model.IvoaDockerExternalPort;
import net.ivoa.calycopis.spring.model.IvoaDockerImageSpec;
import net.ivoa.calycopis.spring.model.IvoaDockerInternalPort;
import net.ivoa.calycopis.spring.model.IvoaDockerNetworkPort;
import net.ivoa.calycopis.spring.model.IvoaDockerNetworkSpec;
import net.ivoa.calycopis.spring.model.IvoaDockerPlatformSpec;
import net.ivoa.calycopis.util.ListWrapper;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * JPA Entity for DockerContainer executables.
 *
 */
@Slf4j
@Entity
@Table(
    name = "dockercontainers"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class DockerContainerEntity
    extends AbstractExecutableEntity
    implements DockerContainer
    {
    @Override
    public URI getKind()
        {
        return DockerContainer.TYPE_DISCRIMINATOR ;
        }

    protected DockerContainerEntity()
        {
        super();
        }

    // TODO Get rid of the class cast.
    protected DockerContainerEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        this(
            session,
            result,
            (IvoaDockerContainer) result.getObject()
            );
        }
    
    protected DockerContainerEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result,
        final IvoaDockerContainer validated
        ){
        super(
            session,
            result,
            validated.getMeta()
            );

        log.debug("DockerContainerEntity validated [{}]", validated);

        this.privileged = validated.getPrivileged();
        this.entrypoint = validated.getEntrypoint();
        this.command = new ArrayList<String>();
        if (validated.getCommand() != null)
            {
            this.command.addAll(
                validated.getCommand()
                );
            }

        this.image = new ImageImpl(
            validated.getImage()
            );
        this.environment = new HashMap<String, String>();
        if (validated.getEnvironment() != null)
            {
            this.environment.putAll(
                validated.getEnvironment()
                );
            }
        
        IvoaDockerNetworkSpec ivoaNetwork = validated.getNetwork() ; 
        if (ivoaNetwork != null)
            {
            for (IvoaDockerNetworkPort port : ivoaNetwork.getPorts())
                {
                this.networkPorts.add(
                    new DockerNetworkPortEntity(
                        this,
                        port
                        )
                    );                
                }
            }
        }

    @Embeddable
    public static class ImageImpl
    implements DockerContainerImage
        {

        public ImageImpl()
            {
            super();
            }

        public ImageImpl(final IvoaDockerImageSpec template)
            {
            super();
            if (template != null)
                {
                this.digest = template.getDigest();
                if (template.getLocations() != null)
                    {
                    this.locations = new ArrayList<String>();
                    this.locations.addAll(
                        template.getLocations()
                        );
                    }
                if (template.getPlatform() != null)
                    {
                    this.platformArch = template.getPlatform().getArchitecture();
                    this.platformOs   = template.getPlatform().getOs();
                    }
                }
            }

        private String digest;
        @Override
        public String getDigest()
            {
            return digest;
            }

        @ElementCollection
        @CollectionTable(
            name="dockerimagelocations",
            joinColumns=@JoinColumn(
                name="parent",
                referencedColumnName = "uuid"
                )
            )
        private List<String> locations;
        @Override
        public List<String> getLocations()
            {
            return locations;
            }

        private String platformArch;
        private String platformOs;
        @Override
        public DockerImagePlatform getImagePlatform()
            {
            return new DockerImagePlatform()
                {
                @Override
                public String getArchitecture()
                    {
                    return platformArch;
                    }

                @Override
                public String getOs()
                    {
                    return platformOs;
                    }
                };
            }
        }
    
    @Embedded
    private ImageImpl image ;
    @Override
    public ImageImpl getImage()
        {
        return image;
        }

    private boolean privileged;
    @Override
    public boolean getPrivileged()
        {
        return privileged;
        }

    private String entrypoint;
    @Override
    public String getEntrypoint()
        {
        return entrypoint;
        }

    @ElementCollection
    @CollectionTable(
        name="dockercommandargs",
        joinColumns=@JoinColumn(
            name="parent",
            referencedColumnName = "uuid"
            )
        )
    @Column(name="arg")
    private List<String> command = new ArrayList<String>();
    @Override
    public List<String> getCommand()
        {
        return this.command;
        }

    @ElementCollection
    @Column(name="envvalue")
    @MapKeyColumn(name="envkey")
    @CollectionTable(
        name="dockerenvironmentvariables",
        joinColumns=@JoinColumn(
            name="parent",
            referencedColumnName = "uuid"
            )
        )
    private Map<String, String> environment;
    @Override
    public Map<String, String> getEnvironment()
        {
        return this.environment;
        }

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<DockerNetworkPortEntity> networkPorts = new ArrayList<DockerNetworkPortEntity>();

    @Override
    public DockerNetwork getNetwork()
        {
        return new DockerNetwork()
            {
            public List<DockerNetworkPort> getPorts()
                {
                return new ListWrapper<DockerNetworkPort, DockerNetworkPortEntity>(
                    networkPorts
                    ){
                    public DockerNetworkPort wrap(final DockerNetworkPortEntity inner)
                        {
                        return (DockerNetworkPort) inner ;
                        }
                    };
                }
            };
        }

    @Override
    public IvoaAbstractExecutable makeBean(final URIBuilder builder)
        {
        return this.fillBean(
            new IvoaDockerContainer().meta(
                this.makeMeta(
                    builder
                    )
                )
            );
        }

    protected IvoaDockerContainer fillBean(final IvoaDockerContainer bean)
        {
        super.fillBean(
            bean
            );
        
        bean.setPrivileged(
            this.privileged
            );
        bean.setEntrypoint(
            this.entrypoint
            );
        if ((this.command != null) && (this.command.isEmpty() == false))
            {
            bean.setCommand(
                this.command
                );
            }

        if ((this.environment != null) && (this.environment.isEmpty() == false))
            {
            bean.setEnvironment(
                this.environment
                );
            }

        if (this.image != null)
            {
            IvoaDockerImageSpec ivoaImage = new IvoaDockerImageSpec();
            
            if ((this.image.getLocations() != null) && (this.image.getLocations().isEmpty() == false))
                {
                ivoaImage.setLocations(
                    this.image.getLocations()
                    );
                }
            ivoaImage.setDigest(
                this.image.getDigest()
                );

            if (this.image.getImagePlatform() != null)
                {
                IvoaDockerPlatformSpec ivoaPlatform = new IvoaDockerPlatformSpec();
                ivoaPlatform.setArchitecture(
                    this.image.getImagePlatform().getArchitecture()
                    );
                ivoaPlatform.setOs(
                    this.image.getImagePlatform().getOs()
                    );
                ivoaImage.setPlatform(
                    ivoaPlatform
                    );
                }
            bean.setImage(
                ivoaImage
                );
            }

        if ((this.networkPorts != null) && (this.networkPorts.isEmpty() == false))
            {
            
            IvoaDockerNetworkSpec ivoaNetworkSpec = new IvoaDockerNetworkSpec();

            for (DockerNetworkPortEntity networkPort : this.networkPorts)
                {
                IvoaDockerNetworkPort ivoaNetworkPort = new IvoaDockerNetworkPort();
                ivoaNetworkPort.setAccess(
                    networkPort.getAccess()
                    );
                ivoaNetworkPort.setPath(
                    networkPort.getPath()
                    );
                ivoaNetworkPort.setProtocol(
                    networkPort.getProtocol()
                    );

                if (networkPort.getInternal() != null)
                    {
                    IvoaDockerInternalPort ivoaInternalPort = new IvoaDockerInternalPort();
                    ivoaInternalPort.setPort(
                        networkPort.getInternal().getPort()
                        );
                    ivoaNetworkPort.setInternal(
                        ivoaInternalPort
                        );
                    }

                if (networkPort.getExternal() != null)
                    {
                    IvoaDockerExternalPort ivoaExternalPort = new IvoaDockerExternalPort();
                    ivoaExternalPort.setPort(
                        networkPort.getExternal().getPort()
                        );
                    for (String address : networkPort.getExternal().getAddresses())
                        {
                        ivoaExternalPort.addAddressesItem(
                            address
                            );
                        }
                    ivoaNetworkPort.setExternal(
                        ivoaExternalPort
                        );
                    }
                ivoaNetworkSpec.addPortsItem(
                    ivoaNetworkPort
                    );
                }
            bean.setNetwork(
                ivoaNetworkSpec
                );
            }
        
        // TODO generate the access URLs
        
        return bean;
        }
    }
