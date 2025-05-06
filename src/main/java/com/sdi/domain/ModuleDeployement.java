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
 * A ModuleDeployement.
 */
@Entity
@Table(name = "module_deployement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModuleDeployement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "moduleDeployement")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "feature", "moduleDeployement" }, allowSetters = true)
    private Set<FeatureDeployement> featureDeployements = new HashSet<>();

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
    private ModuleVersion moduleVersion;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private ProductDeployementDetail productDeployementDetail;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ModuleDeployement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public ModuleDeployement code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNotes() {
        return this.notes;
    }

    public ModuleDeployement notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public ModuleDeployement createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public ModuleDeployement updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public Set<FeatureDeployement> getFeatureDeployements() {
        return this.featureDeployements;
    }

    public void setFeatureDeployements(Set<FeatureDeployement> featureDeployements) {
        if (this.featureDeployements != null) {
            this.featureDeployements.forEach(i -> i.setModuleDeployement(null));
        }
        if (featureDeployements != null) {
            featureDeployements.forEach(i -> i.setModuleDeployement(this));
        }
        this.featureDeployements = featureDeployements;
    }

    public ModuleDeployement featureDeployements(Set<FeatureDeployement> featureDeployements) {
        this.setFeatureDeployements(featureDeployements);
        return this;
    }

    public ModuleDeployement addFeatureDeployement(FeatureDeployement featureDeployement) {
        this.featureDeployements.add(featureDeployement);
        featureDeployement.setModuleDeployement(this);
        return this;
    }

    public ModuleDeployement removeFeatureDeployement(FeatureDeployement featureDeployement) {
        this.featureDeployements.remove(featureDeployement);
        featureDeployement.setModuleDeployement(null);
        return this;
    }

    public ModuleVersion getModuleVersion() {
        return this.moduleVersion;
    }

    public void setModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public ModuleDeployement moduleVersion(ModuleVersion moduleVersion) {
        this.setModuleVersion(moduleVersion);
        return this;
    }

    public ProductDeployementDetail getProductDeployementDetail() {
        return this.productDeployementDetail;
    }

    public void setProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetail = productDeployementDetail;
    }

    public ModuleDeployement productDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.setProductDeployementDetail(productDeployementDetail);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModuleDeployement)) {
            return false;
        }
        return getId() != null && getId().equals(((ModuleDeployement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModuleDeployement{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
