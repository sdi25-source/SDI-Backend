package com.sdi.repository;

import com.sdi.domain.ModuleVersion;
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
public class ModuleVersionRepositoryWithBagRelationshipsImpl implements ModuleVersionRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String MODULEVERSIONS_PARAMETER = "moduleVersions";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ModuleVersion> fetchBagRelationships(Optional<ModuleVersion> moduleVersion) {
        return moduleVersion.map(this::fetchFeatures);
    }

    @Override
    public Page<ModuleVersion> fetchBagRelationships(Page<ModuleVersion> moduleVersions) {
        return new PageImpl<>(
            fetchBagRelationships(moduleVersions.getContent()),
            moduleVersions.getPageable(),
            moduleVersions.getTotalElements()
        );
    }

    @Override
    public List<ModuleVersion> fetchBagRelationships(List<ModuleVersion> moduleVersions) {
        return Optional.of(moduleVersions).map(this::fetchFeatures).orElse(Collections.emptyList());
    }

    ModuleVersion fetchFeatures(ModuleVersion result) {
        return entityManager
            .createQuery(
                "select moduleVersion from ModuleVersion moduleVersion left join fetch moduleVersion.features where moduleVersion.id = :id",
                ModuleVersion.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ModuleVersion> fetchFeatures(List<ModuleVersion> moduleVersions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, moduleVersions.size()).forEach(index -> order.put(moduleVersions.get(index).getId(), index));
        List<ModuleVersion> result = entityManager
            .createQuery(
                "select moduleVersion from ModuleVersion moduleVersion left join fetch moduleVersion.features where moduleVersion in :moduleVersions",
                ModuleVersion.class
            )
            .setParameter(MODULEVERSIONS_PARAMETER, moduleVersions)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
