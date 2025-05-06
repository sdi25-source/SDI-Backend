package com.sdi.repository;

import com.sdi.domain.ProductDeployementDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProductDeployementDetailRepositoryWithBagRelationships {
    Optional<ProductDeployementDetail> fetchBagRelationships(Optional<ProductDeployementDetail> productDeployementDetail);

    List<ProductDeployementDetail> fetchBagRelationships(List<ProductDeployementDetail> productDeployementDetails);

    Page<ProductDeployementDetail> fetchBagRelationships(Page<ProductDeployementDetail> productDeployementDetails);
}
