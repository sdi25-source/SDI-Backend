package com.sdi.web.rest;

import com.sdi.domain.ProductDeployementDetail;
import com.sdi.repository.ProductDeployementDetailRepository;
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
 * REST controller for managing {@link com.sdi.domain.ProductDeployementDetail}.
 */
@RestController
@RequestMapping("/api/product-deployement-details")
@Transactional
public class ProductDeployementDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductDeployementDetailResource.class);

    private static final String ENTITY_NAME = "productDeployementDetail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductDeployementDetailRepository productDeployementDetailRepository;

    public ProductDeployementDetailResource(ProductDeployementDetailRepository productDeployementDetailRepository) {
        this.productDeployementDetailRepository = productDeployementDetailRepository;
    }

    /**
     * {@code POST  /product-deployement-details} : Create a new productDeployementDetail.
     *
     * @param productDeployementDetail the productDeployementDetail to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productDeployementDetail, or with status {@code 400 (Bad Request)} if the productDeployementDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductDeployementDetail> createProductDeployementDetail(
        @RequestBody ProductDeployementDetail productDeployementDetail
    ) throws URISyntaxException {
        LOG.debug("REST request to save ProductDeployementDetail : {}", productDeployementDetail);
        if (productDeployementDetail.getId() != null) {
            throw new BadRequestAlertException("A new productDeployementDetail cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productDeployementDetail = productDeployementDetailRepository.save(productDeployementDetail);
        return ResponseEntity.created(new URI("/api/product-deployement-details/" + productDeployementDetail.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, productDeployementDetail.getId().toString()))
            .body(productDeployementDetail);
    }

    /**
     * {@code PUT  /product-deployement-details/:id} : Updates an existing productDeployementDetail.
     *
     * @param id the id of the productDeployementDetail to save.
     * @param productDeployementDetail the productDeployementDetail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDeployementDetail,
     * or with status {@code 400 (Bad Request)} if the productDeployementDetail is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productDeployementDetail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDeployementDetail> updateProductDeployementDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductDeployementDetail productDeployementDetail
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductDeployementDetail : {}, {}", id, productDeployementDetail);
        if (productDeployementDetail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productDeployementDetail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productDeployementDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productDeployementDetail = productDeployementDetailRepository.save(productDeployementDetail);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDeployementDetail.getId().toString()))
            .body(productDeployementDetail);
    }

    /**
     * {@code PATCH  /product-deployement-details/:id} : Partial updates given fields of an existing productDeployementDetail, field will ignore if it is null
     *
     * @param id the id of the productDeployementDetail to save.
     * @param productDeployementDetail the productDeployementDetail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDeployementDetail,
     * or with status {@code 400 (Bad Request)} if the productDeployementDetail is not valid,
     * or with status {@code 404 (Not Found)} if the productDeployementDetail is not found,
     * or with status {@code 500 (Internal Server Error)} if the productDeployementDetail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductDeployementDetail> partialUpdateProductDeployementDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductDeployementDetail productDeployementDetail
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductDeployementDetail partially : {}, {}", id, productDeployementDetail);
        if (productDeployementDetail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productDeployementDetail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productDeployementDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductDeployementDetail> result = productDeployementDetailRepository
            .findById(productDeployementDetail.getId())
            .map(existingProductDeployementDetail -> {
                if (productDeployementDetail.getStartDeployementDate() != null) {
                    existingProductDeployementDetail.setStartDeployementDate(productDeployementDetail.getStartDeployementDate());
                }
                if (productDeployementDetail.getEndDeployementDate() != null) {
                    existingProductDeployementDetail.setEndDeployementDate(productDeployementDetail.getEndDeployementDate());
                }
                if (productDeployementDetail.getNotes() != null) {
                    existingProductDeployementDetail.setNotes(productDeployementDetail.getNotes());
                }

                return existingProductDeployementDetail;
            })
            .map(productDeployementDetailRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDeployementDetail.getId().toString())
        );
    }

    /**
     * {@code GET  /product-deployement-details} : get all the productDeployementDetails.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productDeployementDetails in body.
     */
    @GetMapping("")
    public List<ProductDeployementDetail> getAllProductDeployementDetails(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ProductDeployementDetails");
        if (eagerload) {
            return productDeployementDetailRepository.findAllWithEagerRelationships();
        } else {
            return productDeployementDetailRepository.findAll();
        }
    }

    /**
     * {@code GET  /product-deployement-details/:id} : get the "id" productDeployementDetail.
     *
     * @param id the id of the productDeployementDetail to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productDeployementDetail, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDeployementDetail> getProductDeployementDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductDeployementDetail : {}", id);
        Optional<ProductDeployementDetail> productDeployementDetail = productDeployementDetailRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(productDeployementDetail);
    }

    /**
     * {@code DELETE  /product-deployement-details/:id} : delete the "id" productDeployementDetail.
     *
     * @param id the id of the productDeployementDetail to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductDeployementDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductDeployementDetail : {}", id);
        productDeployementDetailRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
