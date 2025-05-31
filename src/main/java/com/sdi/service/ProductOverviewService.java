package com.sdi.service;

import com.sdi.domain.Module;
import com.sdi.domain.Product;
import com.sdi.domain.ProductDeployement;
import com.sdi.domain.ProductVersion; // Assumed entity
import com.sdi.repository.ModuleRepository;
import com.sdi.repository.ProductDeployementRepository;
import com.sdi.repository.ProductRepository;
import com.sdi.repository.ProductVersionRepository; // Assumed repository
import com.sdi.service.dto.ProductOverview;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductOverviewService {

    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final ModuleRepository moduleRepository;
    private final ProductDeployementRepository productDeployementRepository;

    public ProductOverviewService(
        ProductRepository productRepository,
        ProductVersionRepository productVersionRepository,
        ModuleRepository moduleRepository,
        ProductDeployementRepository productDeployementRepository
    ) {
        this.productRepository = productRepository;
        this.productVersionRepository = productVersionRepository;
        this.moduleRepository = moduleRepository;
        this.productDeployementRepository = productDeployementRepository;
    }

    public List<ProductOverview> getProductsOverview() {
        // Fetch all products
        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> {
            // Count versions for the product
            Long versionCount = productVersionRepository.countByProductId(product.getId());

            // Count modules for the product
            Long moduleCount = (long) product.getModules().size();

            // Count deployments for the product
            Long deploymentCount = productDeployementRepository.countByProductId(product.getId());

            // Determine badgeClass based on deployment count
            String badgeClass = determineBadgeClass(deploymentCount);

            // Determine icon based on product name
            String icon = determineIcon(product.getName());

            // Map to ProductOverview DTO
            return new ProductOverview(
                product.getName(),
                badgeClass,
                icon,
                versionCount.intValue(),
                moduleCount.intValue(),
                deploymentCount.intValue()
            );
        }).collect(Collectors.toList());
    }

    private String determineBadgeClass(Long deploymentCount) {
        if (deploymentCount <= 5) {
            return "low-deployments"; // Light color, e.g., light green
        } else if (deploymentCount <= 10) {
            return "medium-deployments"; // Medium color, e.g., yellow
        } else if (deploymentCount <= 20) {
            return "high-deployments"; // Darker color, e.g., blue
        } else {
            return "very-high-deployments"; // Darkest color, e.g., dark blue
        }
    }

    private String determineIcon(String productName) {
        String name = productName.toLowerCase();
        if (name.contains("payment")) {
            return "bi bi-credit-card";
        } else if (name.contains("customer") || name.contains("portal")) {
            return "bi bi-people";
        } else if (name.contains("booking")) {
            return "bi bi-calendar-check";
        } else if (name.contains("analytics") || name.contains("dashboard")) {
            return "bi bi-bar-chart";
        } else if (name.contains("mobile")) {
            return "bi bi-phone";
        } else if (name.contains("security")) {
            return "bi bi-lock";
        } else if (name.contains("inventory")) {
            return "bi bi-box-seam";
        } else {
            return "bi bi-box"; // Default icon
        }
    }
}
