package com.sdi.web.rest;

import com.sdi.domain.ClientEvent;
import com.sdi.repository.ClientEventRepository;
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
 * REST controller for managing {@link com.sdi.domain.ClientEvent}.
 */
@RestController
@RequestMapping("/api/client-events")
@Transactional
public class ClientEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientEventResource.class);

    private static final String ENTITY_NAME = "clientEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientEventRepository clientEventRepository;

    public ClientEventResource(ClientEventRepository clientEventRepository) {
        this.clientEventRepository = clientEventRepository;
    }

    /**
     * {@code POST  /client-events} : Create a new clientEvent.
     *
     * @param clientEvent the clientEvent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientEvent, or with status {@code 400 (Bad Request)} if the clientEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientEvent> createClientEvent(@Valid @RequestBody ClientEvent clientEvent) throws URISyntaxException {
        LOG.debug("REST request to save ClientEvent : {}", clientEvent);
        if (clientEvent.getId() != null) {
            throw new BadRequestAlertException("A new clientEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clientEvent = clientEventRepository.save(clientEvent);
        return ResponseEntity.created(new URI("/api/client-events/" + clientEvent.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clientEvent.getId().toString()))
            .body(clientEvent);
    }

    /**
     * {@code PUT  /client-events/:id} : Updates an existing clientEvent.
     *
     * @param id the id of the clientEvent to save.
     * @param clientEvent the clientEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientEvent,
     * or with status {@code 400 (Bad Request)} if the clientEvent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientEvent> updateClientEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientEvent clientEvent
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClientEvent : {}, {}", id, clientEvent);
        if (clientEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clientEvent = clientEventRepository.save(clientEvent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientEvent.getId().toString()))
            .body(clientEvent);
    }

    /**
     * {@code PATCH  /client-events/:id} : Partial updates given fields of an existing clientEvent, field will ignore if it is null
     *
     * @param id the id of the clientEvent to save.
     * @param clientEvent the clientEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientEvent,
     * or with status {@code 400 (Bad Request)} if the clientEvent is not valid,
     * or with status {@code 404 (Not Found)} if the clientEvent is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientEvent> partialUpdateClientEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientEvent clientEvent
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClientEvent partially : {}, {}", id, clientEvent);
        if (clientEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientEvent> result = clientEventRepository
            .findById(clientEvent.getId())
            .map(existingClientEvent -> {
                if (clientEvent.getEvent() != null) {
                    existingClientEvent.setEvent(clientEvent.getEvent());
                }
                if (clientEvent.getDescription() != null) {
                    existingClientEvent.setDescription(clientEvent.getDescription());
                }
                if (clientEvent.getEventDate() != null) {
                    existingClientEvent.setEventDate(clientEvent.getEventDate());
                }
                if (clientEvent.getNotes() != null) {
                    existingClientEvent.setNotes(clientEvent.getNotes());
                }

                return existingClientEvent;
            })
            .map(clientEventRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientEvent.getId().toString())
        );
    }

    /**
     * {@code GET  /client-events} : get all the clientEvents.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientEvents in body.
     */
    @GetMapping("")
    public List<ClientEvent> getAllClientEvents(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ClientEvents");
        if (eagerload) {
            return clientEventRepository.findAllWithEagerRelationships();
        } else {
            return clientEventRepository.findAll();
        }
    }

    /**
     * {@code GET  /client-events/:id} : get the "id" clientEvent.
     *
     * @param id the id of the clientEvent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientEvent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientEvent> getClientEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientEvent : {}", id);
        Optional<ClientEvent> clientEvent = clientEventRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(clientEvent);
    }

    /**
     * {@code DELETE  /client-events/:id} : delete the "id" clientEvent.
     *
     * @param id the id of the clientEvent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientEvent : {}", id);
        clientEventRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
