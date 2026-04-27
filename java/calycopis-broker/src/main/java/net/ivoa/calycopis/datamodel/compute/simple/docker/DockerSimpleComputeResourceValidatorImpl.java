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
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.compute.simple.docker;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntityFactory;
import net.ivoa.calycopis.datamodel.compute.simple.SimpleComputeResourceValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountValidatorFactory;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeCores;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeMemory;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;

/**
 * A validator implementation to handle SimpleComputeResources on a Docker platform.
 * 
 */
@Slf4j
public class DockerSimpleComputeResourceValidatorImpl
extends SimpleComputeResourceValidatorImpl
implements DockerSimpleComputeResourceValidator
    {

    /**
     * Public constructor.
     * 
     */
    public DockerSimpleComputeResourceValidatorImpl(
        final AbstractComputeResourceEntityFactory entityFactory,
        final AbstractVolumeMountValidatorFactory volumeMountValidatorFactory
        ){
        super(
            entityFactory,
            volumeMountValidatorFactory
            );
        }
    
    public static final Long MIN_CORES_DEFAULT =  1L ;
    public static final Long MAX_CORES_LIMIT   = 16L ;

    @Override
    protected boolean validateCores(
        IvoaSimpleComputeResource requested, IvoaSimpleComputeResource validated,
        OfferSetRequestParserContext context
        ){
        boolean success = true ;

        Long mincores = MIN_CORES_DEFAULT;
        Long maxcores = MIN_CORES_DEFAULT;

        if (requested.getCores() != null)
            {
            if (requested.getCores().getMin() != null)
                {
                mincores = requested.getCores().getMin();
                }
            if (requested.getCores().getMax() != null)
                {
                maxcores = requested.getCores().getMax();
                }
            }
        
        if (mincores > MAX_CORES_LIMIT)
            {
            context.addWarning(
                "urn:resource-limit",
                "SimpleComputeResource - Minimum cores exceeds available resources [${resource}][${cores}][${limit}]",
                Map.of(
                    "resource",
                    requested.getMeta().getName(),
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
            maxcores = MAX_CORES_LIMIT;
            context.addWarning(
                "urn:resource-limit",
                "SimpleComputeResource - Maximum cores exceeds available resources [${resource}][${cores}][${limit}]",
                Map.of(
                    "resource",
                    requested.getMeta().getName(),
                    "cores",
                    maxcores,
                    "limit",
                    MAX_CORES_LIMIT
                    )
                );
            }

        IvoaSimpleComputeCores cores = new IvoaSimpleComputeCores();
        cores.setMin(mincores);
        cores.setMax(maxcores);
        validated.setCores(cores);

        context.addMinCores(mincores);
        context.addMaxCores(maxcores);

        return success;
        }

    public static final Long MIN_MEMORY_DEFAULT =  1L;
    public static final Long MAX_MEMORY_LIMIT   = 16L;

    @Override
    protected boolean validateMemory(
        IvoaSimpleComputeResource requested,
        IvoaSimpleComputeResource validated,
        OfferSetRequestParserContext context
        ){
        boolean success = true ;

        Long minmemory = MIN_MEMORY_DEFAULT;
        Long maxmemory = MIN_MEMORY_DEFAULT;
        
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
            context.addWarning(
                "urn:resource-limit",
                "SimpleComputeResource - Requested minimum memory exceeds available resources [${resource}][${memory}][${limit}]",
                Map.of(
                    "resource",
                    requested.getMeta().getName(),
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
            maxmemory = MAX_MEMORY_LIMIT;
            context.addWarning(
                "urn:resource-limit",
                "SimpleComputeResource - Requested maximum memory exceeds available resources [${resource}][${memory}][${limit}]",
                Map.of(
                    "resource",
                    requested.getMeta().getName(),
                    "memory",
                    maxmemory,
                    "limit",
                    MAX_MEMORY_LIMIT
                    )
                );
            }

        IvoaSimpleComputeMemory memory = new IvoaSimpleComputeMemory();
        memory.setMin(minmemory);
        memory.setMax(maxmemory);
        validated.setMemory(memory);
        
        context.addMinMemory(minmemory);
        context.addMaxMemory(maxmemory);
       
        return success;
        }

    public static final Long DEFAULT_PREPARE_TIME = 35L;

    @Override
    protected Long getPrepareDuration(final IvoaSimpleComputeResource validated)
        {
        return DEFAULT_PREPARE_TIME;
        }

    public static final Long DEFAULT_RELEASE_TIME = 1L;

    @Override
    protected Long getReleaseDuration(final IvoaSimpleComputeResource validated)
        {
        return DEFAULT_RELEASE_TIME;
        }
    }
