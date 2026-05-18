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
 *       "value": 15,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.volume.simple;

import java.net.URI;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.volume.AbstractVolumeMountEntityImpl;
import net.ivoa.calycopis.broker.engine.util.URIBuilder;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleVolumeMount;
import net.ivoa.calycopis.schema.spring.model.IvoaSimpleVolumeMount.ModeEnum;

/**
 * A SimpleVolumeMount Entity.
 *
 */
@Slf4j
@Entity
@Table(
    name = "simplevolumemounts"
    )
public class SimpleVolumeMountEntityImpl
extends AbstractVolumeMountEntityImpl
implements SimpleVolumeMount
    {
    
    @Override
    public URI getKind()
        {
        return SimpleVolumeMount.TYPE_DISCRIMINATOR;
        }

    /**
     * Protected constructor for JPA entities.
     *
     */
    protected SimpleVolumeMountEntityImpl()
        {
        super();
        }

    /**
     * Protected constructor used by our factory.
     *
     */
    protected SimpleVolumeMountEntityImpl(
        final AbstractComputeResourceEntity computeResource,
        final AbstractDataResourceEntity        dataResource,
        final SimpleVolumeMountValidator.Result result
        ){
        super(
            computeResource,
            dataResource,
            result.getObject().getMeta()
            );
        this.init(
            result
            );
        }

    /**
     * Protected constructor used by our factory.
     *
     */
    public SimpleVolumeMountEntityImpl(
        final AbstractComputeResourceEntity computeResource,
        final AbstractStorageResourceEntityImpl storageResource,
        final SimpleVolumeMountValidator.Result result
        ){
        super(
            computeResource,
            storageResource,
            result.getObject().getMeta()
            );
        this.init(
            result
            );
        }
    

    protected void init(final SimpleVolumeMountValidator.Result result)
        {
        if (result.getObject() != null)
            {
            if (result.getObject() instanceof IvoaSimpleVolumeMount)
                {
                IvoaSimpleVolumeMount simple = (IvoaSimpleVolumeMount) result.getObject();
                this.mode = simple.getMode();
                this.path = simple.getPath();
                }
            else {
                log.error(
                    "Unexpected type [{}] for validator result getObject()",
                    result.getObject().getClass().getSimpleName()
                    );
                }
            }
        else {
            log.error(
                "Null validator result getObject()"
                );
            }
        }
    
    private ModeEnum mode;
    @Override
    public ModeEnum getMode()
        {
        return this.mode;
        }

    private String path;
    @Override
    public String getPath()
        {
        return this.path;
        }

    @Override
    public IvoaSimpleVolumeMount makeBean(final URIBuilder uribuilder)
        {
        return this.fillBean(
            new IvoaSimpleVolumeMount().meta(
                this.makeMeta(
                    uribuilder
                    )
                )
            );
        }
    
    protected IvoaSimpleVolumeMount fillBean(final IvoaSimpleVolumeMount bean)
        {
        super.fillBean(
            bean
            );
        bean.setPath(path);
        bean.setMode(mode);
        if (this.getDataResource() != null)
            {
            bean.setResource(
                this.getDataResource().getUuid().toString()
                );
            }
        if (this.getStorageResource() != null)
            {
            bean.setResource(
                this.getStorageResource().getUuid().toString()
                );
            }
        return bean;
        }
    }

