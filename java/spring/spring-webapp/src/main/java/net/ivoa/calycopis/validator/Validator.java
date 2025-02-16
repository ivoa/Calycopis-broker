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
package net.ivoa.calycopis.validator;

import java.util.Map;

import net.ivoa.calycopis.offerset.OfferSetRequestParserState;

/**
 * Public interface for a validator.
 *  
 */
public interface Validator<ObjectType>
    {
    /**
     * Result enum for the validation process.
     * CONTINUE means this validator didn't recognise the object. 
     * ACCEPTED means this validator recognised and validated the object. 
     * FAILED means this validator recognised but failed the object.
     * 
     */
    enum ResultEnum{
        CONTINUE(),
        ACCEPTED(),
        FAILED();
        }

    /**
     * Public interface for a validation result.
     * 
     */
    public interface ResultSet<ResultType>
        {
        /**
         * Get the validation result enum.
         * 
         */
        public ResultEnum getEnum();

        /**
         * Get the validated object.
         * 
         */
        public ResultType getObject();

        }

    /**
     * Simple bean implementation of ResultSet.
     *  
     */
    public class ResultSetBean<ResultType>
    implements ResultSet<ResultType>
        {
        public ResultSetBean(final ResultEnum result)
            {
            this(result, null);
            }
        public ResultSetBean(final ResultEnum result, ResultType object)
            {
            this.result = result;
            this.object = object;
            }

        private ResultEnum result;
        @Override
        public ResultEnum getEnum()
            {
            return this.result;
            }

        private ResultType object;
        @Override
        public ResultType getObject()
            {
            return this.object;
            }
        }
    
    /**
     * Validate a request component.
     *
     */
    public Validator.ResultSet<ObjectType> validate(
        final ObjectType requested,
        final OfferSetRequestParserState state
        );
    }
