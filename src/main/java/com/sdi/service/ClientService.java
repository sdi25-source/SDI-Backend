package com.sdi.service;

import com.sdi.domain.Client;
import com.sdi.repository.ClientRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sdi.domain.Client}.
 */
@Service
@Transactional
public class ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Save a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    public Client save(Client client) {
        LOG.debug("Request to save Client : {}", client);
        return clientRepository.save(client);
    }

    /**
     * Update a client.
     *
     * @param client the entity to save.
     * @return the persisted entity.
     */
    public Client update(Client client) {
        LOG.debug("Request to update Client : {}", client);
        return clientRepository.save(client);
    }

    /**
     * Partially update a client.
     *
     * @param client the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Client> partialUpdate(Client client) {
        LOG.debug("Request to partially update Client : {}", client);

        return clientRepository
            .findById(client.getId())
            .map(existingClient -> {
                if (client.getClientLogo() != null) {
                    existingClient.setClientLogo(client.getClientLogo());
                }
                if (client.getName() != null) {
                    existingClient.setName(client.getName());
                }
                if (client.getCode() != null) {
                    existingClient.setCode(client.getCode());
                }
                if (client.getMainContactName() != null) {
                    existingClient.setMainContactName(client.getMainContactName());
                }
                if (client.getMainContactEmail() != null) {
                    existingClient.setMainContactEmail(client.getMainContactEmail());
                }
                if (client.getCurrentCardHolderNumber() != null) {
                    existingClient.setCurrentCardHolderNumber(client.getCurrentCardHolderNumber());
                }
                if (client.getCurrentBruncheNumber() != null) {
                    existingClient.setCurrentBruncheNumber(client.getCurrentBruncheNumber());
                }
                if (client.getCurrentCustomersNumber() != null) {
                    existingClient.setCurrentCustomersNumber(client.getCurrentCustomersNumber());
                }
                if (client.getMainContactPhoneNumber() != null) {
                    existingClient.setMainContactPhoneNumber(client.getMainContactPhoneNumber());
                }
                if (client.getUrl() != null) {
                    existingClient.setUrl(client.getUrl());
                }
                if (client.getAddress() != null) {
                    existingClient.setAddress(client.getAddress());
                }
                if (client.getCreateDate() != null) {
                    existingClient.setCreateDate(client.getCreateDate());
                }
                if (client.getUpdateDate() != null) {
                    existingClient.setUpdateDate(client.getUpdateDate());
                }
                if (client.getNotes() != null) {
                    existingClient.setNotes(client.getNotes());
                }

                return existingClient;
            })
            .map(clientRepository::save);
    }

    /**
     * Get all the clients with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Client> findAllWithEagerRelationships(Pageable pageable) {
        return clientRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one client by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Client> findOne(Long id) {
        LOG.debug("Request to get Client : {}", id);
        return clientRepository.findById(id);
    }


    public Client getClientById(Long idClient) {
        return clientRepository.findByIdWithRelations(idClient)
            .orElseThrow(() -> new RuntimeException("Client not found with ID: " + idClient));
    }




    /**
     * Delete the client by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Client : {}", id);
        clientRepository.deleteById(id);
    }
}
