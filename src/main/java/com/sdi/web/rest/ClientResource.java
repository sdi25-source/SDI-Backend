package com.sdi.web.rest;

import com.sdi.domain.Client;
import com.sdi.domain.ClientEvent;
import com.sdi.repository.ClientEventRepository;
import com.sdi.repository.ClientRepository;
import com.sdi.repository.ProductDeployementRepository;
import com.sdi.repository.RequestOfChangeRepository;
import com.sdi.repository.projection.ProductDeployementSummaryProjection;
import com.sdi.service.ClientOverviewService;
import com.sdi.service.ClientQueryService;
import com.sdi.service.ClientService;
import com.sdi.service.criteria.ClientCriteria;
import com.sdi.service.dto.*;
import com.sdi.service.mapper.ClientMapper;
import com.sdi.service.mapper.ProductDeployementMapper;
import com.sdi.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.io.File;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import com.sdi.utils.ReportUtil;
import org.springframework.http.HttpHeaders;

/**
 * REST controller for managing {@link com.sdi.domain.Client}.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientResource.class);

    private static final String ENTITY_NAME = "client";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientService clientService;

    private final ClientRepository clientRepository;

    private final ProductDeployementRepository productDeployementRepository;
    private final ProductDeployementMapper productDeployementMapper;
    private final RequestOfChangeRepository requestOfChangeRepository;

    private final ClientEventRepository clientEventRepository;


    private final ClientQueryService clientQueryService;

    private final ClientMapper clientMapper;


    private final ClientOverviewService clientOverviewService;

    public ClientResource(ClientService clientService, ClientRepository clientRepository,
                          ClientQueryService clientQueryService,ProductDeployementRepository productDeployementRepository, ClientMapper clientMapper,
                          RequestOfChangeRepository requestOfChangeRepository, ClientEventRepository clientEventRepository, ClientOverviewService clientOverviewService, ProductDeployementMapper productDeployementMapper
    ) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.clientQueryService = clientQueryService;
        this.productDeployementRepository = productDeployementRepository;
        this.clientMapper = clientMapper;
        this.requestOfChangeRepository = requestOfChangeRepository;
        this.clientEventRepository = clientEventRepository;
        this.clientOverviewService = clientOverviewService;
        this.productDeployementMapper = productDeployementMapper;
    }

    @GetMapping("/client-overviews")
    public List<ClientOverview> getClientOverviews() {
        return clientOverviewService.getClientsOverview();
    }


    @GetMapping("/clientData/{idClient}")
    public ResponseEntity<Map<String, Object>> getClientData(@PathVariable(name = "idClient") Long idClient) throws IOException {
        // Validate idClient
        if (idClient == null) {
            throw new BadRequestAlertException("Invalid client ID", ENTITY_NAME, "idnull");
        }

        // Fetch client with given id
        Client client = clientService.getClientById(idClient);
        if (client == null) {
            throw new BadRequestAlertException("Client not found", ENTITY_NAME, "idnotfound");
        }

        // Fetch client events
        List<ClientEvent> clientEvents = clientEventRepository.findEventsByClientId(idClient);
        List<ClientEventDTO> clientEventDTOs = new ArrayList<>();
        for (ClientEvent clientEvent : clientEvents) {
            ClientEventDTO clientEventDTO = new ClientEventDTO();
            clientEventDTO.setEvent(clientEvent.getEvent());
            clientEventDTO.setEventDate(clientEvent.getEventDate());
            clientEventDTO.setDescription(clientEvent.getDescription());
            clientEventDTOs.add(clientEventDTO);
        }

        // Fetch product deployment summaries
        List<ProductDeployementSummaryProjection> productDeployementSummary = productDeployementRepository.findDeployementSummariesByClientId(idClient);

        // Fetch requests of changes
        List<RequestOfChangesDTO> requestOfChangesDTO = requestOfChangeRepository.findRequestOfChangesByClientId(idClient);

        // Build response map
        Map<String, Object> response = new HashMap<>();
        response.put("client", client);
        response.put("clientEvents", clientEventDTOs);
        response.put("productDeployementSummaries", productDeployementSummary);
        response.put("requestOfChanges", requestOfChangesDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/{idClient}")
    public ResponseEntity<byte[]> generateClientReport(@PathVariable(name = "idClient") Long idClient) throws IOException {
        try {
            // Validate idClient
            if (idClient == null) {
                throw new BadRequestAlertException("Invalid client ID", ENTITY_NAME, "idnull");
            }

            // Fetch client with given id client
            Client client = clientService.getClientById(idClient);
            if (client == null) {
                throw new BadRequestAlertException("Client not found", ENTITY_NAME, "idnotfound");
            }
            List<Client> clients = List.of(client);

            // Convert List<Client> to List<ClientDTO>
            List<ClientDTO> clientDTOs = clientMapper.toDtoList(clients);

            // Ensure integer fields are not null
            for (ClientDTO clientDTO : clientDTOs) {
                if (clientDTO.getCurrentCustomersNumber() == null) {
                    clientDTO.setCurrentCustomersNumber(0);
                }
                if (clientDTO.getCurrentCardHolderNumber() == null) {
                    clientDTO.setCurrentCardHolderNumber(0);
                }
                if (clientDTO.getCurrentBruncheNumber() == null) {
                    clientDTO.setCurrentBruncheNumber(0);
                }
            }

            // Path to the compiled .jasper file
            String jasperPath = Objects.requireNonNull(getClass().getClassLoader()
                .getResource("jasper-templates/clientReport.jasper")).getPath();

            // Parameters for the report
            HashMap<String, Object> parameters = new HashMap<>();
            List<ProductDeployementSummaryProjection> projections = productDeployementRepository.findDeployementSummariesByClientId(idClient);
            JRBeanCollectionDataSource dataSource1 = new JRBeanCollectionDataSource(projections);
            parameters.put("TABLE_DATA_SOURCE", dataSource1);

//        List<RequestOfChangesDTO> requestOfChangesDTO = requestOfChangeRepository.findRequestOfChangesByClientId(idClient);
//        JRBeanCollectionDataSource dataSource2 = new JRBeanCollectionDataSource(requestOfChangesDTO);
//        parameters.put("TABLE_DATA_SOURCE_REQUESTS", dataSource2);

            List<ClientEvent> clientEvents = clientEventRepository.findEventsByClientId(idClient);
            List<ClientEventDTO> clientEventDTOs = new ArrayList<>();

            // Map ClientEvent to ClientEventDTO
            for (ClientEvent clientEvent : clientEvents) {
                ClientEventDTO clientEventDTO = new ClientEventDTO();
                clientEventDTO.setEvent(clientEvent.getEvent());
                clientEventDTO.setEventDate(clientEvent.getEventDate());
                clientEventDTO.setDescription(clientEvent.getDescription());
                clientEventDTOs.add(clientEventDTO);
            }

            JRBeanCollectionDataSource dataSource3 = new JRBeanCollectionDataSource(clientEventDTOs);
            parameters.put("TABLE_DATA_SOURCE_EVENTS", dataSource3);

            // Generate the PDF report
            byte[] pdfData = ReportUtil.generateReport(clientDTOs, jasperPath, parameters);

            // Return the PDF as a response
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=client_report.pdf") // Changed to inline for preview
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(pdfData);

        } catch (JRException e) {
            LOG.error("Error generating report: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating report", e);
        }
    }
    /**
     * {@code POST  /clients} : Create a new client.
     *
     * @param client the client to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new client, or with status {@code 400 (Bad Request)} if the client has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) throws URISyntaxException {
        LOG.debug("REST request to save Client : {}", client);
        if (client.getId() != null) {
            throw new BadRequestAlertException("A new client cannot already have an ID", ENTITY_NAME, "idexists");
        }
        client = clientService.save(client);
        return ResponseEntity.created(new URI("/api/clients/" + client.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, client.getId().toString()))
            .body(client);
    }

    /**
     * {@code PUT  /clients/:id} : Updates an existing client.
     *
     * @param id the id of the client to save.
     * @param client the client to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated client,
     * or with status {@code 400 (Bad Request)} if the client is not valid,
     * or with status {@code 500 (Internal Server Error)} if the client couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Client client
    ) throws URISyntaxException {
        LOG.debug("REST request to update Client : {}, {}", id, client);
        if (client.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, client.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        client = clientService.update(client);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, client.getId().toString()))
            .body(client);
    }

    /**
     * {@code PATCH  /clients/:id} : Partial updates given fields of an existing client, field will ignore if it is null
     *
     * @param id the id of the client to save.
     * @param client the client to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated client,
     * or with status {@code 400 (Bad Request)} if the client is not valid,
     * or with status {@code 404 (Not Found)} if the client is not found,
     * or with status {@code 500 (Internal Server Error)} if the client couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Client> partialUpdateClient(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Client client
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Client partially : {}, {}", id, client);
        if (client.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, client.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Client> result = clientService.partialUpdate(client);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, client.getId().toString())
        );
    }

    /**
     * {@code GET  /clients} : get all the clients.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clients in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Client>> getAllClients(ClientCriteria criteria) {
        LOG.debug("REST request to get Clients by criteria: {}", criteria);

        List<Client> entityList = clientQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /clients/count} : count all the clients.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countClients(ClientCriteria criteria) {
        LOG.debug("REST request to count Clients by criteria: {}", criteria);
        return ResponseEntity.ok().body(clientQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /clients/:id} : get the "id" client.
     *
     * @param id the id of the client to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the client, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Client : {}", id);
        Optional<Client> client = clientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(client);
    }

    /**
     * {@code DELETE  /clients/:id} : delete the "id" client.
     *
     * @param id the id of the client to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Client : {}", id);
        clientService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
