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

import java.time.Duration;
import java.time.Instant;

import org.threeten.extra.Interval;

import wtf.metio.storageunits.model.StorageUnit;
import wtf.metio.storageunits.model.StorageUnits;

/**
 * Resources for the SimpleComputeResource database query.
 *  
 */
public interface SimpleComputeResourceQuery
    {

    /**
     * The the granularity of block steps in the database.
     * Set to 60 seconds.
     *
     */
    public static final Long BLOCK_STEP_SECONDS  = 60L ;

    /**
     * How far to look ahead in the schedule.
     * Set to 2 hours.
     * TODO This should be set by the Duration of the requested start Interval.
     *
     */
    public static final Long BLOCK_RANGE_SECONDS = 2L * 60L * 60L;

    /**
     * The default start range if none is specified in the request.
     * Set to 5 minutes.
     *
     */
    public static final Duration DEFAULT_START_RANGE = Duration.ofMinutes(5);

    /**
     * The maximum start time range allowed.
     * Set to 24 hours.
     *
     */
    public static final Duration MAXIMUM_START_RANGE = Duration.ofHours(24);

    /**
     * The default execution duration.
     * Set to 2 hours.
     *
     */
    public static final Duration DEFAULT_DURATION = Duration.ofHours(2);

    /**
     * How much more time we are allowed to offer over the requested duration.
     * Set to twice the requested value.
     *
     */
    public static final int OFFER_DURATION_SCALE = 2;

    /**
     * The maximum execution duration we are allowed to offer.
     * Set to 4 hours.
     *
     */
    public static final Duration MAXIMUM_DURATION = Duration.ofHours(4);

    /**
     * The default number of CPU cores if none is specified in the request.
     * Set to 1 core.
     *
     */
    public static final Long DEFAULT_CPU_CORES_REQUEST = 1L ;

    /**
     * The maximum number of CPU cores.
     * Set to 32 cores.
     *
     */
    public static final Long MAXIMUM_CPU_CORES_REQUEST = 32L ;

    /**
     * The total number of CPU cores available on the platform.
     * Set to 32 cores.
     *
     */
    public static final Long TOTAL_AVAILABLE_CPU_CORES = 32L ;

    /**
     * How many more CPU cores we are allowed to offer over the requested number.
     * Set to twice the requested value.
     *
     */
    public static final int OFFER_CPU_CORES_SCALE = 2;

    /**
     * The default amount of CPU memory.
     * Set to 1 GiB.
     *
     */
    public static final StorageUnit<?> DEFAULT_CPU_MEMORY_REQUEST = StorageUnits.gibibyte(1) ;

    /**
     * The maximum amount of CPU memory we can request.
     * Set to 32 GiB.
     *
     */
    public static final StorageUnit<?> MAXIMUM_CPU_MEMORY_REQUEST = StorageUnits.gibibyte(32);

    /**
     * The total amount of memory available on the platform.
     * Set to 32 GiB.
     *
     */
    public static final StorageUnit<?> TOTAL_AVAILABLE_CPU_MEMORY = StorageUnits.gibibyte(32);

    /**
     * How much more memory we are allowed to offer over the requested amount.
     * Set to twice the requested value.
     *
     */
    public static final int OFFER_CPU_MEMORY_SCALE = 2;

    /**
     * The number of rows in each category to select.
     * Set to 4 rows.
     *
     */
    public static final int QUERY_LIMIT = 4;

    /**
     * The database query.
     * TODO Need to change the query to concentrate on compute resource. 
     * https://github.com/ivoa/Calycopis-broker/issues/291
     * 
     */
    public static final String DATABSE_QUERY =
        """
        WITH ExecutionBlocks AS
            (
            SELECT
                SimpleExecutionSessions.phase AS BlockPhase,
                SimpleExecutionSessions.available_start_instant_seconds  / :blockstep AS BlockStart,
                SimpleExecutionSessions.available_duration_seconds / :blockstep AS BlockLength,
                COALESCE(SimpleComputeResources.maxofferedcores,  SimpleComputeResources.maxrequestedcores)  AS UsedCores,
                COALESCE(SimpleComputeResources.maxofferedmemory, SimpleComputeResources.maxrequestedmemory) AS UsedMemory
            FROM
                SimpleExecutionSessions
            JOIN
                AbstractComputeResources
            ON
                AbstractComputeResources.session = SimpleExecutionSessions.uuid
            JOIN
                SimpleComputeResources
            ON
                SimpleComputeResources.uuid = AbstractComputeResources.uuid
            WHERE
                SimpleExecutionSessions.phase IN ('OFFERED', 'PREPARING', 'WAITING', 'RUNNING', 'RELEASING')
            ),
        AvailableBlocks AS
            (
            SELECT
                StartRange.StartRow AS StartRow,
                COUNT(ExecutionBlocks.BlockStart) AS RowCount,
                (:totalcores  - COALESCE(sum(ExecutionBlocks.UsedCores),  0)) AS FreeCores,
                (:totalmemory - COALESCE(sum(ExecutionBlocks.UsedMemory), 0)) AS FreeMemory
            FROM
                (
                SELECT
                    generate_series + :rangeoffset AS StartRow
                FROM
                     generate_series(:rangestart, :rangeend)
                ) AS StartRange
            LEFT OUTER JOIN
                ExecutionBlocks
            ON  (
                    (ExecutionBlocks.BlockStart <= StartRange.StartRow)
                AND
                    ((ExecutionBlocks.BlockStart + ExecutionBlocks.BlockLength) > StartRange.StartRow)
                )
            GROUP BY
                StartRange.StartRow
            ),
        ConsecutiveBlocks AS (
            SELECT
                AvailableBlocks.StartRow,
                (AvailableBlocks.StartRow + 1) -
                    (
                    ROW_NUMBER() OVER (
                        PARTITION BY (
                            AvailableBlocks.FreeCores  >= :mincores
                            AND
                            AvailableBlocks.FreeMemory >= :minmemory
                            )
                        ORDER BY AvailableBlocks.StartRow
                        )
                    ) AS BlockGroup,
                FreeCores,
                FreeMemory
            FROM
                AvailableBlocks
            WHERE
                AvailableBlocks.FreeCores  >= :mincores
                AND
                AvailableBlocks.FreeMemory >= :minmemory
            ),
        CombinedBlocks AS (
            SELECT
                COUNT(*) AS BlockLength,
                MIN(ConsecutiveBlocks.StartRow)   AS BlockStart,
                MIN(ConsecutiveBlocks.FreeCores)  AS FreeCores,
                MIN(ConsecutiveBlocks.FreeMemory) AS FreeMemory
            FROM
                ConsecutiveBlocks
            WHERE
                ConsecutiveBlocks.BlockGroup IS NOT NULL
            GROUP BY
                ConsecutiveBlocks.BlockGroup
            HAVING
                COUNT(*) >= :minblocklength
            ),
        SplitBlocks AS (
            SELECT
                (CombinedBlocks.BlockStart + (:maxblocklength * (n - 1))) AS BlockStart,
                LEAST(
                    :maxblocklength,
                    (CombinedBlocks.BlockLength - (:maxblocklength * (n - 1)))
                    ) AS BlockLength,
                CombinedBlocks.FreeCores  AS FreeCores,
                CombinedBlocks.FreeMemory AS FreeMemory
            FROM
                CombinedBlocks,
                (
                SELECT
                    generate_series AS n
                FROM
                    generate_series(1, :maxblocklength)
                ) AS Numbers
            WHERE
                (CombinedBlocks.BlockStart + (:maxblocklength * (n - 1))) < (BlockStart + BlockLength)
            ),
        MatchingBlocks AS (
            SELECT
                AvailableBlocks.StartRow,
                SplitBlocks.BlockStart,
                SplitBlocks.BlockLength,
                SplitBlocks.FreeCores,
                SplitBlocks.FreeMemory
            FROM
                AvailableBlocks
            CROSS JOIN
                SplitBlocks
            WHERE
                AvailableBlocks.StartRow >= SplitBlocks.BlockStart
            AND
                AvailableBlocks.StartRow < (SplitBlocks.BlockStart + SplitBlocks.BlockLength)
            AND
                SplitBlocks.BlockLength >= :minblocklength
            AND
                SplitBlocks.BlockLength <= :maxblocklength
            ),
        GroupedBlocks AS (
            SELECT
                MatchingBlocks.BlockStart,
                MatchingBlocks.BlockLength,
                MIN(MatchingBlocks.FreeCores)  AS FreeCores,
                MIN(MatchingBlocks.FreeMemory) AS FreeMemory
            FROM
                MatchingBlocks
            GROUP BY
                MatchingBlocks.BlockStart,
                MatchingBlocks.BlockLength
            ),
        ScaledBlocks AS (
            SELECT
                GroupedBlocks.BlockStart,
                GroupedBlocks.BlockLength,
                LEAST(
                    :maxcores,
                    GroupedBlocks.FreeCores
                    ) AS BlockCores,
                LEAST(
                    :maxmemory,
                    GroupedBlocks.FreeMemory
                    ) AS BlockMemory
            FROM
                GroupedBlocks
            ),
        EarlyBlocks AS (
            SELECT
                *
            FROM
                ScaledBlocks
            ORDER BY
                ScaledBlocks.BlockStart    ASC,
                ScaledBlocks.BlockCores    DESC,
                ScaledBlocks.BlockMemory   DESC,
                ScaledBlocks.BlockLength   DESC
            LIMIT :querylimit
            ),
        HiMemBlocks AS (
            SELECT
                *
            FROM
                ScaledBlocks
            ORDER BY
                ScaledBlocks.BlockMemory   DESC,
                ScaledBlocks.BlockCores    DESC,
                ScaledBlocks.BlockStart    ASC,
                ScaledBlocks.BlockLength   DESC
            LIMIT :querylimit
            ),
        HiCpuBlocks AS (
            SELECT
                *
            FROM
                ScaledBlocks
            ORDER BY
                ScaledBlocks.BlockCores    DESC,
                ScaledBlocks.BlockMemory   DESC,
                ScaledBlocks.BlockStart    ASC,
                ScaledBlocks.BlockLength   DESC
            LIMIT :querylimit
            ),
        CombinedQuery AS (
            (
            SELECT
                *
            FROM
                EarlyBlocks
            )
        UNION
            (
            SELECT
                *
            FROM
                HiMemBlocks
            )
        UNION
            (
            SELECT
                *
            FROM
                HiCpuBlocks
            )
        )

        SELECT * FROM EarlyBlocks

        """;
    
    /**
     * Apply defaults and parameters to build a database query.
     * 
     */
    public static String build(
        Interval requestStart,
        Duration requestDuration,
        Long requestMinCores,
        Long requestMemory,
        int requestLimit
        ){
        String query = new String(DATABSE_QUERY);

        // If no start time, use the default.
        if (requestStart == null)
            {
            requestStart = Interval.of(
                Instant.now(),
                DEFAULT_START_RANGE
                );
            }

        // If no start end, use the default.
        if (requestStart.getEnd() == Instant.MAX)
            {
            requestStart = Interval.of(
                requestStart.getStart(),
                DEFAULT_START_RANGE
                );
            }

        // If no duration, use the default.
        if (requestDuration == null)
            {
            requestDuration = DEFAULT_DURATION;
            }

        // TODO Check for maximum duration.
        
        // If no minimum cores, use the default.
        if (requestMinCores == null)
            {
            requestMinCores = DEFAULT_CPU_CORES_REQUEST;
            }

        // TODO Check for maximum cores.

        // If no minimum memort, use the default.
        if (requestMemory == null)
            {
            requestMemory = DEFAULT_CPU_MEMORY_REQUEST.longValue();
            }

        // TODO Check for maximum memory.

        // Calculate the maximum duration.
        Duration maxduration = Duration.ofSeconds(
            requestDuration.getSeconds() * OFFER_DURATION_SCALE
            );
        if (maxduration.getSeconds() >= MAXIMUM_DURATION.getSeconds())
            {
            maxduration = MAXIMUM_DURATION;
            }

        // This is vulnerable to SQL injection.
        // Ideally we should use JdbcTemplate named parameters,m but that didn't work.
        // TODO Move the parameter replacement into Spring JdbcTemplate.
        
        query = query.replace(":blockstep",   String.valueOf(BLOCK_STEP_SECONDS));
        query = query.replace(":totalcores",  String.valueOf(TOTAL_AVAILABLE_CPU_CORES));
        query = query.replace(":totalmemory", String.valueOf(TOTAL_AVAILABLE_CPU_MEMORY.longValue()));
        query = query.replace(":rangeoffset", String.valueOf(
            requestStart.getStart().getEpochSecond() / BLOCK_STEP_SECONDS
            ));
        query = query.replace(":rangestart",  String.valueOf(1));
        query = query.replace(":rangeend",    String.valueOf(
            BLOCK_RANGE_SECONDS  / BLOCK_STEP_SECONDS
            ));
        query = query.replace(":mincores",   String.valueOf(requestMinCores));
        query = query.replace(":minmemory",  String.valueOf(requestMemory));
        query = query.replace(":minblocklength", String.valueOf(
            requestDuration.getSeconds() / BLOCK_STEP_SECONDS
            ));
        query = query.replace(":maxblocklength", String.valueOf(
            maxduration.getSeconds() / BLOCK_STEP_SECONDS
            ));

        query = query.replace(":maxcores", String.valueOf(
            requestMinCores * OFFER_CPU_CORES_SCALE
            ));
        query = query.replace(":maxmemory", String.valueOf(
            requestMemory * OFFER_CPU_MEMORY_SCALE
            ));
        if (requestLimit > 0)
            {
            query = query.replace(":querylimit", String.valueOf(
                requestLimit
                ));
            }
        else {
            query = query.replace(":querylimit", String.valueOf(
                QUERY_LIMIT
                ));
            }
        
        return query;
        }
    }
