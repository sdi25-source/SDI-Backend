package com.sdi.repository;

import com.sdi.domain.InfraComponentVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InfraComponentVersion entity.
 */
@Repository
public interface InfraComponentVersionRepository extends JpaRepository<InfraComponentVersion, Long> {
    default Optional<InfraComponentVersion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InfraComponentVersion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InfraComponentVersion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select infraComponentVersion from InfraComponentVersion infraComponentVersion left join fetch infraComponentVersion.infraComponent",
        countQuery = "select count(infraComponentVersion) from InfraComponentVersion infraComponentVersion"
    )
    Page<InfraComponentVersion> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select infraComponentVersion from InfraComponentVersion infraComponentVersion left join fetch infraComponentVersion.infraComponent"
    )
    List<InfraComponentVersion> findAllWithToOneRelationships();

    @Query(
        "select infraComponentVersion from InfraComponentVersion infraComponentVersion left join fetch infraComponentVersion.infraComponent where infraComponentVersion.id =:id"
    )
    Optional<InfraComponentVersion> findOneWithToOneRelationships(@Param("id") Long id);
}
