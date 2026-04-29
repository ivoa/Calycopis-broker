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

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.engine.lifecycle.LifecycleComponent;
import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;

/**
 * A {@link MockDelayAction} that decrements a loop counter on a
 * {@link MockMonitorableComponent} and transitions it to
 * {@link LifecyclePhase#RELEASING} once the counter reaches zero.
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.mock.MockMonitorAction} class.
 * It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
@Slf4j
public class MockMonitorAction
extends MockDelayAction
    {

    public MockMonitorAction(final MockMonitorableComponent monitorable, int delay)
        {
        super(
            monitorable,
            delay
            );
        }

    @Override
    public void preProcess(final LifecycleComponent component)
        {
        log.debug(
            "Pre-processing [{}][{}]",
            component.getUuid(),
            component.getClass().getSimpleName()
            );
        super.preProcess(
            component
            );
        }

    @Override
    public void postProcess(final LifecycleComponent component)
        {
        if (component instanceof MockMonitorableComponent)
            {
            MockMonitorableComponent monitorable = (MockMonitorableComponent) component;
            int count = monitorable.getLifecycleLoopCount();
            count--;

            log.debug(
                "Post-processing [{}][{}] count [{}]",
                component.getUuid(),
                component.getClass().getSimpleName(),
                count
                );

            monitorable.setLifecycleLoopCount(
                count
                );

            if (count <= 0)
                {
                component.setPhase(
                    LifecyclePhase.RELEASING
                    );
                }
            }
        else {
            log.error(
                "Unexpected component class [{}] while post-processing [{}]",
                component.getClass().getSimpleName(),
                component.getUuid()
                );
            }

        super.postProcess(
            component
            );
        }

    }
