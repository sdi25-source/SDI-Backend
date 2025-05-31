package com.sdi.web.rest;

import com.sdi.domain.Region;
import com.sdi.repository.RegionRepository;
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
 * REST controller for managing {@link com.sdi.domain.Region}.
 */
@RestController
@RequestMapping("/api/regions")
@Transactional
public class RegionResource {

    private static final Logger LOG = LoggerFactory.getLogger(RegionResource.class);

    private static final String ENTITY_NAME = "region";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RegionRepository regionRepository;

    public RegionResource(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    /**
     * {@code POST  /regions} : Create a new region.
     *
     * @param region the region to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new region, or with status {@code 400 (Bad Request)} if the region has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Region> createRegion(@Valid @RequestBody Region region) throws URISyntaxException {
        LOG.debug("REST request to save Region : {}", region);
        if (region.getId() != null) {
            throw new BadRequestAlertException("A new region cannot already have an ID", ENTITY_NAME, "idexists");
        }
        region = regionRepository.save(region);
        return ResponseEntity.created(new URI("/api/regions/" + region.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, region.getId().toString()))
            .body(region);
    }

    /**
     * {@code PUT  /regions/:id} : Updates an existing region.
     *
     * @param id the id of the region to save.
     * @param region the region to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated region,
     * or with status {@code 400 (Bad Request)} if the region is not valid,
     * or with status {@code 500 (Internal Server Error)} if the region couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Region> updateRegion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Region region
    ) throws URISyntaxException {
        LOG.debug("REST request to update Region : {}, {}", id, region);
        if (region.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, region.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        region = regionRepository.save(region);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, region.getId().toString()))
            .body(region);
    }

    /**
     * {@code PATCH  /regions/:id} : Partial updates given fields of an existing region, field will ignore if it is null
     *
     * @param id the id of the region to save.
     * @param region the region to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated region,
     * or with status {@code 400 (Bad Request)} if the region is not valid,
     * or with status {@code 404 (Not Found)} if the region is not found,
     * or with status {@code 500 (Internal Server Error)} if the region couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Region> partialUpdateRegion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Region region
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Region partially : {}, {}", id, region);
        if (region.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, region.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Region> result = regionRepository
            .findById(region.getId())
            .map(existingRegion -> {
                if (region.getName() != null) {
                    existingRegion.setName(region.getName());
                }
                if (region.getCode() != null) {
                    existingRegion.setCode(region.getCode());
                }
                if (region.getCreaDate() != null) {
                    existingRegion.setCreaDate(region.getCreaDate());
                }
                if (region.getUpdateDate() != null) {
                    existingRegion.setUpdateDate(region.getUpdateDate());
                }
                if (region.getNotes() != null) {
                    existingRegion.setNotes(region.getNotes());
                }

                return existingRegion;
            })
            .map(regionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, region.getId().toString())
        );
    }

    /**
     * {@code GET  /regions} : get all the regions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of regions in body.
     */
    @GetMapping("")
    public List<Region> getAllRegions() {
        LOG.debug("REST request to get all Regions");
        return regionRepository.findAll();
    }

    /**
     * {@code GET  /regions/:id} : get the "id" region.
     *
     * @param id the id of the region to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the region, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Region> getRegion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Region : {}", id);
        Optional<Region> region = regionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(region);
    }

    /**
     * {@code DELETE  /regions/:id} : delete the "id" region.
     *
     * @param id the id of the region to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Region : {}", id);
        regionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
