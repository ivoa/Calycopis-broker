/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.jupyter.mock;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface MockJupyterNotebookEntityRepository
extends JpaRepository<MockJupyterNotebookEntity, UUID>
    {
    }
