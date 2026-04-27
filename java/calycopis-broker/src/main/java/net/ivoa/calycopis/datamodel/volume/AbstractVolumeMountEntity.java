/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2026 University of Manchester.
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
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 30,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.volume;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import net.ivoa.calycopis.datamodel.component.ComponentEntity;
import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.util.URIBuilder;

/**
 *
 */
@Entity
@Table(
    name = "abstractvolumemounts"
    )
@Inheritance(
    strategy = InheritanceType.JOINED
    )
public abstract class AbstractVolumeMountEntity
extends ComponentEntity
implements AbstractVolumeMount
    {
    /**
     * Protected constructor.
     *
     */
    protected AbstractVolumeMountEntity()
        {
        super();
        }

    /**
     * Protected constructor.
     *
     */
    protected AbstractVolumeMountEntity(
        final AbstractComputeResourceEntity computeResource,
        final AbstractDataResourceEntity dataResource,
        final IvoaComponentMetadata meta
        ){
        super(meta);
        this.computeResource = computeResource;
        computeResource.addVolumeMount(
            this
            );
        this.dataResource = dataResource;
        this.dataResource.addVolumeMount(
            this
            );
        }

    /**
     * Protected constructor.
     *
     */
    protected AbstractVolumeMountEntity(
        final AbstractComputeResourceEntity computeResource,
        final AbstractStorageResourceEntity storageResource,
        final IvoaComponentMetadata meta
        ){
        super(meta);
        this.computeResource = computeResource;
        computeResource.addVolumeMount(
            this
            );
        this.storageResource = storageResource;
        this.storageResource.addVolumeMount(
            this
            );
        }
    
    @JoinColumn(name = "computeresource", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AbstractComputeResourceEntity computeResource;

    @Override
    public AbstractComputeResourceEntity getComputeResource()
        {
        return this.computeResource;
        }
    public void setComputeResource(final AbstractComputeResourceEntity computeResource)
        {
        this.computeResource = computeResource;
        }

    @JoinColumn(name = "dataresource", referencedColumnName = "uuid", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private AbstractDataResourceEntity dataResource;

    @Override
    public AbstractDataResourceEntity getDataResource()
        {
        return this.dataResource;
        }
    public void setDataResource(final AbstractDataResourceEntity dataResource)
        {
        this.dataResource = dataResource;
        }

    @JoinColumn(name = "storageresource", referencedColumnName = "uuid", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private AbstractStorageResourceEntity storageResource;

    @Override
    public AbstractStorageResourceEntity getStorageResource()
        {
        return this.storageResource;
        }
    public void setStorageResource(final AbstractStorageResourceEntity storageResource)
        {
        this.storageResource = storageResource;
        }

    public abstract IvoaAbstractVolumeMount makeBean(final URIBuilder uribuilder);
    
    protected IvoaAbstractVolumeMount fillBean(final IvoaAbstractVolumeMount bean)
        {
        bean.setKind(
            this.getKind()
            );
        return bean;
        }

    @Override
    protected URI getWebappPath()
        {
        return AbstractVolumeMount.WEBAPP_PATH;
        }
    }
