package com.sdi.repository;

import com.sdi.domain.ModuleVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ModuleVersion entity.
 *
 * When extending this class, extend ModuleVersionRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ModuleVersionRepository extends ModuleVersionRepositoryWithBagRelationships, JpaRepository<ModuleVersion, Long> {
    default Optional<ModuleVersion> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<ModuleVersion> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<ModuleVersion> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select moduleVersion from ModuleVersion moduleVersion left join fetch moduleVersion.module left join fetch moduleVersion.domaine",
        countQuery = "select count(moduleVersion) from ModuleVersion moduleVersion"
    )
    Page<ModuleVersion> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select moduleVersion from ModuleVersion moduleVersion left join fetch moduleVersion.module left join fetch moduleVersion.domaine"
    )
    List<ModuleVersion> findAllWithToOneRelationships();

    @Query(
        "select moduleVersion from ModuleVersion moduleVersion left join fetch moduleVersion.module left join fetch moduleVersion.domaine where moduleVersion.id =:id"
    )
    Optional<ModuleVersion> findOneWithToOneRelationships(@Param("id") Long id);
}
