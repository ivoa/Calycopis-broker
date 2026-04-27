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
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class URIBuilderImpl implements URIBuilder
    {

    /**
     * 
     */
    public URIBuilderImpl(final URI requestURL, final URI requestURI, final String contextpath)
        {
        this.requestURL  = requestURL  ;
        this.requestURI  = requestURI  ;
        if (contextpath.endsWith("/"))
            {
            this.contextpath = contextpath ; 
            }
        else {
            this.contextpath = contextpath + "/"; 
            }
        }

    private URI requestURL ;
    private URI requestURI ;
    private String contextpath;

    @Override
    public URI buildURI(final URI path, final UUID uuid)
        {
        URI result = requestURL.resolve(
            this.contextpath
            ).resolve(path)
                .resolve(
                    uuid.toString()
                    );
        return result ;
        }
    }
