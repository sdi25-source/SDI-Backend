package com.sdi.web.rest;

import com.sdi.domain.FeatureDeployement;
import com.sdi.repository.FeatureDeployementRepository;
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
 * REST controller for managing {@link com.sdi.domain.FeatureDeployement}.
 */
@RestController
@RequestMapping("/api/feature-deployements")
@Transactional
public class FeatureDeployementResource {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureDeployementResource.class);

    private static final String ENTITY_NAME = "featureDeployement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeatureDeployementRepository featureDeployementRepository;

    public FeatureDeployementResource(FeatureDeployementRepository featureDeployementRepository) {
        this.featureDeployementRepository = featureDeployementRepository;
    }

    /**
     * {@code POST  /feature-deployements} : Create a new featureDeployement.
     *
     * @param featureDeployement the featureDeployement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new featureDeployement, or with status {@code 400 (Bad Request)} if the featureDeployement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FeatureDeployement> createFeatureDeployement(@Valid @RequestBody FeatureDeployement featureDeployement)
        throws URISyntaxException {
        LOG.debug("REST request to save FeatureDeployement : {}", featureDeployement);
        if (featureDeployement.getId() != null) {
            throw new BadRequestAlertException("A new featureDeployement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        featureDeployement = featureDeployementRepository.save(featureDeployement);
        return ResponseEntity.created(new URI("/api/feature-deployements/" + featureDeployement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, featureDeployement.getId().toString()))
            .body(featureDeployement);
    }

    /**
     * {@code PUT  /feature-deployements/:id} : Updates an existing featureDeployement.
     *
     * @param id the id of the featureDeployement to save.
     * @param featureDeployement the featureDeployement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated featureDeployement,
     * or with status {@code 400 (Bad Request)} if the featureDeployement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the featureDeployement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeatureDeployement> updateFeatureDeployement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FeatureDeployement featureDeployement
    ) throws URISyntaxException {
        LOG.debug("REST request to update FeatureDeployement : {}, {}", id, featureDeployement);
        if (featureDeployement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, featureDeployement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!featureDeployementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        featureDeployement = featureDeployementRepository.save(featureDeployement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, featureDeployement.getId().toString()))
            .body(featureDeployement);
    }

    /**
     * {@code PATCH  /feature-deployements/:id} : Partial updates given fields of an existing featureDeployement, field will ignore if it is null
     *
     * @param id the id of the featureDeployement to save.
     * @param featureDeployement the featureDeployement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated featureDeployement,
     * or with status {@code 400 (Bad Request)} if the featureDeployement is not valid,
     * or with status {@code 404 (Not Found)} if the featureDeployement is not found,
     * or with status {@code 500 (Internal Server Error)} if the featureDeployement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FeatureDeployement> partialUpdateFeatureDeployement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FeatureDeployement featureDeployement
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FeatureDeployement partially : {}, {}", id, featureDeployement);
        if (featureDeployement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, featureDeployement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!featureDeployementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FeatureDeployement> result = featureDeployementRepository
            .findById(featureDeployement.getId())
            .map(existingFeatureDeployement -> {
                if (featureDeployement.getCode() != null) {
                    existingFeatureDeployement.setCode(featureDeployement.getCode());
                }
                if (featureDeployement.getCreateDate() != null) {
                    existingFeatureDeployement.setCreateDate(featureDeployement.getCreateDate());
                }
                if (featureDeployement.getUpdateDate() != null) {
                    existingFeatureDeployement.setUpdateDate(featureDeployement.getUpdateDate());
                }
                if (featureDeployement.getNotes() != null) {
                    existingFeatureDeployement.setNotes(featureDeployement.getNotes());
                }

                return existingFeatureDeployement;
            })
            .map(featureDeployementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, featureDeployement.getId().toString())
        );
    }

    /**
     * {@code GET  /feature-deployements} : get all the featureDeployements.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of featureDeployements in body.
     */
    @GetMapping("")
    public List<FeatureDeployement> getAllFeatureDeployements(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all FeatureDeployements");
        if (eagerload) {
            return featureDeployementRepository.findAllWithEagerRelationships();
        } else {
            return featureDeployementRepository.findAll();
        }
    }

    /**
     * {@code GET  /feature-deployements/:id} : get the "id" featureDeployement.
     *
     * @param id the id of the featureDeployement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the featureDeployement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeatureDeployement> getFeatureDeployement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FeatureDeployement : {}", id);
        Optional<FeatureDeployement> featureDeployement = featureDeployementRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(featureDeployement);
    }

    /**
     * {@code DELETE  /feature-deployements/:id} : delete the "id" featureDeployement.
     *
     * @param id the id of the featureDeployement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeatureDeployement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FeatureDeployement : {}", id);
        featureDeployementRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
