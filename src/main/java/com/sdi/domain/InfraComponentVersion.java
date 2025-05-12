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
 * A InfraComponentVersion.
 */
@Entity
@Table(name = "infra_component_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InfraComponentVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "componentType" }, allowSetters = true)
    private InfraComponent infraComponent;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "infraComponentVersions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "productDeployementDetails", "productVersions", "product", "moduleVersions", "infraComponentVersions", "root" },
        allowSetters = true
    )
    private Set<ProductVersion> productVersions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "infraComponentVersions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "productLines", "modules", "infraComponentVersions" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "infraComponentVersions")
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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InfraComponentVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public InfraComponentVersion version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return this.description;
    }

    public InfraComponentVersion description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public InfraComponentVersion createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public InfraComponentVersion updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public InfraComponent getInfraComponent() {
        return this.infraComponent;
    }

    public void setInfraComponent(InfraComponent infraComponent) {
        this.infraComponent = infraComponent;
    }

    public InfraComponentVersion infraComponent(InfraComponent infraComponent) {
        this.setInfraComponent(infraComponent);
        return this;
    }

    public Set<ProductVersion> getProductVersions() {
        return this.productVersions;
    }

    public void setProductVersions(Set<ProductVersion> productVersions) {
        if (this.productVersions != null) {
            this.productVersions.forEach(i -> i.removeInfraComponentVersion(this));
        }
        if (productVersions != null) {
            productVersions.forEach(i -> i.addInfraComponentVersion(this));
        }
        this.productVersions = productVersions;
    }

    public InfraComponentVersion productVersions(Set<ProductVersion> productVersions) {
        this.setProductVersions(productVersions);
        return this;
    }

    public InfraComponentVersion addProductVersion(ProductVersion productVersion) {
        this.productVersions.add(productVersion);
        productVersion.getInfraComponentVersions().add(this);
        return this;
    }

    public InfraComponentVersion removeProductVersion(ProductVersion productVersion) {
        this.productVersions.remove(productVersion);
        productVersion.getInfraComponentVersions().remove(this);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeInfraComponentVersion(this));
        }
        if (products != null) {
            products.forEach(i -> i.addInfraComponentVersion(this));
        }
        this.products = products;
    }

    public InfraComponentVersion products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public InfraComponentVersion addProduct(Product product) {
        this.products.add(product);
        product.getInfraComponentVersions().add(this);
        return this;
    }

    public InfraComponentVersion removeProduct(Product product) {
        this.products.remove(product);
        product.getInfraComponentVersions().remove(this);
        return this;
    }

    public Set<ProductDeployementDetail> getProductDeployementDetails() {
        return this.productDeployementDetails;
    }

    public void setProductDeployementDetails(Set<ProductDeployementDetail> productDeployementDetails) {
        if (this.productDeployementDetails != null) {
            this.productDeployementDetails.forEach(i -> i.removeInfraComponentVersion(this));
        }
        if (productDeployementDetails != null) {
            productDeployementDetails.forEach(i -> i.addInfraComponentVersion(this));
        }
        this.productDeployementDetails = productDeployementDetails;
    }

    public InfraComponentVersion productDeployementDetails(Set<ProductDeployementDetail> productDeployementDetails) {
        this.setProductDeployementDetails(productDeployementDetails);
        return this;
    }

    public InfraComponentVersion addProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.add(productDeployementDetail);
        productDeployementDetail.getInfraComponentVersions().add(this);
        return this;
    }

    public InfraComponentVersion removeProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.remove(productDeployementDetail);
        productDeployementDetail.getInfraComponentVersions().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InfraComponentVersion)) {
            return false;
        }
        return getId() != null && getId().equals(((InfraComponentVersion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InfraComponentVersion{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", description='" + getDescription() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
