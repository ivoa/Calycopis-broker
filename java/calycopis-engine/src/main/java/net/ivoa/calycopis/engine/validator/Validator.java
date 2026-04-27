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
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-04-27T13:46:34",
 *     "name": "Copilot",
 *     "version": "unknown",
 *     "model": "claude-sonnet-4.5",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.engine.validator;

import net.ivoa.calycopis.engine.component.ComponentMetadata;

/**
 * Framework-independent validator interface.
 *
 * A {@code Validator} inspects a request object of type {@code RequestType}
 * and decides whether it can process it (ACCEPTED), must reject it (FAILED),
 * or should defer to the next registered validator (CONTINUE).
 *
 * This is the engine-layer replacement for the broker's
 * {@code net.ivoa.calycopis.functional.validator.Validator} interface.
 * It uses the engine's {@link ComponentMetadata} POJO rather than
 * {@code IvoaComponentMetadata} from the calycopis-spring schema package.
 *
 * @param <RequestType> the type of the inbound request object being validated
 * @param <ContextType> the type of the parsing context used to accumulate results
 */
public interface Validator<RequestType, ContextType>
    {
    /**
     * Outcome of a single validation attempt.
     *
     * <ul>
     *   <li>CONTINUE — this validator did not recognise the object; try the next one.</li>
     *   <li>ACCEPTED — this validator recognised and accepted the object.</li>
     *   <li>FAILED   — this validator recognised but rejected the object.</li>
     * </ul>
     */
    enum ResultEnum
        {
        CONTINUE,
        ACCEPTED,
        FAILED;
        }

    /**
     * Validate a request object and return a result.
     *
     * @param requested the inbound request object to validate
     * @param context   the accumulation context for this offer-set request
     * @return the validation outcome
     */
    public ResultEnum validate(
        final RequestType requested,
        final ContextType context
        );

    /**
     * Contract for a successful validation result, carrying the validated
     * object, the associated entity, and timing metadata.
     *
     * @param <RequestType> the validated request type
     * @param <EntityType>  the JPA entity type produced by validation
     */
    public interface Result<RequestType, EntityType>
        {
        /**
         * Get the human-readable name for this validated component (used for
         * cross-reference keying).
         *
         */
        public String getName();

        /**
         * Get the validation outcome enum.
         *
         */
        public ResultEnum getEnum();

        /**
         * Get the validated request object.
         *
         */
        public RequestType getObject();

        /**
         * Get the engine ComponentMetadata extracted from the validated object.
         * Returns {@code null} if the result represents a FAILED or CONTINUE outcome.
         *
         */
        public ComponentMetadata getMeta();

        /**
         * Get the preparation duration in seconds for this resource.
         *
         */
        public Long getPrepareDuration();

        /**
         * Get the total preparation duration (including dependencies) in seconds.
         *
         */
        public Long getTotalPrepareDuration();

        /**
         * Get the release duration in seconds for this resource.
         *
         */
        public Long getReleaseDuration();
        }
    }
