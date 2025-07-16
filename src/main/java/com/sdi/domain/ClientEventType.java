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
 * A ClientEventType.
 */
@Entity
@Table(name = "client_event_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientEventType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false, unique = true)
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "create_date")
    private LocalDate createDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientEventType")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client", "clientEventType" }, allowSetters = true)
    private Set<ClientEvent> clientEvents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClientEventType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public ClientEventType type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public ClientEventType description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public ClientEventType createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getUpdateDate() {
        return this.updateDate;
    }

    public ClientEventType updateDate(LocalDate updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public Set<ClientEvent> getClientEvents() {
        return this.clientEvents;
    }

    public void setClientEvents(Set<ClientEvent> clientEvents) {
        if (this.clientEvents != null) {
            this.clientEvents.forEach(i -> i.setClientEventType(null));
        }
        if (clientEvents != null) {
            clientEvents.forEach(i -> i.setClientEventType(this));
        }
        this.clientEvents = clientEvents;
    }

    public ClientEventType clientEvents(Set<ClientEvent> clientEvents) {
        this.setClientEvents(clientEvents);
        return this;
    }

    public ClientEventType addClientEvent(ClientEvent clientEvent) {
        this.clientEvents.add(clientEvent);
        clientEvent.setClientEventType(this);
        return this;
    }

    public ClientEventType removeClientEvent(ClientEvent clientEvent) {
        this.clientEvents.remove(clientEvent);
        clientEvent.setClientEventType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientEventType)) {
            return false;
        }
        return getId() != null && getId().equals(((ClientEventType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientEventType{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", description='" + getDescription() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            "}";
    }
}
