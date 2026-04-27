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

import net.ivoa.calycopis.spring.model.IvoaMessageItem;

/**
 * 
 */
public class MessageItemBean extends IvoaMessageItem
    {
    private final MessageEntity entity;

    public MessageItemBean(final MessageEntity entity)
        {
        this.entity = entity;
        }

    @Override
    public String getKind()
        {
        return entity.getType();
        }

    @Override
    public Instant getTime()
        {
        return entity.getTime();
        }

    @Override
    public LevelEnum getLevel()
        {
        return entity.getLevel();
        }

    @Override
    public String getTemplate()
        {
        return entity.getTemplate();
        }

    @Override
    public Map<String, String> getValues()
        {
        return entity.getValues();
        }

    @Override
    public String getMessage()
        {
        return entity.getMessage();
        }
    }
