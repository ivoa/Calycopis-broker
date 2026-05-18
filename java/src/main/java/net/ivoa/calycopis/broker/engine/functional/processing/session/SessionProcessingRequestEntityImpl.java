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

package net.ivoa.calycopis.broker.engine.functional.processing.session;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityImpl;
import net.ivoa.calycopis.broker.engine.functional.platfom.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestEntityImpl;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "sessionprocessingrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class SessionProcessingRequestEntityImpl
extends ProcessingRequestEntityImpl
implements SessionProcessingRequest
    {

    protected SessionProcessingRequestEntityImpl()
        {
        super();
        }

    protected SessionProcessingRequestEntityImpl(final URI kind, final SimpleExecutionSessionEntity session)
        {
        super(kind);
        log.debug("Created SessionProcessingRequestEntity kind [{}] for session [{}]", kind, session.getUuid());
        this.session = session;
        }

    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    protected SimpleExecutionSessionEntity session;

    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }

    protected ProcessingAction failSession(final Platform platform)
        {
        if (this.session != null)
            {
            log.debug("Failing session [{}]", this.session.getUuid());
            this.session.setPhase(
                IvoaSimpleExecutionSessionPhase.FAILED
                );
            platform.getProcessingRequestFactory().getSessionProcessingRequestFactory().createFailSessionRequest(
                this.session
                );            
            }
        else {
            log.debug("No session to fail");
            }
        return ProcessingAction.NO_ACTION ;
        }

    
    protected void scheduleCancelIfActive(final Platform platform, final LifecycleComponentEntity component)
        {
        if (component == null)
            {
            return;
            }

        IvoaLifecyclePhase phase = component.getPhase();

        log.debug(
            "Scheduling [CANCEL] for [{}][{}][{}]",
            component.getUuid(),
            component.getClass().getSimpleName(),
            phase
            );

        switch (phase)
            {
            case AVAILABLE:
            case RUNNING:
            case RELEASING:
                log.debug(
                    "Component [{}][{}] phase is [{}], requesting [CANCEL]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    phase
                    );
                platform.getProcessingRequestFactory().getComponentProcessingRequestFactory().createCancelComponentRequest(
                    component
                    );
                break;

            case COMPLETED:
            case FAILED:
            case CANCELLED:
                log.debug(
                    "Component [{}][{}] phase is already [{}], skipping [CANCEL]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    phase
                    );
                break;

            default:
                log.debug(
                    "Unexpected phase [{}] for component [{}][{}], skipping [CANCEL]",
                    phase,
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                break;
            }
        }

    protected void scheduleReleaseAll(final Platform platform)
        {
        scheduleReleaseIfActive(
            platform,
            this.session.getExecutable()
            );
        scheduleReleaseIfActive(
            platform,
            this.session.getComputeResource()
            );
        for (AbstractDataResourceEntity dataResource : this.session.getDataResources())
            {
            scheduleReleaseIfActive(
                platform,
                dataResource
                );
            }
        for (AbstractStorageResourceEntityImpl storageResource : this.session.getStorageResources())
            {
            scheduleReleaseIfActive(
                platform,
                storageResource
                );
            }
        }
    
    protected void scheduleReleaseIfActive(final Platform platform, final LifecycleComponentEntity component)
        {
        if (component == null)
            {
            return;
            }
        
        IvoaLifecyclePhase phase = component.getPhase();

        log.debug(
            "Scheduling [RELEASE] for [{}][{}][{}]",
            component.getUuid(),
            component.getClass().getSimpleName(),
            phase
            );
        
        switch (phase)
            {
            case AVAILABLE:
            case RUNNING:
                log.debug(
                    "Component [{}][{}] phase is [{}], requesting [RELEASE]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    phase
                    );
                platform.getProcessingRequestFactory().getComponentProcessingRequestFactory().createReleaseComponentRequest(
                    component
                    );
                break;

            case RELEASING:
            case COMPLETED:
            case FAILED:
            case CANCELLED:
                log.debug(
                    "Component [{}][{}] phase is already [{}], skipping [RELEASE]",
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    phase
                    );
                break;

            default:
                log.debug(
                    "Unexpected phase [{}] for component [{}][{}], skipping [RELEASE]",
                    phase,
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                break;
            }
        }
    }
