package com.sdi.web.rest;

import com.sdi.domain.RequestOfChange;
import com.sdi.repository.RequestOfChangeRepository;
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
 * REST controller for managing {@link com.sdi.domain.RequestOfChange}.
 */
@RestController
@RequestMapping("/api/request-of-changes")
@Transactional
public class RequestOfChangeResource {

    private static final Logger LOG = LoggerFactory.getLogger(RequestOfChangeResource.class);

    private static final String ENTITY_NAME = "requestOfChange";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RequestOfChangeRepository requestOfChangeRepository;

    public RequestOfChangeResource(RequestOfChangeRepository requestOfChangeRepository) {
        this.requestOfChangeRepository = requestOfChangeRepository;
    }

    /**
     * {@code POST  /request-of-changes} : Create a new requestOfChange.
     *
     * @param requestOfChange the requestOfChange to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new requestOfChange, or with status {@code 400 (Bad Request)} if the requestOfChange has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RequestOfChange> createRequestOfChange(@Valid @RequestBody RequestOfChange requestOfChange)
        throws URISyntaxException {
        LOG.debug("REST request to save RequestOfChange : {}", requestOfChange);
        if (requestOfChange.getId() != null) {
            throw new BadRequestAlertException("A new requestOfChange cannot already have an ID", ENTITY_NAME, "idexists");
        }
        requestOfChange = requestOfChangeRepository.save(requestOfChange);
        return ResponseEntity.created(new URI("/api/request-of-changes/" + requestOfChange.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, requestOfChange.getId().toString()))
            .body(requestOfChange);
    }

    /**
     * {@code PUT  /request-of-changes/:id} : Updates an existing requestOfChange.
     *
     * @param id the id of the requestOfChange to save.
     * @param requestOfChange the requestOfChange to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requestOfChange,
     * or with status {@code 400 (Bad Request)} if the requestOfChange is not valid,
     * or with status {@code 500 (Internal Server Error)} if the requestOfChange couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RequestOfChange> updateRequestOfChange(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RequestOfChange requestOfChange
    ) throws URISyntaxException {
        LOG.debug("REST request to update RequestOfChange : {}, {}", id, requestOfChange);
        if (requestOfChange.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requestOfChange.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requestOfChangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        requestOfChange = requestOfChangeRepository.save(requestOfChange);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, requestOfChange.getId().toString()))
            .body(requestOfChange);
    }

    /**
     * {@code PATCH  /request-of-changes/:id} : Partial updates given fields of an existing requestOfChange, field will ignore if it is null
     *
     * @param id the id of the requestOfChange to save.
     * @param requestOfChange the requestOfChange to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requestOfChange,
     * or with status {@code 400 (Bad Request)} if the requestOfChange is not valid,
     * or with status {@code 404 (Not Found)} if the requestOfChange is not found,
     * or with status {@code 500 (Internal Server Error)} if the requestOfChange couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RequestOfChange> partialUpdateRequestOfChange(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RequestOfChange requestOfChange
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RequestOfChange partially : {}, {}", id, requestOfChange);
        if (requestOfChange.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requestOfChange.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!requestOfChangeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RequestOfChange> result = requestOfChangeRepository
            .findById(requestOfChange.getId())
            .map(existingRequestOfChange -> {
                if (requestOfChange.getTitle() != null) {
                    existingRequestOfChange.setTitle(requestOfChange.getTitle());
                }
                if (requestOfChange.getKeywords() != null) {
                    existingRequestOfChange.setKeywords(requestOfChange.getKeywords());
                }
                if (requestOfChange.getStatus() != null) {
                    existingRequestOfChange.setStatus(requestOfChange.getStatus());
                }
                if (requestOfChange.getCreateDate() != null) {
                    existingRequestOfChange.setCreateDate(requestOfChange.getCreateDate());
                }
                if (requestOfChange.getDescription() != null) {
                    existingRequestOfChange.setDescription(requestOfChange.getDescription());
                }

                return existingRequestOfChange;
            })
            .map(requestOfChangeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, requestOfChange.getId().toString())
        );
    }

    /**
     * {@code GET  /request-of-changes} : get all the requestOfChanges.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requestOfChanges in body.
     */
    @GetMapping("")
    public List<RequestOfChange> getAllRequestOfChanges(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all RequestOfChanges");
        if (eagerload) {
            return requestOfChangeRepository.findAllWithEagerRelationships();
        } else {
            return requestOfChangeRepository.findAll();
        }
    }

    /**
     * {@code GET  /request-of-changes/:id} : get the "id" requestOfChange.
     *
     * @param id the id of the requestOfChange to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the requestOfChange, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RequestOfChange> getRequestOfChange(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RequestOfChange : {}", id);
        Optional<RequestOfChange> requestOfChange = requestOfChangeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(requestOfChange);
    }

    /**
     * {@code DELETE  /request-of-changes/:id} : delete the "id" requestOfChange.
     *
     * @param id the id of the requestOfChange to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequestOfChange(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RequestOfChange : {}", id);
        requestOfChangeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
