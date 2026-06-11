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

package net.ivoa.calycopis.broker.spring.booking.compute.simple;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceOffer;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceOfferBean;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceQueryHandler;
import net.ivoa.calycopis.broker.spring.query.SpringAbstractQueryHandler;

/**
 * 
 */
@Component
public class SpringSimpleComputeResourceOfferQueryHandlerImpl
extends SpringAbstractQueryHandler<SimpleComputeResourceOffer>
implements SimpleComputeResourceQueryHandler
    {

    /**
     * Public constructor.
     * 
     */
    public SpringSimpleComputeResourceOfferQueryHandlerImpl(
        final JdbcTemplate jdbcTemplate
        ){
        super(
            jdbcTemplate,
            new RowMapper<SimpleComputeResourceOffer>()
                {
                @Override
                public SimpleComputeResourceOffer mapRow(ResultSet resultSet, int rowNum)
                throws SQLException
                    {
                    return SimpleComputeResourceOfferBean.create(
                        resultSet,
                        rowNum
                        );
                    }
                }
            );
        }
    }
