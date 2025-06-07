package com.sdi.web.rest;

import com.sdi.domain.Country;
import com.sdi.domain.Region;
import com.sdi.repository.CountryRepository;
import com.sdi.service.dto.CountryDTO;
import com.sdi.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.sdi.domain.Country}.
 */
@RestController
@RequestMapping("/api/countries")
@Transactional
public class CountryResource {

    private static final Logger LOG = LoggerFactory.getLogger(CountryResource.class);

    private static final String ENTITY_NAME = "country";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CountryRepository countryRepository;

    public CountryResource(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * {@code POST  /countries} : Create a new country.
     *
     * @param country the country to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new country, or with status {@code 400 (Bad Request)} if the country has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Country> createCountry(@Valid @RequestBody Country country) throws URISyntaxException {
        LOG.debug("REST request to save Country : {}", country);
        if (country.getId() != null) {
            throw new BadRequestAlertException("A new country cannot already have an ID", ENTITY_NAME, "idexists");
        }
        country = countryRepository.save(country);
        return ResponseEntity.created(new URI("/api/countries/" + country.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, country.getId().toString()))
            .body(country);
    }

    /**
     * {@code PUT  /countries/:id} : Updates an existing country.
     *
     * @param id the id of the country to save.
     * @param country the country to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated country,
     * or with status {@code 400 (Bad Request)} if the country is not valid,
     * or with status {@code 500 (Internal Server Error)} if the country couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Country country
    ) throws URISyntaxException {
        LOG.debug("REST request to update Country : {}, {}", id, country);
        if (country.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, country.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        country = countryRepository.save(country);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, country.getId().toString()))
            .body(country);
    }

    /**
     * {@code PATCH  /countries/:id} : Partial updates given fields of an existing country, field will ignore if it is null
     *
     * @param id the id of the country to save.
     * @param country the country to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated country,
     * or with status {@code 400 (Bad Request)} if the country is not valid,
     * or with status {@code 404 (Not Found)} if the country is not found,
     * or with status {@code 500 (Internal Server Error)} if the country couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Country> partialUpdateCountry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Country country
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Country partially : {}, {}", id, country);
        if (country.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, country.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!countryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Country> result = countryRepository
            .findById(country.getId())
            .map(existingCountry -> {
                if (country.getCountryname() != null) {
                    existingCountry.setCountryname(country.getCountryname());
                }
                if (country.getCountrycode() != null) {
                    existingCountry.setCountrycode(country.getCountrycode());
                }
                if (country.getCountryFlagcode() != null) {
                    existingCountry.setCountryFlagcode(country.getCountryFlagcode());
                }
                if (country.getCountryFlag() != null) {
                    existingCountry.setCountryFlag(country.getCountryFlag());
                }
                if (country.getNotes() != null) {
                    existingCountry.setNotes(country.getNotes());
                }
                if (country.getCreaDate() != null) {
                    existingCountry.setCreaDate(country.getCreaDate());
                }
                if (country.getUpdateDate() != null) {
                    existingCountry.setUpdateDate(country.getUpdateDate());
                }

                return existingCountry;
            })
            .map(countryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, country.getId().toString())
        );
    }

    /**
     * {@code GET  /countries} : get all the countries.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of countries in body.
     */
    @GetMapping("")
    @Transactional(readOnly = true)
    public List<Country> getAllCountries(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Countries");
        List<Object[]> results = countryRepository.findAllWithEagerRelationships();
        return results.stream()
            .map(row -> {
                Country country = new Country();
                country.setId(((Number) row[0]).longValue()); // c.id
                country.setCountryname((String) row[1]); // c.countryname
                country.setCountrycode((String) row[2]); // c.countrycode
                country.setCountryFlag((String) row[3]); // c.country_flagcode
                country.setCountryFlag((String) row[4]); // c.country_flag
                country.setNotes((String) row[5]); // c.notes
                country.setCreaDate(row[6] != null ? ((Date) row[6]).toLocalDate() : null); // c.crea_date
                country.setUpdateDate(row[7] != null ? ((Date) row[7]).toLocalDate() : null); // c.update_date

                // Populate Region object
                if (row[9] != null) { // r.id
                    Region region = new Region();
                    region.setId(((Number) row[9]).longValue());
                    region.setName((String) row[10]); // r.name
                    country.setRegion(region);
                }

                return country;
            })
            .collect(Collectors.toList());
    }
    /**
     * {@code GET  /countries/:id} : get the "id" country.
     *
     * @param id the id of the country to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the country, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Country : {}", id);
        Optional<Country> country = countryRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(country);
    }

    /**
     * {@code DELETE  /countries/:id} : delete the "id" country.
     *
     * @param id the id of the country to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Country : {}", id);
        countryRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
