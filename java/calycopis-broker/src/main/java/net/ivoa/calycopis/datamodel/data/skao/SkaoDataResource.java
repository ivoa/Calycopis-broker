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

package net.ivoa.calycopis.datamodel.data.skao;

import java.net.URI;
import java.util.List;

import net.ivoa.calycopis.datamodel.data.ivoa.IvoaDataResource;
import net.ivoa.calycopis.spring.model.IvoaSkaoDataResourceBlock.ObjecttypeEnum;

/**
 * Public interface for an IVOA DataResource.
 *
 */
public interface SkaoDataResource
    extends IvoaDataResource
    {
    /**
     * The OpenAPI type identifier.
     *
     */
    public static final URI TYPE_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/EB/schema/v1.0/types/data/skao-data-resource-1.0") ;

    public String getNamespace();
    public String getObjectid();
    public ObjecttypeEnum getObjectType();
    public Long getDataSize();
    public String getChecksumType();
    public String getChecksumValue();
 
    public static interface Replica
        {
        public String getRseName();
        public String getDataUrl();
        }
    public List<Replica> getReplicas();
    }

