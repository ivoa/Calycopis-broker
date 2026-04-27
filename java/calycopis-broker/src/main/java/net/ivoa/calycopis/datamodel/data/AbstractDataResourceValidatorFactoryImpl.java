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
 *       "value": 40,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */
package net.ivoa.calycopis.datamodel.data;

import org.springframework.stereotype.Component;

import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.functional.validator.ValidatorFactoryImpl;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;

/**
 * A factory implementation for DataResource validators.
 * 
 */
@Component
public class AbstractDataResourceValidatorFactoryImpl
extends ValidatorFactoryImpl<IvoaAbstractDataResource, AbstractDataResourceEntity>
implements AbstractDataResourceValidatorFactory
    {

    /**
     * Default constructor.
     * Validators are registered externally by the Platform. 
     * 
     */
    public AbstractDataResourceValidatorFactoryImpl()
        {
        super();
        }
    
    @Override
    public void unknown(
        final OfferSetRequestParserContext context,
        final IvoaAbstractDataResource resource
        ){
        unknown(
            context,
            resource.getKind(),
            resource.getMeta()
            );
        }
    }
