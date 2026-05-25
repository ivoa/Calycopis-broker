/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
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
 *     along with this software. If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: []
 *
 */
package net.ivoa.calycopis.broker.engine.functional.booking;

import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

/**
 * Public interface for a resource offer.
 * 
 */
public interface AbstractResourceOffer
    {
    /**
     * The offer name.
     * 
     */
    @Deprecated
    public String getName();

    /**
     * Get the start time as an Interval.
     * 
     */
    public Interval getStartInterval();

    /**
     * Get the start time as an Instant.
     * 
     */
    public Instant getStartInstant();

    /**
     * Get the offer Duration.
     * 
     */
    public Duration getDuration();

    }
