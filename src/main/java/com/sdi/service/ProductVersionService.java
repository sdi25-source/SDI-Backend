package com.sdi.service;

import com.sdi.domain.ProductVersion;
import com.sdi.repository.ProductVersionRepository;
import com.sdi.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductVersionService {

    private final ProductVersionRepository productVersionRepository;

    public ProductVersionService(ProductVersionRepository productVersionRepository) {
        this.productVersionRepository = productVersionRepository;
    }

    public ProductVersion save(ProductVersion productVersion) {
        if (productVersionRepository.existsByProductAndVersion(productVersion.getProduct(), productVersion.getVersion())) {
            throw new BadRequestAlertException("This product version already exists", "productVersion", "uniqueexists");
        }
        return productVersionRepository.save(productVersion);
    }
    // Autres méthodes CRUD si besoin
}
