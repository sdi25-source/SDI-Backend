package com.sdi.web.rest;

import com.sdi.domain.ProductDeployement;
import com.sdi.repository.ProductDeployementRepository;
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
 * REST controller for managing {@link com.sdi.domain.ProductDeployement}.
 */
@RestController
@RequestMapping("/api/product-deployements")
@Transactional
public class ProductDeployementResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductDeployementResource.class);

    private static final String ENTITY_NAME = "productDeployement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductDeployementRepository productDeployementRepository;

    public ProductDeployementResource(ProductDeployementRepository productDeployementRepository) {
        this.productDeployementRepository = productDeployementRepository;
    }

    /**
     * {@code POST  /product-deployements} : Create a new productDeployement.
     *
     * @param productDeployement the productDeployement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productDeployement, or with status {@code 400 (Bad Request)} if the productDeployement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductDeployement> createProductDeployement(@Valid @RequestBody ProductDeployement productDeployement)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductDeployement : {}", productDeployement);
        if (productDeployement.getId() != null) {
            throw new BadRequestAlertException("A new productDeployement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productDeployement = productDeployementRepository.save(productDeployement);
        return ResponseEntity.created(new URI("/api/product-deployements/" + productDeployement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productDeployement.getId().toString()))
            .body(productDeployement);
    }

    /**
     * {@code PUT  /product-deployements/:id} : Updates an existing productDeployement.
     *
     * @param id the id of the productDeployement to save.
     * @param productDeployement the productDeployement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDeployement,
     * or with status {@code 400 (Bad Request)} if the productDeployement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productDeployement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDeployement> updateProductDeployement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductDeployement productDeployement
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductDeployement : {}, {}", id, productDeployement);
        if (productDeployement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productDeployement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productDeployementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productDeployement = productDeployementRepository.save(productDeployement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDeployement.getId().toString()))
            .body(productDeployement);
    }

    /**
     * {@code PATCH  /product-deployements/:id} : Partial updates given fields of an existing productDeployement, field will ignore if it is null
     *
     * @param id the id of the productDeployement to save.
     * @param productDeployement the productDeployement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDeployement,
     * or with status {@code 400 (Bad Request)} if the productDeployement is not valid,
     * or with status {@code 404 (Not Found)} if the productDeployement is not found,
     * or with status {@code 500 (Internal Server Error)} if the productDeployement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductDeployement> partialUpdateProductDeployement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductDeployement productDeployement
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductDeployement partially : {}, {}", id, productDeployement);
        if (productDeployement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productDeployement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productDeployementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductDeployement> result = productDeployementRepository
            .findById(productDeployement.getId())
            .map(existingProductDeployement -> {
                if (productDeployement.getRefContract() != null) {
                    existingProductDeployement.setRefContract(productDeployement.getRefContract());
                }
                if (productDeployement.getCreateDate() != null) {
                    existingProductDeployement.setCreateDate(productDeployement.getCreateDate());
                }
                if (productDeployement.getUpdateDate() != null) {
                    existingProductDeployement.setUpdateDate(productDeployement.getUpdateDate());
                }
                if (productDeployement.getNotes() != null) {
                    existingProductDeployement.setNotes(productDeployement.getNotes());
                }

                return existingProductDeployement;
            })
            .map(productDeployementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDeployement.getId().toString())
        );
    }

    /**
     * {@code GET  /product-deployements} : get all the productDeployements.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productDeployements in body.
     */
    @GetMapping("")
    public List<ProductDeployement> getAllProductDeployements(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ProductDeployements");
        if (eagerload) {
            return productDeployementRepository.findAllWithEagerRelationships();
        } else {
            return productDeployementRepository.findAll();
        }
    }

    /**
     * {@code GET  /product-deployements/:id} : get the "id" productDeployement.
     *
     * @param id the id of the productDeployement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productDeployement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDeployement> getProductDeployement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductDeployement : {}", id);
        Optional<ProductDeployement> productDeployement = productDeployementRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(productDeployement);
    }

    /**
     * {@code DELETE  /product-deployements/:id} : delete the "id" productDeployement.
     *
     * @param id the id of the productDeployement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductDeployement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductDeployement : {}", id);
        productDeployementRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
