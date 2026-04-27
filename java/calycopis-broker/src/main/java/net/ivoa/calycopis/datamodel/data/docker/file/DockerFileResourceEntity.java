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
 *     "timestamp": "2026-04-14T17:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 30,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.data.docker.file;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponent;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator.Result;
import net.ivoa.calycopis.datamodel.data.simple.SimpleDataResourceEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.platfom.Platform;
import net.ivoa.calycopis.functional.processing.ProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingAction;
import net.ivoa.calycopis.functional.processing.component.ComponentProcessingRequest;
import net.ivoa.calycopis.engine.lifecycle.LifecyclePhase;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "dockerfiledataresources"
    )
public class DockerFileResourceEntity
extends SimpleDataResourceEntity
implements DockerFileResource
    {

    /**
     * 
     */
    public DockerFileResourceEntity()
        {
        super();
        }

    /**
     * 
     */
    public DockerFileResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final Result result
        ){
        super(
            session,
            storage,
            result
            );
        }

    /**
     * 
     */
    public DockerFileResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final Result result,
        final IvoaSimpleDataResource validated
        ){
        super(
            session,
            storage,
            result,
            validated
            );
        }

    @Override
    public ProcessingAction getPrepareAction(Platform platform, ComponentProcessingRequest request)
        {
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                log.debug(
                    "Post-processing component [{}][{}] next phase [AVAILABLE]",
                    component.getUuid(),
                    component.getClass().getSimpleName()
                    );
                component.setPhase(LifecyclePhase.AVAILABLE);
                }
            };
        }

    @Override
    public ProcessingAction getMonitorAction(Platform platform, ComponentProcessingRequest request)
        {
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(LifecyclePhase.COMPLETED);
                }
            };
        }

    @Override
    public ProcessingAction getReleaseAction(Platform platform, ComponentProcessingRequest request)
        {
        return new ComponentProcessingAction()
            {
            @Override
            public void preProcess(final LifecycleComponent component) {}

            @Override
            public void process() {}

            @Override
            public void postProcess(final LifecycleComponent component)
                {
                component.setPhase(LifecyclePhase.COMPLETED);
                }
            };
        }

    }
