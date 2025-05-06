package com.sdi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DeployementType.
 */
@Entity
@Table(name = "deployement_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeployementType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deployementType")
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

    public DeployementType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public DeployementType type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public DeployementType createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public DeployementType updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public DeployementType notes(String notes) {
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
            this.productDeployementDetails.forEach(i -> i.setDeployementType(null));
        }
        if (productDeployementDetails != null) {
            productDeployementDetails.forEach(i -> i.setDeployementType(this));
        }
        this.productDeployementDetails = productDeployementDetails;
    }

    public DeployementType productDeployementDetails(Set<ProductDeployementDetail> productDeployementDetails) {
        this.setProductDeployementDetails(productDeployementDetails);
        return this;
    }

    public DeployementType addProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.add(productDeployementDetail);
        productDeployementDetail.setDeployementType(this);
        return this;
    }

    public DeployementType removeProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        this.productDeployementDetails.remove(productDeployementDetail);
        productDeployementDetail.setDeployementType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeployementType)) {
            return false;
        }
        return getId() != null && getId().equals(((DeployementType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeployementType{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
