package com.sdi.service.dto;

import com.sdi.domain.DeployementType;
import com.sdi.domain.ProductVersion;
import java.io.Serializable;
import java.time.LocalDate;

public class ProductDeployementSummaryDTO implements Serializable {
    private String refContract;
    private String product;
    private String version;
    private String deployementType;
    private LocalDate startDeployementDate;

    public ProductDeployementSummaryDTO() {
    }

    public ProductDeployementSummaryDTO(String refContract, String product,
                                        String version, String deployementType,
                                        LocalDate startDeployementDate) {
        this.refContract = refContract;
        this.product = product;
        this.version = version;
        this.deployementType = deployementType;
        this.startDeployementDate = startDeployementDate;
    }

    public String getRefContract() {
        return refContract;
    }

    public void setRefContract(String refContract) {
        this.refContract = refContract;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeployementType() {
        return deployementType;
    }

    public void setDeployementType(String deployementType) {
        this.deployementType = deployementType;
    }

    public LocalDate getStartDeployementDate() {
        return startDeployementDate;
    }

    public void setStartDeployementDate(LocalDate startDeployementDate) {
        this.startDeployementDate = startDeployementDate;
    }
}
