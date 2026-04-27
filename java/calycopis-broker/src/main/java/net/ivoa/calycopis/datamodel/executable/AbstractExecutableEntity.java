/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntity;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.mapping.LifecyclePhaseMappings;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "abstractexecutables"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractExecutableEntity
extends LifecycleComponentEntity
    implements AbstractExecutable
    {
    /**
     * Protected constructor.
     * 
     */
    protected AbstractExecutableEntity()
        {
        super();
        }
    
    /**
     * Protected constructor.
     * 
     */
    protected AbstractExecutableEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result,
        final IvoaComponentMetadata meta
        ){
        super(
            meta
            );

        this.session = session;
        this.session.setExecutable(
            this
            );

        //
        // Start preparing when the session starts preparing.
        this.prepareDurationSeconds     = result.getPrepareDuration();
        this.prepareStartInstantSeconds = session.getPrepareStartInstantSeconds();

        //
        // Available as soon as the preparation is done.
        this.availableStartDurationSeconds = 0L;
        this.availableStartInstantSeconds  = this.prepareStartInstantSeconds + this.prepareDurationSeconds;
        this.availableDurationSeconds      = 0L;

        //
        // Hard coded 1s release duration.
        // Start releasing as soon as session availability ends.
        this.releaseDurationSeconds     = result.getReleaseDuration(); ; 
        this.releaseStartInstantSeconds = session.getReleaseStartInstantSeconds();         

        }
    
    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private SimpleExecutionSessionEntity session;
    @Override
    public SimpleExecutionSessionEntity getSession()
        {
        return this.session;
        }

    public abstract IvoaAbstractExecutable makeBean(final URIBuilder builder);

    protected IvoaAbstractExecutable fillBean(final IvoaAbstractExecutable bean)
        {
        bean.setKind(
            this.getKind()
            );
        bean.setPhase(
            LifecyclePhaseMappings.toIvoa(this.getPhase())
            );
        bean.setSchedule(
            this.makeScheduleBean()
            );
        return bean;
        }

    @Override
    protected URI getWebappPath()
        {
        return AbstractExecutable.WEBAPP_PATH;
        }

    }
