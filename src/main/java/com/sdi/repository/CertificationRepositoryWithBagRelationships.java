package com.sdi.repository;

import com.sdi.domain.Certification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CertificationRepositoryWithBagRelationships {
    Optional<Certification> fetchBagRelationships(Optional<Certification> certification);

    List<Certification> fetchBagRelationships(List<Certification> certifications);

    Page<Certification> fetchBagRelationships(Page<Certification> certifications);
}
