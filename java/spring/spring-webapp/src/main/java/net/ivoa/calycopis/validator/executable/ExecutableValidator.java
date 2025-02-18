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

package net.ivoa.calycopis.validator.executable;

import net.ivoa.calycopis.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.openapi.model.IvoaAbstractExecutable;
import net.ivoa.calycopis.validator.Validator;

/**
 * Public interface for Executable validators and results.
 * 
 */
public interface ExecutableValidator
extends Validator<IvoaAbstractExecutable, AbstractExecutableEntity>
    {
    /**
     * Public interface for an ExecutableValidator result.
     * 
     */
    public static interface Result
    extends Validator.Result<IvoaAbstractExecutable, AbstractExecutableEntity> 
        {
        // A list of the compute resources this executable is deployed on ?.
        }

    /**
     * Simple Bean implementation of an ExecutableValidator result.
     * 
     */
    public static class ResultBean
    extends Validator.ResultBean<IvoaAbstractExecutable, AbstractExecutableEntity>
    implements Result
        {
        /**
         * Public constructor.
         * 
         */
        public ResultBean(ResultEnum result)
            {
            super(result);
            }

        /**
         * Public constructor.
         * 
         */
        public ResultBean(ResultEnum result, IvoaAbstractExecutable object)
            {
            super(
                result,
                object
                );
            }
        }
    }
