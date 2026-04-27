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

package net.ivoa.calycopis.functional.processing.session;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;
import net.ivoa.calycopis.engine.session.ExecutionSessionPhase;

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
    public ProcessingAction preProcess(final ProcessingRequestFactory processing, final Platform platform)
        {
        log.debug(
            "Pre-processing [PREPARE] for session [{}][{}]",
            this.session.getUuid(),
            this.session.getPhase()
            );

        switch (this.session.getPhase())
            {
            case ExecutionSessionPhase.INITIAL:
            case ExecutionSessionPhase.OFFERED:
            case ExecutionSessionPhase.REJECTED:
            case ExecutionSessionPhase.EXPIRED:
                log.error(
                    "[PREPARE] shouldn't be called for [{}][{}] because phase is stll [{}]",
                    this.session.getUuid(),
                    this.session.getPhase(),
                    session.getPhase()
                    );
                return failSession(
                    processing,
                    platform
                    );
            //
            // Start to prepare the session
            case ExecutionSessionPhase.ACCEPTED:
            case ExecutionSessionPhase.WAITING:
                return beginPreparing(
                    processing,
                    platform
                    );
            //
            // Phase is already PREPARING, no further Action required.
            case ExecutionSessionPhase.PREPARING:
                return ProcessingAction.NO_ACTION ;

            //
            // Phase is past PREPARING, no further Action required.
            case ExecutionSessionPhase.AVAILABLE:
            case ExecutionSessionPhase.RUNNING:
            case ExecutionSessionPhase.RELEASING:
            case ExecutionSessionPhase.COMPLETED:
            case ExecutionSessionPhase.CANCELLED:
            case ExecutionSessionPhase.FAILED:
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

    protected ProcessingAction beginPreparing(final ProcessingRequestFactory processing, final Platform platform)
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
                    ExecutionSessionPhase.WAITING
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
            ExecutionSessionPhase.PREPARING
            );

        log.debug(
            "Scheduling [PREPARE] request for executable [{}][{}]",
            this.session.getExecutable().getUuid(),
            this.session.getExecutable().getClass().getSimpleName()
            );
        processing.getComponentProcessingRequestFactory().createPrepareComponentRequest(
            this.session.getExecutable()
            );

        log.debug(
            "Scheduling [PREPARE] request for compute resource [{}][{}]",
            this.session.getComputeResource().getUuid(),
            this.session.getComputeResource().getClass().getSimpleName()
            );
        processing.getComponentProcessingRequestFactory().createPrepareComponentRequest(
            this.session.getComputeResource()
            );

        for(AbstractStorageResourceEntity storageResource : this.session.getStorageResources())
            {
            log.debug(
                "Scheduling [PREPARE] request for storage resource [{}][{}]",
                storageResource.getUuid(),
                storageResource.getClass().getSimpleName()
                );
            processing.getComponentProcessingRequestFactory().createPrepareComponentRequest(
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
            processing.getComponentProcessingRequestFactory().createPrepareComponentRequest(
                dataResource
                );
            }
        //
        // No further Action required.
        return ProcessingAction.NO_ACTION;
        }

    public static final Duration DEFAULT_WAITING_DELAY = Duration.ofSeconds(30);
    
    @Override
    public void postProcess(final ProcessingRequestFactory processing, final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing [PREPARE] for session [{}][{}]",
            this.session.getUuid(),
            this.session.getPhase()
            );

        switch(this.session.getPhase())
            {
            case ExecutionSessionPhase.INITIAL:
            case ExecutionSessionPhase.OFFERED:
            case ExecutionSessionPhase.REJECTED:
            case ExecutionSessionPhase.EXPIRED:
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
            case ExecutionSessionPhase.WAITING:
                Duration delay = DEFAULT_WAITING_DELAY;
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
            case ExecutionSessionPhase.PREPARING:
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
            case ExecutionSessionPhase.AVAILABLE:
            case ExecutionSessionPhase.RUNNING:
            case ExecutionSessionPhase.RELEASING:
            case ExecutionSessionPhase.COMPLETED:
            case ExecutionSessionPhase.CANCELLED:
            case ExecutionSessionPhase.FAILED:
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
