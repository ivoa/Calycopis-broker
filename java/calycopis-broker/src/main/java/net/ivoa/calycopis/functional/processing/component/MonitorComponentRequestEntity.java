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
 *
 */

package net.ivoa.calycopis.functional.processing.component;

import java.time.Duration;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.ProcessingRequestFactory;

/**
 * A processing request that monitors a component by polling its status
 * via getMonitorAction() and updating the component and session phases
 * accordingly.
 */
@Slf4j
@Entity
@Table(
    name = "monitorcomponentrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class MonitorComponentRequestEntity
extends ComponentProcessingRequestEntity
implements ComponentProcessingRequest
    {

    public static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(10);

    protected MonitorComponentRequestEntity()
        {
        super();
        }

    protected MonitorComponentRequestEntity(final LifecycleComponentEntity component)
        {
        super(component);
        }

    @Override
    public ProcessingAction preProcess(final ProcessingRequestFactory processing, final Platform platform)
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
            //
            // The component is active, return the component's monitor action.
            case AVAILABLE:
            case RUNNING:
                return component.getMonitorAction(
                    platform,
                    this
                    );

            //
            // The component is releasing, return the component's release action.
            case RELEASING:
                return component.getReleaseAction(
                    platform,
                    this
                    );

            //
            // The phase is already beyond active, no action required.
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
                    processing,
                    platform,
                    component
                    );
                return ProcessingAction.NO_ACTION;
            }
        }

    @Override
    public void postProcess(final ProcessingRequestFactory processing, final Platform platform, final ComponentProcessingAction action)
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
            processing.getSessionProcessingRequestFactory().createUpdateSessionRequest(
                component.getSession()
                );
            }
        
        switch(nextPhase)
            {
            case AVAILABLE:
            case RUNNING:
            case RELEASING:
                // TODO Ask the component for the poll interval.
                // https://github.com/ivoa/Calycopis-broker/issues/365
                this.activate(DEFAULT_POLL_INTERVAL);
                break;

            case COMPLETED:
            case CANCELLED:
            case FAILED:
                this.done(platform);
                break;

            default:
                log.error(
                    "Unexpected phase [{}] for post-processing component [{}][{}]",
                    nextPhase.toString(),
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                this.fail(
                    processing,
                    platform,
                    component
                    );
                break;
            }
        }
    }
