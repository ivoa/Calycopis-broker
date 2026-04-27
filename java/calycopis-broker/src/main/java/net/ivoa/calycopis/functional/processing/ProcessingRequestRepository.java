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

package net.ivoa.calycopis.functional.processing;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
public interface ProcessingRequestRepository
extends JpaRepository<ProcessingRequestEntity, UUID>
    {

    public UUID findByUuid(@Param("uuid") final UUID uuid) ;

    public void deleteByUuid(@Param("uuid") final UUID uuid) ;

    @Transactional
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
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
    public UUID selectNextRequest(@Param("service") final UUID service, @Param("kinds") final List<URI> kinds) ;

    @Modifying
    @Transactional
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
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
    public int updateNextRequest(@Param("service") final UUID service, @Param("kinds") final List<URI> kinds);
    
    }
