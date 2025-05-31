package com.sdi.repository;

import com.sdi.domain.CertificationVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CertificationVersion entity.
 */
@Repository
public interface CertificationVersionRepository extends JpaRepository<CertificationVersion, Long> {
    default Optional<CertificationVersion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CertificationVersion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CertificationVersion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select certificationVersion from CertificationVersion certificationVersion left join fetch certificationVersion.certification",
        countQuery = "select count(certificationVersion) from CertificationVersion certificationVersion"
    )
    Page<CertificationVersion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select certificationVersion from CertificationVersion certificationVersion left join fetch certificationVersion.certification")
    List<CertificationVersion> findAllWithToOneRelationships();

    @Query(
        "select certificationVersion from CertificationVersion certificationVersion left join fetch certificationVersion.certification where certificationVersion.id =:id"
    )
    Optional<CertificationVersion> findOneWithToOneRelationships(@Param("id") Long id);
}
