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
 * A CertificationVersion.
 */
@Entity
@Table(name = "certification_version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CertificationVersion implements Serializable {

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

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "clientCertifications" }, allowSetters = true)
    private Certification certification;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "certifications")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "productLines", "certifications", "modules" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CertificationVersion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public CertificationVersion version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public CertificationVersion createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getExpireDate() {
        return this.expireDate;
    }

    public CertificationVersion expireDate(LocalDate expireDate) {
        this.setExpireDate(expireDate);
        return this;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getDescription() {
        return this.description;
    }

    public CertificationVersion description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Certification getCertification() {
        return this.certification;
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
    }

    public CertificationVersion certification(Certification certification) {
        this.setCertification(certification);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeCertification(this));
        }
        if (products != null) {
            products.forEach(i -> i.addCertification(this));
        }
        this.products = products;
    }

    public CertificationVersion products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public CertificationVersion addProduct(Product product) {
        this.products.add(product);
        product.getCertifications().add(this);
        return this;
    }

    public CertificationVersion removeProduct(Product product) {
        this.products.remove(product);
        product.getCertifications().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificationVersion)) {
            return false;
        }
        return getId() != null && getId().equals(((CertificationVersion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertificationVersion{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", expireDate='" + getExpireDate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
