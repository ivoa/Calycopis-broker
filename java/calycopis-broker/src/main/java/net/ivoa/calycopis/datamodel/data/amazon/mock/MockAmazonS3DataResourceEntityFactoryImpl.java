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
 * AIMetrics: [
 *     {
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
package net.ivoa.calycopis.datamodel.data.amazon.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.data.AbstractDataResourceValidator;
import net.ivoa.calycopis.datamodel.data.amazon.AmazonS3DataResourceEntity;
import net.ivoa.calycopis.datamodel.data.amazon.AmazonS3DataResourceEntityFactoryImpl;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;

/**
 *
 */
@Slf4j
@Component
public class MockAmazonS3DataResourceEntityFactoryImpl
extends AmazonS3DataResourceEntityFactoryImpl
implements MockAmazonS3DataResourceEntityFactory
    {

    private final MockAmazonS3DataResourceEntityRepository repository;

    @Autowired
    public MockAmazonS3DataResourceEntityFactoryImpl(
        final MockAmazonS3DataResourceEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AmazonS3DataResourceEntity> select(final UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    @Override
    public AmazonS3DataResourceEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceEntity storage,
        final AbstractDataResourceValidator.Result result
        ){
        return this.repository.save(
            new MockAmazonS3DataResourceEntity(
                session,
                storage,
                result
                )
            );
        }
    }
