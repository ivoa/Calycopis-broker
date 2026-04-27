/**
 * 
 */
package net.ivoa.calycopis.functional.booking;

import java.time.Duration;
import java.time.Instant;

/**
 * Public interface for a resource offer.
 * 
 */
public interface ResourceOffer
    {
    /**
     * The offer name.
     * 
     */
    public String getName();

    /**
     * The offer start time as an instant.
     * TODO Should this be an Interval to allow for batch queue processing.
     * 
     */
    public Instant getStartTime();

    /**
     * The offer length as a Duration.
     * 
     */
    public Duration getDuration();

    }
