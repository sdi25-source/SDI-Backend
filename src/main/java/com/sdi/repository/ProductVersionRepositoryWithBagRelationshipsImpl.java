package com.sdi.repository;

import com.sdi.domain.ProductVersion;
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
public class ProductVersionRepositoryWithBagRelationshipsImpl implements ProductVersionRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PRODUCTVERSIONS_PARAMETER = "productVersions";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ProductVersion> fetchBagRelationships(Optional<ProductVersion> productVersion) {
        return productVersion.map(this::fetchModuleVersions).map(this::fetchInfraComponentVersions).map(this::fetchInfraComponents);
    }

    @Override
    public Page<ProductVersion> fetchBagRelationships(Page<ProductVersion> productVersions) {
        return new PageImpl<>(
            fetchBagRelationships(productVersions.getContent()),
            productVersions.getPageable(),
            productVersions.getTotalElements()
        );
    }

    @Override
    public List<ProductVersion> fetchBagRelationships(List<ProductVersion> productVersions) {
        return Optional.of(productVersions)
            .map(this::fetchModuleVersions)
            .map(this::fetchInfraComponentVersions)
            .map(this::fetchInfraComponents)
            .orElse(Collections.emptyList());
    }

    ProductVersion fetchModuleVersions(ProductVersion result) {
        return entityManager
            .createQuery(
                "select productVersion from ProductVersion productVersion left join fetch productVersion.moduleVersions where productVersion.id = :id",
                ProductVersion.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProductVersion> fetchModuleVersions(List<ProductVersion> productVersions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, productVersions.size()).forEach(index -> order.put(productVersions.get(index).getId(), index));
        List<ProductVersion> result = entityManager
            .createQuery(
                "select productVersion from ProductVersion productVersion left join fetch productVersion.moduleVersions where productVersion in :productVersions",
                ProductVersion.class
            )
            .setParameter(PRODUCTVERSIONS_PARAMETER, productVersions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    ProductVersion fetchInfraComponentVersions(ProductVersion result) {
        return entityManager
            .createQuery(
                "select productVersion from ProductVersion productVersion left join fetch productVersion.infraComponentVersions where productVersion.id = :id",
                ProductVersion.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProductVersion> fetchInfraComponentVersions(List<ProductVersion> productVersions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, productVersions.size()).forEach(index -> order.put(productVersions.get(index).getId(), index));
        List<ProductVersion> result = entityManager
            .createQuery(
                "select productVersion from ProductVersion productVersion left join fetch productVersion.infraComponentVersions where productVersion in :productVersions",
                ProductVersion.class
            )
            .setParameter(PRODUCTVERSIONS_PARAMETER, productVersions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    ProductVersion fetchInfraComponents(ProductVersion result) {
        return entityManager
            .createQuery(
                "select productVersion from ProductVersion productVersion left join fetch productVersion.infraComponents where productVersion.id = :id",
                ProductVersion.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProductVersion> fetchInfraComponents(List<ProductVersion> productVersions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, productVersions.size()).forEach(index -> order.put(productVersions.get(index).getId(), index));
        List<ProductVersion> result = entityManager
            .createQuery(
                "select productVersion from ProductVersion productVersion left join fetch productVersion.infraComponents where productVersion in :productVersions",
                ProductVersion.class
            )
            .setParameter(PRODUCTVERSIONS_PARAMETER, productVersions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
