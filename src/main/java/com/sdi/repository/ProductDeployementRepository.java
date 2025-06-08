package com.sdi.repository;

import com.sdi.domain.ProductDeployement;
import java.util.List;
import java.util.Optional;

import com.sdi.repository.projection.ProductDeployementSummaryProjection;
import com.sdi.service.dto.ProductDeployementSummaryDTO;
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
        value = "select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.product left join fetch productDeployement.client",
        countQuery = "select count(productDeployement) from ProductDeployement productDeployement"
    )
    Page<ProductDeployement> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.product left join fetch productDeployement.client left join fetch productDeployement.certifications"
    )
    List<ProductDeployement> findAllWithToOneRelationships();

    @Query(
        "select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.product left join fetch productDeployement.client where productDeployement.id =:id"
    )
    Optional<ProductDeployement> findOneWithToOneRelationships(@Param("id") Long id);


//    @Query("SELECT new com.sdi.service.dto.ProductDeployementSummaryDTO(" +
//        "pd.refContract, pd.product.name, pdd.productVersion.version, pdd.deployementType.type, pdd.startDeployementDate) " +
//        "FROM ProductDeployementDetail pdd " +
//        "JOIN pdd.productDeployement pd " +
//        "JOIN pd.product " +
//        "LEFT JOIN pdd.productVersion " +
//        "LEFT JOIN pdd.deployementType " +
//        "WHERE pd.client.id = :clientId")
//    List<ProductDeployementSummaryDTO> findDeployementSummariesByClientId(@Param("clientId") Long clientId);

    @Query("SELECT pd.refContract AS refContract, " +
        "pd.product.name AS product, " +
        "pdd.productVersion.version AS version, " +
        "pdd.deployementType.type AS deployementType, " +
        "pdd.startDeployementDate AS startDeployementDate " +
        "FROM ProductDeployementDetail pdd " +
        "JOIN pdd.productDeployement pd " +
        "JOIN pd.product " +
        "LEFT JOIN pdd.productVersion " +
        "LEFT JOIN pdd.deployementType " +
        "WHERE pd.client.id = :clientId")
    List<ProductDeployementSummaryProjection> findDeployementSummariesByClientId(@Param("clientId") Long clientId);

    Long countByProductId(Long productId);
    Long countByClientId(Long clientId);
    List<ProductDeployement> findByClientId(Long clientId);
}
