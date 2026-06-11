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

package net.ivoa.calycopis.broker.engine.functional.processing;

/**
 * An external action that is performed outside of any database transactions.
 * This means that the action may take a non-trivial amount of time without locking up the database and preventing other transactions from proceeding.
 * 
 */
public interface ProcessingAction
    {
    
    /**
     * Perform the action.
     * 
     */
    public void process();

    /**
     * Placeholder action that does nothing.
     * 
     */
    public static final ProcessingAction NO_ACTION = new ProcessingAction()
        {
        @Override
        public void process()
            {
            }
        };
    }
