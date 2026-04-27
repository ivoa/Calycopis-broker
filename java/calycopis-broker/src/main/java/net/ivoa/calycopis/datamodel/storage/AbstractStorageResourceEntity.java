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
 *     "timestamp": "2026-04-14T17:00:00",
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

package net.ivoa.calycopis.datamodel.storage;

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
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.broker.mapping.LifecyclePhaseMappings;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.util.ListWrapper;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "abstractstorageresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractStorageResourceEntity
extends LifecycleComponentEntity
implements AbstractStorageResource
    {
    /**
     * Protected constructor.
     * 
     */
    protected AbstractStorageResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     * Automatically adds this resource to the parent SessionEntity.
     * 
     */
    protected AbstractStorageResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        super(
            (result != null) ? result.getMeta() : null
            );

        this.session = session;
        this.session.addStorageResource(
            this
            );

        //
        // Start preparing when the session starts preparing.
        this.prepareDurationSeconds     = result.getPrepareDuration();
        this.prepareStartInstantSeconds = session.getPrepareStartInstantSeconds();

        //
        // Available as soon as the preparation is done.
        this.availableDurationSeconds      = 0L;
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = this.prepareStartInstantSeconds + this.prepareDurationSeconds;

        //
        // Start releasing after availability ends.
        // TODO Start releasing after DATA releasing ends.
        // TODO Do we need to calculate this, or is it controlled by the lifecycle management?
        this.releaseDurationSeconds     = result.getReleaseDuration(); 
        this.releaseStartInstantSeconds = this.availableStartInstantSeconds + this.availableDurationSeconds + 10L ;         

        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntity session;
    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }

    @OneToMany(
        mappedBy = "storage",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractDataResourceEntity> dataresources = new ArrayList<AbstractDataResourceEntity>();

    @Override
    public List<AbstractDataResourceEntity> getDataResources()
        {
        return dataresources;
        }

    public void addDataResource(final AbstractDataResourceEntity dataResource)
        {
        dataResource.setStorage(
            this
            );
        dataresources.add(
            dataResource
            );
        }

    @OneToMany(
        mappedBy = "storageResource",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractVolumeMountEntity> volumeMounts = new ArrayList<AbstractVolumeMountEntity>();

    @Override
    public List<AbstractVolumeMountEntity> getVolumeMounts()
        {
        return volumeMounts;
        }

    public void addVolumeMount(final AbstractVolumeMountEntity volume)
        {
        volume.setStorageResource(
            this
            );
        volumeMounts.add(
            volume
            );
        }
    
    public abstract IvoaAbstractStorageResource makeBean(final URIBuilder builder);
    
    protected IvoaAbstractStorageResource fillBean(final IvoaAbstractStorageResource bean)
        {
        bean.setKind(
            this.getKind()
            );
        bean.setPhase(
            LifecyclePhaseMappings.toIvoa(this.getPhase())
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        bean.setData(
            new ListWrapper<String, AbstractDataResourceEntity>(
                dataresources
                ){
                public String wrap(final AbstractDataResourceEntity inner)
                    {
                    return inner.getUuid().toString();
                    }
                }
            );
        return bean;
        }

    @Override
    public void link(AbstractStorageLinker linker)
        {
        }

    @Override
    protected URI getWebappPath()
        {
        return AbstractStorageResource.WEBAPP_PATH;
        }

    }
