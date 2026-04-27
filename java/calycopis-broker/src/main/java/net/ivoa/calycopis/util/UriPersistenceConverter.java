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

package net.ivoa.calycopis.util;

import java.net.URI;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * https://stackoverflow.com/questions/33781855/how-to-handle-properties-of-type-uri-with-spring-data-jpa
 * https://stackoverflow.com/a/33785032
 */
@Slf4j
@Converter(autoApply = true)
public class UriPersistenceConverter implements AttributeConverter<URI, String>
    {
    public static final URI INVALID_URI_REPLACEMENT = URI.create("uri://invalid-uri") ; 
    
    public UriPersistenceConverter()
        {
        super();
        }

    @Override
    public String convertToDatabaseColumn(final URI value)
        {
        if (null != value)
            {
            return value.toString();
            }
        else {
            return null ;
            }
        }

    @Override
    public URI convertToEntityAttribute(final String value)
        {
        if (null != value)
            {
            try {
                return URI.create(value);
                }
            catch (Exception ouch)
                {
                log.error("Unable to convert String to URI [{}][{}]", value, ouch.getMessage());
                return INVALID_URI_REPLACEMENT;
                }
            }
        else {
            return null ;
            }
        }
    }
