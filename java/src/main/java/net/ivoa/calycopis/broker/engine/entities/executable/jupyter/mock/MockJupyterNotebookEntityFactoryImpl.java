/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock;

import lombok.extern.slf4j.Slf4j;
import net.ivoa.calycopis.broker.engine.entities.component.AbstractEntityRepository;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableEntity;
import net.ivoa.calycopis.broker.engine.entities.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.broker.engine.entities.executable.jupyter.JupyterNotebookEntityFactoryImpl;
import net.ivoa.calycopis.broker.engine.entities.session.simple.SimpleExecutionSessionEntity;

/**
 *
 */
@Slf4j
public class MockJupyterNotebookEntityFactoryImpl
extends JupyterNotebookEntityFactoryImpl
implements MockJupyterNotebookEntityFactory
    {

    /**
     * Public constructor used by our Platform.
     * 
     */
    public MockJupyterNotebookEntityFactoryImpl(
        final AbstractEntityRepository<AbstractExecutableEntity> repository
        ){
        super(
            repository
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
