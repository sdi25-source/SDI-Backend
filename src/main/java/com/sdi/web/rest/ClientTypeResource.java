package com.sdi.web.rest;

import com.sdi.domain.ClientType;
import com.sdi.repository.ClientTypeRepository;
import com.sdi.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.sdi.domain.ClientType}.
 */
@RestController
@RequestMapping("/api/client-types")
@Transactional
public class ClientTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientTypeResource.class);

    private static final String ENTITY_NAME = "clientType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientTypeRepository clientTypeRepository;

    public ClientTypeResource(ClientTypeRepository clientTypeRepository) {
        this.clientTypeRepository = clientTypeRepository;
    }

    /**
     * {@code POST  /client-types} : Create a new clientType.
     *
     * @param clientType the clientType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientType, or with status {@code 400 (Bad Request)} if the clientType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientType> createClientType(@Valid @RequestBody ClientType clientType) throws URISyntaxException {
        LOG.debug("REST request to save ClientType : {}", clientType);
        if (clientType.getId() != null) {
            throw new BadRequestAlertException("A new clientType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clientType = clientTypeRepository.save(clientType);
        return ResponseEntity.created(new URI("/api/client-types/" + clientType.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clientType.getId().toString()))
            .body(clientType);
    }

    /**
     * {@code PUT  /client-types/:id} : Updates an existing clientType.
     *
     * @param id the id of the clientType to save.
     * @param clientType the clientType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientType,
     * or with status {@code 400 (Bad Request)} if the clientType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientType> updateClientType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientType clientType
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClientType : {}, {}", id, clientType);
        if (clientType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clientType = clientTypeRepository.save(clientType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientType.getId().toString()))
            .body(clientType);
    }

    /**
     * {@code PATCH  /client-types/:id} : Partial updates given fields of an existing clientType, field will ignore if it is null
     *
     * @param id the id of the clientType to save.
     * @param clientType the clientType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientType,
     * or with status {@code 400 (Bad Request)} if the clientType is not valid,
     * or with status {@code 404 (Not Found)} if the clientType is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientType> partialUpdateClientType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientType clientType
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClientType partially : {}, {}", id, clientType);
        if (clientType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientType> result = clientTypeRepository
            .findById(clientType.getId())
            .map(existingClientType -> {
                if (clientType.getType() != null) {
                    existingClientType.setType(clientType.getType());
                }
                if (clientType.getCreateDate() != null) {
                    existingClientType.setCreateDate(clientType.getCreateDate());
                }
                if (clientType.getUpdateDate() != null) {
                    existingClientType.setUpdateDate(clientType.getUpdateDate());
                }
                if (clientType.getNotes() != null) {
                    existingClientType.setNotes(clientType.getNotes());
                }

                return existingClientType;
            })
            .map(clientTypeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientType.getId().toString())
        );
    }

    /**
     * {@code GET  /client-types} : get all the clientTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientTypes in body.
     */
    @GetMapping("")
    public List<ClientType> getAllClientTypes() {
        LOG.debug("REST request to get all ClientTypes");
        return clientTypeRepository.findAll();
    }

    /**
     * {@code GET  /client-types/:id} : get the "id" clientType.
     *
     * @param id the id of the clientType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientType> getClientType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientType : {}", id);
        Optional<ClientType> clientType = clientTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clientType);
    }

    /**
     * {@code DELETE  /client-types/:id} : delete the "id" clientType.
     *
     * @param id the id of the clientType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientType : {}", id);
        clientTypeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
