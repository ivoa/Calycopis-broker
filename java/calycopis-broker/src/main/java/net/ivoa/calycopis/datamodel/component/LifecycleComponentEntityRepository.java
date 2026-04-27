/**
 *
 */
package net.ivoa.calycopis.datamodel.component;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository for LifecycleComponentEntities.
 *
 */
@Repository
public interface LifecycleComponentEntityRepository<EntityType extends LifecycleComponentEntity>
    extends JpaRepository<EntityType, UUID>
    {

    }
