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
package net.ivoa.calycopis.broker.engine.entities.offerset;

import java.net.URI;
import java.time.Instant;

import net.ivoa.calycopis.broker.engine.entities.component.Component;
import net.ivoa.calycopis.broker.engine.entities.session.AbstractExecutionSession;
import net.ivoa.calycopis.schema.spring.model.IvoaOfferSetResponse.ResultEnum;

/**
 * 
 * 
 */
public interface OfferSet
extends Component
    {
    
    /**
     * The webapp path for executables.
     * TODO Move this to AbstractOfferset.
     * 
     */
    public static final URI WEBAPP_PATH = URI.create("offersets/"); 
    
    /**
     * The OpenAPI type identifier.
     * 
     */
    public static final URI KIND_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/offerset/execution-offerset.yaml") ;
    
    /**
     * Get the date/time this OfferSet expires.
     * 
     */
    public Instant getExpires();

    /**
     * Get the OfferSet (YES|NO) result.
     * 
     */
    public ResultEnum getResult();

    /**
     * Get a list of the Execution offers.
     * 
     */
    public Iterable<AbstractExecutionSession> getOffers();
    
    }
