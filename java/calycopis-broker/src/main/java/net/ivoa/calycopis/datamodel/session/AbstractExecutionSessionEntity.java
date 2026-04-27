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

package net.ivoa.calycopis.datamodel.session;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetEntity;
import net.ivoa.calycopis.spring.model.IvoaAbstractExecutionSession;
import net.ivoa.calycopis.util.URIBuilder;

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
    public OfferSetEntity getOfferSet()
        {
        return this.offerset;
        }

    public abstract IvoaAbstractExecutionSession makeBean(final URIBuilder uribuilder);
    
    }
