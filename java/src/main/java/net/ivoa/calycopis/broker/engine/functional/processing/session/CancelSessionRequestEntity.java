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
    name = "cancelsessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
@Deprecated
public class CancelSessionRequestEntity
extends SessionProcessingRequestEntity
implements SessionProcessingRequest
    {

    protected CancelSessionRequestEntity()
        {
        super();
        }

    protected CancelSessionRequestEntity(final SimpleExecutionSessionEntity session)
        {
        super(
            SessionProcessingRequest.KIND,
            session
            );
        }

    public ProcessingAction preProcess(final Platform platform)
        {
        log.debug(
            "Pre-processing [CANCEL] for session [{}][{}][{}]",
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
            case IvoaSimpleExecutionSessionPhase.INITIAL:
            case IvoaSimpleExecutionSessionPhase.OFFERED:
            case IvoaSimpleExecutionSessionPhase.ACCEPTED:
            case IvoaSimpleExecutionSessionPhase.WAITING:
            case IvoaSimpleExecutionSessionPhase.PREPARING:
            case IvoaSimpleExecutionSessionPhase.AVAILABLE:
            case IvoaSimpleExecutionSessionPhase.RUNNING:
            case IvoaSimpleExecutionSessionPhase.RELEASING:
                //
                // Set the session phase to CANCELLED.
                log.debug(
                    "Setting session phase to [{}][CANCELLED]",
                    this.session.getUuid()
                    );
                this.session.setPhase(
                    IvoaSimpleExecutionSessionPhase.CANCELLED
                    );
                break;

            //
            // If the session is already in a terminal phase, then nothing more to do.
            case IvoaSimpleExecutionSessionPhase.REJECTED:
            case IvoaSimpleExecutionSessionPhase.EXPIRED:
            case IvoaSimpleExecutionSessionPhase.CANCELLED:
            case IvoaSimpleExecutionSessionPhase.COMPLETED:
            case IvoaSimpleExecutionSessionPhase.FAILED:
                log.debug(
                    "Skipping [CANCEL] for session [{}][{}], phase is already [{}]",
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
        //
        // Cancel all of the components.
        scheduleCancelIfActive(
            platform,
            this.session.getExecutable()
            );
        scheduleCancelIfActive(
            platform,
            this.session.getComputeResource()
            );
        for (AbstractDataResourceEntity dataResource : this.session.getDataResources())
            {
            scheduleCancelIfActive(
                platform,
                dataResource
                );
            }
        for (AbstractStorageResourceEntity storageResource : this.session.getStorageResources())
            {
            scheduleCancelIfActive(
                platform,
                storageResource
                );
            }
        return ProcessingAction.NO_ACTION ;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing [CANCEL] for session [{}][{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName()
            );
        this.done(
            platform
            );
        }
    }
