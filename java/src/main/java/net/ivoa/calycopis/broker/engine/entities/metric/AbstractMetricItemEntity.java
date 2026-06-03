/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
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
 *     along with this software. If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-06-03T01:33:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.metric;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.broker.engine.entities.component.ComponentEntity;

/**
 * JPA Entity for abstract metric items.
 *
 */
@Entity
@Table(name = "metric_items")
@Inheritance(
    strategy = InheritanceType.SINGLE_TABLE
    )
@DiscriminatorColumn(name = "dtype")
public abstract class AbstractMetricItemEntity
implements AbstractMetricItem
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
     * Protected constructor for JPA.
     *
     */
    protected AbstractMetricItemEntity()
        {
        }

    /**
     * Protected constructor.
     *
     */
    protected AbstractMetricItemEntity(final ComponentEntity parent, final String kind, final String type, final String description)
        {
        this.parent = parent;
        this.kind = kind;
        this.type = type;
        this.description = description;
        }

    @Column(name = "kind")
    private String kind;

    @Override
    public String getKind()
        {
        return this.kind;
        }

    @Column(name = "type")
    private String type;

    @Override
    public String getType()
        {
        return this.type;
        }

    @Column(name = "description")
    private String description;

    @Override
    public String getDescription()
        {
        return this.description;
        }
    }
