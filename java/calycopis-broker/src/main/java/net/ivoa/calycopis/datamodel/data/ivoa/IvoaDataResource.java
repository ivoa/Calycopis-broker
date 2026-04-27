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

package net.ivoa.calycopis.datamodel.data.ivoa;

import java.net.URI;

import net.ivoa.calycopis.datamodel.data.AbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaIvoaDataLinkItem;
import net.ivoa.calycopis.spring.model.IvoaIvoaObsCoreItem;

/**
 * Public interface for an IVOA DataResource.
 *
 */
public interface IvoaDataResource
    extends AbstractDataResource
    {
    /**
     * The OpenAPI type identifier.
     *
     */
    public static final URI TYPE_DISCRIMINATOR = URI.create("https://www.purl.org/ivoa.net/EB/schema/v1.0/types/data/ivoa-data-resource-1.0") ;

    /**
     * Get the data IVOA identifier.
     *
     */
    public URI getIvoid();

    /**
     * Inner class to represent the ObsCore metadata.
     * 
     */
    public static interface ObsCore
        {
        public String  getObsId();
        public String  getObsCollection();
        public String  getObsPublisherDid();
        public String  getObsCreatorDid();
        public String  getDataproductType();
        public Integer getCalibLevel();
        public String  getAccessUrl();
        public String  getAaccessFormat();
        
        public IvoaIvoaObsCoreItem getIvoaBean();
        }

    /**
     * Get the ObsCore metadata.
     * 
     */
    public ObsCore getObsCore();
    
    /**
     * Inner class to represent the DataLink metadata.
     * 
     */
    public static interface DataLink
        {
        public String  getID();
        public String  getAccessUrl();
        public String  getServiceDef();
        public String  getErrorMessage();
        public String  getDescription();
        public String  getSemantics();
        public String  getContentType();
        public Integer getContentLength();
        public String  getContentQualifier();
        public String  getLocalSemantics();
        public String  getLinkAuth();
        public String  getLinkAuthorized();
        
        public IvoaIvoaDataLinkItem getIvoaBean();
        }

    /**
     * Get the DataLink metadata.
     * 
     */
    public DataLink getDataLink();
    
    }

