package com.sdi.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class RequestOfChangesDTO implements Serializable {

    private String title;
    private LocalDate createDate;
    private String productVersion;
    private  String product;
    private String customisationLevel;
    private String description;

    // No-arg constructor
    public RequestOfChangesDTO() {
    }

    // All-arg constructor
    public RequestOfChangesDTO(String title, LocalDate createDate, String product,String productVersion, String customisationLevel, String description
    ) {
        this.title = title;
        this.createDate = createDate;
        this.product = product;
        this.productVersion = productVersion;
        this.customisationLevel = customisationLevel;
        this.description = description;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCustomisationLevel() {
        return customisationLevel;
    }

    public void setCustomisationLevel(String customisationLevel) {
        this.customisationLevel = customisationLevel;
    }
}
