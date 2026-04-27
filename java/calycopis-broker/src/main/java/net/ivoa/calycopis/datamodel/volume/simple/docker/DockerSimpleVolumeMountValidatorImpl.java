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
 * AIMetrics: []
 *
 */
package net.ivoa.calycopis.datamodel.volume.simple.docker;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceEntityFactory;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntityFactory;
import net.ivoa.calycopis.datamodel.volume.simple.SimpleVolumeMountEntityFactory;
import net.ivoa.calycopis.datamodel.volume.simple.SimpleVolumeMountValidatorImpl;
import net.ivoa.calycopis.spring.model.IvoaSimpleVolumeMount;

/**
 * A Validator implementation for mock volume mounts.
 *
 */
@Slf4j
public class DockerSimpleVolumeMountValidatorImpl
extends SimpleVolumeMountValidatorImpl
implements DockerSimpleVolumeMountValidator
    {
    /**
     *
     */
    public DockerSimpleVolumeMountValidatorImpl(
        final SimpleVolumeMountEntityFactory volumeMountFactory,
        final AbstractDataResourceEntityFactory dataResourceFactory,
        final AbstractStorageResourceEntityFactory storageResourceFactory
        ){
        super(
            volumeMountFactory,
            dataResourceFactory,
            storageResourceFactory
            );
        }

    @Override
    protected Long getPrepareDuration(IvoaSimpleVolumeMount validated)
        {
        return 0L;
        }

    @Override
    protected Long getReleaseDuration(IvoaSimpleVolumeMount validated)
        {
        return 0L;
        }
    }
