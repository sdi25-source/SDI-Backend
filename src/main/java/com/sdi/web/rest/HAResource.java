package com.sdi.web.rest;

import com.sdi.domain.HA;
import com.sdi.repository.HARepository;
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
 * REST controller for managing {@link com.sdi.domain.HA}.
 */
@RestController
@RequestMapping("/api/has")
@Transactional
public class HAResource {

    private static final Logger LOG = LoggerFactory.getLogger(HAResource.class);

    private static final String ENTITY_NAME = "hA";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HARepository hARepository;

    public HAResource(HARepository hARepository) {
        this.hARepository = hARepository;
    }

    /**
     * {@code POST  /has} : Create a new hA.
     *
     * @param hA the hA to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hA, or with status {@code 400 (Bad Request)} if the hA has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HA> createHA(@Valid @RequestBody HA hA) throws URISyntaxException {
        LOG.debug("REST request to save HA : {}", hA);
        if (hA.getId() != null) {
            throw new BadRequestAlertException("A new hA cannot already have an ID", ENTITY_NAME, "idexists");
        }
        hA = hARepository.save(hA);
        return ResponseEntity.created(new URI("/api/has/" + hA.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, hA.getId().toString()))
            .body(hA);
    }

    /**
     * {@code PUT  /has/:id} : Updates an existing hA.
     *
     * @param id the id of the hA to save.
     * @param hA the hA to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hA,
     * or with status {@code 400 (Bad Request)} if the hA is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hA couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HA> updateHA(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody HA hA)
        throws URISyntaxException {
        LOG.debug("REST request to update HA : {}, {}", id, hA);
        if (hA.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hA.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hARepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        hA = hARepository.save(hA);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hA.getId().toString()))
            .body(hA);
    }

    /**
     * {@code PATCH  /has/:id} : Partial updates given fields of an existing hA, field will ignore if it is null
     *
     * @param id the id of the hA to save.
     * @param hA the hA to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hA,
     * or with status {@code 400 (Bad Request)} if the hA is not valid,
     * or with status {@code 404 (Not Found)} if the hA is not found,
     * or with status {@code 500 (Internal Server Error)} if the hA couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HA> partialUpdateHA(@PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody HA hA)
        throws URISyntaxException {
        LOG.debug("REST request to partial update HA partially : {}, {}", id, hA);
        if (hA.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hA.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hARepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HA> result = hARepository
            .findById(hA.getId())
            .map(existingHA -> {
                if (hA.getName() != null) {
                    existingHA.setName(hA.getName());
                }
                if (hA.getCreateDate() != null) {
                    existingHA.setCreateDate(hA.getCreateDate());
                }
                if (hA.getUpdateDate() != null) {
                    existingHA.setUpdateDate(hA.getUpdateDate());
                }
                if (hA.getNotes() != null) {
                    existingHA.setNotes(hA.getNotes());
                }

                return existingHA;
            })
            .map(hARepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hA.getId().toString())
        );
    }

    /**
     * {@code GET  /has} : get all the hAS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of hAS in body.
     */
    @GetMapping("")
    public List<HA> getAllHAS() {
        LOG.debug("REST request to get all HAS");
        return hARepository.findAll();
    }

    /**
     * {@code GET  /has/:id} : get the "id" hA.
     *
     * @param id the id of the hA to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hA, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HA> getHA(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HA : {}", id);
        Optional<HA> hA = hARepository.findById(id);
        return ResponseUtil.wrapOrNotFound(hA);
    }

    /**
     * {@code DELETE  /has/:id} : delete the "id" hA.
     *
     * @param id the id of the hA to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHA(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HA : {}", id);
        hARepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
