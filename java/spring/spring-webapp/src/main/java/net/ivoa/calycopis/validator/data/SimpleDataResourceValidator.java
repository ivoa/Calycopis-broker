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
package net.ivoa.calycopis.validator.data;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.data.AbstractDataResourceEntity;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorTools;

/**
 * A validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public class SimpleDataResourceValidator
extends ValidatorTools<IvoaAbstractDataResource, AbstractDataResourceEntity>
implements Validator<IvoaAbstractDataResource, AbstractDataResourceEntity>
    {

    @Override
    public Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());
        if (requested instanceof IvoaSimpleDataResource)
            {
            return validate(
                (IvoaSimpleDataResource) requested,
                state
                );
            }
        else {
            return continueResult();
            }
        }

    /**
     * Validate a simple data resource.
     *
     */
    public Validator.Result<IvoaAbstractDataResource, AbstractDataResourceEntity> validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaSimpleDataResource result = new IvoaSimpleDataResource();

        String name = trimString(
            requested.getName()
            );
        String location = trimString(
            requested.getLocation()
            );

        if ((location == null) || (location.isEmpty()))
            {
            state.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "Data location required"
                );
            success = false ;
            }
        
        //
        // Check for a storage location.
        //

        //
        // Everything is good.
        // Create our result and add it to our state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            result.setName(name);
            result.setName(location);
            state.addDataResource(
                result
                );
            return acceptResult(
                result
                );
            }
        //
        // Something wasn't right, fail the validation.
        else {
            state.valid(false);
            return failResult();
            }
        }
    }
