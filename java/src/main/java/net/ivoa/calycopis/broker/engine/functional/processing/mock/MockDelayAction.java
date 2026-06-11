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

package net.ivoa.calycopis.broker.engine.functional.processing.mock;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponent;
import net.ivoa.calycopis.broker.engine.functional.processing.component.ComponentProcessingAction;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;

/**
 * A ProcessingAction that simply waits for a specified time before completing.
 * Useful for testing the processing framework and simulating long-running tasks.
 * 
 */
@Slf4j
public class MockDelayAction
implements ComponentProcessingAction
    {
    int loopDelay ;

    UUID   componentUuid;
    String componentClass;

    IvoaLifecyclePhase waitPhase ;
    IvoaLifecyclePhase donePhase ;

    /**
     * 
     */
    public MockDelayAction(final LifecycleComponent component, int delay)
        {
        this.componentUuid  = component.getUuid();
        this.componentClass = component.getClass().getSimpleName();
        this.loopDelay = delay ;
        }

    /**
     * 
     */
    public MockDelayAction(final LifecycleComponent component, IvoaLifecyclePhase waitPhase, IvoaLifecyclePhase donePhase, int delay)
        {
        this.componentUuid  = component.getUuid();
        this.componentClass = component.getClass().getSimpleName();
        this.waitPhase = waitPhase ;
        this.donePhase = donePhase ;
        this.loopDelay = delay ;
        }

    @Override
    public void preProcess(LifecycleComponent component)
        {
        log.debug(
            "Pre-processing [{}][{}]",
            componentUuid,
            componentClass
            );
        if (waitPhase != null)
            {
            component.setPhase(
                waitPhase
                );
            }
        }

    
    @Override
    public void process()
        {
        log.debug(
            "Processing [{}][{}]",
            componentUuid,
            componentClass
            );
        if (loopDelay > 0)
            {
            try {
                Thread.sleep(
                    this.loopDelay
                    );
                }
            catch (InterruptedException e)
                {
                log.error(
                    "Interrupted while processing [{}][{}]",
                    componentUuid,
                    componentClass
                    );
                }
            }
        }

    @Override
    public void postProcess(LifecycleComponent component)
        {
        log.debug(
            "Post-processing [{}][{}]",
            componentUuid,
            componentClass
            );
        if (donePhase != null)
            {
            component.setPhase(
                donePhase
                );
            }
        }
    }
