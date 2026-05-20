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
 *     "timestamp": "2026-05-20T14:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.functional.processing.session;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
public class SessionProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements SessionProcessingRequestFactory
    {

    private final AbstractEntityRepository<SessionProcessingRequestEntity> repository;

    /**
     * Public constructor used by our Platform.
     * 
     */
    public SessionProcessingRequestFactoryImpl(final AbstractEntityRepository<SessionProcessingRequestEntity> repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public PrepareSessionRequestEntity createPrepareSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating PrepareSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new PrepareSessionRequestEntity(
                session
                )
            );
        }

    @Override
    public UpdateSessionRequestEntity createUpdateSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating MonitorSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new UpdateSessionRequestEntity(
                session
                )
            );
        }

    @Override
    public ReleaseSessionRequestEntity createReleaseSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating ReleaseSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new ReleaseSessionRequestEntity(
                session
                )
            );
        }
    
    @Override
    public CancelSessionRequestEntity createCancelSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating CancelSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new CancelSessionRequestEntity(
                session
                )
            );
        }

    @Override
    public FailSessionRequestEntity createFailSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating FailSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new FailSessionRequestEntity(
                session
                )
            );
        }
    }
