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

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "preparesessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class PrepareSessionRequestEntity
extends SessionProcessingRequestEntity
implements SessionProcessingRequest
    {

    public static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(5);

    protected PrepareSessionRequestEntity()
        {
        super();
        }

    protected PrepareSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            SessionProcessingRequest.KIND,
            session
            );
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug(
            "Pre-processing [PREPARE] for session [{}][{}]",
            this.session.getUuid(),
            this.session.getPhase()
            );

        switch (this.session.getPhase())
            {
            case IvoaSimpleExecutionSessionPhase.INITIAL:
            case IvoaSimpleExecutionSessionPhase.OFFERED:
            case IvoaSimpleExecutionSessionPhase.REJECTED:
            case IvoaSimpleExecutionSessionPhase.EXPIRED:
                log.error(
                    "[PREPARE] shouldn't be called for [{}][{}] because phase is stll [{}]",
                    this.session.getUuid(),
                    this.session.getPhase(),
                    session.getPhase()
                    );
                return this.failSession(
                    platform
                    );
            //
            // Start to prepare the session
            case IvoaSimpleExecutionSessionPhase.ACCEPTED:
            case IvoaSimpleExecutionSessionPhase.WAITING:
                return this.beginPreparing(
                    platform
                    );
            //
            // Phase is already PREPARING, no further Action required.
            case IvoaSimpleExecutionSessionPhase.PREPARING:
                return ProcessingAction.NO_ACTION ;

            //
            // Phase is past PREPARING, no further Action required.
            case IvoaSimpleExecutionSessionPhase.AVAILABLE:
            case IvoaSimpleExecutionSessionPhase.RUNNING:
            case IvoaSimpleExecutionSessionPhase.RELEASING:
            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
                return ProcessingAction.NO_ACTION ;
            
            default:
                log.error(
                    "Unexpected phase [{}] for session [{}][{}]",
                    this.session.getPhase(),
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                return ProcessingAction.NO_ACTION ;
            }
        }

    protected ProcessingAction beginPreparing(final Platform platform)
        {
        log.debug(
            "Begin preparing session [{}][{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName()
            );
        // If we don't need to start preparing yet.
        if (session.getPrepareStartInstant() != null)
            {
            if (session.getPrepareStartInstant().isAfter(Instant.now()))
                {
                log.debug(
                    "Session [{}][{}] prepare start time is in the future [{}], setting phase to WAITING",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    this.session.getPrepareStartInstant()
                    );
                //
                // Set the session phase to WAITING.
                this.session.setPhase(
                    IvoaSimpleExecutionSessionPhase.WAITING
                    );
                //
                // Done for now.
                return ProcessingAction.NO_ACTION ;
                }
            }
        //
        // Set the session phase to PREPARING and prepare the session components.
        log.debug(
            "Setting session [{}][{}] phase to [PREPARING]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName()
            );
        this.session.setPhase(
            IvoaSimpleExecutionSessionPhase.PREPARING
            );

        log.debug(
            "Scheduling [PREPARE] request for executable [{}][{}]",
            this.session.getExecutable().getUuid(),
            this.session.getExecutable().getClass().getSimpleName()
            );
        platform.getProcessingRequestFactory().getComponentProcessingRequestFactory().createPrepareComponentRequest(
            this.session.getExecutable()
            );

        log.debug(
            "Scheduling [PREPARE] request for compute resource [{}][{}]",
            this.session.getComputeResource().getUuid(),
            this.session.getComputeResource().getClass().getSimpleName()
            );
        platform.getProcessingRequestFactory().getComponentProcessingRequestFactory().createPrepareComponentRequest(
            this.session.getComputeResource()
            );

        for(AbstractStorageResourceEntity storageResource : this.session.getStorageResources())
            {
            log.debug(
                "Scheduling [PREPARE] request for storage resource [{}][{}]",
                storageResource.getUuid(),
                storageResource.getClass().getSimpleName()
                );
            platform.getProcessingRequestFactory().getComponentProcessingRequestFactory().createPrepareComponentRequest(
                storageResource
                );
            }

        for(AbstractDataResourceEntity dataResource : this.session.getDataResources())
            {
            log.debug(
                "Scheduling [PREPARE] request for data resource [{}][{}]",
                dataResource.getUuid(),
                dataResource.getClass().getSimpleName()
                );
            platform.getProcessingRequestFactory().getComponentProcessingRequestFactory().createPrepareComponentRequest(
                dataResource
                );
            }
        //
        // No further Action required.
        return ProcessingAction.NO_ACTION;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing [PREPARE] for session [{}][{}]",
            this.session.getUuid(),
            this.session.getPhase()
            );

        switch(this.session.getPhase())
            {
            case IvoaSimpleExecutionSessionPhase.INITIAL:
            case IvoaSimpleExecutionSessionPhase.OFFERED:
            case IvoaSimpleExecutionSessionPhase.REJECTED:
            case IvoaSimpleExecutionSessionPhase.EXPIRED:
                log.error(
                    "[PREPARE] shouldn't be called for [{}][{}] because phase is stll [${}]",
                    this.session.getUuid(),
                    this.session.getPhase(),
                    session.getPhase()
                    );
                this.done(
                    platform
                    );
                break;
            //
            // Phase is WAITING, reschedule this request for half the time difference.
            case IvoaSimpleExecutionSessionPhase.WAITING:
                Duration delay = DEFAULT_POLL_INTERVAL;
                if ((this.session.getPrepareStartInstant() != null) && (this.session.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    delay = Duration.between(
                        Instant.now(),
                        this.session.getPrepareStartInstant()
                        ).dividedBy(
                            2L
                            );
                    }
                log.debug(
                    "Session [{}][{}] phase is [{}], re-scheduling request for [{}]s",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    this.session.getPhase(),
                    delay.getSeconds()
                    );
                this.activate(  
                    delay
                    );
                break;
            //
            // Phase is PREPARING, wait until the components are ready.
            case IvoaSimpleExecutionSessionPhase.PREPARING:
                log.debug(
                    "Session [{}][{}] phase is [{}], waiting for components to become [AVAILABLE]",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    this.session.getPhase()
                    );
                /*
                 * Marking this request as done assumes we have at least one component
                 * (the executable or the compute resource) still in PREPARING phase
                 * that will trigger a new PrepareSessionRequest when the component
                 * becomes AVAILABLE.
                 * If not, then we should re-schedule this request to check again later.
                 * 
                 */
                this.done(
                    platform
                    );  
                break;
            //
            // The phase has moved beyond PREPARING.
            case IvoaSimpleExecutionSessionPhase.AVAILABLE:
            case IvoaSimpleExecutionSessionPhase.RUNNING:
            case IvoaSimpleExecutionSessionPhase.RELEASING:
            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
                log.debug(
                    "Session [{}][{}] phase is [{}], no further action required",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                this.done(
                    platform
                    );  
                break;
                
            default:
                log.error(
                    "Unexpected phase [{}] for session [{}][{}]",
                    this.session.getPhase(),
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                this.done(
                    platform
                    );
                break;
            }
        }
    }
