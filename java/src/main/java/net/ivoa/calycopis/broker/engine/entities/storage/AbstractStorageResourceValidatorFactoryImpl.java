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
 * AIMetrics: [
 *     {
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 15,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.broker.engine.entities.storage;

import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.broker.engine.functional.validator.ValidatorFactoryImpl;
import net.ivoa.calycopis.schema.spring.model.IvoaAbstractStorageResource;

/**
 * An AbstractStorageResourceValidatorFactory implementation.
 * 
 */
public class AbstractStorageResourceValidatorFactoryImpl
extends ValidatorFactoryImpl<IvoaAbstractStorageResource, AbstractStorageResourceEntity>
implements AbstractStorageResourceValidatorFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public AbstractStorageResourceValidatorFactoryImpl()
        {
        super();
        }

    @Override
    public void unknown(
        final OfferSetRequestParserContext context,
        final IvoaAbstractStorageResource resource
        ){
        unknown(
            context,
            resource.getKind(),
            resource.getMeta()
            );
        }
    }
