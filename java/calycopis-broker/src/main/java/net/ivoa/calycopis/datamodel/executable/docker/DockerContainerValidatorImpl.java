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
 *     "timestamp": "2026-02-17T07:10:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-02-17T13:20:00",
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
package net.ivoa.calycopis.datamodel.executable.docker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaDockerContainer;
import net.ivoa.calycopis.spring.model.IvoaDockerExternalPort;
import net.ivoa.calycopis.spring.model.IvoaDockerImageSpec;
import net.ivoa.calycopis.spring.model.IvoaDockerInternalPort;
import net.ivoa.calycopis.spring.model.IvoaDockerNetworkPort;
import net.ivoa.calycopis.spring.model.IvoaDockerNetworkSpec;
import net.ivoa.calycopis.spring.model.IvoaDockerPlatformSpec;

/**
 * A validator implementation to handle DockerContainers.
 *
 */
@Slf4j
public abstract class DockerContainerValidatorImpl
extends AbstractExecutableValidatorImpl
implements DockerContainerValidator
    {

    private final DockerContainerEntityFactory entityFactory;
    
    @Autowired
    public DockerContainerValidatorImpl(final DockerContainerEntityFactory entityFactory)
        {
        this.entityFactory = entityFactory;
        }

    @Override
    public ResultEnum validate(
        final IvoaAbstractExecutable requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("IvoaAbstractExecutable [{}][{}]", requested.getMeta(), requested.getClass().getName());
        //
        // Use exact class matching rather than instanceof to ensure each
        // validator only handles its specific type, not subclass types.
        // This prevents a parent type's validator from intercepting requests
        // that should be handled by a more specific subclass validator.
        if (requested.getClass() == IvoaDockerContainer.class)
            {
            return validate(
                (IvoaDockerContainer) requested,
                context
                );
            }
        return ResultEnum.CONTINUE;
        }

    /**
     * Validate an IvoaDockerContainer.
     *
     */
    public ResultEnum validate(
        final IvoaDockerContainer requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("IvoaDockerContainer [{}][{}]", requested.getMeta(), requested.getClass().getName());

        boolean success = true ;

        IvoaDockerContainer validated = new IvoaDockerContainer()
            .kind(DockerContainer.TYPE_DISCRIMINATOR)
            .meta(
                makeMeta(
                    requested.getMeta(),
                    context
                    )
                );

        // Created
        // Messages
        
        //
        // Validate the image locations.
        success &= validateImage(
            requested.getImage(),
            validated,
            context
            );

        //
        // Validate the privileged flag.
        success &= validatePrivileged(
            requested.getPrivileged(),
            validated,
            context
            );

        //
        // Validate the command arguments.
        success &= validateCommand(
            requested.getCommand(),
            validated,
            context
            );

        //
        // Validate the requested entrypoint.
        success &= validateEntrypoint(
            requested.getEntrypoint(),
            validated,
            context
            );

        //
        // Validate the environment variables.
        success &= validateEnvironment(
            requested.getEnvironment(),
            validated,
            context
            );

        //
        // Validate the container network.
        success &= validateNetwork(
            requested.getNetwork(),
            validated,
            context
            );
        
        //
        // Everything is good, create a validator Result.
        if (success)
            {
            context.setExecutableResult(
                new AbstractExecutableValidator.ResultBean(
                    Validator.ResultEnum.ACCEPTED,
                    validated
                    ) {
                    @Override
                    public AbstractExecutableEntity build(final SimpleExecutionSessionEntity session)
                        {
                        this.entity = DockerContainerValidatorImpl.this.entityFactory.create(
                            session,
                            this
                            );
                        return this.entity;
                        }
                    
                    @Override
                    public Long getPrepareDuration()
                        {
                        return DockerContainerValidatorImpl.this.getPrepareDuration(
                            validated
                            );
                        }
                    
                    @Override
                    public Long getReleaseDuration()
                        {
                        return DockerContainerValidatorImpl.this.getReleaseDuration(
                            validated
                            );
                        }
                    }
                );
            return ResultEnum.ACCEPTED;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            log.debug("FAIL DockerContainer NOT validated [{}]", validated);
            context.valid(false);
            return ResultEnum.FAILED;
            }
        }

    /**
     * Validate the container image location.
     *
     */
    public boolean validateImage(
        final IvoaDockerImageSpec requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateImage(....)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        if (requested != null)
            {
            IvoaDockerImageSpec image = new IvoaDockerImageSpec();
            if ((requested.getLocations() != null) && (requested.getLocations().isEmpty() == false))
                {
                for (String location : requested.getLocations())
                    {
                    // TODO Better checks
                    success &= notBadValueCheck(
                        location,
                        context
                        );
                    image.addLocationsItem(
                        location
                        );
                    }
                }
            else {
                context.addWarning(
                    "urn:missig-required-value",
                    "DockerContainer - image location required"
                    );
                success = false ;
                }

            String digest = requested.getDigest();
            if (digest != null)
                {
                image.setDigest(
                    digest
                    );
                if (isBadValueCheck(digest,context)) 
                    {
                    context.addWarning(
                        "urn:bad-value",
                        "DockerContainer - image digest matches badvalue blackist [{}]",
                        Map.of(
                            "value",
                            digest
                            )
                        );
                    success = false ;
                    }
                }
            else {
                // TODO Make this configurable
                context.addWarning(
                    "urn:missing-value",
                    "DockerContainer - image digest is required"
                    );
                success = false ;
                }

            success &= validatePlatform(
                requested.getPlatform(),
                image,
                context
                );

            log.debug("Validated image [{}]", image);
            validated.setImage(
                image
                );
            }
        else {
            context.addWarning(
                "urn:missing-value",
                "DockerContainer - image is required"
                );
            success = false ;
            }
        return success;
        }

    public static final String DEFAULT_PLATFORM_ARCH = "amd64"; 
    public static final String DEFAULT_PLATFORM_OS   = "linux"; 

    public boolean validatePlatform(
        final IvoaDockerPlatformSpec requested,
        final IvoaDockerImageSpec validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validatePlatform(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
        IvoaDockerPlatformSpec result = new IvoaDockerPlatformSpec(); 

        result.setArchitecture(
            DEFAULT_PLATFORM_ARCH
            );
        result.setOs(
            DEFAULT_PLATFORM_OS
            );

        if (requested != null)
            {
            String platformArch = requested.getArchitecture();  
            result.setArchitecture(platformArch);
            if (platformArch != null)
                {
                // TODO make this configurable
                switch(platformArch)
                    {
                    case DEFAULT_PLATFORM_ARCH:
                        break ;

                    default:
                        context.addWarning(
                            "urn:invalied-value",
                            "DockerContainer - platform architecture not supported [{}]",
                            Map.of(
                                "value",
                                platformArch
                                )
                            );
                        success = false ;
                        break ;
                    }
                }

            String platformOs = requested.getOs();  
            result.setOs(platformOs);
            if (platformOs != null)
                {
                // TODO make this configurable
                switch(platformOs)
                    {
                    case DEFAULT_PLATFORM_OS:
                        break ;

                    default:
                        context.addWarning(
                            "urn:invalied-value",
                            "DockerContainer - platform operating system not supported [{}]",
                            Map.of(
                                "value",
                                platformOs
                                )
                            );
                        success = false;
                        break ;
                    }
                }
            }
        validated.setPlatform(
            result
            );
        return success;
        }

    /**
     * Validate the container network.
     *
     */
    public boolean validateNetwork(
        final IvoaDockerNetworkSpec requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateNetwork(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
        
        if (requested != null)
            {
            IvoaDockerNetworkSpec result = new IvoaDockerNetworkSpec();
            for (IvoaDockerNetworkPort port : requested.getPorts())
                {
                success &= validateNetworkPort(
                    port,
                    result,
                    context
                    );
                }
            if (success)
                {
                validated.setNetwork(
                    result
                    );
                }
            }
        
        return success;
        }

    /**
     * Apply any platform specific validation rules.
     * 
     */
    protected abstract boolean validateNetworkPortPath(final String path, final OfferSetRequestParserContext context);

    /**
     * Apply any platform specific validation rules.
     * 
     */
    protected abstract boolean validateNetworkPortNumber(final Integer portnum, final OfferSetRequestParserContext context);

    /**
     * Validate a container network port.
     *
     */
    public boolean validateNetworkPort(
        final IvoaDockerNetworkPort requested,
        final IvoaDockerNetworkSpec validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validatePort(...)");
        log.debug("Requested [{}]", requested);
    
        boolean success = true ;
        IvoaDockerNetworkPort  result = new IvoaDockerNetworkPort();

        boolean access = requested.getAccess();
        result.setAccess(access);

        String protocol = requested.getProtocol();
        result.setProtocol(protocol);
        switch(protocol)
            {
            case "UDP":
            case "TCP":
            case "HTTP":
            case "HTTPS":
                break ;

            default:
                context.addWarning(
                    "urn:invalid-value",
                    "DockerContainer - unrecognised network port protocol [{}]",
                    Map.of(
                        "value",
                        protocol
                        )
                    );
                success = false ;
                break ;
            }

        String path = requested.getPath();
        result.setPath(
            path
            );
        success &= validateNetworkPortPath(
            path,
            context
            );

        IvoaDockerInternalPort internal = requested.getInternal();
        Integer portnum = internal.getPort(); 
        result.setInternal(
            new IvoaDockerInternalPort().port(
                portnum 
                )
            );
        success &= validateNetworkPortNumber(
            portnum,
            context
            );

        // Move this to the platform specific implementation, as some platforms may support this.
        if (portnum <= 0)
            {
            context.addWarning(
                "urn:invalid-value",
                "DockerContainer - negative network port number not supported [{}]",
                Map.of(
                    "value",
                    portnum
                    )
                );
            success = false ;
            }

        IvoaDockerExternalPort external = requested.getExternal();
        if (external != null)
            {
            context.addWarning(
                "urn:not-supported",
                "DockerContainer - setting external port details not supported"
                );
            success = false ;
            }
            
        validated.addPortsItem(
            result
            );
        
        return success;
        }
    
    /**
     * Validate the command arguments.
     *
     */
    public boolean validateCommand(
        final java.util.List<String> requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateCommand(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        if (requested != null && !requested.isEmpty())
            {
            java.util.List<String> result = new java.util.ArrayList<String>();
            for (String arg : requested)
                {
                if (isBadValueCheck(arg, context))
                    {
                    context.addWarning(
                        "urn:bad-value",
                        "DockerContainer - command argument matches badvalue blacklist [{}]",
                        Map.of(
                            "value",
                            arg
                            )
                        );
                    success = false ;
                    }
                else {
                    result.add(arg);
                    }
                }
            if (success)
                {
                validated.setCommand(result);
                }
            }
        return success;
        }

    /**
     * Validate the container entrypoint.
     * 
     */
    public boolean validateEntrypoint(
        final String requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateEntrypoint(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
    
        String entrypoint = notEmpty(
            requested
            );
        if (entrypoint != null)
            {
            // TODO Make this configurable.
            success &= notBadValueCheck(
                entrypoint,
                context
                );
            }

        if (success)
            {
            validated.setEntrypoint(
                entrypoint
                );
            }
        else {
            validated.setEntrypoint(
                null
                );
            }
        
        return success;
        }
    
    /**
     * Validate the privileged flag.
     * 
     */
    public boolean validatePrivileged(
        final Boolean requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validatePrivileged(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;
    
        // This implementation doesn't support privileged execution, so fail the request.
        // TODO Make this configurable. 
        if ((requested != null) && (requested == true))
            {
            context.addWarning(
                "urn:not-supported",
                "DockerContainer - Privileged execution not supported"
                );
            success = false ;
            }
        else {
            validated.setPrivileged(false);
            }

        return success;
        }

    /**
     * Validate the environment variables.
     * 
     */
    public boolean validateEnvironment(
        final Map<String, String> requested,
        final IvoaDockerContainer validated,
        final OfferSetRequestParserContext context
        ){
        log.debug("validateEnvironment(...)");
        log.debug("Requested [{}]", requested);

        boolean success = true ;

        if (requested != null)
            {
            Map<String, String> hashmap = new HashMap<String, String>();
            for (Map.Entry<String,String> entry : requested.entrySet())
                {
                if (isBadValueCheck(entry.getKey(),context))
                    {
                    context.addWarning(
                        "urn:bad-value",
                        "DockerContainer - environment variable name matches badvalue blacklist [{}]",
                        Map.of(
                            "value",
                            entry.getKey()
                            )
                        );
                    success = false ;
                    }
                else if (isBadValueCheck(entry.getValue(),context))
                    {
                    context.addWarning(
                        "urn:bad-value",
                        "DockerContainer - environment variable value matches badvalue blacklist [{}]",
                        Map.of(
                            "value",
                            entry.getValue()
                            )
                        );
                    success = false ;
                    }
                else {
                    hashmap.put(
                        entry.getKey(),
                        entry.getValue()
                        ); 
                    }
                }
            //
            // Don't add an empty Map.
            if (hashmap.isEmpty() == false)
                {
                validated.setEnvironment(
                    hashmap
                    );
                }
            }
        return success;
        }
    
    /**
     * Get the prepare duration for a resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getPrepareDuration(final IvoaDockerContainer validated);

    /**
     * Get the release duration for a resource.
     * This will be platform dependent, so it should be implemented in the platform specific subclasses.
     * 
     */
    protected abstract Long getReleaseDuration(final IvoaDockerContainer validated);

    }
