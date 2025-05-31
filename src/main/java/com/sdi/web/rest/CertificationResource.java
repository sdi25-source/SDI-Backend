package com.sdi.web.rest;

import com.sdi.domain.Certification;
import com.sdi.repository.CertificationRepository;
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
 * REST controller for managing {@link com.sdi.domain.Certification}.
 */
@RestController
@RequestMapping("/api/certifications")
@Transactional
public class CertificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(CertificationResource.class);

    private static final String ENTITY_NAME = "certification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CertificationRepository certificationRepository;

    public CertificationResource(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    /**
     * {@code POST  /certifications} : Create a new certification.
     *
     * @param certification the certification to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certification, or with status {@code 400 (Bad Request)} if the certification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Certification> createCertification(@Valid @RequestBody Certification certification) throws URISyntaxException {
        LOG.debug("REST request to save Certification : {}", certification);
        if (certification.getId() != null) {
            throw new BadRequestAlertException("A new certification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        certification = certificationRepository.save(certification);
        return ResponseEntity.created(new URI("/api/certifications/" + certification.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, certification.getId().toString()))
            .body(certification);
    }

    /**
     * {@code PUT  /certifications/:id} : Updates an existing certification.
     *
     * @param id the id of the certification to save.
     * @param certification the certification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certification,
     * or with status {@code 400 (Bad Request)} if the certification is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Certification> updateCertification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Certification certification
    ) throws URISyntaxException {
        LOG.debug("REST request to update Certification : {}, {}", id, certification);
        if (certification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        certification = certificationRepository.save(certification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certification.getId().toString()))
            .body(certification);
    }

    /**
     * {@code PATCH  /certifications/:id} : Partial updates given fields of an existing certification, field will ignore if it is null
     *
     * @param id the id of the certification to save.
     * @param certification the certification to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certification,
     * or with status {@code 400 (Bad Request)} if the certification is not valid,
     * or with status {@code 404 (Not Found)} if the certification is not found,
     * or with status {@code 500 (Internal Server Error)} if the certification couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Certification> partialUpdateCertification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Certification certification
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Certification partially : {}, {}", id, certification);
        if (certification.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certification.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Certification> result = certificationRepository
            .findById(certification.getId())
            .map(existingCertification -> {
                if (certification.getName() != null) {
                    existingCertification.setName(certification.getName());
                }
                if (certification.getCreateDate() != null) {
                    existingCertification.setCreateDate(certification.getCreateDate());
                }
                if (certification.getDescription() != null) {
                    existingCertification.setDescription(certification.getDescription());
                }

                return existingCertification;
            })
            .map(certificationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certification.getId().toString())
        );
    }

    /**
     * {@code GET  /certifications} : get all the certifications.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of certifications in body.
     */
    @GetMapping("")
    public List<Certification> getAllCertifications() {
        LOG.debug("REST request to get all Certifications");
        return certificationRepository.findAll();
    }

    /**
     * {@code GET  /certifications/:id} : get the "id" certification.
     *
     * @param id the id of the certification to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certification, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Certification> getCertification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Certification : {}", id);
        Optional<Certification> certification = certificationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(certification);
    }

    /**
     * {@code DELETE  /certifications/:id} : delete the "id" certification.
     *
     * @param id the id of the certification to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Certification : {}", id);
        certificationRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
