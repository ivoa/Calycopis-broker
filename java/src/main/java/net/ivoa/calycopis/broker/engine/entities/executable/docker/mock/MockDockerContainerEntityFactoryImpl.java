/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.docker.mock;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.executable.docker.DockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;

/**
 *
 */
@Slf4j
public class MockDockerContainerEntityFactoryImpl
extends DockerContainerEntityFactoryImpl
implements MockDockerContainerEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public MockDockerContainerEntityFactoryImpl(
        final AbstractEntityRepository<AbstractExecutableEntity> repository
        ){
        super(
            repository
            );
        }

    @Override
    public MockDockerContainerEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        return this.repository.save(
            new MockDockerContainerEntity(
                session,
                result
                )
            );
        }
    }
