package com.sdi.repository;

import com.sdi.domain.RequestOfChange;
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
public class RequestOfChangeRepositoryWithBagRelationshipsImpl implements RequestOfChangeRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String REQUESTOFCHANGES_PARAMETER = "requestOfChanges";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<RequestOfChange> fetchBagRelationships(Optional<RequestOfChange> requestOfChange) {
        return requestOfChange.map(this::fetchModuleVersions);
    }

    @Override
    public Page<RequestOfChange> fetchBagRelationships(Page<RequestOfChange> requestOfChanges) {
        return new PageImpl<>(
            fetchBagRelationships(requestOfChanges.getContent()),
            requestOfChanges.getPageable(),
            requestOfChanges.getTotalElements()
        );
    }

    @Override
    public List<RequestOfChange> fetchBagRelationships(List<RequestOfChange> requestOfChanges) {
        return Optional.of(requestOfChanges).map(this::fetchModuleVersions).orElse(Collections.emptyList());
    }

    RequestOfChange fetchModuleVersions(RequestOfChange result) {
        return entityManager
            .createQuery(
                "select requestOfChange from RequestOfChange requestOfChange left join fetch requestOfChange.moduleVersions where requestOfChange.id = :id",
                RequestOfChange.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<RequestOfChange> fetchModuleVersions(List<RequestOfChange> requestOfChanges) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, requestOfChanges.size()).forEach(index -> order.put(requestOfChanges.get(index).getId(), index));
        List<RequestOfChange> result = entityManager
            .createQuery(
                "select requestOfChange from RequestOfChange requestOfChange left join fetch requestOfChange.moduleVersions where requestOfChange in :requestOfChanges",
                RequestOfChange.class
            )
            .setParameter(REQUESTOFCHANGES_PARAMETER, requestOfChanges)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
