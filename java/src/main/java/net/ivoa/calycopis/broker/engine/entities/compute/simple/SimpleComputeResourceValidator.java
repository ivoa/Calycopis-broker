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

package net.ivoa.calycopis.broker.engine.entities.compute.simple;

import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.compute.AbstractComputeResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.booking.compute.simple.SimpleComputeResourceOffer;
import net.ivoa.calycopis.openapi.spring.model.IvoaAbstractComputeResource;

/**
 * 
 */
public interface SimpleComputeResourceValidator
extends AbstractComputeResourceValidator
    {
    /**
     * Public interface for a validator result.
     * 
     */
    public static interface Result
    extends AbstractComputeResourceValidator.Result 
        {
        /**
         * Build an ComputeResourceEntity based on a resource offer.
         * 
         */
        public AbstractComputeResourceEntity build(final SimpleExecutionSessionEntity session, final SimpleComputeResourceOffer offer);
        }

    /**
     * Bean implementation of a validator result.
     * 
     */
    public abstract static class ResultBean
    extends AbstractComputeResourceValidator.ResultBean
    implements SimpleComputeResourceValidator.Result
        {
        /**
         * Protected constructor with just a ResultEnum.
         * Used to respond to a failed validation, where we don't have an object to return.
         * 
         */
        protected ResultBean(final ResultEnum result)
            {
            super(result);
            }

        /**
         * Public constructor with a ResultEnum and IvoaAbstractComputeResource.
         * 
         */
        public ResultBean(
            final ResultEnum result,
            final IvoaAbstractComputeResource object
            ){
            super(
                result,
                object
                );
            }
        }
    }
