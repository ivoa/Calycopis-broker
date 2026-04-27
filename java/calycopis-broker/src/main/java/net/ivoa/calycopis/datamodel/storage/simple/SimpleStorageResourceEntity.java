/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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
 * AIMetrics: [
 *     {
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 2,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.storage.simple;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleStorageResource;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * A SimpleStorageResource Entity.
 *
 */
@Entity
@Table(
    name = "simplestorageresources"
    )
public abstract class SimpleStorageResourceEntity
extends AbstractStorageResourceEntity
implements SimpleStorageResource
    {
    @Override
    public URI getKind()
        {
        return SimpleStorageResource.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleStorageResourceEntity()
        {
        super();
        }
    
    /**
     * Protected constructor with parent and validator result.
     *
     */
    protected SimpleStorageResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        super(
            session,
            result
            );

        // TODO Add the storage fields ...
        
        }

    @Override
    public IvoaAbstractStorageResource makeBean(URIBuilder uribuilder)
        {
        return fillBean(
            new IvoaSimpleStorageResource().meta(
                this.makeMeta(
                    uribuilder
                    )
                )
            );
        }

    protected IvoaSimpleStorageResource fillBean(final IvoaSimpleStorageResource bean)
        {
        super.fillBean(bean);
        //
        // Add the storage properties.
        //
        return bean;
        }
    }

