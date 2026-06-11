/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
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
 *     along with this software. If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: []
 *
 */

package net.ivoa.calycopis.broker.engine.query;

import java.util.Map;

import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBase;

/**
 * Public interface for a database query handler.
 * 
 */
public interface AbstractQueryHandler<ResultType>
extends FactoryBase
    {
    /**
     * Execute a query and return a Iterable of results.
     * 
     */
    public Iterable<ResultType> query(final String query);

    /**
     * Execute a query and return a Iterable of results.
     * 
     */
    public Iterable<ResultType> query(final String query, final Map<String, Object> params);
    
    }
