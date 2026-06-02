/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.docker;

import java.net.URI;
import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutable;

/**
 *
 */
public interface DockerContainer
extends AbstractExecutable
    {
    
    /**
     * The OpenAPI type identifier.
     *
     */
    public static final URI KIND_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/executable/docker-container.yaml") ;

    public String getEntrypoint();

    public List<String> getCommand();

    public Map<String, String> getEnvironment();

    public boolean getPrivileged();
    
    public DockerContainerImage getImage();
    
    public static interface DockerContainerImage
        {
        public String getDigest();

        public List<String> getLocations();
        
        public DockerImagePlatform getImagePlatform();
        }

    public static interface DockerImagePlatform
        {
        public String getArchitecture();
    
        public String getOs();
        }

    public DockerNetwork getNetwork();    

    public interface DockerNetwork    
        {
        public List<DockerNetworkPort> getPorts();
        }

    public interface DockerNetworkPort
        {
        public boolean getAccess();

        public DockerInternalPort getInternal();
        
        public DockerExternalPort getExternal();
        
        public String getProtocol();
        
        public String getPath();
        }
    
    public interface DockerInternalPort
        {
        public Integer getPort();
        }
    
    public interface DockerExternalPort
        {
        public Integer getPort();
        
        public List<String> getAddresses();
        }
    }
