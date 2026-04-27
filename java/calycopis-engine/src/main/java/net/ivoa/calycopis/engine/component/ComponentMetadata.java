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

/**
 * Framework-independent replacement for IvoaComponentMetadata.
 *
 * Carries the fields from the OpenAPI-generated IvoaComponentMetadata that are
 * relevant to entity construction and internal business logic.  Unlike
 * IvoaComponentMetadata, this POJO has no dependency on the calycopis-spring
 * schema package.
 *
 * Timestamps and message lists are maintained on the entity itself (via JPA
 * columns) and are therefore not included here.
 */
public class ComponentMetadata
    {
    private String name;
    private String description;

    /**
     * No-arg constructor.
     *
     */
    public ComponentMetadata()
        {
        }

    /**
     * Convenience constructor.
     *
     */
    public ComponentMetadata(final String name)
        {
        this.name = name;
        }

    /**
     * Convenience constructor.
     *
     */
    public ComponentMetadata(final String name, final String description)
        {
        this.name = name;
        this.description = description;
        }

    /**
     * Get the component name.
     *
     */
    public String getName()
        {
        return name;
        }

    /**
     * Set the component name.
     *
     */
    public ComponentMetadata name(final String name)
        {
        this.name = name;
        return this;
        }

    /**
     * Get the component description.
     *
     */
    public String getDescription()
        {
        return description;
        }

    /**
     * Set the component description.
     *
     */
    public ComponentMetadata description(final String description)
        {
        this.description = description;
        return this;
        }
    }
