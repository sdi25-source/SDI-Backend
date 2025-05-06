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
 * A CustomisationLevel.
 */
@Entity
@Table(name = "customisation_level")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomisationLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "level", nullable = false)
    private String level;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customisationLevel")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "productVersion", "client", "moduleVersions", "customisationLevel" }, allowSetters = true)
    private Set<RequestOfChange> requestOfChanges = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomisationLevel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevel() {
        return this.level;
    }

    public CustomisationLevel level(String level) {
        this.setLevel(level);
        return this;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public CustomisationLevel createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public CustomisationLevel updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public CustomisationLevel notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<RequestOfChange> getRequestOfChanges() {
        return this.requestOfChanges;
    }

    public void setRequestOfChanges(Set<RequestOfChange> requestOfChanges) {
        if (this.requestOfChanges != null) {
            this.requestOfChanges.forEach(i -> i.setCustomisationLevel(null));
        }
        if (requestOfChanges != null) {
            requestOfChanges.forEach(i -> i.setCustomisationLevel(this));
        }
        this.requestOfChanges = requestOfChanges;
    }

    public CustomisationLevel requestOfChanges(Set<RequestOfChange> requestOfChanges) {
        this.setRequestOfChanges(requestOfChanges);
        return this;
    }

    public CustomisationLevel addRequestOfChange(RequestOfChange requestOfChange) {
        this.requestOfChanges.add(requestOfChange);
        requestOfChange.setCustomisationLevel(this);
        return this;
    }

    public CustomisationLevel removeRequestOfChange(RequestOfChange requestOfChange) {
        this.requestOfChanges.remove(requestOfChange);
        requestOfChange.setCustomisationLevel(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomisationLevel)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomisationLevel) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomisationLevel{" +
            "id=" + getId() +
            ", level='" + getLevel() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
