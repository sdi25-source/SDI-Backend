package com.sdi.web.rest;

import com.sdi.domain.ClientCertification;
import com.sdi.repository.ClientCertificationRepository;
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
 * REST controller for managing {@link com.sdi.domain.ClientCertification}.
 */
@RestController
@RequestMapping("/api/client-certifications")
@Transactional
public class ClientCertificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientCertificationResource.class);

    private static final String ENTITY_NAME = "clientCertification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientCertificationRepository clientCertificationRepository;

    public ClientCertificationResource(ClientCertificationRepository clientCertificationRepository) {
        this.clientCertificationRepository = clientCertificationRepository;
    }

    /**
     * {@code POST  /client-certifications} : Create a new clientCertification.
     *
     * @param clientCertification the clientCertification to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientCertification, or with status {@code 400 (Bad Request)} if the clientCertification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientCertification> createClientCertification(@Valid @RequestBody ClientCertification clientCertification)
        throws URISyntaxException {
        LOG.debug("REST request to save ClientCertification : {}", clientCertification);
        if (clientCertification.getId() != null) {
            throw new BadRequestAlertException("A new clientCertification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clientCertification = clientCertificationRepository.save(clientCertification);
        return ResponseEntity.created(new URI("/api/client-certifications/" + clientCertification.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clientCertification.getId().toString()))
            .body(clientCertification);
    }

    /**
     * {@code PUT  /client-certifications/:id} : Updates an existing clientCertification.
     *
     * @param id the id of the clientCertification to save.
     * @param clientCertification the clientCertification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientCertification,
     * or with status {@code 400 (Bad Request)} if the clientCertification is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientCertification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientCertification> updateClientCertification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientCertification clientCertification
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClientCertification : {}, {}", id, clientCertification);
        if (clientCertification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientCertification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientCertificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clientCertification = clientCertificationRepository.save(clientCertification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientCertification.getId().toString()))
            .body(clientCertification);
    }

    /**
     * {@code PATCH  /client-certifications/:id} : Partial updates given fields of an existing clientCertification, field will ignore if it is null
     *
     * @param id the id of the clientCertification to save.
     * @param clientCertification the clientCertification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientCertification,
     * or with status {@code 400 (Bad Request)} if the clientCertification is not valid,
     * or with status {@code 404 (Not Found)} if the clientCertification is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientCertification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientCertification> partialUpdateClientCertification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientCertification clientCertification
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClientCertification partially : {}, {}", id, clientCertification);
        if (clientCertification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientCertification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientCertificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientCertification> result = clientCertificationRepository
            .findById(clientCertification.getId())
            .map(existingClientCertification -> {
                if (clientCertification.getCertification() != null) {
                    existingClientCertification.setCertification(clientCertification.getCertification());
                }
                if (clientCertification.getCertificationDate() != null) {
                    existingClientCertification.setCertificationDate(clientCertification.getCertificationDate());
                }
                if (clientCertification.getCreateDate() != null) {
                    existingClientCertification.setCreateDate(clientCertification.getCreateDate());
                }
                if (clientCertification.getUpdateDate() != null) {
                    existingClientCertification.setUpdateDate(clientCertification.getUpdateDate());
                }
                if (clientCertification.getNotes() != null) {
                    existingClientCertification.setNotes(clientCertification.getNotes());
                }

                return existingClientCertification;
            })
            .map(clientCertificationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientCertification.getId().toString())
        );
    }

    /**
     * {@code GET  /client-certifications} : get all the clientCertifications.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientCertifications in body.
     */
    @GetMapping("")
    public List<ClientCertification> getAllClientCertifications(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ClientCertifications");
        if (eagerload) {
            return clientCertificationRepository.findAllWithEagerRelationships();
        } else {
            return clientCertificationRepository.findAll();
        }
    }

    /**
     * {@code GET  /client-certifications/:id} : get the "id" clientCertification.
     *
     * @param id the id of the clientCertification to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientCertification, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientCertification> getClientCertification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientCertification : {}", id);
        Optional<ClientCertification> clientCertification = clientCertificationRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(clientCertification);
    }

    /**
     * {@code DELETE  /client-certifications/:id} : delete the "id" clientCertification.
     *
     * @param id the id of the clientCertification to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientCertification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientCertification : {}", id);
        clientCertificationRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
