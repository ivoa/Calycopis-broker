/**
 * 
 */
package net.ivoa.calycopis.datamodel.executable.jupyter;

import net.ivoa.calycopis.datamodel.executable.AbstractExecutableEntityFactory;
import net.ivoa.calycopis.datamodel.executable.AbstractExecutableValidator;
import net.ivoa.calycopis.datamodel.session.simple.SimpleExecutionSessionEntity;

/**
 * 
 */
public interface JupyterNotebookEntityFactory
extends AbstractExecutableEntityFactory
    {

    /**
     * Create and save a new JupyterNotebookEntity.
     *
     */
    public JupyterNotebookEntity create(
        final SimpleExecutionSessionEntity session,
        final AbstractExecutableValidator.Result result
        );

    }
