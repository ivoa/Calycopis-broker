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
package net.ivoa.calycopis.datamodel.message;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.spring.model.IvoaMessageItem.LevelEnum;

/**
 * Public interface for something that has messages about it.
 *   
 */
public interface MessageSubject
    {
    /**
     * Get the List of messages about this thing.
     * 
     */
    public List<MessageEntity> getMessages();

    /**
     * Add a message with a value map.
     * 
     */
    public void addMessage(final LevelEnum level, final String type, final String template, final Map<String, Object> values);

    /**
     * Add a simple message with no value map .
     * 
     */
    default void addMessage(final LevelEnum level, final String type, final String template)
        {
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
            LevelEnum.DEBUG,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add a DEBUG message.
     * 
     */
    default void addDebug(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.DEBUG,
            type,
            template,
            values
            );
        }

    /**
     * Add an INFO message.
     * 
     */
    default void addInfo(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.INFO,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add an INFO message.
     * 
     */
    default void addInfo(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.INFO,
            type,
            template,
            values
            );
        }
    
    /**
     * Add a WARN message.
     * 
     */
    default void addWarning(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.WARN,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add a WARN message.
     * 
     */
    default void addWarning(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.WARN,
            type,
            template,
            values
            );
        }

    /**
     * Add an ERROR message.
     * 
     */
    default void addError(final String type, final String template)
        {
        this.addMessage(
            LevelEnum.ERROR,
            type,
            template,
            Collections.emptyMap()
            );
        }

    /**
     * Add an ERROR message.
     * 
     */
    default void addError(final String type, final String template, final Map<String, Object> values)
        {
        this.addMessage(
            LevelEnum.ERROR,
            type,
            template,
            values
            );
        }
    }
