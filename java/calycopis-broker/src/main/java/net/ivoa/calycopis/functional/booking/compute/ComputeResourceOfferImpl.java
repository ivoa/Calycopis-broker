/**
 * 
 */
package net.ivoa.calycopis.functional.booking.compute;

import java.time.Duration;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.booking.ResourceOfferImpl;

/**
 * A ComputeResourcesOffer implementation.
 * 
 */
@Slf4j
public class ComputeResourceOfferImpl
    extends ResourceOfferImpl
    implements ComputeResourceOffer
    {

    public ComputeResourceOfferImpl(final String offername, final Instant starttime, final Duration duration, final Long cpucores, final Long cpumemory)
        {
        super(
            offername,
            starttime,
            duration
            );
        this.cpucores  = cpucores;
        this.cpumemory = cpumemory;
        }

    private final Long cpucores;
    @Override
    public Long getCores()
        {
        return this.cpucores;
        }

    private final Long cpumemory;
    @Override
    public Long getMemory()
        {
        return this.cpumemory;
        }
    }
