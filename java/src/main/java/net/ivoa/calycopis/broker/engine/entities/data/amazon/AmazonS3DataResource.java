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
 *
 */

package net.ivoa.calycopis.broker.engine.entities.data.amazon;

import java.net.URI;

import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResource;

/**
 * Public interface for an AmazonS3DataResource.
 *
 */
public interface AmazonS3DataResource
extends AbstractDataResource
    {

    /**
     * The OpenAPI type identifier.
     * 
     */
    public static final URI KIND_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/Calycopis-openapi/schema/v1.0/kinds/data/S3-data-resource.yaml") ;

    /**
     * Get the Amazon S3 service endpoint.
     *
     */
    public String getS3Endpoint();

    /**
     * Get the Amazon S3 URL template.
     *
     */
    public String getS3Template();

    /**
     * Get the Amazon S3 bucket name.
     *
     */
    public String getS3BucketName();

    /**
     * Get the Amazon S3 object name.
     *
     */
    public String getS3ObjectName();
    
    }

