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
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 3,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.session.simple;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.session.AbstractExecutionSession;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.engine.session.ExecutionSessionPhase;

/**
 * Public interface for an execution session.
 *
 */
public interface SimpleExecutionSession
    extends AbstractExecutionSession
    {
    
    /**
     * The type identifier for a simple execution session.
     *
     */
    public static final URI TYPE_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/EB/schema/v1.0/types/session/simple-execution-session-1.0");

    /**
     * Get the Execution phase.
     *
     */
    public ExecutionSessionPhase getPhase();

    /**
     * Set the Execution phase.
     *
     */
    void setPhase(final ExecutionSessionPhase phase);

    /**
     * Get the expiry date for an OFFERED Execution.
     *
     */
    public Instant getExpires();
    
    /**
     * Get the Executable entity.
     *
     */
    public AbstractExecutableEntity getExecutable();

    /**
     * Get the ComputeResource.
     *
     */
    public AbstractComputeResourceEntity getComputeResource();

    /**
     * Get a list of the DataResources.
     *
     */
    public List<AbstractDataResourceEntity> getDataResources();

    /**
     * Get a list of the StorageResources.
     *
     */
    public List<AbstractStorageResourceEntity> getStorageResources();

    /**
     * Get a list of the connectors.
     *
     */
    public List<SimpleSessionConnectorEntity> getConnectors();

    /**
     * Add a new connector.
     *
     */
    public void addConnector(final SimpleSessionConnectorEntity connector);

    /**
     * Add a new connector.
     *
     */
    public void addConnector(final String type, final String protocol, String location);

    /**
    *
    */
   public Instant getPrepareStartInstant();

   /**
    *
    */
   public long getPrepareStartInstantSeconds();

   /**
    *
    */
   public Duration getPrepareDuration();

   /**
    *
    */
   public long getPrepareDurationSeconds();

   /**
    *
    */
   public Interval getAvailableStartInterval();

   /**
    *
    */
   public Instant getAvailableStartInstant();

   /**
    *
    */
   public long getAvailableStartInstantSeconds();

   /**
    *
    */
   public Duration getAvailableStartDuration();

   /**
    *
    */
   public long getAvailableStartDurationSeconds();

   /**
    *
    */
   public Duration getAvailableDuration();

   /**
    *
    */
   public long getAvailableDurationSeconds();

   /**
    *
    */
   public Instant getReleaseStartInstant();

   /**
    *
    */
   public long getReleaseStartInstantSeconds();

   /**
    *
    */
   public Duration getReleaseDuration();

   /**
    *
    */
   public long getReleaseDurationSeconds();
    
    }

