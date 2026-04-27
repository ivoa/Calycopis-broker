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

package net.ivoa.calycopis.datamodel.data.amazon;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.spring.model.IvoaS3DataResource;
import net.ivoa.calycopis.util.URIBuilder;

/**
 * An Amazon S3 data resource.
 *
 */
@Entity
@Table(
    name = "s3dataresources"
    )
public abstract class AmazonS3DataResourceEntity
    extends AbstractDataResourceEntity
    implements AmazonS3DataResource
    {
    @Override
    public URI getKind()
        {
        return AmazonS3DataResource.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor
     *
     */
    protected AmazonS3DataResourceEntity()
        {
        super();
        }

    /**
     * Protected constructor with parent, storage, and validation Result.
     *
     */
    public AmazonS3DataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        this(
            session,
            storage,
            result,
            (IvoaS3DataResource) result.getObject()
            );
        }
    
    /**
     * Protected constructor with parent and template.
     * TODO validated can be replaced by Result.getObject()
     * TODO No need to pass validated.getMeta() separately.
     *
     */
    public AmazonS3DataResourceEntity(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result,
        final IvoaS3DataResource validated
        ){
        super(
            session,
            storage,
            result,
            validated.getMeta()
            );
        this.endpoint = validated.getEndpoint();
        this.template = validated.getTemplate();
        this.bucket   = validated.getBucket();
        this.object   = validated.getObject();

        }

    private String endpoint;
    @Override
    public String getEndpoint()
        {
        return this.endpoint;
        }

    private String template;
    @Override
    public String getTemplate()
        {
        return this.template;
        }

    private String bucket;
    @Override
    public String getBucket()
        {
        return this.bucket;
        }

    private String object;
    @Override
    public String getObject()
        {
        return this.object;
        }

    @Override
    public IvoaS3DataResource makeBean(URIBuilder builder)
        {
        return fillBean(
            new IvoaS3DataResource().meta(
                this.makeMeta(
                    builder
                    )
                )
            );
        }

    protected IvoaS3DataResource fillBean(final IvoaS3DataResource bean)
        {
        super.fillBean(bean);
        // TODO fill in the Amazon fields
        return bean;
        }
    }

