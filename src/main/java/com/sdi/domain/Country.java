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
 * A Country.
 */
@Entity
@Table(name = "country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "countryname", nullable = false)
    private String countryname;

    @NotNull
    @Column(name = "countrycode", nullable = false)
    private String countrycode;

    @Column(name = "country_flagcode")
    private String countryFlagcode;

    @Column(name = "country_flag")
    private String countryFlag;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "crea_date")
    private LocalDate creaDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "productDeployements", "size", "clientType", "country", "certifs" }, allowSetters = true)
    private Set<Client> clients = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "countries" }, allowSetters = true)
    private Region region;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Country id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryname() {
        return this.countryname;
    }

    public Country countryname(String countryname) {
        this.setCountryname(countryname);
        return this;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getCountrycode() {
        return this.countrycode;
    }

    public Country countrycode(String countrycode) {
        this.setCountrycode(countrycode);
        return this;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getCountryFlagcode() {
        return this.countryFlagcode;
    }

    public Country countryFlagcode(String countryFlagcode) {
        this.setCountryFlagcode(countryFlagcode);
        return this;
    }

    public void setCountryFlagcode(String countryFlagcode) {
        this.countryFlagcode = countryFlagcode;
    }

    public String getCountryFlag() {
        return this.countryFlag;
    }

    public Country countryFlag(String countryFlag) {
        this.setCountryFlag(countryFlag);
        return this;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getNotes() {
        return this.notes;
    }

    public Country notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCreaDate() {
        return this.creaDate;
    }

    public Country creaDate(LocalDate creaDate) {
        this.setCreaDate(creaDate);
        return this;
    }

    public void setCreaDate(LocalDate creaDate) {
        this.creaDate = creaDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public Country updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public Set<Client> getClients() {
        return this.clients;
    }

    public void setClients(Set<Client> clients) {
        if (this.clients != null) {
            this.clients.forEach(i -> i.setCountry(null));
        }
        if (clients != null) {
            clients.forEach(i -> i.setCountry(this));
        }
        this.clients = clients;
    }

    public Country clients(Set<Client> clients) {
        this.setClients(clients);
        return this;
    }

    public Country addClient(Client client) {
        this.clients.add(client);
        client.setCountry(this);
        return this;
    }

    public Country removeClient(Client client) {
        this.clients.remove(client);
        client.setCountry(null);
        return this;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Country region(Region region) {
        this.setRegion(region);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        return getId() != null && getId().equals(((Country) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Country{" +
            "id=" + getId() +
            ", countryname='" + getCountryname() + "'" +
            ", countrycode='" + getCountrycode() + "'" +
            ", countryFlagcode='" + getCountryFlagcode() + "'" +
            ", countryFlag='" + getCountryFlag() + "'" +
            ", notes='" + getNotes() + "'" +
            ", creaDate='" + getCreaDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
