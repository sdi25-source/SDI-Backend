package com.sdi.repository;

import com.sdi.domain.RequestOfChange;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface RequestOfChangeRepositoryWithBagRelationships {
    Optional<RequestOfChange> fetchBagRelationships(Optional<RequestOfChange> requestOfChange);

    List<RequestOfChange> fetchBagRelationships(List<RequestOfChange> requestOfChanges);

    Page<RequestOfChange> fetchBagRelationships(Page<RequestOfChange> requestOfChanges);
}
