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
 * A Domaine.
 */
@Entity
@Table(name = "domaine")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Domaine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "domaine")
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

    public Domaine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Domaine name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public Domaine createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public Domaine updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public Domaine notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<ModuleVersion> getModuleVersions() {
        return this.moduleVersions;
    }

    public void setModuleVersions(Set<ModuleVersion> moduleVersions) {
        if (this.moduleVersions != null) {
            this.moduleVersions.forEach(i -> i.setDomaine(null));
        }
        if (moduleVersions != null) {
            moduleVersions.forEach(i -> i.setDomaine(this));
        }
        this.moduleVersions = moduleVersions;
    }

    public Domaine moduleVersions(Set<ModuleVersion> moduleVersions) {
        this.setModuleVersions(moduleVersions);
        return this;
    }

    public Domaine addModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.add(moduleVersion);
        moduleVersion.setDomaine(this);
        return this;
    }

    public Domaine removeModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.remove(moduleVersion);
        moduleVersion.setDomaine(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Domaine)) {
            return false;
        }
        return getId() != null && getId().equals(((Domaine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Domaine{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
