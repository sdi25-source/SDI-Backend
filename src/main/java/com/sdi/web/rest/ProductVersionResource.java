package com.sdi.web.rest;

import com.sdi.domain.ProductVersion;
import com.sdi.repository.ProductVersionRepository;
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
 * REST controller for managing {@link com.sdi.domain.ProductVersion}.
 */
@RestController
@RequestMapping("/api/product-versions")
@Transactional
public class ProductVersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductVersionResource.class);

    private static final String ENTITY_NAME = "productVersion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductVersionRepository productVersionRepository;

    public ProductVersionResource(ProductVersionRepository productVersionRepository) {
        this.productVersionRepository = productVersionRepository;
    }

    /**
     * {@code POST  /product-versions} : Create a new productVersion.
     *
     * @param productVersion the productVersion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productVersion, or with status {@code 400 (Bad Request)} if the productVersion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductVersion> createProductVersion(@Valid @RequestBody ProductVersion productVersion)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductVersion : {}", productVersion);
        if (productVersion.getId() != null) {
            throw new BadRequestAlertException("A new productVersion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productVersion = productVersionRepository.save(productVersion);
        return ResponseEntity.created(new URI("/api/product-versions/" + productVersion.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productVersion.getId().toString()))
            .body(productVersion);
    }

    /**
     * {@code PUT  /product-versions/:id} : Updates an existing productVersion.
     *
     * @param id the id of the productVersion to save.
     * @param productVersion the productVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVersion,
     * or with status {@code 400 (Bad Request)} if the productVersion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductVersion> updateProductVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductVersion productVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductVersion : {}, {}", id, productVersion);
        if (productVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productVersion = productVersionRepository.save(productVersion);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productVersion.getId().toString()))
            .body(productVersion);
    }

    /**
     * {@code PATCH  /product-versions/:id} : Partial updates given fields of an existing productVersion, field will ignore if it is null
     *
     * @param id the id of the productVersion to save.
     * @param productVersion the productVersion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productVersion,
     * or with status {@code 400 (Bad Request)} if the productVersion is not valid,
     * or with status {@code 404 (Not Found)} if the productVersion is not found,
     * or with status {@code 500 (Internal Server Error)} if the productVersion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductVersion> partialUpdateProductVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductVersion productVersion
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductVersion partially : {}, {}", id, productVersion);
        if (productVersion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productVersion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productVersionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductVersion> result = productVersionRepository
            .findById(productVersion.getId())
            .map(existingProductVersion -> {
                if (productVersion.getVersion() != null) {
                    existingProductVersion.setVersion(productVersion.getVersion());
                }
                if (productVersion.getCreateDate() != null) {
                    existingProductVersion.setCreateDate(productVersion.getCreateDate());
                }
                if (productVersion.getUpdateDate() != null) {
                    existingProductVersion.setUpdateDate(productVersion.getUpdateDate());
                }
                if (productVersion.getNotes() != null) {
                    existingProductVersion.setNotes(productVersion.getNotes());
                }

                return existingProductVersion;
            })
            .map(productVersionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productVersion.getId().toString())
        );
    }

    /**
     * {@code GET  /product-versions} : get all the productVersions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productVersions in body.
     */
    @GetMapping("")
    public List<ProductVersion> getAllProductVersions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ProductVersions");
        if (eagerload) {
            return productVersionRepository.findAllWithEagerRelationships();
        } else {
            return productVersionRepository.findAll();
        }
    }

    /**
     * {@code GET  /product-versions/:id} : get the "id" productVersion.
     *
     * @param id the id of the productVersion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productVersion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductVersion> getProductVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductVersion : {}", id);
        Optional<ProductVersion> productVersion = productVersionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(productVersion);
    }

    /**
     * {@code DELETE  /product-versions/:id} : delete the "id" productVersion.
     *
     * @param id the id of the productVersion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVersion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductVersion : {}", id);
        productVersionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
