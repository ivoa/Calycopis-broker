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
 *     "timestamp": "2026-05-26T16:50:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 2,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.functional.processing.component;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "failcomponentrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public class FailComponentRequestEntity
extends ComponentProcessingRequestEntity
implements ComponentProcessingRequest
    {

    protected FailComponentRequestEntity()
        {
        super();
        }

    protected FailComponentRequestEntity(final LifecycleComponentEntity component)
        {
        super(component);
        }

    @Override
    public ProcessingAction preProcess(final Platform platform)
        {
        // TODO Auto-generated method stub
        return null;
        }

    @Override
    protected void postProcess(Platform platform, ComponentProcessingAction action)
        {
        // TODO Auto-generated method stub
        
        }
    }
