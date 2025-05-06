package com.sdi.repository;

import com.sdi.domain.ProductVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProductVersionRepositoryWithBagRelationships {
    Optional<ProductVersion> fetchBagRelationships(Optional<ProductVersion> productVersion);

    List<ProductVersion> fetchBagRelationships(List<ProductVersion> productVersions);

    Page<ProductVersion> fetchBagRelationships(Page<ProductVersion> productVersions);
}
