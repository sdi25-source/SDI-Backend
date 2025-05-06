package com.sdi.web.rest;

import com.sdi.domain.Domaine;
import com.sdi.repository.DomaineRepository;
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
 * REST controller for managing {@link com.sdi.domain.Domaine}.
 */
@RestController
@RequestMapping("/api/domaines")
@Transactional
public class DomaineResource {

    private static final Logger LOG = LoggerFactory.getLogger(DomaineResource.class);

    private static final String ENTITY_NAME = "domaine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DomaineRepository domaineRepository;

    public DomaineResource(DomaineRepository domaineRepository) {
        this.domaineRepository = domaineRepository;
    }

    /**
     * {@code POST  /domaines} : Create a new domaine.
     *
     * @param domaine the domaine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new domaine, or with status {@code 400 (Bad Request)} if the domaine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Domaine> createDomaine(@Valid @RequestBody Domaine domaine) throws URISyntaxException {
        LOG.debug("REST request to save Domaine : {}", domaine);
        if (domaine.getId() != null) {
            throw new BadRequestAlertException("A new domaine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        domaine = domaineRepository.save(domaine);
        return ResponseEntity.created(new URI("/api/domaines/" + domaine.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, domaine.getId().toString()))
            .body(domaine);
    }

    /**
     * {@code PUT  /domaines/:id} : Updates an existing domaine.
     *
     * @param id the id of the domaine to save.
     * @param domaine the domaine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated domaine,
     * or with status {@code 400 (Bad Request)} if the domaine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the domaine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Domaine> updateDomaine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Domaine domaine
    ) throws URISyntaxException {
        LOG.debug("REST request to update Domaine : {}, {}", id, domaine);
        if (domaine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, domaine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!domaineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        domaine = domaineRepository.save(domaine);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, domaine.getId().toString()))
            .body(domaine);
    }

    /**
     * {@code PATCH  /domaines/:id} : Partial updates given fields of an existing domaine, field will ignore if it is null
     *
     * @param id the id of the domaine to save.
     * @param domaine the domaine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated domaine,
     * or with status {@code 400 (Bad Request)} if the domaine is not valid,
     * or with status {@code 404 (Not Found)} if the domaine is not found,
     * or with status {@code 500 (Internal Server Error)} if the domaine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Domaine> partialUpdateDomaine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Domaine domaine
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Domaine partially : {}, {}", id, domaine);
        if (domaine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, domaine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!domaineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Domaine> result = domaineRepository
            .findById(domaine.getId())
            .map(existingDomaine -> {
                if (domaine.getName() != null) {
                    existingDomaine.setName(domaine.getName());
                }
                if (domaine.getCreateDate() != null) {
                    existingDomaine.setCreateDate(domaine.getCreateDate());
                }
                if (domaine.getUpdateDate() != null) {
                    existingDomaine.setUpdateDate(domaine.getUpdateDate());
                }
                if (domaine.getNotes() != null) {
                    existingDomaine.setNotes(domaine.getNotes());
                }

                return existingDomaine;
            })
            .map(domaineRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, domaine.getId().toString())
        );
    }

    /**
     * {@code GET  /domaines} : get all the domaines.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of domaines in body.
     */
    @GetMapping("")
    public List<Domaine> getAllDomaines() {
        LOG.debug("REST request to get all Domaines");
        return domaineRepository.findAll();
    }

    /**
     * {@code GET  /domaines/:id} : get the "id" domaine.
     *
     * @param id the id of the domaine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the domaine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Domaine> getDomaine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Domaine : {}", id);
        Optional<Domaine> domaine = domaineRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(domaine);
    }

    /**
     * {@code DELETE  /domaines/:id} : delete the "id" domaine.
     *
     * @param id the id of the domaine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDomaine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Domaine : {}", id);
        domaineRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
