package com.sdi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Length;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "client_logo")
    private String clientLogo;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "main_contact_name")
    private String mainContactName;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "main_contact_email")
    private String mainContactEmail;

    @Column(name = "current_card_holder_number")
    private BigInteger currentCardHolderNumber; // BigInteger

    @Column(name = "current_brunche_number")
    private Integer currentBruncheNumber;

    @Column(name = "current_customers_number")
    private Integer currentCustomersNumber;

    @Column(name = "main_contact_phone_number")
    private String mainContactPhoneNumber;

    @Column(name = "url")
    private String url;

    @Column(name = "address")
    private String address;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "product", "client" }, allowSetters = true)
    private Set<ProductDeployement> productDeployements = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "clients" }, allowSetters = true)
    private ClientSize size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "clients" }, allowSetters = true)
    private ClientType clientType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "clients", "region" }, allowSetters = true)
    private Country country;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "certif" }, allowSetters = true)
    private Set<ClientCertification> certifs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Client id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientLogo() {
        return this.clientLogo;
    }

    public Client clientLogo(String clientLogo) {
        this.setClientLogo(clientLogo);
        return this;
    }

    public void setClientLogo(String clientLogo) {
        this.clientLogo = clientLogo;
    }

    public String getName() {
        return this.name;
    }

    public Client name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public Client code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMainContactName() {
        return this.mainContactName;
    }

    public Client mainContactName(String mainContactName) {
        this.setMainContactName(mainContactName);
        return this;
    }

    public void setMainContactName(String mainContactName) {
        this.mainContactName = mainContactName;
    }

    public String getMainContactEmail() {
        return this.mainContactEmail;
    }

    public Client mainContactEmail(String mainContactEmail) {
        this.setMainContactEmail(mainContactEmail);
        return this;
    }

    public void setMainContactEmail(String mainContactEmail) {
        this.mainContactEmail = mainContactEmail;
    }

    public BigInteger getCurrentCardHolderNumber() {
        return this.currentCardHolderNumber;
    }

    public Client currentCardHolderNumber(BigInteger currentCardHolderNumber) {
        this.setCurrentCardHolderNumber(currentCardHolderNumber);
        return this;
    }

    public void setCurrentCardHolderNumber(BigInteger currentCardHolderNumber) {
        this.currentCardHolderNumber = currentCardHolderNumber;
    }

    public Integer getCurrentBruncheNumber() {
        return this.currentBruncheNumber;
    }

    public Client currentBruncheNumber(Integer currentBruncheNumber) {
        this.setCurrentBruncheNumber(currentBruncheNumber);
        return this;
    }

    public void setCurrentBruncheNumber(Integer currentBruncheNumber) {
        this.currentBruncheNumber = currentBruncheNumber;
    }

    public Integer getCurrentCustomersNumber() {
        return this.currentCustomersNumber;
    }

    public Client currentCustomersNumber(Integer currentCustomersNumber) {
        this.setCurrentCustomersNumber(currentCustomersNumber);
        return this;
    }

    public void setCurrentCustomersNumber(Integer currentCustomersNumber) {
        this.currentCustomersNumber = currentCustomersNumber;
    }

    public String getMainContactPhoneNumber() {
        return this.mainContactPhoneNumber;
    }

    public Client mainContactPhoneNumber(String mainContactPhoneNumber) {
        this.setMainContactPhoneNumber(mainContactPhoneNumber);
        return this;
    }

    public void setMainContactPhoneNumber(String mainContactPhoneNumber) {
        this.mainContactPhoneNumber = mainContactPhoneNumber;
    }

    public String getUrl() {
        return this.url;
    }

    public Client url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return this.address;
    }

    public Client address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public Client createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public Client updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public Client notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<ProductDeployement> getProductDeployements() {
        return this.productDeployements;
    }

    public void setProductDeployements(Set<ProductDeployement> productDeployements) {
        if (this.productDeployements != null) {
            this.productDeployements.forEach(i -> i.setClient(null));
        }
        if (productDeployements != null) {
            productDeployements.forEach(i -> i.setClient(this));
        }
        this.productDeployements = productDeployements;
    }

    public Client productDeployements(Set<ProductDeployement> productDeployements) {
        this.setProductDeployements(productDeployements);
        return this;
    }

    public Client addProductDeployement(ProductDeployement productDeployement) {
        this.productDeployements.add(productDeployement);
        productDeployement.setClient(this);
        return this;
    }

    public Client removeProductDeployement(ProductDeployement productDeployement) {
        this.productDeployements.remove(productDeployement);
        productDeployement.setClient(null);
        return this;
    }

    public ClientSize getSize() {
        return this.size;
    }

    public void setSize(ClientSize clientSize) {
        this.size = clientSize;
    }

    public Client size(ClientSize clientSize) {
        this.setSize(clientSize);
        return this;
    }

    public ClientType getClientType() {
        return this.clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Client clientType(ClientType clientType) {
        this.setClientType(clientType);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Client country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Set<ClientCertification> getCertifs() {
        return this.certifs;
    }

    public void setCertifs(Set<ClientCertification> clientCertifications) {
        if (this.certifs != null) {
            this.certifs.forEach(i -> i.setClient(null));
        }
        if (clientCertifications != null) {
            clientCertifications.forEach(i -> i.setClient(this));
        }
        this.certifs = clientCertifications;
    }

    public Client certifs(Set<ClientCertification> clientCertifications) {
        this.setCertifs(clientCertifications);
        return this;
    }

    public Client addCertif(ClientCertification clientCertification) {
        this.certifs.add(clientCertification);
        clientCertification.setClient(this);
        return this;
    }

    public Client removeCertif(ClientCertification clientCertification) {
        this.certifs.remove(clientCertification);
        clientCertification.setClient(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return getId() != null && getId().equals(((Client) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", clientLogo='" + getClientLogo() + "'" +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", mainContactName='" + getMainContactName() + "'" +
            ", mainContactEmail='" + getMainContactEmail() + "'" +
            ", currentCardHolderNumber=" + getCurrentCardHolderNumber() +
            ", currentBruncheNumber=" + getCurrentBruncheNumber() +
            ", currentCustomersNumber=" + getCurrentCustomersNumber() +
            ", mainContactPhoneNumber='" + getMainContactPhoneNumber() + "'" +
            ", url='" + getUrl() + "'" +
            ", address='" + getAddress() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
