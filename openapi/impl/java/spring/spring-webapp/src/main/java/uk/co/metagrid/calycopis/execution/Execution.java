/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package uk.co.metagrid.calycopis.execution;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.openapi.model.IvoaExecutionSessionStatus;
import uk.co.metagrid.calycopis.component.Component;
import uk.co.metagrid.calycopis.compute.simple.SimpleComputeResourceEntity;
import uk.co.metagrid.calycopis.executable.AbstractExecutableEntity;
import uk.co.metagrid.calycopis.offerset.OfferSetEntity;

/**
 * Public interface for an Execution (session).
 *
 */
public interface Execution
    extends Component
    {
    /**
     * The database table name for Executions.
     * 
     */
    public static final String TABLE_NAME = "executions" ;
    
    /**
     * The type discriminator for Executions.
     * 
     */
    public static final String TYPE_DISCRIMINATOR = "urn:generic-execution" ;

    /**
     * The URL path for Executions.
     *
     */
    public static final String REQUEST_PATH = "/executions/" ;

    /**
     * Get the Execution state.
     *
     */
    public IvoaExecutionSessionStatus getState();

    /**
     * Get the expiry date for an OFFERED Execution.
     *
     */
    public OffsetDateTime getExpires();

    /**
     * Get the parent OfferSet.
     *
     */
    public OfferSetEntity getParent();

    /**
     * Get the start of the start interval in seconds.
     *
     */
    public long getStartInstantSeconds();
    
    /**
     * Get the start of the start interval as an Instant.
     *
     */
    public Instant getStartInstant();

    /**
     * Get the range for the start interval in seconds.
     *
     */
    public long getStartDurationSeconds();

    /**
     * Get the range for the start interval as a Duration.
     *
     */
    public Duration getStartDuration();

    /**
     * Get the start interval (instant + range) as an Interval.
     *
     */
    public Interval getStartInterval();

    /**
     * Get the Execution duration in seconds.
     *
     */
    public long getExeDurationSeconds();

    /**
     * Get the Execution duration as a Duration.
     *
     */
    public Duration getExeDuration();

    /**
     * Get the Executable entity.
     *
     */
    public AbstractExecutableEntity getExecutable();

    /**
     * Get a list of the ComputeResources.
     *
     */
    public List<SimpleComputeResourceEntity> getComputeResources();


    }

