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

package net.ivoa.calycopis.datamodel.compute.simple;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.openapi.model.IvoaAbstractComputeResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeCores;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeMemory;
import net.ivoa.calycopis.openapi.model.IvoaSimpleComputeResource;

/**
 * A Simple compute resource.
 *
 */
@Entity
@Table(
    name = "simplecomputeresources"
    )
@DiscriminatorValue(
    value = "uri:simple-compute-resources"
    )
public class SimpleComputeResourceEntity
    extends AbstractComputeResourceEntity
    implements SimpleComputeResource
    {

    /**
     * Protected constructor
     *
     */
    protected SimpleComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with session, template and offer.
     *
     */
    public SimpleComputeResourceEntity(
        final ExecutionSessionEntity session,
        final IvoaSimpleComputeResource template,
        final ComputeResourceOffer offer
        ){
        super(
            session,
            template.getName()
            );

        if (template.getCores() != null)
            {
            this.minrequestedcores = template.getCores().getMin();
            this.maxrequestedcores = template.getCores().getMax();
            }

        this.minofferedcores   = offer.getCores();
        this.maxofferedcores   = offer.getCores();

        if (template.getMemory() != null)
            {
            this.minrequestedmemory = template.getMemory().getMin();
            this.maxrequestedmemory = template.getMemory().getMax();
            }

        this.minofferedmemory = offer.getMemory();
        this.maxofferedmemory = offer.getMemory();

        //
        // Add our volumes.
        /*
        for (IvoaSimpleComputeVolume volume : template.getVolumes())
            {
            this.volumes.add(
                new SimpleComputeVolumeEntity(
                    this,
                    volume
                    )
                );                
            }
         * 
         */
        }

    // Does this also have a start and end time ?
    // Does this also go through a similar set of state changes as the parent execution ?
    // YES
    
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

    /*
     * TODO
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<SimpleComputeVolumeEntity> volumes = new ArrayList<SimpleComputeVolumeEntity>();
    @Override
    public List<SimpleComputeVolume> getVolumes()
        {
        return new ListWrapper<SimpleComputeVolume, SimpleComputeVolumeEntity>(
            volumes
            ){
            public SimpleComputeVolume wrap(final SimpleComputeVolumeEntity inner)
                {
                return (SimpleComputeVolume) inner ;
                }
            };
        }
     * 
     */
    
    @Override
    public IvoaAbstractComputeResource getIvoaBean(final String baseurl)
        {
        IvoaSimpleComputeResource bean = new IvoaSimpleComputeResource (
            SimpleComputeResource.TYPE_DISCRIMINATOR
            );
        bean.setUuid(
            this.getUuid()
            );
        bean.setName(
            this.getName()
            );
        bean.setMessages(
            this.getMessageBeans()
            );

        IvoaSimpleComputeCores coresbean = new IvoaSimpleComputeCores();
        coresbean.setMin(minofferedcores);
        coresbean.setMax(maxofferedcores);
        bean.setCores(coresbean);
        
        IvoaSimpleComputeMemory memorybean = new IvoaSimpleComputeMemory();
        memorybean.setMin(minofferedcores);
        memorybean.setMax(maxofferedcores);
        bean.setMemory(memorybean);
        
        return bean;
        }
    }

