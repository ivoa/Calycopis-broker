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
 *     "timestamp": "2026-05-21T10:54:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     },
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

package net.ivoa.calycopis.broker.spring.jpa;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestEntity;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestRepositoryBase;

/**
 * 
 */
public class SpringProcessingRequestEntityRepositoryWrapper
extends FactoryBaseImpl
implements ProcessingRequestRepositoryBase
    {

    private final SpringProcessingRequestEntityRepository inner;

    /**
     * 
     */
    public SpringProcessingRequestEntityRepositoryWrapper(final SpringProcessingRequestEntityRepository inner)
        {
        this.inner = inner;
        }

    @Override
    public Optional<ProcessingRequestEntity> findById(UUID uuid)
        {
        return inner.findById(uuid);
        }

    @Override
    public <ActualType extends ProcessingRequestEntity> ActualType save(ActualType entity)
        {
        return inner.save(entity);
        }

    @Override
    public void delete(ProcessingRequestEntity entity)
        {
        inner.delete(entity);
        }
    }
