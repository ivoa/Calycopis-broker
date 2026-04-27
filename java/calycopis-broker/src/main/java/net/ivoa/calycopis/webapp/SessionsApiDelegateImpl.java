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
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.webapp;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetFactory;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityFactory;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntityUpdateHandler;
import net.ivoa.calycopis.spring.api.SessionsApiDelegate;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutionSession;
import net.ivoa.calycopis.spring.model.IvoaAbstractUpdate;
import net.ivoa.calycopis.spring.model.IvoaExecutionRequest;

@Slf4j
@Service
public class SessionsApiDelegateImpl
    extends BaseDelegateImpl
    implements SessionsApiDelegate
    {

    private final OfferSetFactory offersetFactory ;
    private final SimpleExecutionSessionEntityFactory sessionFactory ;
    private final SimpleExecutionSessionEntityUpdateHandler updateHandler ;

    @Autowired
    public SessionsApiDelegateImpl(
        final NativeWebRequest request,
        final OfferSetFactory offersetFactory,
        final SimpleExecutionSessionEntityFactory sessionFactory,
        final SimpleExecutionSessionEntityUpdateHandler updateHandler
        ){
        super(request);
        this.offersetFactory = offersetFactory ;
        this.sessionFactory = sessionFactory ;
        this.updateHandler = updateHandler ;
        }

    @Override
    public ResponseEntity<IvoaAbstractExecutionSession> executionSessionGet(
        final UUID uuid
        ){
        final Optional<SimpleExecutionSessionEntity> found = sessionFactory.select(
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
    public ResponseEntity<IvoaAbstractExecutionSession> executionUpdatePost(
        final UUID uuid,
        final IvoaAbstractUpdate request
        ){
       final Optional<SimpleExecutionSessionEntity> found = updateHandler.update(
            uuid,
            request
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
    public ResponseEntity<IvoaAbstractExecutionSession> directExecutionPost(
        IvoaExecutionRequest request
        ){
        log.debug("directExecutionPost(IvoaExecutionRequest)");
        //
        // Process the request to create a new execution session.
        SimpleExecutionSessionEntity entity = offersetFactory.direct(request);
        log.debug("Session entity [{}][{}][{}]", entity.getUuid(), entity.getPhase(), entity.getClass().getSimpleName());

        IvoaAbstractExecutionSession bean = entity.makeBean(
            this.getURIBuilder()
            );

        log.debug("Session bean [{}][{}]", bean.getMeta().getUuid(), bean.getMeta().getUrl());
        //
        // If we got a real session, then return a redirect to the session details.
        if (entity.getUuid() != null)
            {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(
                bean.getMeta().getUrl()
                );
            return new ResponseEntity<IvoaAbstractExecutionSession>(
                bean,
                headers,
                HttpStatus.SEE_OTHER
                );
            }
        //
        // If the processing failed, return an error response with the details of the failure.
        else {
            return new ResponseEntity<IvoaAbstractExecutionSession>(
                bean,
                HttpStatus.BAD_REQUEST
                );
            }
        }
    }

