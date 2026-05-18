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

package net.ivoa.calycopis.broker.engine.entities.data.amazon;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.schema.spring.model.IvoaS3DataResource;

/**
 * An AmazonS3DataResourceEntity implementation.
 *
 */
@Entity
@Table(
    name = "s3dataresources"
    )
public abstract class AmazonS3DataResourceEntityImpl
extends AbstractDataResourceEntity
implements AmazonS3DataResource
    {

    @Override
    public URI getKind()
        {
        return AmazonS3DataResource.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor for JPA entities.
     *
     */
    protected AmazonS3DataResourceEntityImpl()
        {
        super();
        }

    /**
     * Protected constructor used by derived classes.
     *
     */
    protected AmazonS3DataResourceEntityImpl(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AmazonS3DataResourceValidator.Result result
        ){
        super(
            session,
            storage,
            result
            );
        
        final IvoaS3DataResource validated = (IvoaS3DataResource) result.getObject();

        this.endpoint = validated.getEndpoint();
        this.template = validated.getTemplate();
        this.bucket   = validated.getBucket();
        this.object   = validated.getObject();
        }

    private String endpoint;
    @Override
    public String getS3Endpoint()
        {
        return this.endpoint;
        }

    private String template;
    @Override
    public String getS3Template()
        {
        return this.template;
        }

    private String bucket;
    @Override
    public String getS3BucketName()
        {
        return this.bucket;
        }

    private String object;
    @Override
    public String getS3ObjectName()
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

