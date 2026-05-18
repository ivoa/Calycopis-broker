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
 *
 */

package net.ivoa.calycopis.broker.engine.functional.processing.component;

import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;

/**
 * 
 */
public class ComponentProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements ComponentProcessingRequestFactory
    {

    private final ComponentProcessingRequestRepository repository;
    
    /**
     * Public constructor used by our Platform.
     * 
     */
    public ComponentProcessingRequestFactoryImpl(final ComponentProcessingRequestRepository repository)
        {
        this.repository = repository;
        }

    @Override
    public ComponentProcessingRequestEntityImpl createPrepareComponentRequest(LifecycleComponentEntity component)
        {
        return repository.save(
            new PrepareComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createMonitorComponentRequest(LifecycleComponentEntity component)
        {
        return repository.save(
            new MonitorComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createReleaseComponentRequest(LifecycleComponentEntity component)
        {
        return repository.save(
            new ReleaseComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createCancelComponentRequest(LifecycleComponentEntity component)
        {
        return repository.save(
            new CancelComponentRequestEntity(
                component
                )
            );
        }

    @Override
    public ComponentProcessingRequestEntityImpl createFailComponentRequest(LifecycleComponentEntity component)
        {
        return repository.save(
            new FailComponentRequestEntity(
                component
                )
            );
        }
    }
