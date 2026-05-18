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

package net.ivoa.calycopis.broker.engine.functional.processing.session;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;

/**
 * 
 */
@Slf4j
@Component
public class SessionProcessingRequestFactoryImpl
extends FactoryBaseImpl
implements SessionProcessingRequestFactory
    {

    private final SessionProcessingRequestRepository repository;

    /**
     * Public constructor used by our Platform.
     * 
     */
    public SessionProcessingRequestFactoryImpl(final SessionProcessingRequestRepository repository)
        {
        super();
        this.repository = repository;
        }

    @Override
    public PrepareSessionRequestEntityImpl createPrepareSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating PrepareSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new PrepareSessionRequestEntityImpl(
                session
                )
            );
        }

    @Override
    public UpdateSessionRequestEntityImpl createUpdateSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating MonitorSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new UpdateSessionRequestEntityImpl(
                session
                )
            );
        }

    @Override
    public ReleaseSessionRequestEntityImpl createReleaseSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating ReleaseSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new ReleaseSessionRequestEntityImpl(
                session
                )
            );
        }
    
    @Override
    public CancelSessionRequestEntityImpl createCancelSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating CancelSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new CancelSessionRequestEntityImpl(
                session
                )
            );
        }

    @Override
    public FailSessionRequestEntityImpl createFailSessionRequest(final SimpleExecutionSessionEntity session)
        {
        log.debug("Creating FailSessionRequest for session [{}]", session.getUuid());
        return repository.save(
            new FailSessionRequestEntityImpl(
                session
                )
            );
        }
    }
