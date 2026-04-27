/**
 * 
 */
package net.ivoa.calycopis.functional.booking.compute;

import java.time.Duration;
import java.util.List;

import org.threeten.extra.Interval;

import net.ivoa.calycopis.functional.booking.ResourceOfferFactory;

/**
 * Public interface for a ComputeResourceOffer factory. 
 *  
 */
public interface ComputeResourceOfferFactory
    extends ResourceOfferFactory
    {
    /**
     * Get the maximum number of CPU cores that can be requested.
     * 
     */
    public Long getMaxCores();

    /**
     * Get the maximum amount of memory that can be requested, in GiB.
     * 
     */
    public Long getMaxMemory();
    
    /**
     * Generate a list of offers.
     *  
     */
    public List<ComputeResourceOffer> generate(final Interval requeststart, final Duration requestduration, final Long requestcores, final Long requestmemory, int offerCount);

    }
