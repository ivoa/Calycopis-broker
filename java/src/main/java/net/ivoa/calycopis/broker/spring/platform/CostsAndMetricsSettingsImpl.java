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
 *     "timestamp": "2026-06-03T03:47:00",
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

package net.ivoa.calycopis.broker.spring.platform;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.broker.engine.functional.platform.CostsAndMetricsSettings;

/**
 * Spring-specific implementation of CostsAndMetricsSettings, bound from
 * the calycopis.broker.costs-and-metrics section of the application configuration.
 *
 */
@Component
@ConfigurationProperties(prefix = "calycopis.broker.costs-and-metrics")
public class CostsAndMetricsSettingsImpl
implements CostsAndMetricsSettings
    {

    public static class ItemImpl
    implements Item
        {
        private String type;
        private String description;
        private Float min;
        private Float max;

        @Override
        public String getType()
            {
            return this.type;
            }

        public void setType(String type)
            {
            this.type = type;
            }

        @Override
        public String getDescription()
            {
            return this.description;
            }

        public void setDescription(String description)
            {
            this.description = description;
            }

        @Override
        public Float getMin()
            {
            return this.min;
            }

        public void setMin(Float min)
            {
            this.min = min;
            }

        @Override
        public Float getMax()
            {
            return this.max;
            }

        public void setMax(Float max)
            {
            this.max = max;
            }
        }

    public static class ComponentConfigImpl
    implements ComponentConfig
        {
        private List<ItemImpl> costs = new ArrayList<>();
        private List<ItemImpl> metrics = new ArrayList<>();

        @Override
        public List<ItemImpl> getCosts()
            {
            return this.costs;
            }

        public void setCosts(List<ItemImpl> costs)
            {
            this.costs = costs;
            }

        @Override
        public List<ItemImpl> getMetrics()
            {
            return this.metrics;
            }

        public void setMetrics(List<ItemImpl> metrics)
            {
            this.metrics = metrics;
            }
        }

    private ComponentConfigImpl session = new ComponentConfigImpl();
    private ComponentConfigImpl compute = new ComponentConfigImpl();

    @Override
    public ComponentConfigImpl getSession()
        {
        return this.session;
        }

    public void setSession(ComponentConfigImpl session)
        {
        this.session = session;
        }

    @Override
    public ComponentConfigImpl getCompute()
        {
        return this.compute;
        }

    public void setCompute(ComponentConfigImpl compute)
        {
        this.compute = compute;
        }
    }
