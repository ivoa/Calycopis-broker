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
 *       "value": 10,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.spring.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.identity.IdentityEntity;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetEntity;
import net.ivoa.calycopis.broker.engine.functional.platform.Platform;
import net.ivoa.calycopis.broker.spring.security.IdentityResolver;
import net.ivoa.calycopis.schema.spring.api.RequestsApiDelegate;
import net.ivoa.calycopis.schema.spring.model.IvoaExecutionRequest;
import net.ivoa.calycopis.schema.spring.model.IvoaOfferSetResponse;

@Slf4j
@Service
public class RequestsApiDelegateImpl
extends BaseDelegateImpl
implements RequestsApiDelegate
    {

    private Platform platform ;
    
    @Autowired
    public RequestsApiDelegateImpl(
        final NativeWebRequest request,
        final Platform platform,
        final IdentityResolver identityResolver
        )
        {
        super(request, identityResolver);
        this.platform = platform;
        this.platform.initialize();
        }


    @Override
    public ResponseEntity<IvoaOfferSetResponse> executionRequest(
        @RequestBody IvoaExecutionRequest request
        ){
        IdentityEntity identity = this.getIdentity();
        OfferSetEntity entity = this.platform.getOfferSetEntityFactory().create(
            request,
            identity
            );
        IvoaOfferSetResponse response = entity.makeBean(
            this.getURIBuilder()
            );
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
            response.getMeta().getUrl()
            );
        return new ResponseEntity<IvoaOfferSetResponse>(
            response,
            headers,
            HttpStatus.SEE_OTHER
            );
	    }
    }
