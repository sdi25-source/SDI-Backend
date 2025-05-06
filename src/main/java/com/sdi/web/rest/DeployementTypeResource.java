package com.sdi.web.rest;

import com.sdi.domain.DeployementType;
import com.sdi.repository.DeployementTypeRepository;
import com.sdi.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.sdi.domain.DeployementType}.
 */
@RestController
@RequestMapping("/api/deployement-types")
@Transactional
public class DeployementTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeployementTypeResource.class);

    private static final String ENTITY_NAME = "deployementType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeployementTypeRepository deployementTypeRepository;

    public DeployementTypeResource(DeployementTypeRepository deployementTypeRepository) {
        this.deployementTypeRepository = deployementTypeRepository;
    }

    /**
     * {@code POST  /deployement-types} : Create a new deployementType.
     *
     * @param deployementType the deployementType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deployementType, or with status {@code 400 (Bad Request)} if the deployementType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DeployementType> createDeployementType(@RequestBody DeployementType deployementType) throws URISyntaxException {
        LOG.debug("REST request to save DeployementType : {}", deployementType);
        if (deployementType.getId() != null) {
            throw new BadRequestAlertException("A new deployementType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        deployementType = deployementTypeRepository.save(deployementType);
        return ResponseEntity.created(new URI("/api/deployement-types/" + deployementType.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, deployementType.getId().toString()))
            .body(deployementType);
    }

    /**
     * {@code PUT  /deployement-types/:id} : Updates an existing deployementType.
     *
     * @param id the id of the deployementType to save.
     * @param deployementType the deployementType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deployementType,
     * or with status {@code 400 (Bad Request)} if the deployementType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deployementType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeployementType> updateDeployementType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DeployementType deployementType
    ) throws URISyntaxException {
        LOG.debug("REST request to update DeployementType : {}, {}", id, deployementType);
        if (deployementType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deployementType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deployementTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        deployementType = deployementTypeRepository.save(deployementType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deployementType.getId().toString()))
            .body(deployementType);
    }

    /**
     * {@code PATCH  /deployement-types/:id} : Partial updates given fields of an existing deployementType, field will ignore if it is null
     *
     * @param id the id of the deployementType to save.
     * @param deployementType the deployementType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deployementType,
     * or with status {@code 400 (Bad Request)} if the deployementType is not valid,
     * or with status {@code 404 (Not Found)} if the deployementType is not found,
     * or with status {@code 500 (Internal Server Error)} if the deployementType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DeployementType> partialUpdateDeployementType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DeployementType deployementType
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DeployementType partially : {}, {}", id, deployementType);
        if (deployementType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deployementType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deployementTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DeployementType> result = deployementTypeRepository
            .findById(deployementType.getId())
            .map(existingDeployementType -> {
                if (deployementType.getType() != null) {
                    existingDeployementType.setType(deployementType.getType());
                }
                if (deployementType.getCreateDate() != null) {
                    existingDeployementType.setCreateDate(deployementType.getCreateDate());
                }
                if (deployementType.getUpdateDate() != null) {
                    existingDeployementType.setUpdateDate(deployementType.getUpdateDate());
                }
                if (deployementType.getNotes() != null) {
                    existingDeployementType.setNotes(deployementType.getNotes());
                }

                return existingDeployementType;
            })
            .map(deployementTypeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deployementType.getId().toString())
        );
    }

    /**
     * {@code GET  /deployement-types} : get all the deployementTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deployementTypes in body.
     */
    @GetMapping("")
    public List<DeployementType> getAllDeployementTypes() {
        LOG.debug("REST request to get all DeployementTypes");
        return deployementTypeRepository.findAll();
    }

    /**
     * {@code GET  /deployement-types/:id} : get the "id" deployementType.
     *
     * @param id the id of the deployementType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deployementType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeployementType> getDeployementType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DeployementType : {}", id);
        Optional<DeployementType> deployementType = deployementTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(deployementType);
    }

    /**
     * {@code DELETE  /deployement-types/:id} : delete the "id" deployementType.
     *
     * @param id the id of the deployementType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeployementType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DeployementType : {}", id);
        deployementTypeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
