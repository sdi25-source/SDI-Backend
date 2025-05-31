package com.sdi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdi.domain.enumeration.RequestStatus;
import com.sdi.domain.enumeration.TypeRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RequestOfChange.
 */
@Entity
@Table(name = "request_of_change")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RequestOfChange implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "keywords")
    private String keywords;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Column(name = "done")
    private Boolean done;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeRequest type;

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
    private ProductVersion productVersion;

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
    private ProductVersion productVersionResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "productDeployements", "size", "clientType", "country", "certifs" }, allowSetters = true)
    private Client client;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_request_of_change__module_version",
        joinColumns = @JoinColumn(name = "request_of_change_id"),
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "requestOfChanges" }, allowSetters = true)
    private CustomisationLevel customisationLevel;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RequestOfChange id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public RequestOfChange title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public RequestOfChange keywords(String keywords) {
        this.setKeywords(keywords);
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public RequestStatus getStatus() {
        return this.status;
    }

    public RequestOfChange status(RequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public RequestOfChange description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public RequestOfChange createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public RequestOfChange updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getDone() {
        return this.done;
    }

    public RequestOfChange done(Boolean done) {
        this.setDone(done);
        return this;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public TypeRequest getType() {
        return this.type;
    }

    public RequestOfChange type(TypeRequest type) {
        this.setType(type);
        return this;
    }

    public void setType(TypeRequest type) {
        this.type = type;
    }

    public ProductVersion getProductVersion() {
        return this.productVersion;
    }

    public void setProductVersion(ProductVersion productVersion) {
        this.productVersion = productVersion;
    }

    public RequestOfChange productVersion(ProductVersion productVersion) {
        this.setProductVersion(productVersion);
        return this;
    }

    public ProductVersion getProductVersionResult() {
        return this.productVersionResult;
    }

    public void setProductVersionResult(ProductVersion productVersionResult) {
        this.productVersionResult = productVersionResult;
    }

    public RequestOfChange productVersionResult(ProductVersion productVersionResult) {
        this.setProductVersionResult(productVersionResult);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public RequestOfChange client(Client client) {
        this.setClient(client);
        return this;
    }

    public Set<ModuleVersion> getModuleVersions() {
        return this.moduleVersions;
    }

    public void setModuleVersions(Set<ModuleVersion> moduleVersions) {
        this.moduleVersions = moduleVersions;
    }

    public RequestOfChange moduleVersions(Set<ModuleVersion> moduleVersions) {
        this.setModuleVersions(moduleVersions);
        return this;
    }

    public RequestOfChange addModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.add(moduleVersion);
        return this;
    }

    public RequestOfChange removeModuleVersion(ModuleVersion moduleVersion) {
        this.moduleVersions.remove(moduleVersion);
        return this;
    }

    public CustomisationLevel getCustomisationLevel() {
        return this.customisationLevel;
    }

    public void setCustomisationLevel(CustomisationLevel customisationLevel) {
        this.customisationLevel = customisationLevel;
    }

    public RequestOfChange customisationLevel(CustomisationLevel customisationLevel) {
        this.setCustomisationLevel(customisationLevel);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestOfChange)) {
            return false;
        }
        return getId() != null && getId().equals(((RequestOfChange) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RequestOfChange{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", keywords='" + getKeywords() + "'" +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", done='" + getDone() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
