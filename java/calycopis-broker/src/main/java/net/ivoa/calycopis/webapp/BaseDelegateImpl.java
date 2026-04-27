/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
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
package net.ivoa.calycopis.webapp;

import java.net.URI;
import java.util.Optional;

import org.springframework.web.context.request.NativeWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.util.URIBuilder;
import net.ivoa.calycopis.util.URIBuilderImpl;

/**
 * Base class for our delegates.
 * Includes methods to get the base URL used in redirects.
 *
 */
@Slf4j
public class BaseDelegateImpl
    {

    private final NativeWebRequest request;

    public BaseDelegateImpl(NativeWebRequest request)
        {
        this.request = request ;
        }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(
            this.request
            );
        }

    public String getRequestURL()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString();
            }
        else {
            return "unknown";
            }
        }

    public String getRequestURI()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getRequestURI().toString();
            }
        else {
            return "unknown";
            }
        }

    public String getPathInfo()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getPathInfo();
            }
        else {
            return "unknown";
            }
        }

    public String getContextPath()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getServletContext().getContextPath();
            }
        else {
            return "unknown";
            }
        }

    public String getServletPath()
        {
        if (this.request != null)
            {
            return this.request.getNativeRequest(HttpServletRequest.class).getServletPath();
            }
        else {
            return "unknown";
            }
        }

    /**
     * Get the base URL needed to create redirect locations.
     * https://gist.github.com/beradrian/d66008b6c5a784185c29
     *
     */
    public URI getBaseUri()
        {
        String contextpath = this.getContextPath();
        URI requestURL  = URI.create(this.getRequestURL());
        URI base = requestURL.resolve(contextpath + "/");
        return base;
        }

    /**
     * Get a URI builder to create Component URLs.
     * 
     */
    public URIBuilder getURIBuilder()
        {
        return new URIBuilderImpl(
            this.getBaseUri(),
            this.getBaseUri(),
            this.getContextPath()
            );
        }
    }

