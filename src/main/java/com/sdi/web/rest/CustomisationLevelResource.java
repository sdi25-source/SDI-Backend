package com.sdi.web.rest;

import com.sdi.domain.CustomisationLevel;
import com.sdi.repository.CustomisationLevelRepository;
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
 * REST controller for managing {@link com.sdi.domain.CustomisationLevel}.
 */
@RestController
@RequestMapping("/api/customisation-levels")
@Transactional
public class CustomisationLevelResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomisationLevelResource.class);

    private static final String ENTITY_NAME = "customisationLevel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomisationLevelRepository customisationLevelRepository;

    public CustomisationLevelResource(CustomisationLevelRepository customisationLevelRepository) {
        this.customisationLevelRepository = customisationLevelRepository;
    }

    /**
     * {@code POST  /customisation-levels} : Create a new customisationLevel.
     *
     * @param customisationLevel the customisationLevel to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customisationLevel, or with status {@code 400 (Bad Request)} if the customisationLevel has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomisationLevel> createCustomisationLevel(@Valid @RequestBody CustomisationLevel customisationLevel)
        throws URISyntaxException {
        LOG.debug("REST request to save CustomisationLevel : {}", customisationLevel);
        if (customisationLevel.getId() != null) {
            throw new BadRequestAlertException("A new customisationLevel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        customisationLevel = customisationLevelRepository.save(customisationLevel);
        return ResponseEntity.created(new URI("/api/customisation-levels/" + customisationLevel.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, customisationLevel.getId().toString()))
            .body(customisationLevel);
    }

    /**
     * {@code PUT  /customisation-levels/:id} : Updates an existing customisationLevel.
     *
     * @param id the id of the customisationLevel to save.
     * @param customisationLevel the customisationLevel to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customisationLevel,
     * or with status {@code 400 (Bad Request)} if the customisationLevel is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customisationLevel couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomisationLevel> updateCustomisationLevel(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CustomisationLevel customisationLevel
    ) throws URISyntaxException {
        LOG.debug("REST request to update CustomisationLevel : {}, {}", id, customisationLevel);
        if (customisationLevel.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customisationLevel.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customisationLevelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        customisationLevel = customisationLevelRepository.save(customisationLevel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customisationLevel.getId().toString()))
            .body(customisationLevel);
    }

    /**
     * {@code PATCH  /customisation-levels/:id} : Partial updates given fields of an existing customisationLevel, field will ignore if it is null
     *
     * @param id the id of the customisationLevel to save.
     * @param customisationLevel the customisationLevel to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customisationLevel,
     * or with status {@code 400 (Bad Request)} if the customisationLevel is not valid,
     * or with status {@code 404 (Not Found)} if the customisationLevel is not found,
     * or with status {@code 500 (Internal Server Error)} if the customisationLevel couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CustomisationLevel> partialUpdateCustomisationLevel(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CustomisationLevel customisationLevel
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CustomisationLevel partially : {}, {}", id, customisationLevel);
        if (customisationLevel.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customisationLevel.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customisationLevelRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomisationLevel> result = customisationLevelRepository
            .findById(customisationLevel.getId())
            .map(existingCustomisationLevel -> {
                if (customisationLevel.getLevel() != null) {
                    existingCustomisationLevel.setLevel(customisationLevel.getLevel());
                }
                if (customisationLevel.getCreateDate() != null) {
                    existingCustomisationLevel.setCreateDate(customisationLevel.getCreateDate());
                }
                if (customisationLevel.getUpdateDate() != null) {
                    existingCustomisationLevel.setUpdateDate(customisationLevel.getUpdateDate());
                }
                if (customisationLevel.getNotes() != null) {
                    existingCustomisationLevel.setNotes(customisationLevel.getNotes());
                }

                return existingCustomisationLevel;
            })
            .map(customisationLevelRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customisationLevel.getId().toString())
        );
    }

    /**
     * {@code GET  /customisation-levels} : get all the customisationLevels.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customisationLevels in body.
     */
    @GetMapping("")
    public List<CustomisationLevel> getAllCustomisationLevels() {
        LOG.debug("REST request to get all CustomisationLevels");
        return customisationLevelRepository.findAll();
    }

    /**
     * {@code GET  /customisation-levels/:id} : get the "id" customisationLevel.
     *
     * @param id the id of the customisationLevel to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customisationLevel, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomisationLevel> getCustomisationLevel(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CustomisationLevel : {}", id);
        Optional<CustomisationLevel> customisationLevel = customisationLevelRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(customisationLevel);
    }

    /**
     * {@code DELETE  /customisation-levels/:id} : delete the "id" customisationLevel.
     *
     * @param id the id of the customisationLevel to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomisationLevel(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CustomisationLevel : {}", id);
        customisationLevelRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
