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
 * A ProductVersion.
 */
@Entity
@Table(name = "product_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductVersion implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productVersion")
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "root")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "productLines", "certifications", "modules" }, allowSetters = true)
    private Product product;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_product_version__module_version",
        joinColumns = @JoinColumn(name = "product_version_id"),
        inverseJoinColumns = @JoinColumn(name = "module_version_id")
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
    private Set<ModuleVersion> moduleVersions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_product_version__infra_component_version",
        joinColumns = @JoinColumn(name = "product_version_id"),
        inverseJoinColumns = @JoinColumn(name = "infra_component_version_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "infraComponent", "productVersions", "productDeployementDetails" }, allowSetters = true)
    private Set<InfraComponentVersion> infraComponentVersions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_product_version__infra_component",
        joinColumns = @JoinColumn(name = "product_version_id"),
        inverseJoinColumns = @JoinColumn(name = "infra_component_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "componentType", "productVersions" }, allowSetters = true)
    private Set<InfraComponent> infraComponents = new HashSet<>();

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
    private ProductVersion root;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public ProductVersion version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public ProductVersion createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public ProductVersion updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public ProductVersion notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<ProductDeployementDetail> getProductDeployementDetails() {
        return this.productDeployementDetails;
    }

    public void setProductDeployementDetails(Set<ProductDeployementDetail> productDeployementDetails) {
        if (this.productDeployementDetails != null) {
            this.productDeployementDetails.forEach(i -> i.setProductVersion(null));
        }
        if (productDeployementDetails != null) {
            productDeployementDetails.forEach(i -> i.setProductVersion(this));
        }
        this.productDeployementDetails = productDeployementDetails;
    }

    public ProductVersion productDeployementDetails(Set<ProductDeployementDetail> productDeployementDetails) {
        this.setProductDeployementDetails(productDeployementDetails);
        return this;
    }

    public ProductVersion addProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.add(productDeployementDetail);
        productDeployementDetail.setProductVersion(this);
        return this;
    }

    public ProductVersion removeProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.remove(productDeployementDetail);
        productDeployementDetail.setProductVersion(null);
        return this;
    }

    public Set<ProductVersion> getProductVersions() {
        return this.productVersions;
    }

    public void setProductVersions(Set<ProductVersion> productVersions) {
        if (this.productVersions != null) {
            this.productVersions.forEach(i -> i.setRoot(null));
        }
        if (productVersions != null) {
            productVersions.forEach(i -> i.setRoot(this));
        }
        this.productVersions = productVersions;
    }

    public ProductVersion productVersions(Set<ProductVersion> productVersions) {
        this.setProductVersions(productVersions);
        return this;
    }

    public ProductVersion addProductVersion(ProductVersion productVersion) {
        this.productVersions.add(productVersion);
        productVersion.setRoot(this);
        return this;
    }

    public ProductVersion removeProductVersion(ProductVersion productVersion) {
        this.productVersions.remove(productVersion);
        productVersion.setRoot(null);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVersion product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Set<ModuleVersion> getModuleVersions() {
        return this.moduleVersions;
    }

    public void setModuleVersions(Set<ModuleVersion> moduleVersions) {
        this.moduleVersions = moduleVersions;
    }

    public ProductVersion moduleVersions(Set<ModuleVersion> moduleVersions) {
        this.setModuleVersions(moduleVersions);
        return this;
    }

    public ProductVersion addModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.add(moduleVersion);
        return this;
    }

    public ProductVersion removeModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.remove(moduleVersion);
        return this;
    }

    public Set<InfraComponentVersion> getInfraComponentVersions() {
        return this.infraComponentVersions;
    }

    public void setInfraComponentVersions(Set<InfraComponentVersion> infraComponentVersions) {
        this.infraComponentVersions = infraComponentVersions;
    }

    public ProductVersion infraComponentVersions(Set<InfraComponentVersion> infraComponentVersions) {
        this.setInfraComponentVersions(infraComponentVersions);
        return this;
    }

    public ProductVersion addInfraComponentVersion(InfraComponentVersion infraComponentVersion) {
        this.infraComponentVersions.add(infraComponentVersion);
        return this;
    }

    public ProductVersion removeInfraComponentVersion(InfraComponentVersion infraComponentVersion) {
        this.infraComponentVersions.remove(infraComponentVersion);
        return this;
    }

    public Set<InfraComponent> getInfraComponents() {
        return this.infraComponents;
    }

    public void setInfraComponents(Set<InfraComponent> infraComponents) {
        this.infraComponents = infraComponents;
    }

    public ProductVersion infraComponents(Set<InfraComponent> infraComponents) {
        this.setInfraComponents(infraComponents);
        return this;
    }

    public ProductVersion addInfraComponent(InfraComponent infraComponent) {
        this.infraComponents.add(infraComponent);
        return this;
    }

    public ProductVersion removeInfraComponent(InfraComponent infraComponent) {
        this.infraComponents.remove(infraComponent);
        return this;
    }

    public ProductVersion getRoot() {
        return this.root;
    }

    public void setRoot(ProductVersion productVersion) {
        this.root = productVersion;
    }

    public ProductVersion root(ProductVersion productVersion) {
        this.setRoot(productVersion);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVersion)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductVersion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductVersion{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
