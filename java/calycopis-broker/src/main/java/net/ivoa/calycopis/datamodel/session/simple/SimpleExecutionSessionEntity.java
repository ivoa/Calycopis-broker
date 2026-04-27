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
 *       "value": 3,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.session.simple;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.threeten.extra.Interval;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.booking.ResourceOffer;
import net.ivoa.calycopis.spring.model.IvoaAbstractOption;
import net.ivoa.calycopis.spring.model.IvoaScheduleStartDurationInstant;
import net.ivoa.calycopis.spring.model.IvoaScheduleStartDurationInterval;
import net.ivoa.calycopis.spring.model.IvoaScheduledExecutionSchedule;
import net.ivoa.calycopis.spring.model.IvoaScheduledExecutionSession;
import net.ivoa.calycopis.spring.model.IvoaSimpleExecutionSession;
import net.ivoa.calycopis.engine.session.ExecutionSessionPhase;
import net.ivoa.calycopis.broker.mapping.SessionPhaseMappings;
import net.ivoa.calycopis.spring.model.IvoaSimpleSessionConnector;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * An execution session Entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "simpleexecutionsessions"
    )
@DiscriminatorValue(
    value = "uri:simple-execution-session"
    )
public class SimpleExecutionSessionEntity
    extends AbstractExecutionSessionEntity
    implements SimpleExecutionSession
    {
    
    @Override
    public URI getKind()
        {
        return SimpleExecutionSession.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    public SimpleExecutionSessionEntity()
        {
        super();
        }

    /**
     * Protected constructor, used to create an example for the find method.
     *
    protected SimpleExecutionSessionEntity(final ExecutionSessionPhase phase)
        {
        super();
        this.phase = phase;
        }
     */
    
    /**
     * Protected constructor with parent.
     *
     */
    public SimpleExecutionSessionEntity(
        final OfferSetEntity offerset,
        final OfferSetRequestParserContext context,
        final ResourceOffer offerblock
        ){
        super(
            offerset,
            offerset.getName() + "-" + offerblock.getName()
            );
        this.phase = ExecutionSessionPhase.OFFERED;
        this.expires = offerset.getExpires();
        }

    @Column(name = "phase")
    @Enumerated(EnumType.STRING)
    private ExecutionSessionPhase phase = ExecutionSessionPhase.INITIAL;
    @Override
    public ExecutionSessionPhase getPhase()
        {
        return this.phase;
        }
    @Override
    public void setPhase(final ExecutionSessionPhase newphase)
        {
        // TODO This is where we need to have the phase transition checking.
        this.phase = newphase;
        }

    @Column(name = "expires")
    private Instant expires;
    @Override
    public Instant getExpires()
        {
        return this.expires;
        }

    @OneToOne(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    private AbstractExecutableEntity executable;
    @Override
    public AbstractExecutableEntity getExecutable()
        {
        return this.executable;
        }
    public void setExecutable(final AbstractExecutableEntity executable)
        {
        this.executable = executable;
        }

    @OneToOne(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    private AbstractComputeResourceEntity computer;

    @Override
    public AbstractComputeResourceEntity getComputeResource()
        {
        return computer;
        }
    public void setComputeResource(final AbstractComputeResourceEntity computer)
        {
        this.computer = computer;
        }

    @OneToMany(
        mappedBy = "session",
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

    public void addDataResource(final AbstractDataResourceEntity resource)
        {
        dataresources.add(
            resource
            );
        }

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<AbstractStorageResourceEntity> storageresources = new ArrayList<AbstractStorageResourceEntity>();

    @Override
    public List<AbstractStorageResourceEntity> getStorageResources()
        {
        return storageresources;
        }

    public void addStorageResource(final AbstractStorageResourceEntity resource)
        {
        storageresources.add(
            resource
            );
        }

    @OneToMany(
        mappedBy = "session",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    List<SimpleSessionConnectorEntity> connectors = new ArrayList<SimpleSessionConnectorEntity>();

    @Override
    public List<SimpleSessionConnectorEntity> getConnectors()
        {
        return connectors;
        }

    @Override
    public void addConnector(final SimpleSessionConnectorEntity connector)
        {
        connectors.add(
            connector
            );
        }

    @Override
    public void addConnector(String type, String protocol, String location)
        {
        this.addConnector(
            new SimpleSessionConnectorEntity(
                this,
                type,
                protocol,
                location
                )
            );
        }

    protected List<@Valid IvoaAbstractOption> getOptions()
        {
        return null;
        }

    public void init(final IvoaScheduledExecutionSchedule schedule)
        {
        if (schedule != null)
            {
            IvoaScheduleStartDurationInstant preparing = schedule.getPreparing();
            if (null != preparing)
                {
                String startInstantString = preparing.getStart();
                if (null != startInstantString)
                    {
                    try {
                        this.prepareStartInstantSeconds = Instant.parse(
                            startInstantString
                            ).getEpochSecond();
                        }
                    catch (Exception ouch)
                        {
                        log.warn("Exception parsing prepare start instant [{}][{}]", startInstantString, ouch.getMessage());
                        }
                    }
                String durationString = preparing.getDuration();
                if (null != durationString)
                    {
                    try {
                        this.prepareDurationSeconds = Duration.parse(
                            durationString
                            ).getSeconds();
                        }
                    catch (Exception ouch)
                        {
                        log.warn("Exception parsing prepare duration [{}][{}]", durationString, ouch.getMessage());
                        }
                    }
                }
            }
        }
        
    @Column(name = "prepare_start_instant_seconds")
    protected long prepareStartInstantSeconds;
    @Override
    public long getPrepareStartInstantSeconds()
        {
        return this.prepareStartInstantSeconds;
        }
    @Override
    public Instant getPrepareStartInstant()
        {
        return Instant.ofEpochSecond(
            prepareStartInstantSeconds
            );
        }
    
    @Column(name = "prepare_duration_seconds")
    protected long prepareDurationSeconds;
    @Override
    public long getPrepareDurationSeconds()
        {
        return this.prepareDurationSeconds;
        }
    @Override
    public Duration getPrepareDuration()
        {
        return Duration.ofSeconds(
            prepareDurationSeconds
            );
        }

    @Column(name = "available_start_instant_seconds")
    protected long availableStartInstantSeconds;
    @Override
    public long getAvailableStartInstantSeconds()
        {
        return this.availableStartInstantSeconds;
        }
    @Override
    public Instant getAvailableStartInstant()
        {
        return Instant.ofEpochSecond(
            availableStartInstantSeconds
            );
        }
    @Column(name = "available_start_duration_seconds")
    protected long availableStartDurationSeconds;
    @Override
    public long getAvailableStartDurationSeconds()
        {
        return this.availableStartDurationSeconds;
        }
    @Override
    public Duration getAvailableStartDuration()
        {
        return Duration.ofSeconds(
            availableStartDurationSeconds
            );
        }
    @Override
    public Interval getAvailableStartInterval()
        {
        return Interval.of(
            getAvailableStartInstant(),
            getAvailableStartDuration()
            );
        }
    
    @Column(name = "available_duration_seconds")
    protected long availableDurationSeconds;
    @Override
    public long getAvailableDurationSeconds()
        {
        return this.availableDurationSeconds;
        }
    @Override
    public Duration getAvailableDuration()
        {
        return Duration.ofSeconds(
            availableDurationSeconds
            );
        }
    
    @Column(name = "release_start_instant_seconds")
    protected long releaseStartInstantSeconds;
    @Override
    public long getReleaseStartInstantSeconds()
        {
        return this.releaseStartInstantSeconds;
        }
    @Override
    public Instant getReleaseStartInstant()
        {
        return Instant.ofEpochSecond(
            releaseStartInstantSeconds
            );
        }
    
    @Column(name = "release_duration_seconds")
    protected long releaseDurationSeconds;
    @Override
    public long getReleaseDurationSeconds()
        {
        return this.releaseDurationSeconds;
        }
    @Override
    public Duration getReleaseDuration()
        {
        return Duration.ofSeconds(
            releaseDurationSeconds
            );
        }

    public IvoaScheduleStartDurationInstant makePreparingBean()
        {
        boolean valid = false;
        IvoaScheduleStartDurationInstant bean = new IvoaScheduleStartDurationInstant(); 
        if (getPrepareStartInstantSeconds() > 0)
            {
            bean.setStart(
                getPrepareStartInstant().toString()
                );
            valid = true;
            }
        if (getPrepareDurationSeconds() > 0)
            {
            bean.setDuration(
                getPrepareDuration().toString()
                );
            valid = true;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }

    public IvoaScheduleStartDurationInterval makeAvailableBean()
        {
        boolean valid = false;
        IvoaScheduleStartDurationInterval bean = new IvoaScheduleStartDurationInterval();
        if (getAvailableStartInstantSeconds() > 0)
            {
            StringBuffer buffer = new StringBuffer();
            buffer.append(
                getAvailableStartInstant().toString()
                );
            buffer.append(
                "/"
                );
            buffer.append(
                getAvailableStartDuration().toString()
                );
            bean.setStart(
                buffer.toString()
                );
            valid = true ;
            }
        if (getAvailableDurationSeconds() > 0)
            {
            bean.setDuration(
                getAvailableDuration().toString()
                );
            valid = true ;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }

    public IvoaScheduleStartDurationInstant makeReleasingBean()
        {
        boolean valid = false;
        IvoaScheduleStartDurationInstant bean = new IvoaScheduleStartDurationInstant(); 
        if (getReleaseStartInstantSeconds() > 0)
            {
            bean.setStart(
                getReleaseStartInstant().toString()
                );
            valid = true;
            }
        if (getReleaseDurationSeconds() > 0)
            {
            bean.setDuration(
                getReleaseDuration().toString()
                );
            valid = true;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }
    
    public IvoaScheduledExecutionSchedule makeScheduleBean()
        {
        boolean valid = false;
        IvoaScheduledExecutionSchedule bean = new IvoaScheduledExecutionSchedule(); 

        IvoaScheduleStartDurationInstant preparing = this.makePreparingBean();
        if (null != preparing)
            {
            bean.setPreparing(
                preparing
                );
            valid = true;
            }

        IvoaScheduleStartDurationInterval available = this.makeAvailableBean();
        if (null != available)
            {
            bean.setAvailable(
                available
                );
            valid = true;
            }

        IvoaScheduleStartDurationInstant releasing = this.makeReleasingBean(); 
        if (releasing != null)
            {
            bean.setReleasing(
                this.makeReleasingBean()
                );
            valid = true ;
            }
        if (valid)
            {
            return bean;
            }
        else {
            return null ;
            }
        }

    public IvoaScheduledExecutionSession makeBean(final URIBuilder uribuilder)
        {
        return this.fillBean(
            uribuilder,
            new IvoaScheduledExecutionSession().meta(
                this.makeMeta(
                    uribuilder
                    )
                )
            );
        }

    public IvoaScheduledExecutionSession fillBean(
            final URIBuilder uribuilder,
            final IvoaScheduledExecutionSession bean
            ){
            this.fillBean(
                uribuilder,
                (IvoaSimpleExecutionSession ) bean
                );
            bean.setSchedule(
                this.makeScheduleBean()
                );
            return bean;
            }
    
    public IvoaSimpleExecutionSession fillBean(
        final URIBuilder uribuilder,
        final IvoaSimpleExecutionSession bean
        ){
        bean.setKind(
            this.getKind()
            );
        bean.setPhase(
            SessionPhaseMappings.toIvoa(this.getPhase())
            );
        bean.setExpires(
            this.getExpires()
            );
        bean.setExecutable(
            this.getExecutable().makeBean(
                uribuilder
                )
            );
        bean.setCompute(
            this.getComputeResource().makeBean(
                uribuilder
                )
            );

        for (AbstractDataResourceEntity resource : this.getDataResources())
            {
            bean.addDataItem(
                resource.makeBean(
                    uribuilder
                    )
                );
            }

        for (AbstractStorageResourceEntity resource : this.getStorageResources())
            {
            bean.addStorageItem(
                resource.makeBean(
                    uribuilder
                    )
                );
            }

        for (SimpleSessionConnectorEntity connector : this.getConnectors())
            {
            IvoaSimpleSessionConnector accessor = new IvoaSimpleSessionConnector();
            accessor.setKind(connector.getType());
            accessor.setProtocol(connector.getProtocol());
            accessor.setLocation(connector.getLocation());
            bean.addConnectorsItem(
                accessor
                );
            }
        
        return bean;
        }
    }

