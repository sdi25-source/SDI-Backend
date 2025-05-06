package com.sdi.web.rest;

import com.sdi.domain.InfraComponentVersion;
import com.sdi.repository.InfraComponentVersionRepository;
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
 * REST controller for managing {@link com.sdi.domain.InfraComponentVersion}.
 */
@RestController
@RequestMapping("/api/infra-component-versions")
@Transactional
public class InfraComponentVersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(InfraComponentVersionResource.class);

    private static final String ENTITY_NAME = "infraComponentVersion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InfraComponentVersionRepository infraComponentVersionRepository;

    public InfraComponentVersionResource(InfraComponentVersionRepository infraComponentVersionRepository) {
        this.infraComponentVersionRepository = infraComponentVersionRepository;
    }

    /**
     * {@code POST  /infra-component-versions} : Create a new infraComponentVersion.
     *
     * @param infraComponentVersion the infraComponentVersion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new infraComponentVersion, or with status {@code 400 (Bad Request)} if the infraComponentVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InfraComponentVersion> createInfraComponentVersion(
        @Valid @RequestBody InfraComponentVersion infraComponentVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to save InfraComponentVersion : {}", infraComponentVersion);
        if (infraComponentVersion.getId() != null) {
            throw new BadRequestAlertException("A new infraComponentVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        infraComponentVersion = infraComponentVersionRepository.save(infraComponentVersion);
        return ResponseEntity.created(new URI("/api/infra-component-versions/" + infraComponentVersion.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, infraComponentVersion.getId().toString()))
            .body(infraComponentVersion);
    }

    /**
     * {@code PUT  /infra-component-versions/:id} : Updates an existing infraComponentVersion.
     *
     * @param id the id of the infraComponentVersion to save.
     * @param infraComponentVersion the infraComponentVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated infraComponentVersion,
     * or with status {@code 400 (Bad Request)} if the infraComponentVersion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the infraComponentVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InfraComponentVersion> updateInfraComponentVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InfraComponentVersion infraComponentVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to update InfraComponentVersion : {}, {}", id, infraComponentVersion);
        if (infraComponentVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, infraComponentVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!infraComponentVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        infraComponentVersion = infraComponentVersionRepository.save(infraComponentVersion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, infraComponentVersion.getId().toString()))
            .body(infraComponentVersion);
    }

    /**
     * {@code PATCH  /infra-component-versions/:id} : Partial updates given fields of an existing infraComponentVersion, field will ignore if it is null
     *
     * @param id the id of the infraComponentVersion to save.
     * @param infraComponentVersion the infraComponentVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated infraComponentVersion,
     * or with status {@code 400 (Bad Request)} if the infraComponentVersion is not valid,
     * or with status {@code 404 (Not Found)} if the infraComponentVersion is not found,
     * or with status {@code 500 (Internal Server Error)} if the infraComponentVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InfraComponentVersion> partialUpdateInfraComponentVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InfraComponentVersion infraComponentVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InfraComponentVersion partially : {}, {}", id, infraComponentVersion);
        if (infraComponentVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, infraComponentVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!infraComponentVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InfraComponentVersion> result = infraComponentVersionRepository
            .findById(infraComponentVersion.getId())
            .map(existingInfraComponentVersion -> {
                if (infraComponentVersion.getVersion() != null) {
                    existingInfraComponentVersion.setVersion(infraComponentVersion.getVersion());
                }
                if (infraComponentVersion.getDescription() != null) {
                    existingInfraComponentVersion.setDescription(infraComponentVersion.getDescription());
                }
                if (infraComponentVersion.getCreateDate() != null) {
                    existingInfraComponentVersion.setCreateDate(infraComponentVersion.getCreateDate());
                }
                if (infraComponentVersion.getUpdateDate() != null) {
                    existingInfraComponentVersion.setUpdateDate(infraComponentVersion.getUpdateDate());
                }

                return existingInfraComponentVersion;
            })
            .map(infraComponentVersionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, infraComponentVersion.getId().toString())
        );
    }

    /**
     * {@code GET  /infra-component-versions} : get all the infraComponentVersions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of infraComponentVersions in body.
     */
    @GetMapping("")
    public List<InfraComponentVersion> getAllInfraComponentVersions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all InfraComponentVersions");
        if (eagerload) {
            return infraComponentVersionRepository.findAllWithEagerRelationships();
        } else {
            return infraComponentVersionRepository.findAll();
        }
    }

    /**
     * {@code GET  /infra-component-versions/:id} : get the "id" infraComponentVersion.
     *
     * @param id the id of the infraComponentVersion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the infraComponentVersion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InfraComponentVersion> getInfraComponentVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InfraComponentVersion : {}", id);
        Optional<InfraComponentVersion> infraComponentVersion = infraComponentVersionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(infraComponentVersion);
    }

    /**
     * {@code DELETE  /infra-component-versions/:id} : delete the "id" infraComponentVersion.
     *
     * @param id the id of the infraComponentVersion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInfraComponentVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InfraComponentVersion : {}", id);
        infraComponentVersionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
