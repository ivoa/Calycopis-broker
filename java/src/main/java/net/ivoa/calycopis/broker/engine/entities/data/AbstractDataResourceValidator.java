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
 *     "timestamp": "2026-02-17T13:20:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 1,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.engine.entities.data;

import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.functional.validator.Validator;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractDataResource;

/**
 * Public interface for AbstractDataResource Validators.
 * 
 */
public interface AbstractDataResourceValidator
extends Validator<IvoaAbstractDataResource, AbstractDataResourceEntity>
    {
    /**
     * Validate a component.
     *
     */
    public ResultEnum validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        );

    /**
     * Public interface for a validator result.
     * 
     */
    public interface Result
    extends Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> 
        {
        /**
         * Build a DataResourceEntity based on the validation result. 
         *
         */
        public AbstractDataResourceEntity build(final SimpleExecutionSessionEntity session);
        }

    /**
     * Bean implementation of a validator result.
     * 
     */
    public static abstract class ResultBean
    extends Validator.ResultBean<IvoaAbstractDataResource, AbstractDataResourceEntity>
    implements AbstractDataResourceValidator.Result
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
         * Protected constructor with a ResultEnum and IvoaAbstractDataResource.
         * 
         */
        protected ResultBean(
            final ResultEnum result,
            final IvoaAbstractDataResource object
            ){
            super(
                result,
                object,
                object.getMeta()
                );
            }
        }
    }
