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
 *     },
 *     {
 *     "timestamp": "2026-06-03T01:33:00",
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

package net.ivoa.calycopis.broker.engine.entities.data;

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
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResource;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntity;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.openapi.spring.model.IvoaAbstractDataResource;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "abstractdataresources"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractDataResourceEntity
extends LifecycleComponentEntity
implements AbstractDataResource
    {

    /**
     * Protected constructor for JPA entities.
     * 
     */
    protected AbstractDataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor used by derived classes.
     * 
     */
    protected AbstractDataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        super(
            result.getMeta(),
            session.getOwner()
            );

        this.session = session;
        this.session.addDataResource(
            this
            );

        this.storage = storage;
        this.storage.addDataResource(
            this
            );

        //
        // Start preparing when the storage becomes available. 
        this.prepareDurationSeconds     = result.getPrepareDuration();
        this.prepareStartInstantSeconds = storage.getAvailableStartInstantSeconds();
        
        //
        // Available as soon as the preparation is done.
        this.availableDurationSeconds      = 0L;
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = this.prepareStartInstantSeconds + this.prepareDurationSeconds;
        
        //
        // Hard coded 10s release duration.
        // Start releasing as soon as availability ends.
        this.releaseDurationSeconds = 10L ; 
        this.releaseStartInstantSeconds = this.availableStartInstantSeconds + this.availableDurationSeconds ;         
        
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntity session;
    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session ;
        }
    
    @JoinColumn(name = "storage", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AbstractStorageResourceEntity storage;
    @Override
    public AbstractStorageResource getStorage()
        {
        return this.storage;
        }
    public void setStorage(final AbstractStorageResourceEntity storage)
        {
        this.storage = storage;
        }
    
    @OneToMany(
        mappedBy = "dataResource",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractVolumeMountEntity> volumeMounts = new ArrayList<AbstractVolumeMountEntity>();

    public List<AbstractVolumeMountEntity> getVolumeMountEntities()
        {
        return volumeMounts;
        }

    public void addVolumeMount(final AbstractVolumeMountEntity volume)
        {
        volume.setDataResource(
            this
            );
        volumeMounts.add(
            volume
            );
        }
    
    public abstract IvoaAbstractDataResource makeBean(final URIBuilder builder);

    protected IvoaAbstractDataResource fillBean(final IvoaAbstractDataResource bean)
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
        bean.setStorage(
            this.storage.getUuid().toString()
            );
        bean.setCosts(
            this.getCostBeans()
            );
        bean.setMetrics(
            this.getMetricBeans()
            );
        return bean;
        }

    @Override
    protected URI getWebappPath()
        {
        return AbstractDataResource.WEBAPP_PATH;
        }

    }
