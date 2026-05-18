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

package net.ivoa.calycopis.broker.engine.entities.compute;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMount;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.ComputeResourceOffer;
import net.ivoa.calycopis.broker.engine.util.ListWrapper;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractComputeResource;

/**
 * 
 */
@Entity
@Table(
    name = "abstractcomputeresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractComputeResourceEntity
extends LifecycleComponentEntity
implements AbstractComputeResource
    {

    /**
     * Protected constructor for JPA entities.
     * 
     */
    protected AbstractComputeResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor used by derived classes.
     * 
     */
    protected AbstractComputeResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractComputeResourceValidator.Result result,
        final ComputeResourceOffer offer
        ){
        super(
            result.getMeta()
            );
        this.session = session;
        this.session.setComputeResource(
            this
            );
        
        //
        // Start preparing before the offer is available.
        // TODO Add available time and preparation time to the offer.
        this.prepareDurationSeconds     = result.getPrepareDuration();
        this.prepareStartInstantSeconds = offer.getStartTime().getEpochSecond() - result.getPrepareDuration(); 

        //
        // Available as soon as the preparation is done.
        // TODO Add available time and preparation time to the offer.
        this.availableDurationSeconds      = offer.getDuration().getSeconds();
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = offer.getStartTime().getEpochSecond();

        //
        // Hard coded 10s release duration.
        // Start releasing 5s after availability ends.
        this.releaseDurationSeconds = 10L ; 
        this.releaseStartInstantSeconds = this.availableStartInstantSeconds + this.availableDurationSeconds + 5L ;         
                
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    protected SimpleExecutionSessionEntity session;
    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }

    @OneToMany(
        mappedBy = "computeResource",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractVolumeMountEntity> volumeMounts = new ArrayList<AbstractVolumeMountEntity>();
    
    public Iterable<AbstractVolumeMountEntity> getVolumeMountEntities()
        {
        return volumeMounts;
        }

    @Override
    public Iterable<AbstractVolumeMount> getVolumeMounts()
        {
        return new ListWrapper<AbstractVolumeMount, AbstractVolumeMountEntity>(
            this.volumeMounts
            ){
            public AbstractVolumeMount wrap(final AbstractVolumeMountEntity inner)
                {
                return inner;
                }
            };
        }
    
    public void addVolumeMount(final AbstractVolumeMountEntity volume)
        {
        volumeMounts.add(
            volume
            );
        }
    
    public abstract IvoaAbstractComputeResource makeBean(final URIBuilder builder);

    protected IvoaAbstractComputeResource fillBean(final IvoaAbstractComputeResource bean)
        {
        bean.setKind(
            this.getKind()
            );
        bean.setPhase(
            this.getPhase()
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        return bean;
        }

    @Override
    protected URI getWebappPath()
        {
        return AbstractComputeResource.WEBAPP_PATH;
        }
    }