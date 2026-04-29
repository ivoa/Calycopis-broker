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

package net.ivoa.calycopis.engine.processing;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.engine.platform.Platform;

/**
 * Framework-independent abstract JPA entity for processing requests.
 *
 * Subclasses in the broker add Spring-specific concerns (repositories,
 * transaction management) and references to broker entity types
 * (sessions, components).
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.processing.ProcessingRequestEntity} class.
 * It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
@Slf4j
@Entity
@Table(
    name = "processingrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class ProcessingRequestEntity
implements ProcessingRequest
    {

    protected ProcessingRequestEntity()
        {
        super();
        }

    protected ProcessingRequestEntity(final URI kind)
        {
        super();
        this.kind = kind;
        this.activation = Instant.now();
        }

    private URI kind;

    @Override
    public URI getKind()
        {
        return this.kind;
        }

    @Id
    @GeneratedValue
    protected UUID uuid;

    @Override
    public UUID getUuid()
        {
        return this.uuid;
        }

    private UUID service;

    @Override
    public UUID getService()
        {
        return this.service;
        }

    public void setService(final UUID service)
        {
        this.service = service;
        }

    private Instant created;

    @Override
    public Instant getCreated()
        {
        return this.created;
        }

    private Instant modified;

    @Override
    public Instant getModified()
        {
        return this.modified;
        }

    private Instant activation;

    @Override
    public Instant getActivationTime()
        {
        return this.activation;
        }

    public void activate()
        {
        this.activate(0);
        }

    public void activate(int delay)
        {
        this.activate(Duration.ofSeconds(delay));
        }

    public void activate(final Duration delay)
        {
        this.activation = Instant.now().plus(delay);
        }

    /**
     * Mark this request as done, removing it from the processing queue.
     * Called by concrete subclasses after all post-processing is complete.
     *
     */
    protected void done(final Platform platform)
        {
        log.debug(
            "ProcessingRequest [{}][{}] done",
            this.getUuid(),
            this.getClass().getSimpleName()
            );
        platform.getProcessingRequestFactory().delete(
            this
            );
        }

    }
