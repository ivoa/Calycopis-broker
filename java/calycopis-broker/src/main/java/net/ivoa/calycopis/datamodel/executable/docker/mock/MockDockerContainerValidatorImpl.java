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

package net.ivoa.calycopis.datamodel.executable.docker.mock;

import java.util.List;
import java.util.Map;

import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactory;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerValidatorImpl;
import net.ivoa.calycopis.datamodel.offerset.OfferSetRequestParserContext;
import net.ivoa.calycopis.spring.model.IvoaDockerContainer;

/**
 * 
 */
public class MockDockerContainerValidatorImpl
extends DockerContainerValidatorImpl
implements MockDockerContainerValidator
    {

    public MockDockerContainerValidatorImpl(final DockerContainerEntityFactory entityFactory)
        {
        super(entityFactory);
        }

    public static final List<String> PORT_PATH_BLACKLIST = List.of(
        "/badpath",
        "/alsobadpath"
        );

    @Override
    protected boolean validateNetworkPortPath(final String path, final OfferSetRequestParserContext context)
        {
        if (PORT_PATH_BLACKLIST.contains(path))
            {
            context.addWarning(
                "urn:invalid-value",
                "DockerContainer - network path is blacklisted [{}]",
                Map.of(
                    "value",
                    path
                    )
                );
            return false;
            }
        else {
            return true;
            }
        }

    public static final List<Integer> PORT_NUMBER_BLACKLIST = List.of(
        Integer.valueOf(1234),
        Integer.valueOf(5678)
        );

    @Override
    protected boolean validateNetworkPortNumber(final Integer portnum, final OfferSetRequestParserContext context)
        {
        if (PORT_NUMBER_BLACKLIST.contains(portnum))
            {
            context.addWarning(
                "urn:invalid-value",
                "DockerContainer - network port number is blacklisted [{}]",
                Map.of(
                    "value",
                    portnum
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
    protected Long getPrepareDuration(final IvoaDockerContainer validated)
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
    protected Long getReleaseDuration(final IvoaDockerContainer validated)
        {
        return DEFAULT_RELEASE_ESTIMATE ;
        }
    }
