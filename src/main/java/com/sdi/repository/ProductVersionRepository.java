package com.sdi.repository;

import com.sdi.domain.ProductVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductVersion entity.
 *
 * When extending this class, extend ProductVersionRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProductVersionRepository extends ProductVersionRepositoryWithBagRelationships, JpaRepository<ProductVersion, Long> {
    default Optional<ProductVersion> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<ProductVersion> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<ProductVersion> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select productVersion from ProductVersion productVersion left join fetch productVersion.product",
        countQuery = "select count(productVersion) from ProductVersion productVersion"
    )
    Page<ProductVersion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select productVersion from ProductVersion productVersion left join fetch productVersion.product")
    List<ProductVersion> findAllWithToOneRelationships();

    @Query("select productVersion from ProductVersion productVersion left join fetch productVersion.product where productVersion.id =:id")
    Optional<ProductVersion> findOneWithToOneRelationships(@Param("id") Long id);

    Long countByProductId(Long productId);
}
