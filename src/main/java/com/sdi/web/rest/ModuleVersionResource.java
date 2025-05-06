package com.sdi.web.rest;

import com.sdi.domain.ModuleVersion;
import com.sdi.repository.ModuleVersionRepository;
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
 * REST controller for managing {@link com.sdi.domain.ModuleVersion}.
 */
@RestController
@RequestMapping("/api/module-versions")
@Transactional
public class ModuleVersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleVersionResource.class);

    private static final String ENTITY_NAME = "moduleVersion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ModuleVersionRepository moduleVersionRepository;

    public ModuleVersionResource(ModuleVersionRepository moduleVersionRepository) {
        this.moduleVersionRepository = moduleVersionRepository;
    }

    /**
     * {@code POST  /module-versions} : Create a new moduleVersion.
     *
     * @param moduleVersion the moduleVersion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new moduleVersion, or with status {@code 400 (Bad Request)} if the moduleVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ModuleVersion> createModuleVersion(@Valid @RequestBody ModuleVersion moduleVersion) throws URISyntaxException {
        LOG.debug("REST request to save ModuleVersion : {}", moduleVersion);
        if (moduleVersion.getId() != null) {
            throw new BadRequestAlertException("A new moduleVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        moduleVersion = moduleVersionRepository.save(moduleVersion);
        return ResponseEntity.created(new URI("/api/module-versions/" + moduleVersion.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, moduleVersion.getId().toString()))
            .body(moduleVersion);
    }

    /**
     * {@code PUT  /module-versions/:id} : Updates an existing moduleVersion.
     *
     * @param id the id of the moduleVersion to save.
     * @param moduleVersion the moduleVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moduleVersion,
     * or with status {@code 400 (Bad Request)} if the moduleVersion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the moduleVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ModuleVersion> updateModuleVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ModuleVersion moduleVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to update ModuleVersion : {}, {}", id, moduleVersion);
        if (moduleVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, moduleVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moduleVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        moduleVersion = moduleVersionRepository.save(moduleVersion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, moduleVersion.getId().toString()))
            .body(moduleVersion);
    }

    /**
     * {@code PATCH  /module-versions/:id} : Partial updates given fields of an existing moduleVersion, field will ignore if it is null
     *
     * @param id the id of the moduleVersion to save.
     * @param moduleVersion the moduleVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moduleVersion,
     * or with status {@code 400 (Bad Request)} if the moduleVersion is not valid,
     * or with status {@code 404 (Not Found)} if the moduleVersion is not found,
     * or with status {@code 500 (Internal Server Error)} if the moduleVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ModuleVersion> partialUpdateModuleVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ModuleVersion moduleVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ModuleVersion partially : {}, {}", id, moduleVersion);
        if (moduleVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, moduleVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moduleVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ModuleVersion> result = moduleVersionRepository
            .findById(moduleVersion.getId())
            .map(existingModuleVersion -> {
                if (moduleVersion.getVersion() != null) {
                    existingModuleVersion.setVersion(moduleVersion.getVersion());
                }
                if (moduleVersion.getCreateDate() != null) {
                    existingModuleVersion.setCreateDate(moduleVersion.getCreateDate());
                }
                if (moduleVersion.getUpdateDate() != null) {
                    existingModuleVersion.setUpdateDate(moduleVersion.getUpdateDate());
                }
                if (moduleVersion.getNotes() != null) {
                    existingModuleVersion.setNotes(moduleVersion.getNotes());
                }

                return existingModuleVersion;
            })
            .map(moduleVersionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, moduleVersion.getId().toString())
        );
    }

    /**
     * {@code GET  /module-versions} : get all the moduleVersions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of moduleVersions in body.
     */
    @GetMapping("")
    public List<ModuleVersion> getAllModuleVersions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ModuleVersions");
        if (eagerload) {
            return moduleVersionRepository.findAllWithEagerRelationships();
        } else {
            return moduleVersionRepository.findAll();
        }
    }

    /**
     * {@code GET  /module-versions/:id} : get the "id" moduleVersion.
     *
     * @param id the id of the moduleVersion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the moduleVersion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModuleVersion> getModuleVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ModuleVersion : {}", id);
        Optional<ModuleVersion> moduleVersion = moduleVersionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(moduleVersion);
    }

    /**
     * {@code DELETE  /module-versions/:id} : delete the "id" moduleVersion.
     *
     * @param id the id of the moduleVersion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModuleVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ModuleVersion : {}", id);
        moduleVersionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
