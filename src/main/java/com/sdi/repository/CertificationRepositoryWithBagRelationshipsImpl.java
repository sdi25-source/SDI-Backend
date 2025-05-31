package com.sdi.repository;

import com.sdi.domain.Certification;
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
public class CertificationRepositoryWithBagRelationshipsImpl implements CertificationRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String CERTIFICATIONS_PARAMETER = "certifications";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Certification> fetchBagRelationships(Optional<Certification> certification) {
        return certification.map(this::fetchCertificationVersions);
    }

    @Override
    public Page<Certification> fetchBagRelationships(Page<Certification> certifications) {
        return new PageImpl<>(
            fetchBagRelationships(certifications.getContent()),
            certifications.getPageable(),
            certifications.getTotalElements()
        );
    }

    @Override
    public List<Certification> fetchBagRelationships(List<Certification> certifications) {
        return Optional.of(certifications).map(this::fetchCertificationVersions).orElse(Collections.emptyList());
    }

    Certification fetchCertificationVersions(Certification result) {
        return entityManager
            .createQuery(
                "select certification from Certification certification left join fetch certification.certificationVersions where certification.id = :id",
                Certification.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Certification> fetchCertificationVersions(List<Certification> certifications) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, certifications.size()).forEach(index -> order.put(certifications.get(index).getId(), index));
        List<Certification> result = entityManager
            .createQuery(
                "select certification from Certification certification left join fetch certification.certificationVersions where certification in :certifications",
                Certification.class
            )
            .setParameter(CERTIFICATIONS_PARAMETER, certifications)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
