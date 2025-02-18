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
package net.ivoa.calycopis.validator.storage;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorTools;

/**
 * A Validator implementation to handle simple storage resources.
 * 
 */
@Slf4j
public class SimpleStorageResourceValidator
extends ValidatorTools
implements StorageResourceValidator
    {

    @Override
    public StorageResourceValidator.Result validate(
        final IvoaAbstractStorageResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaAbstractStorageResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());
        switch(requested)
            {
            case IvoaSimpleStorageResource simple:
                return validate(
                        simple,
                        state
                        );
            default:
                return new ResultBean(
                    Validator.ResultEnum.CONTINUE
                    );
            }
        }

    /**
     * Validate an IvoaSimpleStorageResource.
     *
     */
    public StorageResourceValidator.Result validate(
        final IvoaSimpleStorageResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaSimpleStorageResource)");
        log.debug("Resource [{}][{}]", requested.getName(), requested.getClass().getName());

        boolean success = true ;
        IvoaSimpleStorageResource validated = new IvoaSimpleStorageResource();

        //
        // Check for maximum limits,
        // Validate the lifetime values,
        //

        validated.setName(requested.getName());
        
        //
        // Everything is good.
        // Create our result and add it to our state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            log.debug("Success - creating the StorageResourceValidator.Result.");
            StorageResourceValidator.Result result = new ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                );
            state.getValidatedOfferSetRequest().getResources().addStorageItem(
                validated
                );
            state.addStorageResourceValidatorResult(
                result
                );
            return result;
            }
        //
        // Something wasn't right, fail the validation.
        else {
            state.valid(false);
            return new ResultBean(
                Validator.ResultEnum.FAILED
                );
            }
        }
    }
