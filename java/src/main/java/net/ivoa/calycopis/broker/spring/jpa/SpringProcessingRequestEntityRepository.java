/*
 * <meta:header>
 *   <meta:licence>
 *     Copyright (c) 2026, University of Manchester (http://www.manchester.ac.uk/)
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
 *     along with this software. If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * AIMetrics: [
 *     {
 *     "timestamp": "2026-05-21T10:54:00",
 *     "name": "Cursor CLI",
 *     "version": "2026.02.13-41ac335",
 *     "model": "Claude 4.6 Opus (Thinking)",
 *     "contribution": {
 *       "value": 100,
 *       "units": "%"
 *       }
 *     }
 *   ]
 *
 */

package net.ivoa.calycopis.broker.spring.jpa;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.ivoa.calycopis.broker.engine.functional.processing.ProcessingRequestEntity;

/**
 * 
 */
@Repository
public interface SpringProcessingRequestEntityRepository
extends SpringAbstractEntityRepository<ProcessingRequestEntity>
    {

    public UUID findByUuid(@Param("uuid") final UUID uuid);

    public void deleteByUuid(@Param("uuid") final UUID uuid);

    @Transactional
    @Query(
        """
        SELECT
            p.uuid
        FROM
            ProcessingRequestEntity p
        WHERE
            p.service = :service
        AND
            p.kind IN :kinds
        AND
            p.activation < CURRENT_TIMESTAMP
        ORDER BY
            p.activation ASC
        LIMIT 1
        """
            )
    public UUID selectNextRequest(@Param("service") final UUID service, @Param("kinds") final Iterable<URI> iterable);

    @Modifying
    @Transactional
    @Query(
        """
        UPDATE
            ProcessingRequestEntity pe
        SET
            pe.service = :service
        WHERE
            pe.uuid = (
                SELECT
                    q.uuid
                FROM
                    ProcessingRequestEntity q
                WHERE
                    q.service IS NULL
                AND
                    q.kind IN :kinds
                AND
                    q.activation < CURRENT_TIMESTAMP
                ORDER BY
                    q.activation ASC
                LIMIT 1
                )
        """
        )
    public int updateNextRequest(@Param("service") final UUID service, @Param("kinds") final Iterable<URI> iterable);

    }
