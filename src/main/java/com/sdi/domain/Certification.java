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
 * A Certification.
 */
@Entity
@Table(name = "certification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Certification implements Serializable {

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

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "certif")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "certif" }, allowSetters = true)
    private Set<ClientCertification> clientCertifications = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Certification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Certification name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public Certification createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getDescription() {
        return this.description;
    }

    public Certification description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ClientCertification> getClientCertifications() {
        return this.clientCertifications;
    }

    public void setClientCertifications(Set<ClientCertification> clientCertifications) {
        if (this.clientCertifications != null) {
            this.clientCertifications.forEach(i -> i.setCertif(null));
        }
        if (clientCertifications != null) {
            clientCertifications.forEach(i -> i.setCertif(this));
        }
        this.clientCertifications = clientCertifications;
    }

    public Certification clientCertifications(Set<ClientCertification> clientCertifications) {
        this.setClientCertifications(clientCertifications);
        return this;
    }

    public Certification addClientCertification(ClientCertification clientCertification) {
        this.clientCertifications.add(clientCertification);
        clientCertification.setCertif(this);
        return this;
    }

    public Certification removeClientCertification(ClientCertification clientCertification) {
        this.clientCertifications.remove(clientCertification);
        clientCertification.setCertif(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certification)) {
            return false;
        }
        return getId() != null && getId().equals(((Certification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Certification{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
