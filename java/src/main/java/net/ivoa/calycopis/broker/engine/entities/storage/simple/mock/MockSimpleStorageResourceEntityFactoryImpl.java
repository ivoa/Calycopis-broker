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
package net.ivoa.calycopis.broker.engine.entities.storage.simple.mock;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceEntity;
import net.ivoa.calycopis.broker.engine.entities.storage.AbstractStorageResourceValidator;
import net.ivoa.calycopis.broker.engine.entities.storage.simple.SimpleStorageResourceEntityFactoryImpl;

/**
 *
 */
@Slf4j
public class MockSimpleStorageResourceEntityFactoryImpl
extends SimpleStorageResourceEntityFactoryImpl
implements MockSimpleStorageResourceEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public MockSimpleStorageResourceEntityFactoryImpl(
        final AbstractEntityRepository<AbstractStorageResourceEntity> repository
        ){
        super(
            repository
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
