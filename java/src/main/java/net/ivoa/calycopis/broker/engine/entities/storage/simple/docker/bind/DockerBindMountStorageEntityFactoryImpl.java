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
 *
 */

package net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.bind;

import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntityImpl;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidator.Result;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.docker.DockerSimpleStorageResourceEntityFactoryImpl;

/**
 * 
 */
public class DockerBindMountStorageEntityFactoryImpl
extends DockerSimpleStorageResourceEntityFactoryImpl
implements DockerBindMountStorageEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public DockerBindMountStorageEntityFactoryImpl(
        final DockerBindMountStorageEntityRepository repository
        ){
        super(repository);
        }

    @Override
    public DockerBindMountStorageEntityImpl create(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result,
        final String path
        ){
        DockerBindMountStorageEntityImpl entity = this.repository.save(
            new DockerBindMountStorageEntityImpl(
                session,
                result,
                path
                )
            );
        return entity ;
        }

    @Override
    public AbstractStorageResourceEntityImpl create(
        final SimpleExecutionSessionEntity session,
        final Result result
        ){
        throw new UnsupportedOperationException(
            "Creating DockerBindMountStorageEntity with no path is not supported"
            );
        }
    }
