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
 *
 */

package net.ivoa.calycopis.datamodel.data.mock;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator.Result;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidatorFactory;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResource;
import net.ivoa.calycopis.functional.factory.FactoryBaseImpl;
import net.ivoa.calycopis.functional.validator.Validator.ResultEnum;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;
import net.ivoa.calycopis.spring.model.IvoaAbstractStorageResource;
import net.ivoa.calycopis.spring.model.IvoaComponentMetadata;
import net.ivoa.calycopis.spring.model.IvoaSimpleStorageResource;

/**
 * 
 */
@Slf4j
@Component
public class MockDataStorageLinkerImpl
extends FactoryBaseImpl
implements MockDataStorageLinker
    {
    /**
     * A factory for StorageResource Validators.
     *
     */
    private final AbstractStorageResourceValidatorFactory storageValidators;

    /**
     * 
     */
    public MockDataStorageLinkerImpl(
        final AbstractStorageResourceValidatorFactory storageValidators
        ){
        this.storageValidators = storageValidators ;
        }

    @Override
    public Result linkStorage(
        final IvoaAbstractDataResource requested,
        final IvoaAbstractDataResource validated,
        final OfferSetRequestParserContext context
        ){
        
        //
        // Check for a validated storage resource.
        if (requested.getStorage() != null)
            {
            AbstractStorageResourceValidator.Result found = context.findStorageValidatorResult(
                requested.getStorage()
                );

            if (found != null)
                {
                return acceptResult(
                    found,
                    requested,
                    validated,
                    context
                    );
                }
            else {
                context.addWarning(
                    "urn:resource-not-found",
                    "Unable to find storage resource [${ref}]",
                    Map.of(
                        "ref",
                        requested.getStorage()
                        )
                    );
                }
            }
            
        else {
            // TODO Replace this with the default storage pool for this platform.
            IvoaSimpleStorageResource template = new IvoaSimpleStorageResource()
                .kind(
                    SimpleStorageResource.TYPE_DISCRIMINATOR
                    )                    
                .meta(
                    new IvoaComponentMetadata().name(
                        "Storage for [" + context.makeDataValidatorResultKey(requested) + "]"
                        ).uuid(
                            UUID.randomUUID()
                            )
                );

            //
            // Validate the new StorageResource.
            // TODO Better if the validate method returned the Result directly.
            storageValidators.validate(
                template,
                context
                );
            
            //
            // Find the validation result in the context.
            // TODO Better if the validate method returned the Result directly.
            AbstractStorageResourceValidator.Result created = context.findStorageValidatorResult(
                template
                );

            //
            // If the new storage was accepted, add a reference to our data resource.
            // TODO Check the result state is ACCEPTED rather than null
            if (created != null)
                {
                return acceptResult(
                    created,
                    requested,
                    validated,
                    context
                    );
                }
            else {
                log.warn("Storage validation failed");
                context.addWarning(
                    "urn:storage-required",
                    "Unable to assign storage resource"
                    );
                }
            }

        return new AbstractStorageResourceValidator.ResultBean(
            ResultEnum.FAILED
            );
        }

    
    public AbstractStorageResourceValidator.Result acceptResult(
        final AbstractStorageResourceValidator.Result result, 
        final IvoaAbstractDataResource requested,
        final IvoaAbstractDataResource validated,
        final OfferSetRequestParserContext context
        ){

        if (ResultEnum.ACCEPTED.equals(result.getEnum()))
            {
            // TODO Simplify this bit by saving the key in the validator Result.
            IvoaAbstractStorageResource resource = result.getObject(); 
            if (null != resource)
                {
                validated.setStorage(
                    context.makeStorageValidatorResultKey(
                        resource 
                        )
                    );
                return result;
                }
            else {
                log.error("Storage result has null object [{}]", result);
                context.addWarning(
                    "urn:storage-required",
                    "Unable to assign storage resource",
                    Map.of(
                        "storageref",
                        requested.getStorage()
                        )
                    );
                }
            }
        else {
            log.warn("Unexpected storage result state [{}]", result.getEnum());
            context.addWarning(
                "urn:storage-required",
                "Unable to assign storage resource",
                Map.of(
                    "storageref",
                    requested.getStorage()
                    )
                );
            }
        
        return new AbstractStorageResourceValidator.ResultBean(
            ResultEnum.FAILED
            );
        }
    }
