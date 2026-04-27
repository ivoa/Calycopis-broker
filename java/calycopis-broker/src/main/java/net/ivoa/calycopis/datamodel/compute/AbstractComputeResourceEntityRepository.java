/**
 *
 */
package net.ivoa.calycopis.datamodel.compute;

import org.springframework.stereotype.Repository;

import net.ivoa.calycopis.datamodel.component.LifecycleComponentEntityRepository;

/**
 * JpaRepository for DockerContainerEntity.
 *
 */
@Repository
public interface AbstractComputeResourceEntityRepository
extends LifecycleComponentEntityRepository<AbstractComputeResourceEntity>
    {

    }
