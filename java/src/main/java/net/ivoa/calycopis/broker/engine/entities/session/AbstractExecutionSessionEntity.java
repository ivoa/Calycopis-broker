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

package net.ivoa.calycopis.broker.engine.entities.session;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.ComponentEntity;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSet;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetEntity;
import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetEntity;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractExecutionSession;

/**
 * 
 */
@Slf4j
@Entity
@Table(
    name = "abstractexecutionsessions"
    )
public abstract class AbstractExecutionSessionEntity
extends ComponentEntity
implements AbstractExecutionSession
    {
    @Override
    protected URI getWebappPath()
        {
        return AbstractExecutionSession.WEBAPP_PATH;
        }

    /**
     * 
     */
    public AbstractExecutionSessionEntity()
        {
        super();
        }

    /**
     * 
     */
    public AbstractExecutionSessionEntity(final OfferSetEntity offerset, final String name)
        {
        super(name);
        this.offerset = offerset;
        offerset.addExecutionSession(
            this
            );
        }

    @JoinColumn(name = "offerset", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OfferSetEntity offerset;

    @Override
    public OfferSet getOfferSet()
        {
        return this.offerset;
        }

    /**
     * Get the parent OfferSet.
     *
     */
    public OfferSetEntity getOfferSetEntity()
        {
        return this.offerset;
        }

    public abstract IvoaAbstractExecutionSession makeBean(final URIBuilder uribuilder);
    
    }
