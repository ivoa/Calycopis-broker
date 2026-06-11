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
 *     "timestamp": "2026-05-30T05:50:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 8,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-06-03T01:33:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 15,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.component;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.cost.AbstractCostItemEntity;
import net.ivoa.calycopis.broker.engine.entities.cost.CostItemBean;
import net.ivoa.calycopis.broker.engine.entities.cost.SimpleMinMaxFloatCostEntity;
import net.ivoa.calycopis.broker.engine.entities.identity.Identity;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntity;
import net.ivoa.calycopis.broker.engine.entities.message.Message;
import net.ivoa.calycopis.broker.engine.entities.message.MessageEntity;
import net.ivoa.calycopis.broker.engine.entities.message.MessageItemBean;
import net.ivoa.calycopis.broker.engine.entities.metric.AbstractMetricItemEntity;
import net.ivoa.calycopis.broker.engine.entities.metric.MetricItemBean;
import net.ivoa.calycopis.broker.engine.entities.metric.SimpleMinMaxFloatMetricEntity;
import net.ivoa.calycopis.broker.engine.util.ListWrapper;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractCostItem;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractMetricItem;
import net.ivoa.calycopis.schema.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.schema.spring.model.IvoaMessageItem;
import net.ivoa.calycopis.schema.spring.model.IvoaMessageItem.LevelEnum;

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
     * Protected constructor for JPA entities.
     *
     */
    protected ComponentEntity()
        {
        super();
        }

    /**
     * Protected constructor used by our Factories.
     *
     */
    protected ComponentEntity(final String name, final IdentityEntity owner)
        {
        this(
            name,
            null,
            Instant.now(),
            owner
            );
        }

    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final IvoaComponentMetadata meta, final IdentityEntity owner)
        {
        this(
            meta.getName(),
            meta.getDescription(),
            Instant.now(),
            owner
            );
        }
    
    /**
     * Protected constructor.
     *
     */
    protected ComponentEntity(final String name, final String description, final Instant created, final IdentityEntity owner)
        {
        this.name = name;
        this.created = created;
        this.description = description;
        this.owner = owner;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_uuid")
    private IdentityEntity owner;

    @Override
    public IdentityEntity getOwner()
        {
        return this.owner;
        }

    protected void setOwner(final IdentityEntity owner)
        {
        this.owner = owner;
        }

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<MessageEntity> messages = new ArrayList<MessageEntity>();

    public Iterable<MessageEntity> getMessageEntities()
        {
        return this.messages;
        }
    
    @Override
    public Iterable<Message> getMessages()
        {
        return new ListWrapper<Message, MessageEntity>(
            this.messages
            ){
            public Message wrap(final MessageEntity inner)
                {
                return inner;
                }
            };
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
    
    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<AbstractCostItemEntity> costs = new ArrayList<AbstractCostItemEntity>();

    public Iterable<AbstractCostItemEntity> getCostEntities()
        {
        return this.costs;
        }

    public void addCost(final SimpleMinMaxFloatCostEntity cost)
        {
        this.costs.add(cost);
        }

    /**
     * Wrap a List of JPA AbstractCostItemEntity(s) as a List of IvoaAbstractCostItems.
     *
     */
    public List<IvoaAbstractCostItem> getCostBeans()
        {
        return new ListWrapper<IvoaAbstractCostItem, AbstractCostItemEntity>(
            this.costs
            ){
            public IvoaAbstractCostItem wrap(final AbstractCostItemEntity inner)
                {
                if (inner instanceof SimpleMinMaxFloatCostEntity)
                    {
                    return new CostItemBean(
                        (SimpleMinMaxFloatCostEntity) inner
                        );
                    }
                else {
                    return null;
                    }
                }
            };
        }

    @OneToMany(
        mappedBy = "parent",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
        )
    protected List<AbstractMetricItemEntity> metrics = new ArrayList<AbstractMetricItemEntity>();

    public Iterable<AbstractMetricItemEntity> getMetricEntities()
        {
        return this.metrics;
        }

    public void addMetric(final SimpleMinMaxFloatMetricEntity metric)
        {
        this.metrics.add(metric);
        }

    /**
     * Wrap a List of JPA AbstractMetricItemEntity(s) as a List of IvoaAbstractMetricItems.
     *
     */
    public List<IvoaAbstractMetricItem> getMetricBeans()
        {
        return new ListWrapper<IvoaAbstractMetricItem, AbstractMetricItemEntity>(
            this.metrics
            ){
            public IvoaAbstractMetricItem wrap(final AbstractMetricItemEntity inner)
                {
                if (inner instanceof SimpleMinMaxFloatMetricEntity)
                    {
                    return new MetricItemBean(
                        (SimpleMinMaxFloatMetricEntity) inner
                        );
                    }
                else {
                    return null;
                    }
                }
            };
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
            this.messages
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
