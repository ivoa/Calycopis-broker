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

package net.ivoa.calycopis.broker.engine.entities.component;

import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.engine.functional.platform.mock.MockPlatform;
import net.ivoa.calycopis.broker.engine.functional.platform.mock.MockPlatformSettings;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.broker.engine.functional.processing.mock.MockDelayAction;
import net.ivoa.calycopis.schema.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecycleSchedule;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecycleStartDurationInstant;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecycleStartDurationInterval;

/**
 * 
 */
@Slf4j
@Entity
@Table(name = "lifecyclecomponents")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class LifecycleComponentEntity
extends ComponentEntity
implements LifecycleComponent
    {
    /**
     * Protected constructor for JPA entities.
     * 
     */
    protected LifecycleComponentEntity()
        {
        super();
        }

    /**
     * Protected constructor used by our Factories.
     * 
     */
    protected LifecycleComponentEntity(
        final IvoaComponentMetadata meta
        ){
        this(
            null,
            meta
            );
        }
    
    /**
     * 
     */
    public LifecycleComponentEntity(
        final IvoaLifecycleSchedule schedule,
        final IvoaComponentMetadata meta
        ){
        super(
            meta
            );
        if (schedule != null)
            {
            IvoaLifecycleStartDurationInstant preparing = schedule.getPreparing();
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

    /**
     * Get the parent Session.  
     *
     */
    public abstract SimpleExecutionSessionEntity getSession();
    
    @Column(name = "phase")
    @Enumerated(EnumType.STRING)
    private IvoaLifecyclePhase phase = IvoaLifecyclePhase.INITIALIZING;
    @Override
    public IvoaLifecyclePhase getPhase()
        {
        return this.phase;
        }
    @Override
    public void setPhase(final IvoaLifecyclePhase newphase)
        {
        // TODO This is where we need to have the phase transition checking.
        this.phase = newphase;
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

    public IvoaLifecycleStartDurationInstant makePreparingBean()
        {
        boolean valid = false;
        IvoaLifecycleStartDurationInstant bean = new IvoaLifecycleStartDurationInstant(); 
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

    public IvoaLifecycleStartDurationInterval makeAvailableBean()
        {
        boolean valid = false;
        IvoaLifecycleStartDurationInterval bean = new IvoaLifecycleStartDurationInterval();
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

    public IvoaLifecycleStartDurationInstant makeReleasingBean()
        {
        boolean valid = false;
        IvoaLifecycleStartDurationInstant bean = new IvoaLifecycleStartDurationInstant(); 
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
    
    public IvoaLifecycleSchedule makeScheduleBean()
        {
        boolean valid = false;
        IvoaLifecycleSchedule bean = new IvoaLifecycleSchedule(); 

        IvoaLifecycleStartDurationInstant preparing = this.makePreparingBean();
        if (null != preparing)
            {
            bean.setPreparing(
                preparing
                );
            valid = true;
            }

        IvoaLifecycleStartDurationInterval available = this.makeAvailableBean();
        if (null != available)
            {
            bean.setAvailable(
                available
                );
            valid = true;
            }

        IvoaLifecycleStartDurationInstant releasing = this.makeReleasingBean(); 
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
    
    @Override
    public ProcessingAction getCancelAction(final Platform platform, final ComponentProcessingRequest request)
        {
        int delay = 30_000;
        if (platform instanceof MockPlatform)
            {
            MockPlatformSettings settings = ((MockPlatform) platform).getMockEntitySettings();
            delay = settings.getCancelDelayMillis();
            }
        return new MockDelayAction(
            this,
            IvoaLifecyclePhase.CANCELLED,
            null,
            delay
            );
        }

    @Override
    public ProcessingAction getFailAction(final Platform platform, final ComponentProcessingRequest request)
        {
        int delay = 30_000;
        if (platform instanceof MockPlatform)
            {
            MockPlatformSettings settings = ((MockPlatform) platform).getMockEntitySettings();
            delay = settings.getFailDelayMillis();
            }
        return new MockDelayAction(
            this,
            IvoaLifecyclePhase.FAILED,
            null,
            delay
            );
        }
    }
