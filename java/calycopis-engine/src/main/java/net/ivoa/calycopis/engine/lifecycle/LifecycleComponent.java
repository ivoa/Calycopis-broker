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
 *     "timestamp": "2026-04-27T13:46:34",
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

package net.ivoa.calycopis.engine.lifecycle;

import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.engine.component.Component;

/**
 * Framework-independent interface for a component that participates in the
 * lifecycle state machine (preparing → available → releasing → completed).
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.datamodel.component.LifecycleComponent} interface.
 * It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 *
 * The {@code getPrepareAction}, {@code getMonitorAction} etc. methods are
 * omitted here because they return broker-specific types
 * ({@code ProcessingAction}, {@code Platform}).  Those methods are defined in
 * the broker interface which extends this one.
 */
public interface LifecycleComponent
    extends Component
    {
    /**
     * Get the current lifecycle phase.
     *
     */
    public LifecyclePhase getPhase();

    /**
     * Set the current lifecycle phase.
     *
     */
    public void setPhase(final LifecyclePhase phase);

    /**
     * Get the instant at which preparation should start.
     *
     */
    public Instant getPrepareStartInstant();

    /**
     * Get the preparation start instant as seconds since the Unix epoch.
     *
     */
    public long getPrepareStartInstantSeconds();

    /**
     * Get the preparation duration.
     *
     */
    public Duration getPrepareDuration();

    /**
     * Get the preparation duration in seconds.
     *
     */
    public long getPrepareDurationSeconds();

    /**
     * Get the interval from the start of preparation to when availability begins.
     *
     */
    public Interval getAvailableStartInterval();

    /**
     * Get the instant at which availability starts.
     *
     */
    public Instant getAvailableStartInstant();

    /**
     * Get the availability start instant as seconds since the Unix epoch.
     *
     */
    public long getAvailableStartInstantSeconds();

    /**
     * Get the availability start as a duration offset from the prepare start.
     *
     */
    public Duration getAvailableStartDuration();

    /**
     * Get the availability start offset in seconds.
     *
     */
    public long getAvailableStartDurationSeconds();

    /**
     * Get the duration for which the component is available.
     *
     */
    public Duration getAvailableDuration();

    /**
     * Get the availability duration in seconds.
     *
     */
    public long getAvailableDurationSeconds();

    /**
     * Get the instant at which release should start.
     *
     */
    public Instant getReleaseStartInstant();

    /**
     * Get the release start instant as seconds since the Unix epoch.
     *
     */
    public long getReleaseStartInstantSeconds();

    /**
     * Get the release duration.
     *
     */
    public Duration getReleaseDuration();

    /**
     * Get the release duration in seconds.
     *
     */
    public long getReleaseDurationSeconds();
    }
