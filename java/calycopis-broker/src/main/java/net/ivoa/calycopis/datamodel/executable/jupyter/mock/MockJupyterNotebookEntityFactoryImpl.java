/**
 *
 */
package net.ivoa.calycopis.datamodel.executable.jupyter.mock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.executable.jupyter.JupyterNotebookEntityFactoryImpl;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;

/**
 *
 */
@Slf4j
@Component
public class MockJupyterNotebookEntityFactoryImpl
extends JupyterNotebookEntityFactoryImpl
implements MockJupyterNotebookEntityFactory
    {

    private final MockJupyterNotebookEntityRepository repository;

    @Autowired
    public MockJupyterNotebookEntityFactoryImpl(
        final MockJupyterNotebookEntityRepository repository
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
    public MockJupyterNotebookEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        ){
        MockJupyterNotebookEntity entity = this.repository.save(
            new MockJupyterNotebookEntity(
                session,
                result
                )
            );
        return entity;
        }
    }
