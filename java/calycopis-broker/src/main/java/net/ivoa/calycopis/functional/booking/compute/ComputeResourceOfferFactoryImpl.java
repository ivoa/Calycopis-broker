/**
 *
 */
package net.ivoa.calycopis.functional.booking.compute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.threeten.extra.Interval;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import wtf.metio.storageunits.model.StorageUnit;
import wtf.metio.storageunits.model.StorageUnits;

/**
 * A ComputeResourceOfferFactory implementation.
 *
 * AIMetrics: [
 *     {
 *     "name": "ChatGPT",
 *     "contribution": "90%"
 *     }
 *   ]
 *
 */
@Slf4j
@Component
public class ComputeResourceOfferFactoryImpl
    extends FactoryBaseImpl
    implements ComputeResourceOfferFactory
    {

    /**
     * Our Spring database template.
     *
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Public constructor.
     *
     */
    @Autowired
    public ComputeResourceOfferFactoryImpl(final JdbcTemplate jdbcTemplate)
        {
        this.jdbcTemplate = jdbcTemplate;
        }

    // TODO Move all of these values to a PlatformConfiguration that can be shared with the OfferSetRequestParser.
    // TODO Perform the main verification checks in the OfferSetRequestParser.

    /**
     * The the granularity of time values in the database.
     * Set to 5 minute steps.
     * TODO make this configurable.
     *
     */
    //public static final Long BLOCK_STEP_SECONDS  = 60L * 5L ;
    public static final Long BLOCK_STEP_SECONDS  = 60L ;

    /**
     * How far to look ahead.
     * Set to 24 hours.
     * TODO This should be set by the Duration of the requested start Interval.
     *
     */
    //public static final Long BLOCK_RANGE_SECONDS = 24L * 60L * 60L;
    public static final Long BLOCK_RANGE_SECONDS = 2L * 60L * 60L;

    /**
     * The default start time range if none is specified in the request.
     * Set to 5 minutes.
     * TODO make this configurable.
     *
     */
    public static final Duration DEFAULT_START_RANGE = Duration.ofMinutes(5);

    /**
     * The maximum start time range allowed.
     * Set to 24 hours.
     * TODO make this configurable.
     *
     */
    public static final Duration MAXIMUM_START_RANGE = Duration.ofHours(24);

    /**
     * The default execution duration if none is specified in the request.
     * Set to 2 hours.
     * TODO make this configurable.
     *
     */
    public static final Duration DEFAULT_DURATION = Duration.ofHours(2);

    /**
     * How much more time we are allowed to offer over the original requested duration.
     * Set to twice the requested value.
     * TODO make this configurable.
     *
     */
    public static final int OFFER_DURATION_SCALE = 2;

    /**
     * The maximum execution duration we are allowed to offer.
     * Set to 4 hours.
     * TODO make this configurable.
     *
     */
    public static final Duration MAXIMUM_DURATION = Duration.ofHours(4);

    /**
     * The default number of CPU cores if none is specified in the request.
     * TODO make this configurable.
     *
     */
    public static final Long DEFAULT_CPU_CORES_REQUEST = 1L ;

    /**
     * The maximum number of CPU cores we can request.
     * TODO make this configurable.
     *
     */
    public static final Long MAXIMUM_CPU_CORES_REQUEST = 32L ;

    /**
     * The total number of CPU cores available on the platform.
     * TODO make this configurable.
     *
     */
    public static final Long TOTAL_AVAILABLE_CPU_CORES = 32L ;

    /**
     * How many more CPU cores we are allowed to offer over the original requested duration.
     * Set to twice the requested value.
     * TODO make this configurable.
     *
     */
    public static final int OFFER_CPU_CORES_SCALE = 2;

    /**
     * The default amount of CPU memory if none is specified in the request.
     * TODO make this configurable.
     * TODO make this configurable.
     *
     */
    public static final StorageUnit<?> DEFAULT_CPU_MEMORY_REQUEST = StorageUnits.gibibyte(1) ;

    /**
     * The maximum number amount of memory we can request.
     * TODO make this configurable.
     *
     */
    public static final StorageUnit<?> MAXIMUM_CPU_MEMORY_REQUEST = StorageUnits.gibibyte(32);

    /**
     * The total amount of memory available on the platform.
     * TODO make this configurable.
     *
     */
    public static final StorageUnit<?> TOTAL_AVAILABLE_CPU_MEMORY = StorageUnits.gibibyte(32);

    /**
     * How much more memory we are allowed to offer over the original requested duration.
     * Set to twice the requested value.
     * TODO make this configurable.
     *
     */
    public static final int OFFER_CPU_MEMORY_SCALE = 2;

    /**
     * The number of rows in each category to select.
     * TODO make this configurable.
     *
     */
    public static final int QUERY_ROW_LIMIT = 4;

    @Override
    public Duration getMaxStartRange()
        {
        return MAXIMUM_START_RANGE ;
        }

    @Override
    public Duration getMaxDuration()
        {
        return MAXIMUM_DURATION;
        }

    @Override
    public Long getMaxCores()
        {
        return MAXIMUM_CPU_CORES_REQUEST;
        }

    @Override
    public Long getMaxMemory()
        {
        return MAXIMUM_CPU_MEMORY_REQUEST.longValue();
        }

    @Override
    public List<ComputeResourceOffer> generate(Interval requeststart, Duration requestduration, Long requestcores, Long requestmemory, int offerCount)
        {
        log.debug("generate(Interval, Duration, Long, Long");
        log.debug("Interval [{}]", requeststart);
        log.debug("Duration [{}]", requestduration);
        // If no starttime, use the default.
        if (requeststart == null)
            {
            requeststart = Interval.of(
                Instant.now(),
                DEFAULT_START_RANGE
                );
            }

        // If no startend, use the default.
        if (requeststart.getEnd() == Instant.MAX)
            {
            requeststart = Interval.of(
                requeststart.getStart(),
                DEFAULT_START_RANGE
                );
            }

        // If no duration, use the default.
        if (requestduration == null)
            {
            requestduration = DEFAULT_DURATION;
            }

        if (requestcores == null)
            {
            requestcores = DEFAULT_CPU_CORES_REQUEST;
            }

        if (requestmemory == null)
            {
            requestmemory = DEFAULT_CPU_MEMORY_REQUEST.longValue();
            }

        // Calculate the maximum duration.
        Duration maxduration = Duration.ofSeconds(
            requestduration.getSeconds() * OFFER_DURATION_SCALE
            );
        if (maxduration.getSeconds() >= MAXIMUM_DURATION.getSeconds())
            {
            maxduration = MAXIMUM_DURATION;
            }
      //--
        // Need to change the query to concentrate on compute resource. 
        // https://github.com/ivoa/Calycopis-broker/issues/291
        String query =
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

            query = query.replace(":blockstep",   String.valueOf(BLOCK_STEP_SECONDS));
            query = query.replace(":totalcores",  String.valueOf(TOTAL_AVAILABLE_CPU_CORES));
            query = query.replace(":totalmemory", String.valueOf(TOTAL_AVAILABLE_CPU_MEMORY.longValue()));
            query = query.replace(":rangeoffset", String.valueOf(
                requeststart.getStart().getEpochSecond() / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":rangestart",  String.valueOf(1));
            query = query.replace(":rangeend",    String.valueOf(
                BLOCK_RANGE_SECONDS  / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":mincores",   String.valueOf(requestcores));
            query = query.replace(":minmemory",  String.valueOf(requestmemory));
            query = query.replace(":minblocklength", String.valueOf(
                requestduration.getSeconds() / BLOCK_STEP_SECONDS
                ));
            query = query.replace(":maxblocklength", String.valueOf(
                maxduration.getSeconds() / BLOCK_STEP_SECONDS
                ));

            query = query.replace(":maxcores", String.valueOf(
                requestcores * OFFER_CPU_CORES_SCALE
                ));
            query = query.replace(":maxmemory", String.valueOf(
                requestmemory * OFFER_CPU_MEMORY_SCALE
                ));
            if (offerCount > 0)
                {
                query = query.replace(":querylimit", String.valueOf(
                    offerCount
                    ));
                }
            else {
                query = query.replace(":querylimit", String.valueOf(
                    QUERY_ROW_LIMIT
                    ));
                }
            //--
        log.debug("Running query ...");
        log.debug(query);
        List<ComputeResourceOffer> list = JdbcClient.create(jdbcTemplate)
            .sql(query)
            .query(new ComputeOfferMapper())
            .list();
        log.debug("Count [{}]", list.size());
        return list;
        }

    static class ComputeOfferMapper implements RowMapper<ComputeResourceOffer>
        {
        @Override
        public ComputeResourceOffer mapRow(ResultSet resultset, int rownumber)
        throws SQLException
            {
            log.debug("mapRow(ResultSet, int)");
            log.debug("Row number [{}]", rownumber);
            try {
                ComputeResourceOffer offer = new ComputeResourceOfferImpl(
                    "offer-" + Integer.toString(rownumber),
                    Instant.ofEpochSecond(
                        resultset.getLong("BlockStart") * BLOCK_STEP_SECONDS
                        ),
                    Duration.ofSeconds(
                        resultset.getLong("BlockLength") * BLOCK_STEP_SECONDS
                        ),
                    resultset.getLong("BlockCores"),
                    resultset.getLong("BlockMemory")
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
        }
    }
