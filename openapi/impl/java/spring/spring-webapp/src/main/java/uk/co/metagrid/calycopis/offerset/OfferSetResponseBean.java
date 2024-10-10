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

package uk.co.metagrid.calycopis.offerset;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.openapi.model.IvoaExecutionResponse;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;
import net.ivoa.calycopis.openapi.model.IvoaOfferSetResponse;
import uk.co.metagrid.calycopis.execution.ExecutionEntity;
import uk.co.metagrid.calycopis.execution.ExecutionResponseBean;
import uk.co.metagrid.calycopis.message.MessageEntity;
import uk.co.metagrid.calycopis.message.MessageItemBean;
import uk.co.metagrid.calycopis.util.ListWrapper;

/**
 * 
 */
@Slf4j
public class OfferSetResponseBean
    extends IvoaOfferSetResponse {

    /**
     * The URL path for the offersets endpoint.
     * 
     */
    private static final String REQUEST_PATH = "/offerset/" ;


    /**
     * The base URL for the current request.
     * 
     */
    private final String baseurl;

    /**
     * The OfferSet entity to wrap.
     * 
     */
    private final OfferSetEntity offerset;

	/**
	 * Protected constructor.
	 * 
	 */
	public OfferSetResponseBean(final String baseurl, final OfferSetEntity entity)
	    {
	    super();
        this.baseurl = baseurl;
        this.offerset = entity;
	    }

	/**
	 * Generate the href URL based on our baseurl and UUID.
	 * 
	 */
	@Override
	public String getHref()
	    {
	    return this.baseurl + REQUEST_PATH + offerset.getUuid();
	    }

    @Override
    public UUID getUuid()
        {
        return offerset.getUuid();
        }

    @Override
    public String getName()
        {
        return offerset.getName();
        }

    @Override
    public ResultEnum getResult()
        {
        return offerset.getResult();
        }

    @Override
    public OffsetDateTime getCreated()
        {
        return offerset.getCreated();
        }

    @Override
    public OffsetDateTime getExpires()
        {
        return offerset.getExpires();
        }

    @Override
    public List<@Valid IvoaExecutionResponse> getOffers()
        {
        return new ListWrapper<IvoaExecutionResponse, ExecutionEntity>(
            offerset.getOffers()
            ){
            public IvoaExecutionResponse wrap(final ExecutionEntity inner)
                {
                return new ExecutionResponseBean(
                     baseurl,
                     inner
                     );
                }
            };
        }

    @Override
    public List<@Valid IvoaMessageItem> getMessages()
        {
        return new ListWrapper<IvoaMessageItem, MessageEntity>(
            offerset.getMessages()
            ){
            public IvoaMessageItem wrap(final MessageEntity inner)
                {
                return new MessageItemBean(
                    inner
                    );
                }
            };
        }
    }
