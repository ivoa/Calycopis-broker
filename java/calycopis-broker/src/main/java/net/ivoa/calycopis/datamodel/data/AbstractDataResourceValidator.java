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

package net.ivoa.calycopis.datamodel.data;

import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;

/**
 * Public interface for DataResource validators and results.
 * 
 */
public interface AbstractDataResourceValidator
extends Validator<IvoaAbstractDataResource, AbstractDataResourceEntity>
    {
    /**
     * Public interface for a validator result.
     * 
     */
    public interface Result
    extends Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> 
        {
        // TODO Keep references to the storage resource for this data.
        /**
         * Build an entity based on a validation result. 
         *
         */
        public AbstractDataResourceEntity build(final SimpleExecutionSessionEntity session);
        }

    /**
     * Validate a component.
     *
     */
    public ResultEnum validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        );

    /**
     * Simple Bean implementation of a DataResourceValidator result.
     * TODO Move this to AbstractDataResourceValidatorImpl, and include a factory method that can be inherited.
     * 
     */
    public static abstract class ResultBean
    extends Validator.ResultBean<IvoaAbstractDataResource, AbstractDataResourceEntity>
    implements Result
        {
        /**
         * Public constructor.
         * 
         */
        public ResultBean(final ResultEnum result)
            {
            super(result);
            }

        /**
         * Public constructor.
         * 
         */
        public ResultBean(
            final ResultEnum result,
            final IvoaAbstractDataResource object
            ){
            super(
                result,
                object,
                (object != null) ? object.getMeta() : null
                );
            }

        @Override
        // Here because we need to create Results with just a status and no entity
        public AbstractDataResourceEntity build(final SimpleExecutionSessionEntity session)
            {
            return null;
            }
        }
    }
