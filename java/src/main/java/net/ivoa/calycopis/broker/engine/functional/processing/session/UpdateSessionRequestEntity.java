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
 *       "value": 1,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.functional.processing.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleExecutionSessionPhase;

/**
 * A session-level processing request that checks whether all components
 * have completed (or failed) and transitions the session phase accordingly.
 */
@Slf4j
@Entity
@Table(
    name = "monitorsessionrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class UpdateSessionRequestEntity
extends SessionProcessingRequestEntity
implements SessionProcessingRequest
    {

    protected UpdateSessionRequestEntity()
        {
        super();
        }

    protected UpdateSessionRequestEntity(final SimpleExecutionSessionEntity session)
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
            "Pre-processing update request for session [{}][{}][{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );

        final List<LifecycleComponentEntity> preparingList = new ArrayList<LifecycleComponentEntity>();
        final List<LifecycleComponentEntity> availableList = new ArrayList<LifecycleComponentEntity>();
        final List<LifecycleComponentEntity> releasingList = new ArrayList<LifecycleComponentEntity>();
        final List<LifecycleComponentEntity> completedList = new ArrayList<LifecycleComponentEntity>();
        final List<LifecycleComponentEntity> cancelledList = new ArrayList<LifecycleComponentEntity>();
        final List<LifecycleComponentEntity> failedList = new ArrayList<LifecycleComponentEntity>();

        final AtomicInteger count = new AtomicInteger(0);
        
        class ComponentFilter
            {
            void count(LifecycleComponentEntity component)
                {
                if (component != null)
                    {
                    count.incrementAndGet();
                    switch (component.getPhase())
                        {
                        case WAITING:
                        case PREPARING:
                            preparingList.add(component);
                            break;
                        case AVAILABLE:
                        case RUNNING:
                            availableList.add(component);
                            break;
                        case RELEASING:
                            releasingList.add(component);
                            break;
                        case COMPLETED:
                            completedList.add(component);
                            break;
                        case CANCELLED:
                            cancelledList.add(component);
                            break;
                        case FAILED:
                            failedList.add(component);
                            break;
                        default:
                            log.warn(
                                "Component [{}][{}] in session [{}][{}] has unexpected phase [{}]",
                                component.getUuid(),
                                component.getClass().getSimpleName(),
                                UpdateSessionRequestEntity.this.session.getUuid(),
                                UpdateSessionRequestEntity.this.session.getClass().getSimpleName(),
                                component.getPhase()
                                );
                        }
                    }
                }
            }
        
        ComponentFilter filter = new ComponentFilter();
        
        filter.count(
            session.getExecutable()
            );
        filter.count(
            session.getComputeResource()
            );
        for (LifecycleComponentEntity data: session.getDataResources())
            {
            filter.count(
                data
                );
            }
        for (LifecycleComponentEntity storage: session.getStorageResources())
            {
            filter.count(
                storage
                );
            }

        //
        // If ANY of the components are FAILED
        if (failedList.size() > 0)
            {
            log.debug(
                "[{}] components marked as [FAILED]",
                failedList.size()
                );
            switch (this.session.getPhase())
                {
                case COMPLETED:
                case CANCELLED:
                case FAILED:
                    log.debug(
                        "Skipping change to [FAILED] for session [{}][{}], phase is already [{}]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    break;
                    
                default:
                    log.debug(
                        "Setting session [{}][{}] phase to [FAILED]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName()
                        );
                    this.session.setPhase(
                        IvoaSimpleExecutionSessionPhase.FAILED
                        );
                    this.scheduleReleaseAll(
                        platform
                        );
                    break;
                }
            }

        //
        // If ANY of the components are CANCELLED
        else if (cancelledList.size() > 0)
            {
            log.debug(
                "[{}] components marked as [CANCELLED]",
                cancelledList.size()
                );
            switch (this.session.getPhase())
                {
                case COMPLETED:
                case CANCELLED:
                case FAILED:
                    log.debug(
                        "Skipping change to [CANCELED] for session [{}][{}], phase is already [{}]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    break;
                    
                default:
                    log.debug(
                        "Setting session [{}][{}] phase to [CANCELLED]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName()
                        );
                    this.session.setPhase(
                        IvoaSimpleExecutionSessionPhase.CANCELLED
                        );
                    this.scheduleReleaseAll(
                        platform
                        );
                    break;
                }
            }

        //
        // If ANY of the components are PREPARING
        else if (preparingList.size() > 0)
            {
            log.debug(
                "[{}] components marked as [PREPARING]",
                preparingList.size()
                );
            switch (this.session.getPhase())
                {
                case AVAILABLE:
                case RUNNING:
                case COMPLETED:
                case CANCELLED:
                case FAILED:
                    log.debug(
                        "Skipping change to [PREPARING] for session [{}][{}], phase is already [{}]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    break;
                    
                default:
                    log.debug(
                        "Setting session [{}][{}][{}] phase to [PREPARING]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    this.session.setPhase(
                        IvoaSimpleExecutionSessionPhase.PREPARING
                        );
                    break;
                }
            }

        //
        // If ANY of the components are RELEASING
        else if (releasingList.size() > 0)
            {
            log.debug(
                "[{}] components marked as [RELEASING]",
                releasingList.size()
                );
            switch (this.session.getPhase())
                {
                case RELEASING:
                case COMPLETED:
                case CANCELLED:
                case FAILED:
                    log.debug(
                        "Skipping change to [RELEASING] for session [{}][{}], phase is already [{}]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    break;

                default:
                    log.debug(
                        "Setting session [{}][{}] phase to [RELEASING]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    this.session.setPhase(
                        IvoaSimpleExecutionSessionPhase.RELEASING
                        );
                    this.scheduleReleaseAll(
                        platform
                        );
                    break;
                }
            }

        //
        // If ALL of the components are AVAILABLE
        else if (availableList.size() >= count.get())
            {
            log.debug(
                "[{}] components marked as [AVAILABLE]",
                availableList.size()
                );
            switch (this.session.getPhase())
                {
                case AVAILABLE:
                case RUNNING:
                case COMPLETED:
                case CANCELLED:
                case FAILED:
                    log.debug(
                        "Skipping change to [AVAILABLE] for session [{}][{}], phase is already [{}]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    break;
                    
                default:
                    log.debug(
                        "Setting session [{}][{}] phase to [AVAILABLE]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    this.session.setPhase(
                        IvoaSimpleExecutionSessionPhase.AVAILABLE
                        );
                    break;
                }
            }

        //
        // If ALL of the components are COMPLETED
        else if (completedList.size() >= count.get())
            {
            log.debug(
                "[{}] components marked as [COMPLETED]",
                completedList.size()
                );
            switch (this.session.getPhase())
                {
                case COMPLETED:
                case CANCELLED:
                case FAILED:
                    log.debug(
                        "Skipping change to [COMPLETED] for session [{}][{}], phase is already [{}]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    break;
                    
                default:
                    log.debug(
                        "Setting session [{}][{}] phase to [COMPLETED]",
                        this.session.getUuid(),
                        this.session.getClass().getSimpleName(),
                        this.session.getPhase()
                        );
                    this.session.setPhase(
                        IvoaSimpleExecutionSessionPhase.COMPLETED
                        );
                    break;
                }
            }
        else {
            log.debug(
                "No change to session [{}][{}] phase [{}]",
                this.session.getUuid(),
                this.session.getClass().getSimpleName(),
                this.session.getPhase()
                );
            
            }
        return ProcessingAction.NO_ACTION;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        log.debug(
            "Post-processing monitor for session [{}][{}] with phase [{}]",
            this.session.getUuid(),
            this.session.getClass().getSimpleName(),
            this.session.getPhase()
            );
        this.done(platform);
        }
    }
