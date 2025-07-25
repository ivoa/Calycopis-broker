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

package net.ivoa.calycopis.datamodel.data.simple;

import java.util.Optional;
import java.util.UUID;

import net.ivoa.calycopis.datamodel.session.ExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.functional.factory.FactoryBase;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;

/**
 * A SimpleDataResource Factory.
 *
 */
public interface SimpleDataResourceEntityFactory
    extends FactoryBase
    {

    /**
     * Select a SimpleDataResource based on UUID.
     *
     */
    public Optional<SimpleDataResourceEntity> select(final UUID uuid);

    /**
     * Create a new SimpleDataResource based on a template.
     *
     */
    public SimpleDataResourceEntity create(final ExecutionSessionEntity session, final AbstractStorageResourceEntity storage, final IvoaSimpleDataResource template);

    }

