package com.sdi.repository;

import com.sdi.domain.ProductDeployement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProductDeployementRepositoryWithBagRelationships {
    Optional<ProductDeployement> fetchBagRelationships(Optional<ProductDeployement> productDeployement);

    List<ProductDeployement> fetchBagRelationships(List<ProductDeployement> productDeployements);

    Page<ProductDeployement> fetchBagRelationships(Page<ProductDeployement> productDeployements);
}
