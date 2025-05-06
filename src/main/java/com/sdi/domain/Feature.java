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
 * A Feature.
 */
@Entity
@Table(name = "feature")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Feature implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "api_version")
    private String apiVersion;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "feature")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "feature", "moduleDeployement" }, allowSetters = true)
    private Set<FeatureDeployement> featureDeployements = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "features")
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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Feature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Feature name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public Feature apiVersion(String apiVersion) {
        this.setApiVersion(apiVersion);
        return this;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getDescription() {
        return this.description;
    }

    public Feature description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public Feature createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public Feature updateDate(LocalDate updateDate) {
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
            this.featureDeployements.forEach(i -> i.setFeature(null));
        }
        if (featureDeployements != null) {
            featureDeployements.forEach(i -> i.setFeature(this));
        }
        this.featureDeployements = featureDeployements;
    }

    public Feature featureDeployements(Set<FeatureDeployement> featureDeployements) {
        this.setFeatureDeployements(featureDeployements);
        return this;
    }

    public Feature addFeatureDeployement(FeatureDeployement featureDeployement) {
        this.featureDeployements.add(featureDeployement);
        featureDeployement.setFeature(this);
        return this;
    }

    public Feature removeFeatureDeployement(FeatureDeployement featureDeployement) {
        this.featureDeployements.remove(featureDeployement);
        featureDeployement.setFeature(null);
        return this;
    }

    public Set<ModuleVersion> getModuleVersions() {
        return this.moduleVersions;
    }

    public void setModuleVersions(Set<ModuleVersion> moduleVersions) {
        if (this.moduleVersions != null) {
            this.moduleVersions.forEach(i -> i.removeFeature(this));
        }
        if (moduleVersions != null) {
            moduleVersions.forEach(i -> i.addFeature(this));
        }
        this.moduleVersions = moduleVersions;
    }

    public Feature moduleVersions(Set<ModuleVersion> moduleVersions) {
        this.setModuleVersions(moduleVersions);
        return this;
    }

    public Feature addModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.add(moduleVersion);
        moduleVersion.getFeatures().add(this);
        return this;
    }

    public Feature removeModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.remove(moduleVersion);
        moduleVersion.getFeatures().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Feature)) {
            return false;
        }
        return getId() != null && getId().equals(((Feature) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Feature{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", apiVersion='" + getApiVersion() + "'" +
            ", description='" + getDescription() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
