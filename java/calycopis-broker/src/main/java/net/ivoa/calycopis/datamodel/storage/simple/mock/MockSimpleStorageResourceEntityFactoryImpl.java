/*
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
package net.ivoa.calycopis.datamodel.storage.simple.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.datamodel.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.datamodel.storage.simple.SimpleStorageResourceEntityFactoryImpl;

/**
 *
 */
@Slf4j
@Component
public class MockSimpleStorageResourceEntityFactoryImpl
extends SimpleStorageResourceEntityFactoryImpl
implements MockSimpleStorageResourceEntityFactory
    {

    private final MockSimpleStorageResourceEntityRepository repository;

    @Autowired
    public MockSimpleStorageResourceEntityFactoryImpl(
        final MockSimpleStorageResourceEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractStorageResourceEntity> select(final UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    @Override
    public MockSimpleStorageResourceEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractStorageResourceValidator.Result result
        ){
        MockSimpleStorageResourceEntity entity = this.repository.save(
            new MockSimpleStorageResourceEntity(
                session,
                result
                )
            );
        return entity ;
        }
    }
