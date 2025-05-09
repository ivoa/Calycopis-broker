/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 *
 */

package net.ivoa.calycopis.functional.execution;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;

/**
 * 
 */
@Entity
@Table(name = "executionsteps")
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractExecutionStepEntity
implements ExecutionStep
    {

    public AbstractExecutionStepEntity(final ExecutionSessionEntity session, final ComponentEntity component, final Duration offset, final Duration duration)
        {
        this(session, component);
        this.offset   = offset;
        this.duration = duration;
        }

    public AbstractExecutionStepEntity(final ExecutionSessionEntity session, final ComponentEntity component)
        {
        super();
        this.session   = session;
        this.component = component ;
        }

    @Id
    @GeneratedValue
    protected UUID uuid;
    @Override
    public UUID getUuid()
        {
        return this.uuid ;
        }
    
    @JoinColumn(name = "session", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExecutionSessionEntity session;
    @Override
    public ExecutionSessionEntity getSession()
        {
        return this.session;
        }

    @JoinColumn(name = "component", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ComponentEntity component;
    @Override
    public ComponentEntity getComponent()
        {
        return this.component;
        }

    @JoinColumn(name = "prev", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractExecutionStepEntity prev;
    @Override
    public AbstractExecutionStepEntity getPrev()
        {
        return this.prev;
        }
    public void setPrev(final AbstractExecutionStepEntity step)
        {
        this.prev = step;
        }

    @JoinColumn(name = "next", referencedColumnName = "uuid", nullable = true)
    @OneToOne(fetch = FetchType.LAZY)
    private AbstractExecutionStepEntity next;
    @Override
    public AbstractExecutionStepEntity getNext()
        {
        return this.next;
        }
    public void setNext(final AbstractExecutionStepEntity step)
        {
        this.next = step;
        }

    @Column(name = "phase")
    private Phase phase;
    @Override
    public Phase getPhase()
        {
        return this.phase;
        }
    protected void setPhase(final Phase phase)
        {
        this.phase = phase;
        }

    @Column(name = "start")
    private Instant start;
    @Override
    public Instant getStart()
        {
        return this.start;
        }
    public void setStart(final Instant instant)
        {
        this.start= instant;
        }

    @Column(name = "fred")
    private Duration offset;
    @Override
    public Duration getOffset()
        {
        return this.offset;
        }
    public void setoffset(final Duration offset)
        {
        this.offset = offset;
        }

    @Column(name = "duration")
    private Duration duration;
    @Override
    public Duration getDuration()
        {
        return this.duration;
        }
    public void setDuration(final Duration duration)
        {
        this.duration = duration;
        }
    }
