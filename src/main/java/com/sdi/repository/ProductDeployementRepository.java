package com.sdi.repository;

import com.sdi.domain.ProductDeployement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductDeployement entity.
 */
@Repository
public interface ProductDeployementRepository extends JpaRepository<ProductDeployement, Long> {
    default Optional<ProductDeployement> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductDeployement> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductDeployement> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.client",
        countQuery = "select count(productDeployement) from ProductDeployement productDeployement"
    )
    Page<ProductDeployement> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.client")
    List<ProductDeployement> findAllWithToOneRelationships();

    @Query(
        "select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.client where productDeployement.id =:id"
    )
    Optional<ProductDeployement> findOneWithToOneRelationships(@Param("id") Long id);
}
