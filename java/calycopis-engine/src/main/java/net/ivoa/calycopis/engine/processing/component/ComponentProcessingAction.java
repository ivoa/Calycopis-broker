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

package net.ivoa.calycopis.engine.processing.component;

import net.ivoa.calycopis.engine.lifecycle.LifecycleComponent;
import net.ivoa.calycopis.engine.processing.ProcessingAction;

/**
 * Framework-independent interface for actions that prepare, monitor, release,
 * cancel, or fail a lifecycle component.
 *
 * Each action is executed outside any transaction context via {@link #process()}.
 * The {@link #preProcess(LifecycleComponent)} and
 * {@link #postProcess(LifecycleComponent)} hooks are called inside a transaction
 * before and after {@code process()}.
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.component.ComponentProcessingAction}
 * interface.  It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
public interface ComponentProcessingAction
extends ProcessingAction
    {

    /**
     * Prepare the component state before the main action executes
     * (called inside a transaction).
     *
     */
    public void preProcess(final LifecycleComponent component);

    /**
     * Update the component state after the main action completes
     * (called inside a transaction).
     *
     */
    public void postProcess(final LifecycleComponent component);

    }
