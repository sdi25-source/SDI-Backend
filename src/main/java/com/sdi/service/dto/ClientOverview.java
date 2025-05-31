package com.sdi.service.dto;

public class ClientOverview {

    private String name;
    private String type;
    private String badgeClass;
    private String icon;
    private Integer products;
    private Integer requestsOfChanges;
    private Integer deployments;

    // Default constructor
    public ClientOverview() {
    }

    // Constructor with all parameters
    public ClientOverview(String name, String type, String badgeClass, String icon,
                          Integer products, Integer requestsOfChanges, Integer deployments) {
        this.name = name;
        this.type = type;
        this.badgeClass = badgeClass;
        this.icon = icon;
        this.products = products;
        this.requestsOfChanges = requestsOfChanges;
        this.deployments = deployments;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public void setBadgeClass(String badgeClass) {
        this.badgeClass = badgeClass;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getProducts() {
        return products;
    }

    public void setProducts(Integer products) {
        this.products = products;
    }

    public Integer getRequestsOfChanges() {
        return requestsOfChanges;
    }

    public void setRequestsOfChanges(Integer requestsOfChanges) {
        this.requestsOfChanges = requestsOfChanges;
    }

    public Integer getDeployments() {
        return deployments;
    }

    public void setDeployments(Integer deployments) {
        this.deployments = deployments;
    }
}
