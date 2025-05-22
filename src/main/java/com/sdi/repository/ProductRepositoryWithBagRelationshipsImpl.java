package com.sdi.repository;

import com.sdi.domain.Product;
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
public class ProductRepositoryWithBagRelationshipsImpl implements ProductRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PRODUCTS_PARAMETER = "products";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Product> fetchBagRelationships(Optional<Product> product) {
        return product.map(this::fetchProductLines).map(this::fetchCertifications).map(this::fetchModules);
    }

    @Override
    public Page<Product> fetchBagRelationships(Page<Product> products) {
        return new PageImpl<>(fetchBagRelationships(products.getContent()), products.getPageable(), products.getTotalElements());
    }

    @Override
    public List<Product> fetchBagRelationships(List<Product> products) {
        return Optional.of(products)
            .map(this::fetchProductLines)
            .map(this::fetchCertifications)
            .map(this::fetchModules)
            .orElse(Collections.emptyList());
    }

    Product fetchProductLines(Product result) {
        return entityManager
            .createQuery("select product from Product product left join fetch product.productLines where product.id = :id", Product.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Product> fetchProductLines(List<Product> products) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, products.size()).forEach(index -> order.put(products.get(index).getId(), index));
        List<Product> result = entityManager
            .createQuery(
                "select product from Product product left join fetch product.productLines where product in :products",
                Product.class
            )
            .setParameter(PRODUCTS_PARAMETER, products)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Product fetchCertifications(Product result) {
        return entityManager
            .createQuery("select product from Product product left join fetch product.certifications where product.id = :id", Product.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Product> fetchCertifications(List<Product> products) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, products.size()).forEach(index -> order.put(products.get(index).getId(), index));
        List<Product> result = entityManager
            .createQuery(
                "select product from Product product left join fetch product.certifications where product in :products",
                Product.class
            )
            .setParameter(PRODUCTS_PARAMETER, products)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Product fetchModules(Product result) {
        return entityManager
            .createQuery("select product from Product product left join fetch product.modules where product.id = :id", Product.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Product> fetchModules(List<Product> products) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, products.size()).forEach(index -> order.put(products.get(index).getId(), index));
        List<Product> result = entityManager
            .createQuery("select product from Product product left join fetch product.modules where product in :products", Product.class)
            .setParameter(PRODUCTS_PARAMETER, products)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
