package com.sdi.web.rest;

import com.sdi.domain.ComponentType;
import com.sdi.repository.ComponentTypeRepository;
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
 * REST controller for managing {@link com.sdi.domain.ComponentType}.
 */
@RestController
@RequestMapping("/api/component-types")
@Transactional
public class ComponentTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTypeResource.class);

    private static final String ENTITY_NAME = "componentType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ComponentTypeRepository componentTypeRepository;

    public ComponentTypeResource(ComponentTypeRepository componentTypeRepository) {
        this.componentTypeRepository = componentTypeRepository;
    }

    /**
     * {@code POST  /component-types} : Create a new componentType.
     *
     * @param componentType the componentType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new componentType, or with status {@code 400 (Bad Request)} if the componentType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ComponentType> createComponentType(@Valid @RequestBody ComponentType componentType) throws URISyntaxException {
        LOG.debug("REST request to save ComponentType : {}", componentType);
        if (componentType.getId() != null) {
            throw new BadRequestAlertException("A new componentType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        componentType = componentTypeRepository.save(componentType);
        return ResponseEntity.created(new URI("/api/component-types/" + componentType.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, componentType.getId().toString()))
            .body(componentType);
    }

    /**
     * {@code PUT  /component-types/:id} : Updates an existing componentType.
     *
     * @param id the id of the componentType to save.
     * @param componentType the componentType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated componentType,
     * or with status {@code 400 (Bad Request)} if the componentType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the componentType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ComponentType> updateComponentType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ComponentType componentType
    ) throws URISyntaxException {
        LOG.debug("REST request to update ComponentType : {}, {}", id, componentType);
        if (componentType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, componentType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!componentTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        componentType = componentTypeRepository.save(componentType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, componentType.getId().toString()))
            .body(componentType);
    }

    /**
     * {@code PATCH  /component-types/:id} : Partial updates given fields of an existing componentType, field will ignore if it is null
     *
     * @param id the id of the componentType to save.
     * @param componentType the componentType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated componentType,
     * or with status {@code 400 (Bad Request)} if the componentType is not valid,
     * or with status {@code 404 (Not Found)} if the componentType is not found,
     * or with status {@code 500 (Internal Server Error)} if the componentType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ComponentType> partialUpdateComponentType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ComponentType componentType
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ComponentType partially : {}, {}", id, componentType);
        if (componentType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, componentType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!componentTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ComponentType> result = componentTypeRepository
            .findById(componentType.getId())
            .map(existingComponentType -> {
                if (componentType.getType() != null) {
                    existingComponentType.setType(componentType.getType());
                }

                return existingComponentType;
            })
            .map(componentTypeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, componentType.getId().toString())
        );
    }

    /**
     * {@code GET  /component-types} : get all the componentTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of componentTypes in body.
     */
    @GetMapping("")
    public List<ComponentType> getAllComponentTypes() {
        LOG.debug("REST request to get all ComponentTypes");
        return componentTypeRepository.findAll();
    }

    /**
     * {@code GET  /component-types/:id} : get the "id" componentType.
     *
     * @param id the id of the componentType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the componentType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComponentType> getComponentType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ComponentType : {}", id);
        Optional<ComponentType> componentType = componentTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(componentType);
    }

    /**
     * {@code DELETE  /component-types/:id} : delete the "id" componentType.
     *
     * @param id the id of the componentType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponentType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ComponentType : {}", id);
        componentTypeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
