/**
 *
 */
package net.ivoa.calycopis.datamodel.executable;

import org.springframework.stereotype.Repository;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityRepository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
@Repository
public interface AbstractExecutableEntityRepository
extends LifecycleComponentEntityRepository<AbstractExecutableEntity>
    {

    }
