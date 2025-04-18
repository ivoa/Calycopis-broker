/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.openapi.model.IvoaMessageItem;

/**
 * Unfortunately, our Beans can't extend this class because they need to extend
 * the generated Ivoa bean class to get the serialization annotations.
 * TODO Move this to an interface with defaults.
 * 
 */
@Deprecated
public abstract class AbstractExecutableBean
    extends IvoaAbstractExecutable
    {

    public abstract String getRequestPath();
    
    /**
     * The base URL for the current request.
     *
     */
    private final String baseurl;

    /**
     * The ComponentEntity to wrap.
     *
     */
    private final ComponentEntity entity;
    
    /**
     * Protected constructor.
     *
     */
    public AbstractExecutableBean(final String baseurl, final ComponentEntity entity)
        {
        super();
        this.baseurl = baseurl;
        this.entity  = entity;
        }

    @Override
    public UUID getUuid()
        {
        return entity.getUuid();
        }
    
    @Override
    public String getName()
        {
        return entity.getName();
        }
    
    @Override
    public OffsetDateTime getCreated()
        {
        return this.entity.getCreated();
        }
    
    @Override
    public List<@Valid IvoaMessageItem> getMessages()
        {
        return entity.getMessageBeans();
        }
    }
