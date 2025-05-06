package com.sdi.repository;

import com.sdi.domain.FeatureDeployement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FeatureDeployement entity.
 */
@Repository
public interface FeatureDeployementRepository extends JpaRepository<FeatureDeployement, Long> {
    default Optional<FeatureDeployement> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FeatureDeployement> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FeatureDeployement> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select featureDeployement from FeatureDeployement featureDeployement left join fetch featureDeployement.moduleDeployement",
        countQuery = "select count(featureDeployement) from FeatureDeployement featureDeployement"
    )
    Page<FeatureDeployement> findAllWithToOneRelationships(Pageable pageable);

    @Query("select featureDeployement from FeatureDeployement featureDeployement left join fetch featureDeployement.moduleDeployement")
    List<FeatureDeployement> findAllWithToOneRelationships();

    @Query(
        "select featureDeployement from FeatureDeployement featureDeployement left join fetch featureDeployement.moduleDeployement where featureDeployement.id =:id"
    )
    Optional<FeatureDeployement> findOneWithToOneRelationships(@Param("id") Long id);
}
