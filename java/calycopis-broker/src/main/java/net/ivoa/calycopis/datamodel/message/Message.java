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

import java.time.Instant;
import java.util.Map;

import net.ivoa.calycopis.spring.model.IvoaMessageItem.LevelEnum;

/**
 * Public interface for messages.
 * 
 */
public interface Message
    {
    /**
     * The message type identifier.
     * Typically a URL pointing to a human readable description of the message. 
     * @return type
     */

    public String getType();

    /**
     * The date and time of the message. 
     * @return time
     */
    public Instant getTime();

   /**
     * The message level. 
     * @return level
     */
    public LevelEnum getLevel();

   /**
     * The message template. 
     * @return template
     */
    public String getTemplate();

   /**
     * A map of name->value properties to apply to the template.
     * @return values
     */
    public Map<String, String> getValues();

   /**
     * The resulting message. 
     * @return message
     */
    public String getMessage();
    

    }
