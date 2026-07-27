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

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonTypeName;

import net.ivoa.calycopis.openapi.spring.model.IvoaSimpleMinMaxFloatMetric;

/**
 * Adapter that wraps a SimpleMinMaxFloatMetricEntity as an IvoaSimpleMinMaxFloatMetric
 * for serialization in API responses.
 *
 */
@JsonTypeName("https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/metrics/simple-minmax-float-metric.yaml")
public class MetricItemBean
extends IvoaSimpleMinMaxFloatMetric
    {
    private final SimpleMinMaxFloatMetricEntity entity;

    public MetricItemBean(final SimpleMinMaxFloatMetricEntity entity)
        {
        this.entity = entity;
        }

    @Override
    public URI getKind()
        {
        return URI.create(entity.getKind());
        }

    @Override
    public URI getType()
        {
        if (entity.getType() != null)
            {
            return URI.create(entity.getType());
            }
        else {
            return null;
            }
        }

    @Override
    public String getDescription()
        {
        return entity.getDescription();
        }

    @Override
    public Float getMin()
        {
        return entity.getMin();
        }

    @Override
    public Float getMax()
        {
        return entity.getMax();
        }
    }
