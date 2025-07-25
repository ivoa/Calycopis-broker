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
package net.ivoa.calycopis.datamodel.compute.simple;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeCores;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeMemory;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;

/**
 * A validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public class SimpleComputeResourceValidator
extends AbstractValidatorImpl
implements AbstractComputeResourceValidator
    {

    /**
     * Factory for creating Entities.
     * 
     */
    final SimpleComputeResourceEntityFactory entityFactory;
    
    /**
     * Public constructor.
     * 
     */
    public SimpleComputeResourceValidator(final SimpleComputeResourceEntityFactory entityFactory)
        {
        super();
        this.entityFactory = entityFactory ;
        }
    
    @Override
    public Validator.Result<IvoaAbstractComputeResource, AbstractComputeResourceEntity> validate(
        final IvoaAbstractComputeResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaAbstractComputeResource)");
        log.debug("Resource [{}]", requested);
        if (requested instanceof IvoaSimpleComputeResource )
            {
            return validate(
                (IvoaSimpleComputeResource) requested,
                context
                );
            }
        else {
            return new ResultBean(
                Validator.ResultEnum.CONTINUE
                );
            }
        }

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MIN_CORES_DEFAULT =  1L ;

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MAX_CORES_LIMIT   = 16L ;

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MIN_MEMORY_DEFAULT =  1L;

    /**
     * Hard coded defaults.
     * TODO make these configurable.
     * 
     */
    public static final Long MAX_MEMORY_LIMIT   = 16L;

    /**
     * Validate an IvoaAbstractComputeResource.
     *
     */
    public AbstractComputeResourceValidator.Result validate(
        final IvoaSimpleComputeResource requested,
        final OfferSetRequestParserContext context
        ){
        log.debug("validate(IvoaSimpleComputeResource)");
        log.debug("Resource [{}]", requested);

        boolean success = true ;
        IvoaSimpleComputeResource validated = new IvoaSimpleComputeResource(
            SimpleComputeResource.TYPE_DISCRIMINATOR
            );

        Long mincores = MIN_CORES_DEFAULT;
        Long maxcores = MIN_CORES_DEFAULT;

        Boolean minimalcores  = false;

        if (requested.getCores() != null)
            {
            mincores = requested.getCores().getMin();
            maxcores = requested.getCores().getMax();
            }
        if (mincores > MAX_CORES_LIMIT)
            {
            context.getOfferSetEntity().addWarning(
                "urn:resource-limit",
                "Minimum cores exceeds available resources [${resource}][${cores}][${limit}]",
                Map.of(
                    "resource",
                    requested.getName(),
                    "cores",
                    mincores,
                    "limit",
                    MAX_CORES_LIMIT
                    )
                );
            success = false;
            }
        if (maxcores > MAX_CORES_LIMIT)
            {
            context.getOfferSetEntity().addWarning(
                "urn:resource-limit",
                "Maximum cores exceeds available resources [${resource}][${cores}][${limit}]",
                Map.of(
                    "resource",
                    requested.getName(),
                    "cores",
                    maxcores,
                    "limit",
                    MAX_CORES_LIMIT
                    )
                );
            success = false;
            }

        Long minmemory = MIN_MEMORY_DEFAULT;
        Long maxmemory = MIN_MEMORY_DEFAULT;
        Boolean minimalmemory = false;
        
        if (requested.getMemory() != null)
            {
            if (requested.getMemory().getMin() != null)
                {
                minmemory = requested.getMemory().getMin();
                }
            if (requested.getMemory().getMax() != null)
                {
                maxmemory = requested.getMemory().getMax();
                }
            }

        if (minmemory > MAX_MEMORY_LIMIT)
            {
            context.getOfferSetEntity().addWarning(
                "urn:resource-limit",
                "Minimum memory exceeds available resources [${resource}][${memory}][${limit}]",
                Map.of(
                    "resource",
                    requested.getName(),
                    "memory",
                    minmemory,
                    "limit",
                    MAX_MEMORY_LIMIT
                    )
                );
            success = false;
            }

        if (maxmemory > MAX_MEMORY_LIMIT)
            {
            context.getOfferSetEntity().addWarning(
                "urn:resource-limit",
                "Maximum memory exceeds available resources [${resource}][${memory}][${limit}]",
                Map.of(
                    "resource",
                    requested.getName(),
                    "memory",
                    maxmemory,
                    "limit",
                    MAX_MEMORY_LIMIT
                    )
                );
            success = false;
            }
        
        //
        // Process the network ports.
        // ....

        // Save the results in our IvoaSimpleComputeResource 
        validated.setName(requested.getName());

        IvoaSimpleComputeCores cores = new IvoaSimpleComputeCores();
        cores.setMin(mincores);
        cores.setMax(maxcores);
        validated.setCores(cores);

        IvoaSimpleComputeMemory memory = new IvoaSimpleComputeMemory();
        memory.setMin(minmemory);
        memory.setMax(maxmemory);
        validated.setMemory(memory);
        
        //
        // Process the volume mounts.
        log.debug("Processing the volume mounts");
        if (requested.getVolumes() != null)
            {
            /*
             * TODO
            for (IvoaSimpleComputeVolume volumeRequest : requested.getVolumes())
                {
                // Try finding a storage resource.
                AbstractStorageResourceValidator.Result storage = context.findStorageValidatorResult(volumeRequest.getResource());
                // If we din't find a storage resource.
                if (storage == null)
                    {
                    // Try finding a data resource.
                    AbstractDataResourceValidator.Result data = context.findDataValidatorResult(volumeRequest.getResource());
                    // If we found a data resource.
                    if (data != null)
                        {
                        // Try finding a corresponding storage resource.
                        storage = null;
                        }
                    }
                
                if (storage  != null)
                    {
                    // Create a volume linking the storage resource to the compute resource.
                    
                    }
                else {
                    // error unmatched data resource ...
                    }
                }
             * 
             */
            }
        log.debug("Done processing the volume mounts");

        //
        // Everything is good.
        // Create our result and add it to our state.
        if (success)
            {
            EntityBuilder builder = new EntityBuilder()
                {
                @Override
                public SimpleComputeResourceEntity build(final ExecutionSessionEntity session, final ComputeResourceOffer offer)
                    {
                    return entityFactory.create(
                        session,
                        validated,
                        offer
                        );
                    }
                }; 
            
            AbstractComputeResourceValidator.Result result = new AbstractComputeResourceValidator.ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated,
                builder
                );
            context.addComputeValidatorResult(
                result
                );
            //
            // Update the running totals in our context.
            // TODO Do we move these to ComputeResourceValidator.Result ?
            context.addMinCores(
                mincores
                );
            context.addMaxCores(
                maxcores
                );
            context.addMinMemory(
                minmemory
                );
            context.addMaxMemory(
                maxmemory
                );
            
            return result;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            context.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }
    }
