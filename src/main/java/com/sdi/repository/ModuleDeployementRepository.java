package com.sdi.repository;

import com.sdi.domain.ModuleDeployement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ModuleDeployement entity.
 */
@Repository
public interface ModuleDeployementRepository extends JpaRepository<ModuleDeployement, Long> {
    default Optional<ModuleDeployement> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ModuleDeployement> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ModuleDeployement> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select moduleDeployement from ModuleDeployement moduleDeployement left join fetch moduleDeployement.moduleVersion",
        countQuery = "select count(moduleDeployement) from ModuleDeployement moduleDeployement"
    )
    Page<ModuleDeployement> findAllWithToOneRelationships(Pageable pageable);

    @Query("select moduleDeployement from ModuleDeployement moduleDeployement left join fetch moduleDeployement.moduleVersion")
    List<ModuleDeployement> findAllWithToOneRelationships();

    @Query(
        "select moduleDeployement from ModuleDeployement moduleDeployement left join fetch moduleDeployement.moduleVersion where moduleDeployement.id =:id"
    )
    Optional<ModuleDeployement> findOneWithToOneRelationships(@Param("id") Long id);
}
