/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.LifecycleComponentEntity;
import net.ivoa.calycopis.broker.engine.functional.platfom.Platform;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingAction;
import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestEntityImpl;
import net.ivoa.calycopis.schema.spring.model.IvoaLifecyclePhase;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "componentprocessingrequests"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class ComponentProcessingRequestEntityImpl
extends ProcessingRequestEntityImpl
implements ComponentProcessingRequest
    {

    public static class ComponentNotFoundException
    extends RuntimeException
        {
        private static final long serialVersionUID = -4591107759829303303L;
        protected URI componentKind;
        protected UUID componentUuid;

        public ComponentNotFoundException(final ComponentProcessingRequestEntityImpl request)
            {
            super(message(request));
            this.componentKind = request.componentKind;
            this.componentUuid = request.componentUuid;
            }

        public static String message(final ComponentProcessingRequestEntityImpl request)
            {
            return String.format(
                "Unable to find component [%s][%s] for processing request [%s]",
                request.componentUuid,
                request.componentKind,
                request.getUuid().toString()
                );
            }
        }

    protected ComponentProcessingRequestEntityImpl()
        {
        super();
        }

    protected ComponentProcessingRequestEntityImpl(final LifecycleComponentEntity component)
        {
        this(
            ComponentProcessingRequest.KIND,
            component
            );
        }

    protected ComponentProcessingRequestEntityImpl(final URI kind, final LifecycleComponentEntity component)
        {
        super(kind);
        this.componentKind = component.getKind();
        this.componentUuid = component.getUuid();
        }

    protected URI componentKind;
    protected UUID componentUuid;
    protected IvoaLifecyclePhase prevPhase ;
    protected IvoaLifecyclePhase nextPhase ;
    
    protected LifecycleComponentEntity getComponent(final Platform platform)
        {
        LifecycleComponentEntity component = platform.select(
            this.componentKind,
            this.componentUuid
            );
        if (component == null)
            {
            log.error(
                "Unable to find component for pre-processing [{}][{}]",
                this.componentUuid,
                this.componentKind
                );
            throw new ComponentNotFoundException(this);
            }
        return component;
        }

    @Override
    public void postProcess(final Platform platform, final ProcessingAction action)
        {
        if (action instanceof ComponentProcessingAction)
            {
            this.postProcess(
                platform,
                (ComponentProcessingAction) action
                );
            }
        else {
            this.postProcess(
                platform,
                (ComponentProcessingAction) null
                );
            }
        }

    protected abstract void postProcess(final Platform platform, final ComponentProcessingAction action);

    @Deprecated
    protected void fail(final Platform platform)
        {
        this.fail(
            platform,
            this.getComponent(
                platform
                )
            );
        }

    protected void fail(final Platform platform, final LifecycleComponentEntity component)
        {
        log.debug(
            "ProcessingRequest [{}][{}] failed",
            this.getUuid(),
            this.getClass().getSimpleName()
            );
        if (component != null)
            {
            component.setPhase(
                IvoaLifecyclePhase.FAILED
                );
            this.updateSession(
                platform,
                component
                );
            }
        this.done(platform);
        }
    
    protected void updateSession(final Platform platform, final LifecycleComponentEntity component)
        {
        platform.getProcessingRequestFactory().getSessionProcessingRequestFactory().createUpdateSessionRequest(
            component.getSession()
            );
        }
    }
