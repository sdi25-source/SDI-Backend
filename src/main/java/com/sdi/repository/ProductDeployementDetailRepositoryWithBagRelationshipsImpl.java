package com.sdi.repository;

import com.sdi.domain.ProductDeployementDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ProductDeployementDetailRepositoryWithBagRelationshipsImpl implements ProductDeployementDetailRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PRODUCTDEPLOYEMENTDETAILS_PARAMETER = "productDeployementDetails";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ProductDeployementDetail> fetchBagRelationships(Optional<ProductDeployementDetail> productDeployementDetail) {
        return productDeployementDetail.map(this::fetchInfraComponentVersions).map(this::fetchAllowedModuleVersions);
    }

    @Override
    public Page<ProductDeployementDetail> fetchBagRelationships(Page<ProductDeployementDetail> productDeployementDetails) {
        return new PageImpl<>(
            fetchBagRelationships(productDeployementDetails.getContent()),
            productDeployementDetails.getPageable(),
            productDeployementDetails.getTotalElements()
        );
    }

    @Override
    public List<ProductDeployementDetail> fetchBagRelationships(List<ProductDeployementDetail> productDeployementDetails) {
        return Optional.of(productDeployementDetails)
            .map(this::fetchInfraComponentVersions)
            .map(this::fetchAllowedModuleVersions)
            .orElse(Collections.emptyList());
    }

    ProductDeployementDetail fetchInfraComponentVersions(ProductDeployementDetail result) {
        return entityManager
            .createQuery(
                "select productDeployementDetail from ProductDeployementDetail productDeployementDetail left join fetch productDeployementDetail.infraComponentVersions where productDeployementDetail.id = :id",
                ProductDeployementDetail.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProductDeployementDetail> fetchInfraComponentVersions(List<ProductDeployementDetail> productDeployementDetails) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, productDeployementDetails.size()).forEach(index -> order.put(productDeployementDetails.get(index).getId(), index)
        );
        List<ProductDeployementDetail> result = entityManager
            .createQuery(
                "select productDeployementDetail from ProductDeployementDetail productDeployementDetail left join fetch productDeployementDetail.infraComponentVersions where productDeployementDetail in :productDeployementDetails",
                ProductDeployementDetail.class
            )
            .setParameter(PRODUCTDEPLOYEMENTDETAILS_PARAMETER, productDeployementDetails)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    ProductDeployementDetail fetchAllowedModuleVersions(ProductDeployementDetail result) {
        return entityManager
            .createQuery(
                "select productDeployementDetail from ProductDeployementDetail productDeployementDetail left join fetch productDeployementDetail.allowedModuleVersions where productDeployementDetail.id = :id",
                ProductDeployementDetail.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProductDeployementDetail> fetchAllowedModuleVersions(List<ProductDeployementDetail> productDeployementDetails) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, productDeployementDetails.size()).forEach(index -> order.put(productDeployementDetails.get(index).getId(), index)
        );
        List<ProductDeployementDetail> result = entityManager
            .createQuery(
                "select productDeployementDetail from ProductDeployementDetail productDeployementDetail left join fetch productDeployementDetail.allowedModuleVersions where productDeployementDetail in :productDeployementDetails",
                ProductDeployementDetail.class
            )
            .setParameter(PRODUCTDEPLOYEMENTDETAILS_PARAMETER, productDeployementDetails)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
