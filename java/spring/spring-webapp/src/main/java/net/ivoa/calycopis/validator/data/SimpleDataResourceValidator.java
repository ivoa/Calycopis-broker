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

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.offerset.OfferSetRequestParserState;
import net.ivoa.calycopis.openapi.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.openapi.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleDataResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageResource;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageSize;
import net.ivoa.calycopis.openapi.model.IvoaSimpleStorageSizeRequested;
import net.ivoa.calycopis.validator.Validator;
import net.ivoa.calycopis.validator.ValidatorTools;
import net.ivoa.calycopis.validator.storage.StorageResourceValidator;

/**
 * A Validator implementation to handle simple data resources.
 * 
 */
@Slf4j
public class SimpleDataResourceValidator
extends ValidatorTools
implements DataResourceValidator
    {

    @Override
    public DataResourceValidator.Result validate(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaAbstractDataResource)");
        log.debug("Resource [{}][{}]", state.makeDataValidatorResultKey(requested), requested.getClass().getName());
        if (requested instanceof IvoaSimpleDataResource)
            {
            return validate(
                (IvoaSimpleDataResource) requested,
                state
                );
            }
        else {
            return new ResultBean(
                Validator.ResultEnum.CONTINUE
                );
            }
        }

    /**
     * Validate a simple data resource.
     *
     */
    public DataResourceValidator.Result validate(
        final IvoaSimpleDataResource requested,
        final OfferSetRequestParserState state
        ){
        log.debug("validate(IvoaSimpleDataResource)");
        log.debug("Resource [{}]", state.makeDataValidatorResultKey(requested));

        boolean success = true ;
        
        //
        // Check for a duplicate resource.
        DataResourceValidator.Result duplicate = state.findDataValidatorResult(
            requested
            );
        if (duplicate != null)
            {
            state.getOfferSetEntity().addWarning(
                "urn:duplicate-resource",
                "Duplicate data resource found [${requested}][${duplicate }]",
                Map.of(
                    "requested",
                    state.makeDataValidatorResultKey(requested),
                    "duplicate",
                    state.makeDataValidatorResultKey(duplicate)
                    )
                );
            success = false ;
            }
        
        //
        // Create our validated object.
        IvoaSimpleDataResource validated = new IvoaSimpleDataResource();

        validated.setUuid(
            requested.getUuid()
            );
        validated.setName(
            trim(requested.getName())
            );

        String location = trim(
            requested.getLocation()
            );
        validated.setLocation(location);
        if ((location == null) || (location.isEmpty()))
            {
            state.getOfferSetEntity().addWarning(
                "urn:missing-required-value",
                "Data location required"
                );
            success = false ;
            }
            
        //
        // Make a guess at the data size.
        // TODO Add optional size to abstract data resource.
        long size = 1024L;
        
        StorageResourceValidator.Result storageResult = null;
        //
        // If the data resource has a storage reference.
        if (requested.getStorage() != null)
            {
            // Try to find the storage resource.
            storageResult = state.findStorageValidatorResult(
                requested.getStorage()
                );
            //
            // If we found a storage resource.
            if (storageResult != null)
                {
                // 
                // Check the result has an object.
                IvoaAbstractStorageResource storageResource = storageResult.getObject(); 
                if (storageResource != null)
                    {
                    //
                    // Check the size is big enough.
                    if (storageResource instanceof IvoaSimpleStorageResource)
                        {
                        //
                        // Check the size is big enough.
                        Long min = ((IvoaSimpleStorageResource) storageResource).getSize().getRequested().getMin(); 
                        Long max = ((IvoaSimpleStorageResource) storageResource).getSize().getRequested().getMax();
                        if ((min >= size) && (max >= size))
                            {
                            log.debug("PASS : Storage is big enough [{}][{}][{}]", size, min, max);
                            }
                        else {
                            log.warn("FAIL : Storage is NOT big enough [{}][{}][{}]", size, min, max);
                            state.getOfferSetEntity().addWarning(
                                "urn:size-error",
                                "Storage resource [${storagename}][${storagesize}] is not big enough for data [${dataname}][${datasize}]",
                                Map.of(
                                    "storagename",
                                    state.makeStorageValidatorResultKey(storageResource),
                                    "storagesize",
                                    min,
                                    "dataname",
                                    state.makeDataValidatorResultKey(requested),
                                    "datasize",
                                    size
                                    )
                                );
                            success = false ;
                            }
                        }
                    else {
                        log.warn("Unexpected storage type [{}]", storageResource.getClass().getName());
                        }
                    }
                // If the storage result doesn't have an object.
                else {
                    log.error("StorageResult has null object [{}]", storageResult);
                    state.getOfferSetEntity().addWarning(
                        "urn:resource-not-found",
                        "Unable to find storage resource [${storageref}]",
                        Map.of(
                            "storageref",
                            requested.getStorage()
                            )
                        );
                    success = false ;
                    }
                }
            //
            // If we couldn't find the storage resource.
            else {
                state.getOfferSetEntity().addWarning(
                    "urn:resource-not-found",
                    "Unable to find storage resource [${storageref}]",
                    Map.of(
                        "storageref",
                        requested.getStorage()
                        )
                    );
                success = false ;
                }
            }
        //
        // If the data resource doesn't have a storage reference.
        else {
            // Create a new storage resource.
            // BUG we are filling in the requested fields, but this wasn't requested.
            IvoaSimpleStorageResource storageResource = new IvoaSimpleStorageResource();
            storageResource.setName("Storage for [" + state.makeDataValidatorResultKey(requested) + "]");
            storageResource.setSize(
                new IvoaSimpleStorageSize()
                );
            storageResource.getSize().setRequested(
                new IvoaSimpleStorageSizeRequested()
                );
            storageResource.getSize().getRequested().setMin(size);
            storageResource.getSize().getRequested().setMax(size);

            // TODO This should be passed up the tree for validation.
            storageResult = new StorageResourceValidator.ResultBean(
                ResultEnum.ACCEPTED,
                storageResource
                );
            //
            // If the new storage was accepted.
            if (ResultEnum.ACCEPTED.equals(storageResult.getEnum()))
                {
                validated.setStorage(
                    state.makeStorageValidatorResultKey(
                        storageResult
                        )
                    );
                }
            else {
                state.getOfferSetEntity().addWarning(
                    "urn:create-failed",
                    "Unable to create new storage resource",
                    Map.of(
                        "storageref",
                        state.makeStorageValidatorResultKey(
                            storageResource
                            )
                        )
                    );
                success = false ;
                }
            }
        //
        // Everything is good.
        // Create our result and add it to our state.
        // TODO Need to add a reference to the builder.
        if (success)
            {
            log.debug("Success - creating the Validator.Result.");
            // Add the validated result.
            DataResourceValidator.Result dataResult = new ResultBean(
                Validator.ResultEnum.ACCEPTED,
                validated
                );
            state.getValidatedOfferSetRequest().getResources().addDataItem(
                validated
                );
            state.addDataValidatorResult(
                dataResult
                );
            // Add the link between data and storage.
            state.addDataStorageResult(
                dataResult,
                storageResult
                );
            return dataResult;
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
