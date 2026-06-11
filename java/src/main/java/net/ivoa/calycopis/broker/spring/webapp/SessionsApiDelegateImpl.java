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
 *     "timestamp": "2026-03-24T15:00:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-05-21T10:54:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
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
 *     },
 *     {
 *     "timestamp": "2026-05-30T06:47:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 8,
 *       "units": "%"
 *       }
 *     },
 *     {
 *     "timestamp": "2026-05-30T07:55:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 15,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.spring.webapp;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.identity.Identity;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.spring.security.IdentityResolver;
import net.ivoa.calycopis.schema.spring.api.SessionsApiDelegate;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractExecutionSession;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.schema.spring.model.IvoaExecutionRequest;

@Slf4j
@Service
public class SessionsApiDelegateImpl
extends BaseDelegateImpl
implements SessionsApiDelegate
    {

    private Platform platform ;

    @Autowired
    public SessionsApiDelegateImpl(
        final NativeWebRequest request,
        final Platform platform,
        final IdentityResolver identityResolver
        ){
        super(request, identityResolver);
        this.platform = platform ;
        this.platform.initialize();
        }

    @Override
    public ResponseEntity<IvoaAbstractExecutionSession> sessionSelect(
        final UUID uuid
        ){
        final Optional<SimpleExecutionSessionEntity> found = platform.getExecutionSessionEntityFactory().select(
            uuid
            );
        if (found.isPresent())
            {
            return new ResponseEntity<IvoaAbstractExecutionSession>(
                found.get().makeBean(
                    this.getURIBuilder()
                    ),
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<IvoaAbstractExecutionSession>(
                HttpStatus.NOT_FOUND
                );
            }
        }

    @Override
    public ResponseEntity<IvoaAbstractExecutionSession> sessionUpdate(
        final UUID uuid,
        final IvoaAbstractUpdate request
        ){
        //
        // Look up the session to check ownership before allowing the update.
        final Optional<SimpleExecutionSessionEntity> existing = platform.getExecutionSessionEntityFactory().select(uuid);
        if (existing.isEmpty())
            {
            return new ResponseEntity<IvoaAbstractExecutionSession>(
                HttpStatus.NOT_FOUND
                );
            }
        //
        // Check that the caller owns this session.
        IdentityEntity caller = this.getIdentity();
        Identity sessionOwner = existing.get().getOwner();
        if (sessionOwner != null && caller != null)
            {
            if (!sessionOwner.getUuid().equals(caller.getUuid()))
                {
                log.warn("Authorization denied: caller [{}] does not own session [{}]", caller.getUuid(), uuid);
                return new ResponseEntity<IvoaAbstractExecutionSession>(
                    HttpStatus.FORBIDDEN
                    );
                }
            }
        //
        // Apply the update.
        final Optional<SimpleExecutionSessionEntity> updated = platform.getExecutionSessionEntityUpdater().update(
            uuid,
            request
            );
        if (updated.isPresent())
            {
            return new ResponseEntity<IvoaAbstractExecutionSession>(
                updated.get().makeBean(
                    this.getURIBuilder()
                    ),
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<IvoaAbstractExecutionSession>(
                HttpStatus.NOT_FOUND
                );
            }
        }
    }
