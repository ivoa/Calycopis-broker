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

package net.ivoa.calycopis.datamodel.data;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.functional.validator.AbstractValidatorImpl;
import net.ivoa.calycopis.spring.model.IvoaAbstractDataResource;

/**
 * 
 */
@Slf4j
public abstract class AbstractDataResourceValidatorImpl
extends AbstractValidatorImpl<IvoaAbstractDataResource, AbstractDataResourceEntity>
implements AbstractDataResourceValidator
    {

    private final AbstractDataStorageLinker storageLinker ;
    
    /**
     * 
     */
    public AbstractDataResourceValidatorImpl(
        final AbstractDataStorageLinker storageLinker
        ){
        super();
        this.storageLinker = storageLinker ;
        }

    /**
     * Check our context for for a duplicate resource.
     * 
     */
    protected boolean duplicateCheck(
        final IvoaAbstractDataResource requested,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;
        AbstractDataResourceValidator.Result duplicate = context.findDataValidatorResult(
            requested
            );
        if (duplicate != null)
            {
            context.addWarning(
                "urn:duplicate-resource",
                "Duplicate data resource found [${requested}][${duplicate}]",
                Map.of(
                    "requested",
                    context.makeDataValidatorResultKey(requested),
                    "duplicate",
                    context.makeDataValidatorResultKey(duplicate)
                    )
                );
            success = false ;
            }
        return success;
        }

    /**
     * Find (or create) the corresponding storage resource.
     *  
     */
    protected AbstractStorageResourceValidator.Result linkStorage(
        final IvoaAbstractDataResource requested,
        final IvoaAbstractDataResource validated,
        final OfferSetRequestParserContext context
        ){
        return this.storageLinker.linkStorage(
            requested,
            validated,
            context
            );
        }
    }
   