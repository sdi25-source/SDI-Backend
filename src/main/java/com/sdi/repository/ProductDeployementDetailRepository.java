package com.sdi.repository;

import com.sdi.domain.ProductDeployementDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductDeployementDetail entity.
 *
 * When extending this class, extend ProductDeployementDetailRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProductDeployementDetailRepository
    extends ProductDeployementDetailRepositoryWithBagRelationships, JpaRepository<ProductDeployementDetail, Long> {
    default Optional<ProductDeployementDetail> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<ProductDeployementDetail> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<ProductDeployementDetail> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select productDeployementDetail from ProductDeployementDetail productDeployementDetail left join fetch productDeployementDetail.productDeployement left join fetch productDeployementDetail.productVersion left join fetch productDeployementDetail.deployementType",
        countQuery = "select count(productDeployementDetail) from ProductDeployementDetail productDeployementDetail"
    )
    Page<ProductDeployementDetail> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select productDeployementDetail from ProductDeployementDetail productDeployementDetail left join fetch productDeployementDetail.productDeployement left join fetch productDeployementDetail.productVersion left join fetch productDeployementDetail.deployementType"
    )
    List<ProductDeployementDetail> findAllWithToOneRelationships();

    @Query(
        "select productDeployementDetail from ProductDeployementDetail productDeployementDetail left join fetch productDeployementDetail.productDeployement left join fetch productDeployementDetail.productVersion left join fetch productDeployementDetail.deployementType where productDeployementDetail.id =:id"
    )
    Optional<ProductDeployementDetail> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("SELECT pdd FROM ProductDeployementDetail pdd WHERE pdd.productVersion.id = :id")
    List<ProductDeployementDetail> findProductDeployementDetailByProductVersionId(Long id);

}
