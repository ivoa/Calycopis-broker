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
 * A session-level processing request that schedules release requests
 * for all active components in the session.
 */
@Slf4j
@Entity
@Table(
    name = "releasesessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class ReleaseSessionRequestEntity
extends SessionProcessingRequestEntity
implements SessionProcessingRequest
    {

    protected ReleaseSessionRequestEntity()
        {
        super();
        }

    protected ReleaseSessionRequestEntity(final SimpleExecutionSessionEntity session)
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
            "Pre-processing [RELEASE] for session [{}][{}][{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );

        //
        // Check the current phase.
        switch (this.session.getPhase())
            {
            //
            // If the session hasn't reached a terminal phase yet. 
            case ExecutionSessionPhase.INITIAL:
            case ExecutionSessionPhase.OFFERED:
            case ExecutionSessionPhase.ACCEPTED:
            case ExecutionSessionPhase.WAITING:
            case ExecutionSessionPhase.PREPARING:
            case ExecutionSessionPhase.AVAILABLE:
            case ExecutionSessionPhase.RUNNING:
            case ExecutionSessionPhase.RELEASING:

                //
                // Set the session phase to RELEASING.
                log.debug(
                    "Setting session [{}][{}] phase to [RELEASING]",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                this.session.setPhase(
                    ExecutionSessionPhase.RELEASING
                    );
                break;

            //
            // If the session is already in a terminal phase, then nothing more to do.
            case ExecutionSessionPhase.REJECTED:
            case ExecutionSessionPhase.EXPIRED:
            case ExecutionSessionPhase.CANCELLED:
            case ExecutionSessionPhase.COMPLETED:
            case ExecutionSessionPhase.FAILED:
                log.debug(
                    "Skipping [RELEASE] for session [{}][{}], phase is already [{}]",
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName(),
                    this.session.getPhase()
                    );
                break;
                
            default:
                log.error(
                    "Unexpected phase [{}] for session [{}][{}]",
                    this.session.getPhase(),
                    this.session.getUuid(),
                    this.session.getClass().getSimpleName()
                    );
                break;
            }
        
        scheduleReleaseIfActive(
            processing,
            platform,
            this.session.getExecutable()
            );
        scheduleReleaseIfActive(
            processing,
            platform,
            this.session.getComputeResource()
            );
        for (AbstractDataResourceEntity dataResource : this.session.getDataResources())
            {
            scheduleReleaseIfActive(
                processing,
                platform,
                dataResource
                );
            }
        for (AbstractStorageResourceEntity storageResource : this.session.getStorageResources())
            {
            scheduleReleaseIfActive(
                processing,
                platform,
                storageResource
                );
            }

        return ProcessingAction.NO_ACTION;
        }

    @Override
    public void postProcess(final ProcessingRequestFactory processing, final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing release for session [{}][{}][{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );
        this.done(
            platform
            );
        }
    }
