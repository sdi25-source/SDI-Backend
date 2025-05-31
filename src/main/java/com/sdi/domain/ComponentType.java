package com.sdi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ComponentType.
 */
@Entity
@Table(name = "component_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComponentType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "componentType")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "componentType", "productVersions" }, allowSetters = true)
    private Set<InfraComponent> infraComponents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ComponentType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public ComponentType type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<InfraComponent> getInfraComponents() {
        return this.infraComponents;
    }

    public void setInfraComponents(Set<InfraComponent> infraComponents) {
        if (this.infraComponents != null) {
            this.infraComponents.forEach(i -> i.setComponentType(null));
        }
        if (infraComponents != null) {
            infraComponents.forEach(i -> i.setComponentType(this));
        }
        this.infraComponents = infraComponents;
    }

    public ComponentType infraComponents(Set<InfraComponent> infraComponents) {
        this.setInfraComponents(infraComponents);
        return this;
    }

    public ComponentType addInfraComponent(InfraComponent infraComponent) {
        this.infraComponents.add(infraComponent);
        infraComponent.setComponentType(this);
        return this;
    }

    public ComponentType removeInfraComponent(InfraComponent infraComponent) {
        this.infraComponents.remove(infraComponent);
        infraComponent.setComponentType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComponentType)) {
            return false;
        }
        return getId() != null && getId().equals(((ComponentType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ComponentType{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
