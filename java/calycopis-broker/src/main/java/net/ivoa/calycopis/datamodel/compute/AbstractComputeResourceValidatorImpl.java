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

package net.ivoa.calycopis.datamodel.compute;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.spring.model.IvoaAbstractComputeResource;

/**
 * 
 */
@Slf4j
public abstract class AbstractComputeResourceValidatorImpl
extends AbstractValidatorImpl<IvoaAbstractComputeResource, AbstractComputeResourceEntity>
implements AbstractComputeResourceValidator
    {
    public AbstractComputeResourceValidatorImpl()
        {
        super();
        }
    }
