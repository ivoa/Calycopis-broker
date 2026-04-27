/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.docker.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.docker.DockerContainerEntityFactoryImpl;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;

/**
 *
 */
@Slf4j
@Component
public class MockDockerContainerEntityFactoryImpl
extends DockerContainerEntityFactoryImpl
implements MockDockerContainerEntityFactory
    {
    
    private final MockDockerContainerEntityRepository repository;
    
    @Autowired
    public MockDockerContainerEntityFactoryImpl(
        final MockDockerContainerEntityRepository repository
        ){
        super();
        this.repository = repository;
        }

    @Override
    public Optional<AbstractExecutableEntity> select(final UUID uuid)
        {
        return Optional.of(
            this.repository.findById(uuid).get()
            );
        }

    @Override
    public MockDockerContainerEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        MockDockerContainerEntity entity = this.repository.save(
            new MockDockerContainerEntity(
                session,
                result
                )
            );
        return entity ;
        }
    }
