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
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import net.ivoa.calycopis.broker.engine.entities.component.ComponentEntity;

/**
 * JPA Entity for a simple min/max float metric.
 *
 */
@Entity
@DiscriminatorValue("SimpleMinMaxFloatMetric")
public class SimpleMinMaxFloatMetricEntity
extends AbstractMetricItemEntity
implements SimpleMinMaxFloatMetric
    {

    /**
     * Protected constructor for JPA.
     *
     */
    protected SimpleMinMaxFloatMetricEntity()
        {
        super();
        }

    /**
     * Public constructor.
     *
     */
    public SimpleMinMaxFloatMetricEntity(final ComponentEntity parent, final String type, final String description, final Float min, final Float max)
        {
        super(
            parent,
            TYPE_DISCRIMINATOR,
            type,
            description
            );
        this.min = min;
        this.max = max;
        }

    @Column(name = "min_value")
    private Float min;

    @Override
    public Float getMin()
        {
        return this.min;
        }

    @Column(name = "max_value")
    private Float max;

    @Override
    public Float getMax()
        {
        return this.max;
        }
    }
