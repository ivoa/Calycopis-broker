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
 *     "timestamp": "2026-05-26T16:50:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 2,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.functional.processing.component;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.openapi.spring.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "preparecomponentrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class PrepareComponentRequestEntity
extends ComponentProcessingRequestEntity
implements ComponentProcessingRequest
    {

    public static final Duration DEFAULT_PREPARE_LOOP_INTERVAL = Duration.ofSeconds(5);
    
    protected PrepareComponentRequestEntity()
        {
        super();
        }

    protected PrepareComponentRequestEntity(final LifecycleComponentEntity component)
        {
        super(component);
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        LifecycleComponentEntity component = this.getComponent(
            platform
            );
        log.debug(
            "Pre-processing component [{}][{}][{}]",
            component.getUuid(),
            component.getKind(),
            component.getClass().getSimpleName()
            );

        prevPhase = component.getPhase();

        switch(prevPhase)
            {
            case INITIALIZING:
            case WAITING:
                // If the start time is in the future, set the phase to WAITING.
                if ((component.getPrepareStartInstant() != null) && (component.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    log.debug(
                        "Component [{}][{}] prepare start is in the future [{}]",
                        component.getUuid(),
                        component.getClass().getSimpleName(),
                        component.getPrepareStartInstant()
                        );
                    component.setPhase(
                        IvoaLifecyclePhase.WAITING
                        );
                    return ProcessingAction.NO_ACTION;
                    }
                // Start the prepare process and return a prepare action.
                else {
                    component.setPhase(
                        IvoaLifecyclePhase.PREPARING
                        );
                    return component.getPrepareAction(
                        platform,
                        this
                        );            
                    }

            //
            // Component is still PREPARING, so continue the prepare action.
            case PREPARING:
                return component.getPrepareAction(
                    platform,
                    this
                    );

            //
            // Phase is past PREPARING, no action needed.
            case AVAILABLE:
            case RUNNING:
            case RELEASING:
            case COMPLETED:
            case CANCELLED:
            case FAILED:
                return ProcessingAction.NO_ACTION;

            default:
                log.error(
                    "Unexpected phase [{}] for pre-processing component [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                this.fail(
                    platform,
                    component
                    );
                return ProcessingAction.NO_ACTION;
            }
        }

    protected void postProcess(final Platform platform, final ComponentProcessingAction action)
        {
        LifecycleComponentEntity component = this.getComponent(
            platform
            );
        log.debug(
            "Post-processing component [{}][{}][{}]",
            component.getUuid(),
            component.getKind(),
            component.getClass().getSimpleName()
            );

        if (action != null)
            {
            action.postProcess(
                component
                );
            }
        nextPhase = component.getPhase();

        if (prevPhase != nextPhase)
            {
            platform.getProcessingRequestFactory().getSessionProcessingRequestFactory().createUpdateSessionRequest(
                component.getSession()
                );
            }
        
        switch(nextPhase)
            {
            //
            // If the next phase is WAITING, reschedule this request.
            // TODO Ask the component how long to wait.
            case WAITING:
                Duration delay;
                if ((component.getPrepareStartInstant() != null) && (component.getPrepareStartInstant().isAfter(Instant.now())))
                    {
                    delay = Duration.between(
                        Instant.now(),
                        component.getPrepareStartInstant()
                        ).dividedBy(
                            2L
                            );
                    }
                else {
                    delay = Duration.ZERO;
                    }
                log.debug(
                    "Re-scheduling request [{}][{}] for component [{}][{}] in [{}]s",
                    this.getUuid(),
                    this.getClass().getSimpleName(),
                    component.getUuid(),
                    component.getClass().getSimpleName(),
                    delay.getSeconds()
                    );
                this.activate(delay);
                break;
                
            //
            // If the component is still PREPARING, update the activation time and wait.
            // TODO Ask the component how long to wait.
            case PREPARING:
                this.activate(
                    DEFAULT_PREPARE_LOOP_INTERVAL
                    );  
                break;

            //
            // If the component is AVAILABLE, schedule a monitor component request.
            case AVAILABLE:
            case RUNNING:
                if (prevPhase != nextPhase)
                    {
                    platform.getProcessingRequestFactory().getComponentProcessingRequestFactory().createMonitorComponentRequest(
                        component
                        );
                    }
                this.done(platform);
                break;

            //
            // Phase is past PREPARING, we are done.
            case RELEASING:
            case COMPLETED:
            case CANCELLED:
            case FAILED:
                this.done(platform);
                break;

            default:
                log.error(
                    "Unexpected phase [{}] for post-processing component [{}][{}]",
                    component.getPhase(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                this.fail(
                    platform,
                    component
                    );
                break;
            }
        }
    }
