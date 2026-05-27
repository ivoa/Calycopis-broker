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
package net.ivoa.calycopis.broker.engine.functional.validator;

import java.util.Map;
import java.util.UUID;

import net.ivoa.calycopis.broker.engine.entities.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.schema.spring.model.IvoaComponentMetadata;

/**
 * Provides a set of tools for validators to use.
 * 
 *  
 */
public interface ValidatorTools
    {

    /**
     * Null-safe String trim.
     *
     */
    public static String trim(String string)
        {
        if (string != null)
            {
            string = string.trim();
            }
        return string;
        }

    /**
     * Non empty String.
     * https://english.stackexchange.com/questions/102771/not-empty-set-in-one-word
     * @return Either the trimmed String or null if the trimmed String is empty. 
     * 
     */
    public static String notEmpty(String string)
        {
        if (string != null)
            {
            string = string.trim();
            if (string.isEmpty())
                {
                return null;
                }
            }
        return string;
        }
    
    /**
     * Validate a metadata block.
     * 
     */
    public static IvoaComponentMetadata makeMeta(
        final IvoaComponentMetadata requested,
        final OfferSetRequestParserContext context
        ){

        final IvoaComponentMetadata meta = new IvoaComponentMetadata();
        
        if (requested != null)
            {
            meta.uuid(
                requested.getUuid()
                );
            meta.name(
                notEmpty(
                    requested.getName()
                    )
                );
            }
        
        return meta;
        }
    
    /**
     * Null-safe UUID toString.
     * 
     */
    public static String string(final UUID uuid)
        {
        if (null != uuid)
            {
            return uuid.toString();
            }
        else {
            return null ;
            }
        }

    /**
     * Test value to trigger a failure.
     * 
     */
    public static final String BAD_VALUE_TRIGGER = "bad-value-trigger";

    /**
     * Check for a bad value.
     * 
     */
    public static boolean isBadValueCheck(
        final String requested,
        final OfferSetRequestParserContext context
        ){
        return isBadValueCheck(
            requested,
            BAD_VALUE_TRIGGER,
            context
            );
        }

    /**
     * Check for a bad value.
     * 
     */
    public static boolean notBadValueCheck(
        final String requested,
        final OfferSetRequestParserContext context
        ){
        return notBadValueCheck(
            requested,
            BAD_VALUE_TRIGGER,
            context
            );
        }
    
    /**
     * Check for a bad value.
     * 
     */
    public static boolean isBadValueCheck(
        final String requested,
        final String trigger,
        final OfferSetRequestParserContext context
        ){
        return ! notBadValueCheck(
            requested,
            trigger,
            context
            );
        }

    /**
     * Check for a bad value.
     * 
     */
    public static boolean notBadValueCheck(
        final String requested,
        final String trigger,
        final OfferSetRequestParserContext context
        ){
        boolean success = true ;

        if (requested == null)
            {
            return true ;
            }
        else {
            String value = notEmpty(
                requested
                );
    
            if (trigger.equals(value))
                {
                context.addWarning(
                    "urn:test-trigger-failed",
                    "Bad value detected [${value}]",
                    Map.of(
                        "value",
                        value
                        )
                    );
                success = false ;
                }
            
            return success ;
            }
        }
    }
