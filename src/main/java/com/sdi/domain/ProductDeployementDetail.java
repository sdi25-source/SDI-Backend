package com.sdi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProductDeployementDetail.
 */
@Entity
@Table(name = "product_deployement_detail")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductDeployementDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start_deployement_date")
    private LocalDate startDeployementDate;

    @Column(name = "end_deployement_date")
    private LocalDate endDeployementDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productDeployementDetail")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "featureDeployements", "moduleVersion", "productDeployementDetail" }, allowSetters = true)
    private Set<ModuleDeployement> moduleDeployements = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product", "client" }, allowSetters = true)
    private ProductDeployement productDeployement;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_product_deployement_detail__infra_component_version",
        joinColumns = @JoinColumn(name = "product_deployement_detail_id"),
        inverseJoinColumns = @JoinColumn(name = "infra_component_version_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "infraComponent", "productVersions", "productDeployementDetails" }, allowSetters = true)
    private Set<InfraComponentVersion> infraComponentVersions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_product_deployement_detail__allowed_module_version",
        joinColumns = @JoinColumn(name = "product_deployement_detail_id"),
        inverseJoinColumns = @JoinColumn(name = "allowed_module_version_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = {
            "moduleDeployements",
            "moduleVersions",
            "module",
            "features",
            "domaine",
            "root",
            "productVersions",
            "productDeployementDetails",
            "requestOfChanges",
        },
        allowSetters = true
    )
    private Set<ModuleVersion> allowedModuleVersions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "productDeployementDetails",
            "productVersions",
            "product",
            "moduleVersions",
            "infraComponentVersions",
            "infraComponents",
            "root",
        },
        allowSetters = true
    )
    private ProductVersion productVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "productDeployementDetails" }, allowSetters = true)
    private DeployementType deployementType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductDeployementDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDeployementDate() {
        return this.startDeployementDate;
    }

    public ProductDeployementDetail startDeployementDate(LocalDate startDeployementDate) {
        this.setStartDeployementDate(startDeployementDate);
        return this;
    }

    public void setStartDeployementDate(LocalDate startDeployementDate) {
        this.startDeployementDate = startDeployementDate;
    }

    public LocalDate getEndDeployementDate() {
        return this.endDeployementDate;
    }

    public ProductDeployementDetail endDeployementDate(LocalDate endDeployementDate) {
        this.setEndDeployementDate(endDeployementDate);
        return this;
    }

    public void setEndDeployementDate(LocalDate endDeployementDate) {
        this.endDeployementDate = endDeployementDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public ProductDeployementDetail notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<ModuleDeployement> getModuleDeployements() {
        return this.moduleDeployements;
    }

    public void setModuleDeployements(Set<ModuleDeployement> moduleDeployements) {
        if (this.moduleDeployements != null) {
            this.moduleDeployements.forEach(i -> i.setProductDeployementDetail(null));
        }
        if (moduleDeployements != null) {
            moduleDeployements.forEach(i -> i.setProductDeployementDetail(this));
        }
        this.moduleDeployements = moduleDeployements;
    }

    public ProductDeployementDetail moduleDeployements(Set<ModuleDeployement> moduleDeployements) {
        this.setModuleDeployements(moduleDeployements);
        return this;
    }

    public ProductDeployementDetail addModuleDeployement(ModuleDeployement moduleDeployement) {
        this.moduleDeployements.add(moduleDeployement);
        moduleDeployement.setProductDeployementDetail(this);
        return this;
    }

    public ProductDeployementDetail removeModuleDeployement(ModuleDeployement moduleDeployement) {
        this.moduleDeployements.remove(moduleDeployement);
        moduleDeployement.setProductDeployementDetail(null);
        return this;
    }

    public ProductDeployement getProductDeployement() {
        return this.productDeployement;
    }

    public void setProductDeployement(ProductDeployement productDeployement) {
        this.productDeployement = productDeployement;
    }

    public ProductDeployementDetail productDeployement(ProductDeployement productDeployement) {
        this.setProductDeployement(productDeployement);
        return this;
    }

    public Set<InfraComponentVersion> getInfraComponentVersions() {
        return this.infraComponentVersions;
    }

    public void setInfraComponentVersions(Set<InfraComponentVersion> infraComponentVersions) {
        this.infraComponentVersions = infraComponentVersions;
    }

    public ProductDeployementDetail infraComponentVersions(Set<InfraComponentVersion> infraComponentVersions) {
        this.setInfraComponentVersions(infraComponentVersions);
        return this;
    }

    public ProductDeployementDetail addInfraComponentVersion(InfraComponentVersion infraComponentVersion) {
        this.infraComponentVersions.add(infraComponentVersion);
        return this;
    }

    public ProductDeployementDetail removeInfraComponentVersion(InfraComponentVersion infraComponentVersion) {
        this.infraComponentVersions.remove(infraComponentVersion);
        return this;
    }

    public Set<ModuleVersion> getAllowedModuleVersions() {
        return this.allowedModuleVersions;
    }

    public void setAllowedModuleVersions(Set<ModuleVersion> moduleVersions) {
        this.allowedModuleVersions = moduleVersions;
    }

    public ProductDeployementDetail allowedModuleVersions(Set<ModuleVersion> moduleVersions) {
        this.setAllowedModuleVersions(moduleVersions);
        return this;
    }

    public ProductDeployementDetail addAllowedModuleVersion(ModuleVersion moduleVersion) {
        this.allowedModuleVersions.add(moduleVersion);
        return this;
    }

    public ProductDeployementDetail removeAllowedModuleVersion(ModuleVersion moduleVersion) {
        this.allowedModuleVersions.remove(moduleVersion);
        return this;
    }

    public ProductVersion getProductVersion() {
        return this.productVersion;
    }

    public void setProductVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
    }

    public ProductDeployementDetail productVersion(ProductVersion productVersion) {
        this.setProductVersion(productVersion);
        return this;
    }

    public DeployementType getDeployementType() {
        return this.deployementType;
    }

    public void setDeployementType(DeployementType deployementType) {
        this.deployementType = deployementType;
    }

    public ProductDeployementDetail deployementType(DeployementType deployementType) {
        this.setDeployementType(deployementType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDeployementDetail)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductDeployementDetail) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDeployementDetail{" +
            "id=" + getId() +
            ", startDeployementDate='" + getStartDeployementDate() + "'" +
            ", endDeployementDate='" + getEndDeployementDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
