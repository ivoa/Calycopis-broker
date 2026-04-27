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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.message.MessageEntity;
import net.ivoa.calycopis.datamodel.message.MessageItemBean;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaMessageItem;
import net.ivoa.calycopis.spring.model.IvoaMessageItem.LevelEnum;
import net.ivoa.calycopis.util.ListWrapper;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * JPA Entity for a Component
 * https://www.javatpoint.com/hibernate-table-per-hierarchy-using-annotation-tutorial-example
 *
 */
@Slf4j
@Entity
@Table(name = "components")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class ComponentEntity
    implements Component
    {

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final String name)
        {
        this(
            name,
            null,
            Instant.now()
            );
        }

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final IvoaComponentMetadata meta)
        {
        this(
            meta.getName(),
            meta.getDescription(),
            Instant.now()
            );
        }
    
    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final String name, final String description, final Instant created)
        {
        this.name = name;
        this.created = created;
        this.description = description;
        }

    @Id
    @GeneratedValue
    protected UUID uuid;

    public UUID getUuid()
        {
        return this.uuid ;
        }

    @Column(name = "name")
    private String name;
    @Override
    public String getName()
        {
        return this.name;
        }

    @Column(name = "description")
    private String description;
    @Override
    public String getDescription()
        {
        return this.description;
        }

    @Column(name = "created")
    private Instant created;
    @Override
    public Instant getCreated()
        {
        return this.created;
        }

    @Column(name = "modified")
    private Instant modified;
    @Override
    public Instant getModified()
        {
        return this.modified;
        }
    
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<MessageEntity> messages = new ArrayList<MessageEntity>();

    @Override
    public List<MessageEntity> getMessages()
        {
        return messages ;
        }

    @Override
    public void addMessage(final LevelEnum level, final String type, final String template, final Map<String, Object> values)
        {
        MessageEntity message = new MessageEntity(
            this,
            level,
            type,
            template,
            values
            );
        messages.add(
            message
            );
        }

    /**
     * Claim a set of messages by setting the message parent and adding it to our list.
     * 
     */
    public void claimMessages(final List<MessageEntity> messages)
        {
        for (MessageEntity message : messages)
            {
            message.setParent(
                this
                );
            this.messages.add(
                message
                );
            }
        }
    
    @Override
    public boolean equals(Object object)
        {
        if (null != object)
            {
            if (this == object)
                {
                return true;
                }
            if (object.getClass().equals(this.getClass()))
                {
                if (this.uuid != null)
                    {
                    return this.uuid.equals(
                        ((ComponentEntity) object).getUuid()
                        );
                    }
                }
            }
        return false ;
        }
    
    /**
     * Wrap a List of JPA MessageEntity(s) as a List of IvoaMessageItems.
     * 
     */
    public List<IvoaMessageItem> getMessageBeans()
        {
        return new ListWrapper<IvoaMessageItem, MessageEntity>(
            this.getMessages()
            ){
            public IvoaMessageItem wrap(final MessageEntity inner)
                {
                return new MessageItemBean(
                    inner
                    );
                }
            };
        }

    protected IvoaComponentMetadata makeMeta(
        final URIBuilder builder
        ){
        return this.fillMeta(
            builder,
            new IvoaComponentMetadata()
            ) ;
        }

    protected abstract URI getWebappPath() ;
    
    protected IvoaComponentMetadata fillMeta(
        final URIBuilder builder,
        final IvoaComponentMetadata bean
        ){
        bean.setUuid(
            this.getUuid()
            );
        bean.setUrl(
            builder.buildURI(
                this.getWebappPath(),
                this.uuid
                )
            );
        bean.setName(
            this.getName()
            );
        bean.setCreated(
                this.getCreated()
                );
        bean.setModified(
            this.getModified()
            );
        bean.setMessages(
            this.getMessageBeans()
            );
        return bean ;
        }
    }
