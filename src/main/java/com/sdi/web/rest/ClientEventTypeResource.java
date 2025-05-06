package com.sdi.web.rest;

import com.sdi.domain.ClientEventType;
import com.sdi.repository.ClientEventTypeRepository;
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
 * REST controller for managing {@link com.sdi.domain.ClientEventType}.
 */
@RestController
@RequestMapping("/api/client-event-types")
@Transactional
public class ClientEventTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientEventTypeResource.class);

    private static final String ENTITY_NAME = "clientEventType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientEventTypeRepository clientEventTypeRepository;

    public ClientEventTypeResource(ClientEventTypeRepository clientEventTypeRepository) {
        this.clientEventTypeRepository = clientEventTypeRepository;
    }

    /**
     * {@code POST  /client-event-types} : Create a new clientEventType.
     *
     * @param clientEventType the clientEventType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientEventType, or with status {@code 400 (Bad Request)} if the clientEventType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientEventType> createClientEventType(@Valid @RequestBody ClientEventType clientEventType)
        throws URISyntaxException {
        LOG.debug("REST request to save ClientEventType : {}", clientEventType);
        if (clientEventType.getId() != null) {
            throw new BadRequestAlertException("A new clientEventType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clientEventType = clientEventTypeRepository.save(clientEventType);
        return ResponseEntity.created(new URI("/api/client-event-types/" + clientEventType.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clientEventType.getId().toString()))
            .body(clientEventType);
    }

    /**
     * {@code PUT  /client-event-types/:id} : Updates an existing clientEventType.
     *
     * @param id the id of the clientEventType to save.
     * @param clientEventType the clientEventType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientEventType,
     * or with status {@code 400 (Bad Request)} if the clientEventType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientEventType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientEventType> updateClientEventType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientEventType clientEventType
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClientEventType : {}, {}", id, clientEventType);
        if (clientEventType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientEventType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientEventTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clientEventType = clientEventTypeRepository.save(clientEventType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientEventType.getId().toString()))
            .body(clientEventType);
    }

    /**
     * {@code PATCH  /client-event-types/:id} : Partial updates given fields of an existing clientEventType, field will ignore if it is null
     *
     * @param id the id of the clientEventType to save.
     * @param clientEventType the clientEventType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientEventType,
     * or with status {@code 400 (Bad Request)} if the clientEventType is not valid,
     * or with status {@code 404 (Not Found)} if the clientEventType is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientEventType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientEventType> partialUpdateClientEventType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientEventType clientEventType
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClientEventType partially : {}, {}", id, clientEventType);
        if (clientEventType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientEventType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientEventTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientEventType> result = clientEventTypeRepository
            .findById(clientEventType.getId())
            .map(existingClientEventType -> {
                if (clientEventType.getType() != null) {
                    existingClientEventType.setType(clientEventType.getType());
                }
                if (clientEventType.getDescription() != null) {
                    existingClientEventType.setDescription(clientEventType.getDescription());
                }
                if (clientEventType.getCreateDate() != null) {
                    existingClientEventType.setCreateDate(clientEventType.getCreateDate());
                }
                if (clientEventType.getUpdateDate() != null) {
                    existingClientEventType.setUpdateDate(clientEventType.getUpdateDate());
                }

                return existingClientEventType;
            })
            .map(clientEventTypeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientEventType.getId().toString())
        );
    }

    /**
     * {@code GET  /client-event-types} : get all the clientEventTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientEventTypes in body.
     */
    @GetMapping("")
    public List<ClientEventType> getAllClientEventTypes() {
        LOG.debug("REST request to get all ClientEventTypes");
        return clientEventTypeRepository.findAll();
    }

    /**
     * {@code GET  /client-event-types/:id} : get the "id" clientEventType.
     *
     * @param id the id of the clientEventType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientEventType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientEventType> getClientEventType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientEventType : {}", id);
        Optional<ClientEventType> clientEventType = clientEventTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clientEventType);
    }

    /**
     * {@code DELETE  /client-event-types/:id} : delete the "id" clientEventType.
     *
     * @param id the id of the clientEventType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientEventType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientEventType : {}", id);
        clientEventTypeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
