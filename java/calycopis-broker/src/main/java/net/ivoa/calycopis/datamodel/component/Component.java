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

package net.ivoa.calycopis.datamodel.component;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.message.MessageEntity;
import net.ivoa.calycopis.datamodel.message.MessageSubject;

/**
 * Public interface for a Component
 * 
 */
public interface Component
extends MessageSubject
    {

    /**
     * Get the Component UUID.
     *
     */
    public UUID getUuid();

    /**
     * Get the Component kind (type).
     *
     */
    public URI getKind() ;

    /**
     * Get the Component name.
     *
     */
    public String getName();

    /**
     * Get the Component description.
     *
     */
    public String getDescription();

    /**
     * Get the Component created date.
     *
     */
    public Instant getCreated();

    /**
     * Get the Component modified date.
     *
     */
    public Instant getModified();
    
    /**
     * Get a list of messages for this Component.
     * TODO Split this into a separate MessageList interface.
     *
     */
    public List<MessageEntity> getMessages();

    }
