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

package net.ivoa.calycopis.engine.component;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import net.ivoa.calycopis.engine.message.MessageLevel;

/**
 * Framework-independent interface for a named, identifiable component.
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.datamodel.component.Component} interface.
 * It carries no dependency on Spring, calycopis-spring, or any other
 * framework-specific type.
 */
public interface Component
    {

    /**
     * Get the component UUID.
     *
     */
    public UUID getUuid();

    /**
     * Get the component kind (type URI).
     *
     */
    public URI getKind();

    /**
     * Get the component name.
     *
     */
    public String getName();

    /**
     * Get the component description.
     *
     */
    public String getDescription();

    /**
     * Get the creation timestamp.
     *
     */
    public Instant getCreated();

    /**
     * Get the last-modified timestamp.
     *
     */
    public Instant getModified();

    /**
     * Add a message with a value map.
     *
     */
    public void addMessage(
        final MessageLevel level,
        final String type,
        final String template,
        final Map<String, Object> values
        );

    /**
     * Add a simple message with no value map.
     *
     */
    default void addMessage(
        final MessageLevel level,
        final String type,
        final String template
        ){
        this.addMessage(
            level,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add a DEBUG message.
     *
     */
    default void addDebug(final String type, final String template)
        {
        this.addMessage(
            MessageLevel.DEBUG,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add an INFO message.
     *
     */
    default void addInfo(final String type, final String template)
        {
        this.addMessage(
            MessageLevel.INFO,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add a WARN message.
     *
     */
    default void addWarning(final String type, final String template)
        {
        this.addMessage(
            MessageLevel.WARN,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add an ERROR message.
     *
     */
    default void addError(final String type, final String template)
        {
        this.addMessage(
            MessageLevel.ERROR,
            type,
            template,
            Collections.emptyMap()
            );
        }
    }
