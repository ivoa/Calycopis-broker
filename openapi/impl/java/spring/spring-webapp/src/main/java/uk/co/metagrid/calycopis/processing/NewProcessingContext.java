/**
 * 
 */
package uk.co.metagrid.calycopis.processing;

import net.ivoa.calycopis.openapi.model.IvoaOfferSetRequest;
import uk.co.metagrid.calycopis.offerset.OfferSetEntity;

/**
 * 
 */
public interface NewProcessingContext
    {
    public boolean valid();

    public void process(final IvoaOfferSetRequest request, final OfferSetEntity offerset);

/*
 * 
    public IvoaOfferSetRequest getOfferSetRequest();
    public OfferSetEntity getOfferSetEntity();

    public List<SimpleDataResourceEntity> getDataResourceList();
    public SimpleDataResourceEntity findDataResource(final String key);

    public List<SimpleComputeResourceEntity> getComputeResourceList();
    public SimpleComputeResourceEntity findComputeResource(final String key);

    public AbstractExecutable getExecutable();

    // This is a total over all the compute resources.
    public long getMinCores();
    //public int getMaxCores();
    public void addMinCores(long delta);
    //public void addMaxCores(int delta);

    // This is a total over all the compute resources.
    public long getMinMemory();
    //public int getMaxMemory();
    public void addMinMemory(long delta);
    //public void addMaxMemory(int delta);

    public interface ScheduleItem
        {
        public Interval getStartTime();
        public Duration getDuration();
        }

    public ScheduleItem getPreparationTime();
    public void setPreparationTime(final Interval starttime, final Duration duration);

    public ScheduleItem getExecutionTime();
    public void setExecutionTime(final Interval starttime, final Duration duration);

 * 
 */

    }
