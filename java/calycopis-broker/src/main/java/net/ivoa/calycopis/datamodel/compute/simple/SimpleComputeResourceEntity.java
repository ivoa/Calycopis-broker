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
 *       "value": 15,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.compute.simple;

import java.net.URI;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeCores;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeMemory;
import net.ivoa.calycopis.spring.model.IvoaSimpleComputeResource;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * A Simple compute resource.
 *
 */
@Slf4j
@Entity
@Table(
    name = "simplecomputeresources"
    )
@DiscriminatorValue(
    value = "uri:simple-compute-resources"
    )
public abstract class SimpleComputeResourceEntity
    extends AbstractComputeResourceEntity
    implements SimpleComputeResource
    {
    @Override
    public URI getKind()
        {
        return SimpleComputeResource.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with session, validation result, and offer.
     *
     */
    protected SimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final SimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer
        ){
        this(
            session,
            result,
            offer,
            (IvoaSimpleComputeResource) result.getObject()
            );
        }
    
    /**
     * Protected constructor with session, template and offer.
     * TODO validated can be replaced by Result.getObject()
     * 
     */
    protected SimpleComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final SimpleComputeResourceValidator.Result result,
        final ComputeResourceOffer offer,
        final IvoaSimpleComputeResource validated
        ){
        super(
            session,
            result,
            offer,
            validated.getMeta()
            );
        
        if (validated.getCores() != null)
            {
            this.minrequestedcores = validated.getCores().getMin();
            this.maxrequestedcores = validated.getCores().getMax();
            }

        this.minofferedcores   = offer.getCores();
        this.maxofferedcores   = offer.getCores();

        if (validated.getMemory() != null)
            {
            this.minrequestedmemory = validated.getMemory().getMin();
            this.maxrequestedmemory = validated.getMemory().getMax();
            }

        this.minofferedmemory = offer.getMemory();
        this.maxofferedmemory = offer.getMemory();
        }

    @Column(name="minrequestedcores")
    private Long minrequestedcores;
    @Override
    public Long getMinRequestedCores()
        {
        return this.minrequestedcores;
        }

    @Column(name="maxrequestedcores")
    private Long maxrequestedcores;
    @Override
    public Long getMaxRequestedCores()
        {
        return this.maxrequestedcores;
        }
    
    @Column(name="minofferedcores")
    private Long minofferedcores;
    @Override
    public Long getMinOfferedCores()
        {
        return this.minofferedcores;
        }

    @Column(name="maxofferedcores")
    private Long maxofferedcores;
    @Override
    public Long getMaxOfferedCores()
        {
        return this.maxofferedcores;
        }
    
    @Column(name="minrequestedmemory")
    private Long minrequestedmemory;
    @Override
    public Long getMinRequestedMemory()
        {
        return this.minrequestedmemory;
        }

    @Column(name="maxrequestedmemory")
    private Long maxrequestedmemory;
    @Override
    public Long getMaxRequestedMemory()
        {
        return this.maxrequestedmemory;
        }
    
    @Column(name="minofferedmemory")
    private Long minofferedmemory;
    @Override
    public Long getMinOfferedMemory()
        {
        return this.minofferedmemory;
        }

    @Column(name="maxofferedmemory")
    private Long maxofferedmemory;
    @Override
    public Long getMaxOfferedMemory()
        {
        return this.maxofferedmemory;
        }

    @Override
    public IvoaSimpleComputeResource makeBean(final URIBuilder builder)
        {
        return fillBean(
            builder,
            new IvoaSimpleComputeResource().meta(
                this.makeMeta(
                    builder
                    )
                )               
            );
        }

    public IvoaSimpleComputeResource fillBean(final URIBuilder uribuilder, final IvoaSimpleComputeResource bean)
        {
        super.fillBean(bean);
        
        IvoaSimpleComputeCores coresbean = new IvoaSimpleComputeCores();
        coresbean.setMin(minofferedcores);
        coresbean.setMax(maxofferedcores);
        bean.setCores(coresbean);
        
        IvoaSimpleComputeMemory memorybean = new IvoaSimpleComputeMemory();
        memorybean.setMin(minofferedcores);
        memorybean.setMax(maxofferedcores);
        bean.setMemory(memorybean);

        for (AbstractVolumeMountEntity volume : this.getVolumeMounts())
            {
            bean.addVolumesItem(
                volume.makeBean(uribuilder)
                );
            }
        
        return bean;
        }

    @Override
    public abstract ProcessingAction getPrepareAction(final Platform platform, final ComponentProcessingRequest request);

    }

