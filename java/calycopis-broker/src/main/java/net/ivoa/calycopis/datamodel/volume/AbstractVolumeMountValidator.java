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
 *     },
 *     {
 *     "timestamp": "2026-03-25T14:45:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 5,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.datamodel.volume;

import net.ivoa.calycopis.datamodel.compute.AbstractComputeResourceEntity;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.validator.Validator;
import net.ivoa.calycopis.spring.model.IvoaAbstractVolumeMount;

/**
 * Public interface for VolumeMount validators and results.
 *
 */
public interface AbstractVolumeMountValidator
extends Validator<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
    {

    /**
     * Public interface for a validator result.
     *
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
        {
        /**
         * Build an entity based on a validation result.
         *
         */
        public AbstractVolumeMountEntity build(final AbstractComputeResourceEntity computeResource);
        }

    /**
     * Validate a component.
     *
     */
    public ResultEnum validate(
        final IvoaAbstractVolumeMount requested,
        final OfferSetRequestParserContext context
        );
    
    /**
     * Simple Bean implementation of a VolumeMountValidator result.
     *
     */
    public abstract static class ResultBean
    extends Validator.ResultBean<IvoaAbstractVolumeMount, AbstractVolumeMountEntity>
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
            final IvoaAbstractVolumeMount object
            ){
            super(
                result,
                object
                );
            }
        }
    }
