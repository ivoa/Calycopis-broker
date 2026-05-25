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
package net.ivoa.calycopis.broker.engine.functional.booking.compute.simple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.functional.booking.AbstractResourceOfferBean;

/**
 * A SimpleComputeResourcesOffer bean implementation.
 * 
 */
@Slf4j
public class SimpleComputeResourceOfferBean
extends AbstractResourceOfferBean
implements SimpleComputeResourceOffer
    {
    /**
     * Create a SimpleComputeResourceOfferBean from a JDBC ResultSet.
     * 
     */
    public static SimpleComputeResourceOfferBean create(final ResultSet resultSet, int rowNumber)
    throws SQLException
        {
        log.debug("create(ResultSet, int)");
        log.debug("Row number [{}]", rowNumber);
        try {
            SimpleComputeResourceOfferBean offer = new SimpleComputeResourceOfferBean(
                "offer-" + Integer.toString(rowNumber),
                Interval.of(
                    Instant.ofEpochSecond(
                        resultSet.getLong("BlockStart") * SimpleComputeResourceQuery.BLOCK_STEP_SECONDS
                        ),
                Duration.ofSeconds(
                        0
                        )
                    ),
                Duration.ofSeconds(
                    resultSet.getLong("BlockLength") * SimpleComputeResourceQuery.BLOCK_STEP_SECONDS
                    ),
                resultSet.getLong("BlockCores"),
                resultSet.getLong("BlockMemory")
                );
            return offer;
            }
        catch (IllegalArgumentException ouch)
            {
            throw new SQLException(
                ouch
                );
            }
        }
    
    /**
     * Protected constructor.
     * 
     */
    protected SimpleComputeResourceOfferBean(final String offername, final Interval interval, final Duration duration, final Long cpucores, final Long memory)
        {
        super(
            offername,
            interval,
            duration
            );
        this.minCpuCores = cpucores;
        this.maxCpuCores = cpucores;
        this.minMemory   = memory;
        this.maxMemory   = memory;
        }

    private final Long minCpuCores;
    @Override
    public Long getMinCores()
        {
        return this.minCpuCores;
        }

    private final Long maxCpuCores;
    @Override
    public Long getMaxCores()
        {
        return this.maxCpuCores;
        }
    
    private final Long minMemory;
    @Override
    public Long getMinMemory()
        {
        return this.minMemory;
        }

    private final Long maxMemory;
    @Override
    public Long getMaxMemory()
        {
        return this.maxMemory;
        }
    }
