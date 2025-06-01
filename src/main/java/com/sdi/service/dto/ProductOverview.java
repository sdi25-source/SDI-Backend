package com.sdi.service.dto;

public class ProductOverview {

    private String name;
    private String badgeClass;
    private String icon;
    private Integer versions;
    private Integer modules;
    private Integer clients;

    // Constructeur par défaut
    public ProductOverview() {
    }

    // Constructeur avec tous les paramètres
    public ProductOverview(String name, String badgeClass, String icon,
                           Integer versions, Integer modules, Integer clients) {
        this.name = name;
        this.badgeClass = badgeClass;
        this.icon = icon;
        this.versions = versions;
        this.modules = modules;
        this.clients = clients;
    }

    // Getters
    public String getName() {
        return name;
    }


    public String getBadgeClass() {
        return badgeClass;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getVersions() {
        return versions;
    }

    public Integer getModules() {
        return modules;
    }

    public Integer getClients() {
        return clients;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }


    public void setBadgeClass(String badgeClass) {
        this.badgeClass = badgeClass;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setVersions(Integer versions) {
        this.versions = versions;
    }

    public void setModules(Integer modules) {
        this.modules = modules;
    }

    public void setClients(Integer deploiements) {
        this.clients = clients;
    }
}
