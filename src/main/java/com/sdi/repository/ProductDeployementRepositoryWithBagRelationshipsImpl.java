package com.sdi.repository;

import com.sdi.domain.ProductDeployement;
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
public class ProductDeployementRepositoryWithBagRelationshipsImpl implements ProductDeployementRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PRODUCTDEPLOYEMENTS_PARAMETER = "productDeployements";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ProductDeployement> fetchBagRelationships(Optional<ProductDeployement> productDeployement) {
        return productDeployement.map(this::fetchCertifications);
    }

    @Override
    public Page<ProductDeployement> fetchBagRelationships(Page<ProductDeployement> productDeployements) {
        return new PageImpl<>(
            fetchBagRelationships(productDeployements.getContent()),
            productDeployements.getPageable(),
            productDeployements.getTotalElements()
        );
    }

    @Override
    public List<ProductDeployement> fetchBagRelationships(List<ProductDeployement> productDeployements) {
        return Optional.of(productDeployements).map(this::fetchCertifications).orElse(Collections.emptyList());
    }

    ProductDeployement fetchCertifications(ProductDeployement result) {
        return entityManager
            .createQuery(
                "select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.certifications where productDeployement.id = :id",
                ProductDeployement.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProductDeployement> fetchCertifications(List<ProductDeployement> productDeployements) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, productDeployements.size()).forEach(index -> order.put(productDeployements.get(index).getId(), index));
        List<ProductDeployement> result = entityManager
            .createQuery(
                "select productDeployement from ProductDeployement productDeployement left join fetch productDeployement.certifications where productDeployement in :productDeployements",
                ProductDeployement.class
            )
            .setParameter(PRODUCTDEPLOYEMENTS_PARAMETER, productDeployements)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
