/**
 * 
 */
package net.ivoa.calycopis.functional.booking;

import java.time.Duration;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

/**
 * A ResourceOffer implementation.
 * 
 */
@Slf4j
public class ResourceOfferImpl
    implements ResourceOffer
    {

    public ResourceOfferImpl(final String offername, final Instant starttime, final Duration duration)
        {
        log.debug("ResourceOfferImpl(...)");
        log.debug("values [{}][{}][{}]", offername, starttime, duration);
        this.offername = offername;
        this.starttime = starttime;
        this.duration  = duration;
        }

    protected final String offername;
    @Override
    public String getName()
        {
        return this.offername;
        }

    protected final Instant starttime;
    @Override
    public Instant getStartTime()
        {
        return this.starttime;
        }

    protected final Duration duration;
    @Override
    public Duration getDuration()
        {
        return this.duration;
        }
    }
