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
 *     "timestamp": "2026-04-29T10:00:00",
 *     "name": "Copilot",
 *     "version": "unknown",
 *     "model": "claude-sonnet-4.5",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.engine.processing.mock;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.engine.lifecycle.LifecycleComponent;
import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;
import net.ivoa.calycopis.engine.processing.component.ComponentProcessingAction;

/**
 * A {@link ComponentProcessingAction} that simply sleeps for a configurable
 * delay before completing.  Useful for testing the processing framework and
 * simulating long-running tasks without a real execution platform.
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.mock.MockDelayAction} class.
 * It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
@Slf4j
public class MockDelayAction
implements ComponentProcessingAction
    {

    protected int loopDelay;

    protected UUID   componentUuid;
    protected String componentClass;

    protected LifecyclePhase waitPhase;
    protected LifecyclePhase donePhase;

    /**
     * Construct a delay action with a fixed sleep time and no phase transitions.
     *
     */
    public MockDelayAction(final LifecycleComponent component, int delay)
        {
        this.componentUuid  = component.getUuid();
        this.componentClass = component.getClass().getSimpleName();
        this.loopDelay = delay;
        }

    /**
     * Construct a delay action that transitions the component phase before sleeping
     * ({@code waitPhase}) and again after sleeping ({@code donePhase}).
     *
     */
    public MockDelayAction(final LifecycleComponent component, LifecyclePhase waitPhase, LifecyclePhase donePhase, int delay)
        {
        this.componentUuid  = component.getUuid();
        this.componentClass = component.getClass().getSimpleName();
        this.waitPhase = waitPhase;
        this.donePhase = donePhase;
        this.loopDelay = delay;
        }

    @Override
    public void preProcess(final LifecycleComponent component)
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
                Thread.currentThread().interrupt();
                }
            }
        }

    @Override
    public void postProcess(final LifecycleComponent component)
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
