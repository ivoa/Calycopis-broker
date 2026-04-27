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
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 25,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.executable;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;

/**
 *
 */
@Slf4j
public abstract class AbstractExecutableEntityFactoryImpl
extends FactoryBaseImpl
implements AbstractExecutableEntityFactory
    {

    private AbstractExecutableEntityRepository repository;

    @Autowired
    public AbstractExecutableEntityFactoryImpl(final AbstractExecutableEntityRepository repository)
        {
        super();
        this.repository = repository;
        }

    // TODO This is needed because of the Optional<>.
    // Get rid of of the Optional<> and this whole class goes away,
    // because the DockerContainer and JupyterNotebook factories
    // can implement the AbstractExecutableEntityFactory interface.
    @Override
    public Optional<AbstractExecutableEntity> select(UUID uuid)
        {
        return repository.findById(uuid);
        }
    }
