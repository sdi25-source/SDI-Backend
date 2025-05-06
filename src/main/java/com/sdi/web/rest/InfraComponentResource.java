package com.sdi.web.rest;

import com.sdi.domain.InfraComponent;
import com.sdi.repository.InfraComponentRepository;
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
 * REST controller for managing {@link com.sdi.domain.InfraComponent}.
 */
@RestController
@RequestMapping("/api/infra-components")
@Transactional
public class InfraComponentResource {

    private static final Logger LOG = LoggerFactory.getLogger(InfraComponentResource.class);

    private static final String ENTITY_NAME = "infraComponent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InfraComponentRepository infraComponentRepository;

    public InfraComponentResource(InfraComponentRepository infraComponentRepository) {
        this.infraComponentRepository = infraComponentRepository;
    }

    /**
     * {@code POST  /infra-components} : Create a new infraComponent.
     *
     * @param infraComponent the infraComponent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new infraComponent, or with status {@code 400 (Bad Request)} if the infraComponent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InfraComponent> createInfraComponent(@Valid @RequestBody InfraComponent infraComponent)
        throws URISyntaxException {
        LOG.debug("REST request to save InfraComponent : {}", infraComponent);
        if (infraComponent.getId() != null) {
            throw new BadRequestAlertException("A new infraComponent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        infraComponent = infraComponentRepository.save(infraComponent);
        return ResponseEntity.created(new URI("/api/infra-components/" + infraComponent.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, infraComponent.getId().toString()))
            .body(infraComponent);
    }

    /**
     * {@code PUT  /infra-components/:id} : Updates an existing infraComponent.
     *
     * @param id the id of the infraComponent to save.
     * @param infraComponent the infraComponent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated infraComponent,
     * or with status {@code 400 (Bad Request)} if the infraComponent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the infraComponent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InfraComponent> updateInfraComponent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InfraComponent infraComponent
    ) throws URISyntaxException {
        LOG.debug("REST request to update InfraComponent : {}, {}", id, infraComponent);
        if (infraComponent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, infraComponent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!infraComponentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        infraComponent = infraComponentRepository.save(infraComponent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, infraComponent.getId().toString()))
            .body(infraComponent);
    }

    /**
     * {@code PATCH  /infra-components/:id} : Partial updates given fields of an existing infraComponent, field will ignore if it is null
     *
     * @param id the id of the infraComponent to save.
     * @param infraComponent the infraComponent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated infraComponent,
     * or with status {@code 400 (Bad Request)} if the infraComponent is not valid,
     * or with status {@code 404 (Not Found)} if the infraComponent is not found,
     * or with status {@code 500 (Internal Server Error)} if the infraComponent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InfraComponent> partialUpdateInfraComponent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InfraComponent infraComponent
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InfraComponent partially : {}, {}", id, infraComponent);
        if (infraComponent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, infraComponent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!infraComponentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InfraComponent> result = infraComponentRepository
            .findById(infraComponent.getId())
            .map(existingInfraComponent -> {
                if (infraComponent.getName() != null) {
                    existingInfraComponent.setName(infraComponent.getName());
                }
                if (infraComponent.getVendor() != null) {
                    existingInfraComponent.setVendor(infraComponent.getVendor());
                }
                if (infraComponent.getNotes() != null) {
                    existingInfraComponent.setNotes(infraComponent.getNotes());
                }
                if (infraComponent.getCreateDate() != null) {
                    existingInfraComponent.setCreateDate(infraComponent.getCreateDate());
                }

                return existingInfraComponent;
            })
            .map(infraComponentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, infraComponent.getId().toString())
        );
    }

    /**
     * {@code GET  /infra-components} : get all the infraComponents.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of infraComponents in body.
     */
    @GetMapping("")
    public List<InfraComponent> getAllInfraComponents(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all InfraComponents");
        if (eagerload) {
            return infraComponentRepository.findAllWithEagerRelationships();
        } else {
            return infraComponentRepository.findAll();
        }
    }

    /**
     * {@code GET  /infra-components/:id} : get the "id" infraComponent.
     *
     * @param id the id of the infraComponent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the infraComponent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InfraComponent> getInfraComponent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InfraComponent : {}", id);
        Optional<InfraComponent> infraComponent = infraComponentRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(infraComponent);
    }

    /**
     * {@code DELETE  /infra-components/:id} : delete the "id" infraComponent.
     *
     * @param id the id of the infraComponent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInfraComponent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InfraComponent : {}", id);
        infraComponentRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
