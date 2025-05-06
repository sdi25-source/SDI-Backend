package com.sdi.web.rest;

import com.sdi.domain.ProductLine;
import com.sdi.repository.ProductLineRepository;
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
 * REST controller for managing {@link com.sdi.domain.ProductLine}.
 */
@RestController
@RequestMapping("/api/product-lines")
@Transactional
public class ProductLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductLineResource.class);

    private static final String ENTITY_NAME = "productLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductLineRepository productLineRepository;

    public ProductLineResource(ProductLineRepository productLineRepository) {
        this.productLineRepository = productLineRepository;
    }

    /**
     * {@code POST  /product-lines} : Create a new productLine.
     *
     * @param productLine the productLine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productLine, or with status {@code 400 (Bad Request)} if the productLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductLine> createProductLine(@Valid @RequestBody ProductLine productLine) throws URISyntaxException {
        LOG.debug("REST request to save ProductLine : {}", productLine);
        if (productLine.getId() != null) {
            throw new BadRequestAlertException("A new productLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productLine = productLineRepository.save(productLine);
        return ResponseEntity.created(new URI("/api/product-lines/" + productLine.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productLine.getId().toString()))
            .body(productLine);
    }

    /**
     * {@code PUT  /product-lines/:id} : Updates an existing productLine.
     *
     * @param id the id of the productLine to save.
     * @param productLine the productLine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productLine,
     * or with status {@code 400 (Bad Request)} if the productLine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productLine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductLine> updateProductLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductLine productLine
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductLine : {}, {}", id, productLine);
        if (productLine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productLine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productLine = productLineRepository.save(productLine);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productLine.getId().toString()))
            .body(productLine);
    }

    /**
     * {@code PATCH  /product-lines/:id} : Partial updates given fields of an existing productLine, field will ignore if it is null
     *
     * @param id the id of the productLine to save.
     * @param productLine the productLine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productLine,
     * or with status {@code 400 (Bad Request)} if the productLine is not valid,
     * or with status {@code 404 (Not Found)} if the productLine is not found,
     * or with status {@code 500 (Internal Server Error)} if the productLine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductLine> partialUpdateProductLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductLine productLine
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductLine partially : {}, {}", id, productLine);
        if (productLine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productLine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductLine> result = productLineRepository
            .findById(productLine.getId())
            .map(existingProductLine -> {
                if (productLine.getName() != null) {
                    existingProductLine.setName(productLine.getName());
                }
                if (productLine.getCreateDate() != null) {
                    existingProductLine.setCreateDate(productLine.getCreateDate());
                }
                if (productLine.getUpdateDate() != null) {
                    existingProductLine.setUpdateDate(productLine.getUpdateDate());
                }
                if (productLine.getNotes() != null) {
                    existingProductLine.setNotes(productLine.getNotes());
                }

                return existingProductLine;
            })
            .map(productLineRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productLine.getId().toString())
        );
    }

    /**
     * {@code GET  /product-lines} : get all the productLines.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productLines in body.
     */
    @GetMapping("")
    public List<ProductLine> getAllProductLines() {
        LOG.debug("REST request to get all ProductLines");
        return productLineRepository.findAll();
    }

    /**
     * {@code GET  /product-lines/:id} : get the "id" productLine.
     *
     * @param id the id of the productLine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productLine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductLine> getProductLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductLine : {}", id);
        Optional<ProductLine> productLine = productLineRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(productLine);
    }

    /**
     * {@code DELETE  /product-lines/:id} : delete the "id" productLine.
     *
     * @param id the id of the productLine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductLine : {}", id);
        productLineRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
