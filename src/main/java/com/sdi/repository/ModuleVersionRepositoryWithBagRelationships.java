package com.sdi.repository;

import com.sdi.domain.ModuleVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ModuleVersionRepositoryWithBagRelationships {
    Optional<ModuleVersion> fetchBagRelationships(Optional<ModuleVersion> moduleVersion);

    List<ModuleVersion> fetchBagRelationships(List<ModuleVersion> moduleVersions);

    Page<ModuleVersion> fetchBagRelationships(Page<ModuleVersion> moduleVersions);
}
