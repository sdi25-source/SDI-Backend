package com.sdi.web.rest;

import com.sdi.domain.ModuleDeployement;
import com.sdi.repository.ModuleDeployementRepository;
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
 * REST controller for managing {@link com.sdi.domain.ModuleDeployement}.
 */
@RestController
@RequestMapping("/api/module-deployements")
@Transactional
public class ModuleDeployementResource {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleDeployementResource.class);

    private static final String ENTITY_NAME = "moduleDeployement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ModuleDeployementRepository moduleDeployementRepository;

    public ModuleDeployementResource(ModuleDeployementRepository moduleDeployementRepository) {
        this.moduleDeployementRepository = moduleDeployementRepository;
    }

    /**
     * {@code POST  /module-deployements} : Create a new moduleDeployement.
     *
     * @param moduleDeployement the moduleDeployement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new moduleDeployement, or with status {@code 400 (Bad Request)} if the moduleDeployement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ModuleDeployement> createModuleDeployement(@Valid @RequestBody ModuleDeployement moduleDeployement)
        throws URISyntaxException {
        LOG.debug("REST request to save ModuleDeployement : {}", moduleDeployement);
        if (moduleDeployement.getId() != null) {
            throw new BadRequestAlertException("A new moduleDeployement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        moduleDeployement = moduleDeployementRepository.save(moduleDeployement);
        return ResponseEntity.created(new URI("/api/module-deployements/" + moduleDeployement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, moduleDeployement.getId().toString()))
            .body(moduleDeployement);
    }

    /**
     * {@code PUT  /module-deployements/:id} : Updates an existing moduleDeployement.
     *
     * @param id the id of the moduleDeployement to save.
     * @param moduleDeployement the moduleDeployement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moduleDeployement,
     * or with status {@code 400 (Bad Request)} if the moduleDeployement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the moduleDeployement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ModuleDeployement> updateModuleDeployement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ModuleDeployement moduleDeployement
    ) throws URISyntaxException {
        LOG.debug("REST request to update ModuleDeployement : {}, {}", id, moduleDeployement);
        if (moduleDeployement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, moduleDeployement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moduleDeployementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        moduleDeployement = moduleDeployementRepository.save(moduleDeployement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, moduleDeployement.getId().toString()))
            .body(moduleDeployement);
    }

    /**
     * {@code PATCH  /module-deployements/:id} : Partial updates given fields of an existing moduleDeployement, field will ignore if it is null
     *
     * @param id the id of the moduleDeployement to save.
     * @param moduleDeployement the moduleDeployement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moduleDeployement,
     * or with status {@code 400 (Bad Request)} if the moduleDeployement is not valid,
     * or with status {@code 404 (Not Found)} if the moduleDeployement is not found,
     * or with status {@code 500 (Internal Server Error)} if the moduleDeployement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ModuleDeployement> partialUpdateModuleDeployement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ModuleDeployement moduleDeployement
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ModuleDeployement partially : {}, {}", id, moduleDeployement);
        if (moduleDeployement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, moduleDeployement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moduleDeployementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ModuleDeployement> result = moduleDeployementRepository
            .findById(moduleDeployement.getId())
            .map(existingModuleDeployement -> {
                if (moduleDeployement.getCode() != null) {
                    existingModuleDeployement.setCode(moduleDeployement.getCode());
                }
                if (moduleDeployement.getNotes() != null) {
                    existingModuleDeployement.setNotes(moduleDeployement.getNotes());
                }
                if (moduleDeployement.getCreateDate() != null) {
                    existingModuleDeployement.setCreateDate(moduleDeployement.getCreateDate());
                }
                if (moduleDeployement.getUpdateDate() != null) {
                    existingModuleDeployement.setUpdateDate(moduleDeployement.getUpdateDate());
                }

                return existingModuleDeployement;
            })
            .map(moduleDeployementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, moduleDeployement.getId().toString())
        );
    }

    /**
     * {@code GET  /module-deployements} : get all the moduleDeployements.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of moduleDeployements in body.
     */
    @GetMapping("")
    public List<ModuleDeployement> getAllModuleDeployements(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ModuleDeployements");
        if (eagerload) {
            return moduleDeployementRepository.findAllWithEagerRelationships();
        } else {
            return moduleDeployementRepository.findAll();
        }
    }

    /**
     * {@code GET  /module-deployements/:id} : get the "id" moduleDeployement.
     *
     * @param id the id of the moduleDeployement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the moduleDeployement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModuleDeployement> getModuleDeployement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ModuleDeployement : {}", id);
        Optional<ModuleDeployement> moduleDeployement = moduleDeployementRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(moduleDeployement);
    }

    /**
     * {@code DELETE  /module-deployements/:id} : delete the "id" moduleDeployement.
     *
     * @param id the id of the moduleDeployement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModuleDeployement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ModuleDeployement : {}", id);
        moduleDeployementRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
