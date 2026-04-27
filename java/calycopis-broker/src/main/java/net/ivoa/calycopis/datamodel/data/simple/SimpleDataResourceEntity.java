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

package net.ivoa.calycopis.datamodel.data.simple;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * A Simple data resource.
 *
 */
@Entity
@Table(
    name = "simpledataresources"
    )
public abstract class SimpleDataResourceEntity
    extends AbstractDataResourceEntity
    implements SimpleDataResource
    {
    @Override
    public URI getKind()
        {
        return SimpleDataResource.TYPE_DISCRIMINATOR ;
        }

    /**
     * Protected constructor
     *
     */
    protected SimpleDataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent.
     *
     */
    public SimpleDataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        this(
            session,
            storage,
            result,
            (IvoaSimpleDataResource) result.getObject()
            );
        }
    
    /**
     * Protected constructor with parent.
     * TODO validated can be replaced by Result.getObject()
     * TODO No need to pass validated.getMeta() separately.
     *
     */
    public SimpleDataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result,
        final IvoaSimpleDataResource validated
        ){
        super(
            session,
            storage,
            result,
            validated.getMeta()
            );
        this.location = validated.getLocation();
        }

    private String location;
    @Override
    public String getLocation()
        {
        return this.location;
        }

    @Override
    public IvoaAbstractDataResource makeBean(final URIBuilder builder)
        {
        return fillBean(
            new IvoaSimpleDataResource().meta(
                this.makeMeta(
                    builder
                    )
                )
            );
        }

    protected IvoaSimpleDataResource fillBean(final IvoaSimpleDataResource bean)
        {
        super.fillBean(bean);
        bean.setLocation(
            this.getLocation()
            );
        return bean;
        }
    }

