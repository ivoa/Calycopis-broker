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
import java.util.Collections;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.spring.model.IvoaMessageItem.LevelEnum;

/**
 * JPA Entity for MessageItems.
 * 
 */
@Entity
@Table(
    name = "messages"
    )
public class MessageEntity
    implements Message
    {

    @Id
    @GeneratedValue
    private Long ident;
    
    @JoinColumn(name = "parent", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ComponentEntity parent;

    public ComponentEntity getParent()
        {
        return this.parent;
        }

    public void setParent(final ComponentEntity parent)
        {
        this.parent = parent;
        }
    
    /**
     * Protected constructor
     * 
     */
    protected MessageEntity()
        {
        }

    /**
     * Public constructor
     * 
     */
    public MessageEntity(final ComponentEntity parent, final LevelEnum level, final String type, final String template, final Map<String, Object> values)
        {
        this.type = type;
        this.level = level;
        this.parent = parent;
        this.template = template;
        this.datetime = Instant.now();
        }

    @Column(name = "type")
    private String type;

    @Override
    public String getType()
        {
        return this.type;
        }

    @Column(name = "datetime")
    private Instant datetime;

    @Override
    public Instant getTime()
        {
        return this.datetime;
        }

    @Column(name = "level")    
    private LevelEnum level;

    @Override
    public LevelEnum getLevel()
        {
        return this.level;
        }

    @Column(name = "template")    
    private String template;

    @Override
    public String getTemplate()
        {
        return this.template;
        }

    @Override
    public Map<String, String> getValues()
        {
        return Collections.emptyMap();
        }

    @Column(name = "message")    
    private String message;

    @Override
    public String getMessage()
        {
        return this.message;
        }

    public static String safeString(final Object value)
        {
        if (value == null)
            {
            return "null" ;
            }
        else {
            if (value instanceof String)
                {
                return (String) value ;
                }
            else {
                return value.toString();
                }
            }
        }
    }
