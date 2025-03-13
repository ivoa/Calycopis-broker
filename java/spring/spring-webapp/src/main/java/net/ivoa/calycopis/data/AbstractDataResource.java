/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2025 University of Manchester.
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

package net.ivoa.calycopis.data;

import net.ivoa.calycopis.component.Component;
import net.ivoa.calycopis.execution.ExecutionSessionEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;

/**
 * 
 */
public interface AbstractDataResource
    extends Component
    {
    /**
     * Get the parent ExecutionSession.  
     *
     */
    public ExecutionSessionEntity getParent();

    /**
     * Get an Ivoa bean representation.
     *  
     */
    public IvoaAbstractDataResource getIvoaBean();

    }
