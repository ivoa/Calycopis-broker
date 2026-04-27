/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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

package net.ivoa.calycopis.webapp;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetFactory;
import net.ivoa.calycopis.spring.api.OffersetsApiDelegate;
import net.ivoa.calycopis.spring.model.IvoaExecutionRequest;
import net.ivoa.calycopis.spring.model.IvoaOfferSetResponse;

@Slf4j
@Service
public class OffersetsApiDelegateImpl
    extends BaseDelegateImpl
    implements OffersetsApiDelegate {

    private final OfferSetFactory offersetFactory ;

    @Autowired
    public OffersetsApiDelegateImpl(
        NativeWebRequest request,
        OfferSetFactory factory
        )
        {
        super(request);
        this.offersetFactory = factory ;
        }

    @Override
    public ResponseEntity<IvoaOfferSetResponse> offerSetGet(final UUID uuid)
        {
        final Optional<OfferSetEntity> found = offersetFactory.select(
            uuid
            );
        if (found.isPresent())
            {
            return new ResponseEntity<IvoaOfferSetResponse>(
                found.get().makeBean(
                    this.getURIBuilder()
                    ),
                HttpStatus.OK
                );
            }
        else {
            return new ResponseEntity<IvoaOfferSetResponse>(
                HttpStatus.NOT_FOUND
                );
            }
        }

    @Override
    public ResponseEntity<IvoaOfferSetResponse> offerSetPost(
        @RequestBody IvoaExecutionRequest request
        ){
        OfferSetEntity entity = offersetFactory.create(
            request
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
