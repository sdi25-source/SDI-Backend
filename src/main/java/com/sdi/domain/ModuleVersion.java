package com.sdi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ModuleVersion.
 */
@Entity
@Table(name = "module_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModuleVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "moduleVersion")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "featureDeployements", "moduleVersion", "productDeployementDetail" }, allowSetters = true)
    private Set<ModuleDeployement> moduleDeployements = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "root")
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
    private Set<ModuleVersion> moduleVersions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Module module;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_module_version__feature",
        joinColumns = @JoinColumn(name = "module_version_id"),
        inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "featureDeployements", "moduleVersions" }, allowSetters = true)
    private Set<Feature> features = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "moduleVersions" }, allowSetters = true)
    private Domaine domaine;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private ModuleVersion root;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "moduleVersions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<ProductVersion> productVersions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "allowedModuleVersions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = {
            "moduleDeployements",
            "productDeployement",
            "infraComponentVersions",
            "allowedModuleVersions",
            "productVersion",
            "deployementType",
        },
        allowSetters = true
    )
    private Set<ProductDeployementDetail> productDeployementDetails = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "moduleVersions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "productVersion", "client", "moduleVersions", "customisationLevel" }, allowSetters = true)
    private Set<RequestOfChange> requestOfChanges = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ModuleVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public ModuleVersion version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public ModuleVersion createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public ModuleVersion updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public ModuleVersion notes(String notes) {
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
            this.moduleDeployements.forEach(i -> i.setModuleVersion(null));
        }
        if (moduleDeployements != null) {
            moduleDeployements.forEach(i -> i.setModuleVersion(this));
        }
        this.moduleDeployements = moduleDeployements;
    }

    public ModuleVersion moduleDeployements(Set<ModuleDeployement> moduleDeployements) {
        this.setModuleDeployements(moduleDeployements);
        return this;
    }

    public ModuleVersion addModuleDeployement(ModuleDeployement moduleDeployement) {
        this.moduleDeployements.add(moduleDeployement);
        moduleDeployement.setModuleVersion(this);
        return this;
    }

    public ModuleVersion removeModuleDeployement(ModuleDeployement moduleDeployement) {
        this.moduleDeployements.remove(moduleDeployement);
        moduleDeployement.setModuleVersion(null);
        return this;
    }

    public Set<ModuleVersion> getModuleVersions() {
        return this.moduleVersions;
    }

    public void setModuleVersions(Set<ModuleVersion> moduleVersions) {
        if (this.moduleVersions != null) {
            this.moduleVersions.forEach(i -> i.setRoot(null));
        }
        if (moduleVersions != null) {
            moduleVersions.forEach(i -> i.setRoot(this));
        }
        this.moduleVersions = moduleVersions;
    }

    public ModuleVersion moduleVersions(Set<ModuleVersion> moduleVersions) {
        this.setModuleVersions(moduleVersions);
        return this;
    }

    public ModuleVersion addModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.add(moduleVersion);
        moduleVersion.setRoot(this);
        return this;
    }

    public ModuleVersion removeModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.remove(moduleVersion);
        moduleVersion.setRoot(null);
        return this;
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public ModuleVersion module(Module module) {
        this.setModule(module);
        return this;
    }

    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public ModuleVersion features(Set<Feature> features) {
        this.setFeatures(features);
        return this;
    }

    public ModuleVersion addFeature(Feature feature) {
        this.features.add(feature);
        return this;
    }

    public ModuleVersion removeFeature(Feature feature) {
        this.features.remove(feature);
        return this;
    }

    public Domaine getDomaine() {
        return this.domaine;
    }

    public void setDomaine(Domaine domaine) {
        this.domaine = domaine;
    }

    public ModuleVersion domaine(Domaine domaine) {
        this.setDomaine(domaine);
        return this;
    }

    public ModuleVersion getRoot() {
        return this.root;
    }

    public void setRoot(ModuleVersion moduleVersion) {
        this.root = moduleVersion;
    }

    public ModuleVersion root(ModuleVersion moduleVersion) {
        this.setRoot(moduleVersion);
        return this;
    }

    public Set<ProductVersion> getProductVersions() {
        return this.productVersions;
    }

    public void setProductVersions(Set<ProductVersion> productVersions) {
        if (this.productVersions != null) {
            this.productVersions.forEach(i -> i.removeModuleVersion(this));
        }
        if (productVersions != null) {
            productVersions.forEach(i -> i.addModuleVersion(this));
        }
        this.productVersions = productVersions;
    }

    public ModuleVersion productVersions(Set<ProductVersion> productVersions) {
        this.setProductVersions(productVersions);
        return this;
    }

    public ModuleVersion addProductVersion(ProductVersion productVersion) {
        this.productVersions.add(productVersion);
        productVersion.getModuleVersions().add(this);
        return this;
    }

    public ModuleVersion removeProductVersion(ProductVersion productVersion) {
        this.productVersions.remove(productVersion);
        productVersion.getModuleVersions().remove(this);
        return this;
    }

    public Set<ProductDeployementDetail> getProductDeployementDetails() {
        return this.productDeployementDetails;
    }

    public void setProductDeployementDetails(Set<ProductDeployementDetail> productDeployementDetails) {
        if (this.productDeployementDetails != null) {
            this.productDeployementDetails.forEach(i -> i.removeAllowedModuleVersion(this));
        }
        if (productDeployementDetails != null) {
            productDeployementDetails.forEach(i -> i.addAllowedModuleVersion(this));
        }
        this.productDeployementDetails = productDeployementDetails;
    }

    public ModuleVersion productDeployementDetails(Set<ProductDeployementDetail> productDeployementDetails) {
        this.setProductDeployementDetails(productDeployementDetails);
        return this;
    }

    public ModuleVersion addProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.add(productDeployementDetail);
        productDeployementDetail.getAllowedModuleVersions().add(this);
        return this;
    }

    public ModuleVersion removeProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.remove(productDeployementDetail);
        productDeployementDetail.getAllowedModuleVersions().remove(this);
        return this;
    }

    public Set<RequestOfChange> getRequestOfChanges() {
        return this.requestOfChanges;
    }

    public void setRequestOfChanges(Set<RequestOfChange> requestOfChanges) {
        if (this.requestOfChanges != null) {
            this.requestOfChanges.forEach(i -> i.removeModuleVersion(this));
        }
        if (requestOfChanges != null) {
            requestOfChanges.forEach(i -> i.addModuleVersion(this));
        }
        this.requestOfChanges = requestOfChanges;
    }

    public ModuleVersion requestOfChanges(Set<RequestOfChange> requestOfChanges) {
        this.setRequestOfChanges(requestOfChanges);
        return this;
    }

    public ModuleVersion addRequestOfChange(RequestOfChange requestOfChange) {
        this.requestOfChanges.add(requestOfChange);
        requestOfChange.getModuleVersions().add(this);
        return this;
    }

    public ModuleVersion removeRequestOfChange(RequestOfChange requestOfChange) {
        this.requestOfChanges.remove(requestOfChange);
        requestOfChange.getModuleVersions().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModuleVersion)) {
            return false;
        }
        return getId() != null && getId().equals(((ModuleVersion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModuleVersion{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
