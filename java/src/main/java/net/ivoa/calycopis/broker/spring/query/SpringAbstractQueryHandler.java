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

package net.ivoa.calycopis.broker.spring.query;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import net.ivoa.calycopis.broker.engine.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.broker.engine.query.AbstractQueryHandler;

/**
 * 
 */
public class SpringAbstractQueryHandler<ResultType>
extends FactoryBaseImpl
implements AbstractQueryHandler<ResultType>
    {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ResultType> rowMapper;
    
    /**
     * 
     */
    public SpringAbstractQueryHandler(
        final JdbcTemplate jdbcTemplate,
        final RowMapper<ResultType> rowMapper
        ){
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
        }
    
    @Override
    public Iterable<ResultType> query(
        final String sqlQuery,
        final Map<String, Object> parameters
        ){
        //
        // TODO - Apply parameters to the query.
        //
        return JdbcClient.create(jdbcTemplate)
            .sql(sqlQuery)
            .query(rowMapper)
            .list();
        }

    @Override
    public Iterable<ResultType> query(
        final String sqlQuery
        ){
        return JdbcClient.create(jdbcTemplate)
            .sql(sqlQuery)
            .query(rowMapper)
            .list();
        }
    }
