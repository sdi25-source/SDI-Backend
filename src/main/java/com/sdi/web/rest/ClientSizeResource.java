package com.sdi.web.rest;

import com.sdi.domain.ClientSize;
import com.sdi.repository.ClientSizeRepository;
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
 * REST controller for managing {@link com.sdi.domain.ClientSize}.
 */
@RestController
@RequestMapping("/api/client-sizes")
@Transactional
public class ClientSizeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSizeResource.class);

    private static final String ENTITY_NAME = "clientSize";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientSizeRepository clientSizeRepository;

    public ClientSizeResource(ClientSizeRepository clientSizeRepository) {
        this.clientSizeRepository = clientSizeRepository;
    }

    /**
     * {@code POST  /client-sizes} : Create a new clientSize.
     *
     * @param clientSize the clientSize to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientSize, or with status {@code 400 (Bad Request)} if the clientSize has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientSize> createClientSize(@Valid @RequestBody ClientSize clientSize) throws URISyntaxException {
        LOG.debug("REST request to save ClientSize : {}", clientSize);
        if (clientSize.getId() != null) {
            throw new BadRequestAlertException("A new clientSize cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clientSize = clientSizeRepository.save(clientSize);
        return ResponseEntity.created(new URI("/api/client-sizes/" + clientSize.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clientSize.getId().toString()))
            .body(clientSize);
    }

    /**
     * {@code PUT  /client-sizes/:id} : Updates an existing clientSize.
     *
     * @param id the id of the clientSize to save.
     * @param clientSize the clientSize to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientSize,
     * or with status {@code 400 (Bad Request)} if the clientSize is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientSize couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientSize> updateClientSize(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientSize clientSize
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClientSize : {}, {}", id, clientSize);
        if (clientSize.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientSize.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientSizeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clientSize = clientSizeRepository.save(clientSize);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientSize.getId().toString()))
            .body(clientSize);
    }

    /**
     * {@code PATCH  /client-sizes/:id} : Partial updates given fields of an existing clientSize, field will ignore if it is null
     *
     * @param id the id of the clientSize to save.
     * @param clientSize the clientSize to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientSize,
     * or with status {@code 400 (Bad Request)} if the clientSize is not valid,
     * or with status {@code 404 (Not Found)} if the clientSize is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientSize couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientSize> partialUpdateClientSize(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientSize clientSize
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClientSize partially : {}, {}", id, clientSize);
        if (clientSize.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientSize.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientSizeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientSize> result = clientSizeRepository
            .findById(clientSize.getId())
            .map(existingClientSize -> {
                if (clientSize.getSizeName() != null) {
                    existingClientSize.setSizeName(clientSize.getSizeName());
                }
                if (clientSize.getSizeCode() != null) {
                    existingClientSize.setSizeCode(clientSize.getSizeCode());
                }
                if (clientSize.getSizeDescription() != null) {
                    existingClientSize.setSizeDescription(clientSize.getSizeDescription());
                }
                if (clientSize.getCreateDate() != null) {
                    existingClientSize.setCreateDate(clientSize.getCreateDate());
                }
                if (clientSize.getUpdateDate() != null) {
                    existingClientSize.setUpdateDate(clientSize.getUpdateDate());
                }
                if (clientSize.getNotes() != null) {
                    existingClientSize.setNotes(clientSize.getNotes());
                }

                return existingClientSize;
            })
            .map(clientSizeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientSize.getId().toString())
        );
    }

    /**
     * {@code GET  /client-sizes} : get all the clientSizes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientSizes in body.
     */
    @GetMapping("")
    public List<ClientSize> getAllClientSizes() {
        LOG.debug("REST request to get all ClientSizes");
        return clientSizeRepository.findAll();
    }

    /**
     * {@code GET  /client-sizes/:id} : get the "id" clientSize.
     *
     * @param id the id of the clientSize to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientSize, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientSize> getClientSize(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientSize : {}", id);
        Optional<ClientSize> clientSize = clientSizeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clientSize);
    }

    /**
     * {@code DELETE  /client-sizes/:id} : delete the "id" clientSize.
     *
     * @param id the id of the clientSize to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientSize(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientSize : {}", id);
        clientSizeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
