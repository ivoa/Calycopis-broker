/**
 *
 */
package net.ivoa.calycopis.broker.engine.entities.executable.docker.mock;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface MockDockerContainerEntityRepository
extends JpaRepository<MockDockerContainerEntity, UUID>
    {
    }
