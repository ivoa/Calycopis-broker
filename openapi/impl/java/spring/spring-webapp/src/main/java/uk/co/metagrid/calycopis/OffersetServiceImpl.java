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
 * Based on
 * https://www.geeksforgeeks.org/spring-boot-jparepository-with-example/
 * https://howtodoinjava.com/spring-boot/spring-boot-jparepository-example/
 *
 */
package uk.co.metagrid.calycopis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing Offersets.
 *
 */
@Service
public class OffersetServiceImpl
    implements OffersetService
    {

    private OffersetRepository repository;

    @Autowired
    public OffersetServiceImpl(final OffersetRepository repository)
        {
        super();
        this.repository = repository;
        }

    /**
     * Save an Offerset.
     *
     */
    public OffersetEntity save(OffersetEntity entity)
        {
        return repository.save(
            entity
            );
        }

    /**
     * List all the Offersets.
     *
     */
    public List<OffersetEntity> list()
        {
        return (List<OffersetEntity>) repository.findAll();
        }

    }

