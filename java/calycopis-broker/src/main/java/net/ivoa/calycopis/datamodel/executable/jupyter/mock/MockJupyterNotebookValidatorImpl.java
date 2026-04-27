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

package net.ivoa.calycopis.datamodel.executable.jupyter.mock;

import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntityFactory;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.spring.model.IvoaJupyterNotebook;

/**
 * 
 */
public class MockJupyterNotebookValidatorImpl
extends JupyterNotebookValidatorImpl
    implements MockJupyterNotebookValidator
    {

    public MockJupyterNotebookValidatorImpl(final JupyterNotebookEntityFactory entityFactory)
        {
        super(entityFactory);
        }

    public static final List<String> LOCATION_BLACKLIST = List.of(
            "http://example.com/blacklisted.ipynb",
            "http://example.com/blacklisted.ipynb"
        ); 

    @Override
    protected boolean validateLocation(String location, OfferSetRequestParserContext context)
        {
        if (LOCATION_BLACKLIST.contains(location))
            {
            context.addWarning(
                "urn:invalid-value",
                "JupyterNotebook - location is blacklisted [{}]",
                Map.of(
                    "value",
                    location
                    )
                );
            return false;
            }
        else {
            return true;
            }
        }

    /**
     * Default prepare duration, 30 seconds.
     * 
     */
    public static final Long DEFAULT_PREPARE_ESTIMATE = 30L;

    /**
     * Get the prepare duration for a resource.
     * Returns DEFAULT_PREPARE_ESTIMATE if the request does not specify a value.
     * 
     */
    protected Long getPrepareDuration(final IvoaJupyterNotebook validated)
        {
        Long duration = getPrepareDuration(
            validated.getSchedule()
            );
        if (duration != null)
            {
            return duration ;
            }
        else {
            return DEFAULT_PREPARE_ESTIMATE ;
            }
        }

    /**
     * Default release duration, 0 seconds.
     * 
     */
    public static final Long DEFAULT_RELEASE_ESTIMATE = 0L;

    /**
     * Get the release duration for a resource.
     * 
     */
    protected Long getReleaseDuration(final IvoaJupyterNotebook validated)
        {
        return DEFAULT_RELEASE_ESTIMATE ;
        }
    }
