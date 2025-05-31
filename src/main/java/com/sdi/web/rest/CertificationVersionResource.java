package com.sdi.web.rest;

import com.sdi.domain.CertificationVersion;
import com.sdi.repository.CertificationVersionRepository;
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
 * REST controller for managing {@link com.sdi.domain.CertificationVersion}.
 */
@RestController
@RequestMapping("/api/certification-versions")
@Transactional
public class CertificationVersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CertificationVersionResource.class);

    private static final String ENTITY_NAME = "certificationVersion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CertificationVersionRepository certificationVersionRepository;

    public CertificationVersionResource(CertificationVersionRepository certificationVersionRepository) {
        this.certificationVersionRepository = certificationVersionRepository;
    }

    /**
     * {@code POST  /certification-versions} : Create a new certificationVersion.
     *
     * @param certificationVersion the certificationVersion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificationVersion, or with status {@code 400 (Bad Request)} if the certificationVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CertificationVersion> createCertificationVersion(@Valid @RequestBody CertificationVersion certificationVersion)
        throws URISyntaxException {
        LOG.debug("REST request to save CertificationVersion : {}", certificationVersion);
        if (certificationVersion.getId() != null) {
            throw new BadRequestAlertException("A new certificationVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        certificationVersion = certificationVersionRepository.save(certificationVersion);
        return ResponseEntity.created(new URI("/api/certification-versions/" + certificationVersion.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, certificationVersion.getId().toString()))
            .body(certificationVersion);
    }

    /**
     * {@code PUT  /certification-versions/:id} : Updates an existing certificationVersion.
     *
     * @param id the id of the certificationVersion to save.
     * @param certificationVersion the certificationVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificationVersion,
     * or with status {@code 400 (Bad Request)} if the certificationVersion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certificationVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CertificationVersion> updateCertificationVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CertificationVersion certificationVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to update CertificationVersion : {}, {}", id, certificationVersion);
        if (certificationVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificationVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificationVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        certificationVersion = certificationVersionRepository.save(certificationVersion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificationVersion.getId().toString()))
            .body(certificationVersion);
    }

    /**
     * {@code PATCH  /certification-versions/:id} : Partial updates given fields of an existing certificationVersion, field will ignore if it is null
     *
     * @param id the id of the certificationVersion to save.
     * @param certificationVersion the certificationVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificationVersion,
     * or with status {@code 400 (Bad Request)} if the certificationVersion is not valid,
     * or with status {@code 404 (Not Found)} if the certificationVersion is not found,
     * or with status {@code 500 (Internal Server Error)} if the certificationVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CertificationVersion> partialUpdateCertificationVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CertificationVersion certificationVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CertificationVersion partially : {}, {}", id, certificationVersion);
        if (certificationVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, certificationVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!certificationVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CertificationVersion> result = certificationVersionRepository
            .findById(certificationVersion.getId())
            .map(existingCertificationVersion -> {
                if (certificationVersion.getVersion() != null) {
                    existingCertificationVersion.setVersion(certificationVersion.getVersion());
                }
                if (certificationVersion.getCreateDate() != null) {
                    existingCertificationVersion.setCreateDate(certificationVersion.getCreateDate());
                }
                if (certificationVersion.getExpireDate() != null) {
                    existingCertificationVersion.setExpireDate(certificationVersion.getExpireDate());
                }
                if (certificationVersion.getDescription() != null) {
                    existingCertificationVersion.setDescription(certificationVersion.getDescription());
                }

                return existingCertificationVersion;
            })
            .map(certificationVersionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificationVersion.getId().toString())
        );
    }

    /**
     * {@code GET  /certification-versions} : get all the certificationVersions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of certificationVersions in body.
     */
    @GetMapping("")
    public List<CertificationVersion> getAllCertificationVersions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all CertificationVersions");
        if (eagerload) {
            return certificationVersionRepository.findAllWithEagerRelationships();
        } else {
            return certificationVersionRepository.findAll();
        }
    }

    /**
     * {@code GET  /certification-versions/:id} : get the "id" certificationVersion.
     *
     * @param id the id of the certificationVersion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificationVersion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CertificationVersion> getCertificationVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CertificationVersion : {}", id);
        Optional<CertificationVersion> certificationVersion = certificationVersionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(certificationVersion);
    }

    /**
     * {@code DELETE  /certification-versions/:id} : delete the "id" certificationVersion.
     *
     * @param id the id of the certificationVersion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificationVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CertificationVersion : {}", id);
        certificationVersionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
