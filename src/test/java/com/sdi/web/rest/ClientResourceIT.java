package com.sdi.web.rest;

import static com.sdi.domain.ClientAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.Client;
import com.sdi.domain.ClientSize;
import com.sdi.domain.ClientType;
import com.sdi.domain.Country;
import com.sdi.repository.ClientRepository;
import com.sdi.service.ClientService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClientResourceIT {

    private static final String DEFAULT_CLIENT_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_LOGO = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MAIN_CONTACT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MAIN_CONTACT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MAIN_CONTACT_EMAIL = "C*o@Z3D3a.wS";
    private static final String UPDATED_MAIN_CONTACT_EMAIL = "OwB@|.b3";

    private static final Integer DEFAULT_CURRENT_CARD_HOLDER_NUMBER = 1;
    private static final Integer UPDATED_CURRENT_CARD_HOLDER_NUMBER = 2;
    private static final Integer SMALLER_CURRENT_CARD_HOLDER_NUMBER = 1 - 1;

    private static final Integer DEFAULT_CURRENT_BRUNCHE_NUMBER = 1;
    private static final Integer UPDATED_CURRENT_BRUNCHE_NUMBER = 2;
    private static final Integer SMALLER_CURRENT_BRUNCHE_NUMBER = 1 - 1;

    private static final Integer DEFAULT_CURRENT_CUSTOMERS_NUMBER = 1;
    private static final Integer UPDATED_CURRENT_CUSTOMERS_NUMBER = 2;
    private static final Integer SMALLER_CURRENT_CUSTOMERS_NUMBER = 1 - 1;

    private static final String DEFAULT_MAIN_CONTACT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_MAIN_CONTACT_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATE_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_UPDATE_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientRepository clientRepository;

    @Mock
    private ClientRepository clientRepositoryMock;

    @Mock
    private ClientService clientServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    private Client insertedClient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity() {
        return new Client()
            .clientLogo(DEFAULT_CLIENT_LOGO)
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .mainContactName(DEFAULT_MAIN_CONTACT_NAME)
            .mainContactEmail(DEFAULT_MAIN_CONTACT_EMAIL)
            .currentCardHolderNumber(DEFAULT_CURRENT_CARD_HOLDER_NUMBER)
            .currentBruncheNumber(DEFAULT_CURRENT_BRUNCHE_NUMBER)
            .currentCustomersNumber(DEFAULT_CURRENT_CUSTOMERS_NUMBER)
            .mainContactPhoneNumber(DEFAULT_MAIN_CONTACT_PHONE_NUMBER)
            .url(DEFAULT_URL)
            .address(DEFAULT_ADDRESS)
            .createDate(DEFAULT_CREATE_DATE)
            .updateDate(DEFAULT_UPDATE_DATE)
            .notes(DEFAULT_NOTES)
            .countryName(DEFAULT_COUNTRY_NAME)
            .region(DEFAULT_REGION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity() {
        return new Client()
            .clientLogo(UPDATED_CLIENT_LOGO)
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .mainContactName(UPDATED_MAIN_CONTACT_NAME)
            .mainContactEmail(UPDATED_MAIN_CONTACT_EMAIL)
            .currentCardHolderNumber(UPDATED_CURRENT_CARD_HOLDER_NUMBER)
            .currentBruncheNumber(UPDATED_CURRENT_BRUNCHE_NUMBER)
            .currentCustomersNumber(UPDATED_CURRENT_CUSTOMERS_NUMBER)
            .mainContactPhoneNumber(UPDATED_MAIN_CONTACT_PHONE_NUMBER)
            .url(UPDATED_URL)
            .address(UPDATED_ADDRESS)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES)
            .countryName(UPDATED_COUNTRY_NAME)
            .region(UPDATED_REGION);
    }

    @BeforeEach
    public void initTest() {
        client = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedClient != null) {
            clientRepository.delete(insertedClient);
            insertedClient = null;
        }
    }

    @Test
    @Transactional
    void createClient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Client
        var returnedClient = om.readValue(
            restClientMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Client.class
        );

        // Validate the Client in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientUpdatableFieldsEquals(returnedClient, getPersistedClient(returnedClient));

        insertedClient = returnedClient;
    }

    @Test
    @Transactional
    void createClientWithExistingId() throws Exception {
        // Create the Client with an existing ID
        client.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        client.setName(null);

        // Create the Client, which fails.

        restClientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClients() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].clientLogo").value(hasItem(DEFAULT_CLIENT_LOGO)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].mainContactName").value(hasItem(DEFAULT_MAIN_CONTACT_NAME)))
            .andExpect(jsonPath("$.[*].mainContactEmail").value(hasItem(DEFAULT_MAIN_CONTACT_EMAIL)))
            .andExpect(jsonPath("$.[*].currentCardHolderNumber").value(hasItem(DEFAULT_CURRENT_CARD_HOLDER_NUMBER)))
            .andExpect(jsonPath("$.[*].currentBruncheNumber").value(hasItem(DEFAULT_CURRENT_BRUNCHE_NUMBER)))
            .andExpect(jsonPath("$.[*].currentCustomersNumber").value(hasItem(DEFAULT_CURRENT_CUSTOMERS_NUMBER)))
            .andExpect(jsonPath("$.[*].mainContactPhoneNumber").value(hasItem(DEFAULT_MAIN_CONTACT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].countryName").value(hasItem(DEFAULT_COUNTRY_NAME)))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientsWithEagerRelationshipsIsEnabled() throws Exception {
        when(clientServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clientServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(clientServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(clientRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClient() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get the client
        restClientMockMvc
            .perform(get(ENTITY_API_URL_ID, client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.clientLogo").value(DEFAULT_CLIENT_LOGO))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.mainContactName").value(DEFAULT_MAIN_CONTACT_NAME))
            .andExpect(jsonPath("$.mainContactEmail").value(DEFAULT_MAIN_CONTACT_EMAIL))
            .andExpect(jsonPath("$.currentCardHolderNumber").value(DEFAULT_CURRENT_CARD_HOLDER_NUMBER))
            .andExpect(jsonPath("$.currentBruncheNumber").value(DEFAULT_CURRENT_BRUNCHE_NUMBER))
            .andExpect(jsonPath("$.currentCustomersNumber").value(DEFAULT_CURRENT_CUSTOMERS_NUMBER))
            .andExpect(jsonPath("$.mainContactPhoneNumber").value(DEFAULT_MAIN_CONTACT_PHONE_NUMBER))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.countryName").value(DEFAULT_COUNTRY_NAME))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION));
    }

    @Test
    @Transactional
    void getClientsByIdFiltering() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        Long id = client.getId();

        defaultClientFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClientFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClientFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClientsByClientLogoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where clientLogo equals to
        defaultClientFiltering("clientLogo.equals=" + DEFAULT_CLIENT_LOGO, "clientLogo.equals=" + UPDATED_CLIENT_LOGO);
    }

    @Test
    @Transactional
    void getAllClientsByClientLogoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where clientLogo in
        defaultClientFiltering("clientLogo.in=" + DEFAULT_CLIENT_LOGO + "," + UPDATED_CLIENT_LOGO, "clientLogo.in=" + UPDATED_CLIENT_LOGO);
    }

    @Test
    @Transactional
    void getAllClientsByClientLogoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where clientLogo is not null
        defaultClientFiltering("clientLogo.specified=true", "clientLogo.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByClientLogoContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where clientLogo contains
        defaultClientFiltering("clientLogo.contains=" + DEFAULT_CLIENT_LOGO, "clientLogo.contains=" + UPDATED_CLIENT_LOGO);
    }

    @Test
    @Transactional
    void getAllClientsByClientLogoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where clientLogo does not contain
        defaultClientFiltering("clientLogo.doesNotContain=" + UPDATED_CLIENT_LOGO, "clientLogo.doesNotContain=" + DEFAULT_CLIENT_LOGO);
    }

    @Test
    @Transactional
    void getAllClientsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where name equals to
        defaultClientFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where name in
        defaultClientFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where name is not null
        defaultClientFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where name contains
        defaultClientFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where name does not contain
        defaultClientFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where code equals to
        defaultClientFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where code in
        defaultClientFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where code is not null
        defaultClientFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where code contains
        defaultClientFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where code does not contain
        defaultClientFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllClientsByMainContactNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactName equals to
        defaultClientFiltering(
            "mainContactName.equals=" + DEFAULT_MAIN_CONTACT_NAME,
            "mainContactName.equals=" + UPDATED_MAIN_CONTACT_NAME
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactName in
        defaultClientFiltering(
            "mainContactName.in=" + DEFAULT_MAIN_CONTACT_NAME + "," + UPDATED_MAIN_CONTACT_NAME,
            "mainContactName.in=" + UPDATED_MAIN_CONTACT_NAME
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactName is not null
        defaultClientFiltering("mainContactName.specified=true", "mainContactName.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByMainContactNameContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactName contains
        defaultClientFiltering(
            "mainContactName.contains=" + DEFAULT_MAIN_CONTACT_NAME,
            "mainContactName.contains=" + UPDATED_MAIN_CONTACT_NAME
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactName does not contain
        defaultClientFiltering(
            "mainContactName.doesNotContain=" + UPDATED_MAIN_CONTACT_NAME,
            "mainContactName.doesNotContain=" + DEFAULT_MAIN_CONTACT_NAME
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactEmail equals to
        defaultClientFiltering(
            "mainContactEmail.equals=" + DEFAULT_MAIN_CONTACT_EMAIL,
            "mainContactEmail.equals=" + UPDATED_MAIN_CONTACT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactEmail in
        defaultClientFiltering(
            "mainContactEmail.in=" + DEFAULT_MAIN_CONTACT_EMAIL + "," + UPDATED_MAIN_CONTACT_EMAIL,
            "mainContactEmail.in=" + UPDATED_MAIN_CONTACT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactEmail is not null
        defaultClientFiltering("mainContactEmail.specified=true", "mainContactEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByMainContactEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactEmail contains
        defaultClientFiltering(
            "mainContactEmail.contains=" + DEFAULT_MAIN_CONTACT_EMAIL,
            "mainContactEmail.contains=" + UPDATED_MAIN_CONTACT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactEmail does not contain
        defaultClientFiltering(
            "mainContactEmail.doesNotContain=" + UPDATED_MAIN_CONTACT_EMAIL,
            "mainContactEmail.doesNotContain=" + DEFAULT_MAIN_CONTACT_EMAIL
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCardHolderNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCardHolderNumber equals to
        defaultClientFiltering(
            "currentCardHolderNumber.equals=" + DEFAULT_CURRENT_CARD_HOLDER_NUMBER,
            "currentCardHolderNumber.equals=" + UPDATED_CURRENT_CARD_HOLDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCardHolderNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCardHolderNumber in
        defaultClientFiltering(
            "currentCardHolderNumber.in=" + DEFAULT_CURRENT_CARD_HOLDER_NUMBER + "," + UPDATED_CURRENT_CARD_HOLDER_NUMBER,
            "currentCardHolderNumber.in=" + UPDATED_CURRENT_CARD_HOLDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCardHolderNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCardHolderNumber is not null
        defaultClientFiltering("currentCardHolderNumber.specified=true", "currentCardHolderNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCardHolderNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCardHolderNumber is greater than or equal to
        defaultClientFiltering(
            "currentCardHolderNumber.greaterThanOrEqual=" + DEFAULT_CURRENT_CARD_HOLDER_NUMBER,
            "currentCardHolderNumber.greaterThanOrEqual=" + UPDATED_CURRENT_CARD_HOLDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCardHolderNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCardHolderNumber is less than or equal to
        defaultClientFiltering(
            "currentCardHolderNumber.lessThanOrEqual=" + DEFAULT_CURRENT_CARD_HOLDER_NUMBER,
            "currentCardHolderNumber.lessThanOrEqual=" + SMALLER_CURRENT_CARD_HOLDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCardHolderNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCardHolderNumber is less than
        defaultClientFiltering(
            "currentCardHolderNumber.lessThan=" + UPDATED_CURRENT_CARD_HOLDER_NUMBER,
            "currentCardHolderNumber.lessThan=" + DEFAULT_CURRENT_CARD_HOLDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCardHolderNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCardHolderNumber is greater than
        defaultClientFiltering(
            "currentCardHolderNumber.greaterThan=" + SMALLER_CURRENT_CARD_HOLDER_NUMBER,
            "currentCardHolderNumber.greaterThan=" + DEFAULT_CURRENT_CARD_HOLDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentBruncheNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentBruncheNumber equals to
        defaultClientFiltering(
            "currentBruncheNumber.equals=" + DEFAULT_CURRENT_BRUNCHE_NUMBER,
            "currentBruncheNumber.equals=" + UPDATED_CURRENT_BRUNCHE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentBruncheNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentBruncheNumber in
        defaultClientFiltering(
            "currentBruncheNumber.in=" + DEFAULT_CURRENT_BRUNCHE_NUMBER + "," + UPDATED_CURRENT_BRUNCHE_NUMBER,
            "currentBruncheNumber.in=" + UPDATED_CURRENT_BRUNCHE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentBruncheNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentBruncheNumber is not null
        defaultClientFiltering("currentBruncheNumber.specified=true", "currentBruncheNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCurrentBruncheNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentBruncheNumber is greater than or equal to
        defaultClientFiltering(
            "currentBruncheNumber.greaterThanOrEqual=" + DEFAULT_CURRENT_BRUNCHE_NUMBER,
            "currentBruncheNumber.greaterThanOrEqual=" + UPDATED_CURRENT_BRUNCHE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentBruncheNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentBruncheNumber is less than or equal to
        defaultClientFiltering(
            "currentBruncheNumber.lessThanOrEqual=" + DEFAULT_CURRENT_BRUNCHE_NUMBER,
            "currentBruncheNumber.lessThanOrEqual=" + SMALLER_CURRENT_BRUNCHE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentBruncheNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentBruncheNumber is less than
        defaultClientFiltering(
            "currentBruncheNumber.lessThan=" + UPDATED_CURRENT_BRUNCHE_NUMBER,
            "currentBruncheNumber.lessThan=" + DEFAULT_CURRENT_BRUNCHE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentBruncheNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentBruncheNumber is greater than
        defaultClientFiltering(
            "currentBruncheNumber.greaterThan=" + SMALLER_CURRENT_BRUNCHE_NUMBER,
            "currentBruncheNumber.greaterThan=" + DEFAULT_CURRENT_BRUNCHE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCustomersNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCustomersNumber equals to
        defaultClientFiltering(
            "currentCustomersNumber.equals=" + DEFAULT_CURRENT_CUSTOMERS_NUMBER,
            "currentCustomersNumber.equals=" + UPDATED_CURRENT_CUSTOMERS_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCustomersNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCustomersNumber in
        defaultClientFiltering(
            "currentCustomersNumber.in=" + DEFAULT_CURRENT_CUSTOMERS_NUMBER + "," + UPDATED_CURRENT_CUSTOMERS_NUMBER,
            "currentCustomersNumber.in=" + UPDATED_CURRENT_CUSTOMERS_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCustomersNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCustomersNumber is not null
        defaultClientFiltering("currentCustomersNumber.specified=true", "currentCustomersNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCustomersNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCustomersNumber is greater than or equal to
        defaultClientFiltering(
            "currentCustomersNumber.greaterThanOrEqual=" + DEFAULT_CURRENT_CUSTOMERS_NUMBER,
            "currentCustomersNumber.greaterThanOrEqual=" + UPDATED_CURRENT_CUSTOMERS_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCustomersNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCustomersNumber is less than or equal to
        defaultClientFiltering(
            "currentCustomersNumber.lessThanOrEqual=" + DEFAULT_CURRENT_CUSTOMERS_NUMBER,
            "currentCustomersNumber.lessThanOrEqual=" + SMALLER_CURRENT_CUSTOMERS_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCustomersNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCustomersNumber is less than
        defaultClientFiltering(
            "currentCustomersNumber.lessThan=" + UPDATED_CURRENT_CUSTOMERS_NUMBER,
            "currentCustomersNumber.lessThan=" + DEFAULT_CURRENT_CUSTOMERS_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByCurrentCustomersNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where currentCustomersNumber is greater than
        defaultClientFiltering(
            "currentCustomersNumber.greaterThan=" + SMALLER_CURRENT_CUSTOMERS_NUMBER,
            "currentCustomersNumber.greaterThan=" + DEFAULT_CURRENT_CUSTOMERS_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactPhoneNumber equals to
        defaultClientFiltering(
            "mainContactPhoneNumber.equals=" + DEFAULT_MAIN_CONTACT_PHONE_NUMBER,
            "mainContactPhoneNumber.equals=" + UPDATED_MAIN_CONTACT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactPhoneNumber in
        defaultClientFiltering(
            "mainContactPhoneNumber.in=" + DEFAULT_MAIN_CONTACT_PHONE_NUMBER + "," + UPDATED_MAIN_CONTACT_PHONE_NUMBER,
            "mainContactPhoneNumber.in=" + UPDATED_MAIN_CONTACT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactPhoneNumber is not null
        defaultClientFiltering("mainContactPhoneNumber.specified=true", "mainContactPhoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByMainContactPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactPhoneNumber contains
        defaultClientFiltering(
            "mainContactPhoneNumber.contains=" + DEFAULT_MAIN_CONTACT_PHONE_NUMBER,
            "mainContactPhoneNumber.contains=" + UPDATED_MAIN_CONTACT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByMainContactPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where mainContactPhoneNumber does not contain
        defaultClientFiltering(
            "mainContactPhoneNumber.doesNotContain=" + UPDATED_MAIN_CONTACT_PHONE_NUMBER,
            "mainContactPhoneNumber.doesNotContain=" + DEFAULT_MAIN_CONTACT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllClientsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where url equals to
        defaultClientFiltering("url.equals=" + DEFAULT_URL, "url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllClientsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where url in
        defaultClientFiltering("url.in=" + DEFAULT_URL + "," + UPDATED_URL, "url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllClientsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where url is not null
        defaultClientFiltering("url.specified=true", "url.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where url contains
        defaultClientFiltering("url.contains=" + DEFAULT_URL, "url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllClientsByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where url does not contain
        defaultClientFiltering("url.doesNotContain=" + UPDATED_URL, "url.doesNotContain=" + DEFAULT_URL);
    }

    @Test
    @Transactional
    void getAllClientsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where address equals to
        defaultClientFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where address in
        defaultClientFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where address is not null
        defaultClientFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where address contains
        defaultClientFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where address does not contain
        defaultClientFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllClientsByCreateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where createDate equals to
        defaultClientFiltering("createDate.equals=" + DEFAULT_CREATE_DATE, "createDate.equals=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByCreateDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where createDate in
        defaultClientFiltering("createDate.in=" + DEFAULT_CREATE_DATE + "," + UPDATED_CREATE_DATE, "createDate.in=" + UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByCreateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where createDate is not null
        defaultClientFiltering("createDate.specified=true", "createDate.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCreateDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where createDate is greater than or equal to
        defaultClientFiltering(
            "createDate.greaterThanOrEqual=" + DEFAULT_CREATE_DATE,
            "createDate.greaterThanOrEqual=" + UPDATED_CREATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllClientsByCreateDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where createDate is less than or equal to
        defaultClientFiltering("createDate.lessThanOrEqual=" + DEFAULT_CREATE_DATE, "createDate.lessThanOrEqual=" + SMALLER_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByCreateDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where createDate is less than
        defaultClientFiltering("createDate.lessThan=" + UPDATED_CREATE_DATE, "createDate.lessThan=" + DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByCreateDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where createDate is greater than
        defaultClientFiltering("createDate.greaterThan=" + SMALLER_CREATE_DATE, "createDate.greaterThan=" + DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByUpdateDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where updateDate equals to
        defaultClientFiltering("updateDate.equals=" + DEFAULT_UPDATE_DATE, "updateDate.equals=" + UPDATED_UPDATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByUpdateDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where updateDate in
        defaultClientFiltering("updateDate.in=" + DEFAULT_UPDATE_DATE + "," + UPDATED_UPDATE_DATE, "updateDate.in=" + UPDATED_UPDATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByUpdateDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where updateDate is not null
        defaultClientFiltering("updateDate.specified=true", "updateDate.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByUpdateDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where updateDate is greater than or equal to
        defaultClientFiltering(
            "updateDate.greaterThanOrEqual=" + DEFAULT_UPDATE_DATE,
            "updateDate.greaterThanOrEqual=" + UPDATED_UPDATE_DATE
        );
    }

    @Test
    @Transactional
    void getAllClientsByUpdateDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where updateDate is less than or equal to
        defaultClientFiltering("updateDate.lessThanOrEqual=" + DEFAULT_UPDATE_DATE, "updateDate.lessThanOrEqual=" + SMALLER_UPDATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByUpdateDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where updateDate is less than
        defaultClientFiltering("updateDate.lessThan=" + UPDATED_UPDATE_DATE, "updateDate.lessThan=" + DEFAULT_UPDATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByUpdateDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where updateDate is greater than
        defaultClientFiltering("updateDate.greaterThan=" + SMALLER_UPDATE_DATE, "updateDate.greaterThan=" + DEFAULT_UPDATE_DATE);
    }

    @Test
    @Transactional
    void getAllClientsByCountryNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where countryName equals to
        defaultClientFiltering("countryName.equals=" + DEFAULT_COUNTRY_NAME, "countryName.equals=" + UPDATED_COUNTRY_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByCountryNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where countryName in
        defaultClientFiltering(
            "countryName.in=" + DEFAULT_COUNTRY_NAME + "," + UPDATED_COUNTRY_NAME,
            "countryName.in=" + UPDATED_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllClientsByCountryNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where countryName is not null
        defaultClientFiltering("countryName.specified=true", "countryName.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByCountryNameContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where countryName contains
        defaultClientFiltering("countryName.contains=" + DEFAULT_COUNTRY_NAME, "countryName.contains=" + UPDATED_COUNTRY_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByCountryNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where countryName does not contain
        defaultClientFiltering("countryName.doesNotContain=" + UPDATED_COUNTRY_NAME, "countryName.doesNotContain=" + DEFAULT_COUNTRY_NAME);
    }

    @Test
    @Transactional
    void getAllClientsByRegionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where region equals to
        defaultClientFiltering("region.equals=" + DEFAULT_REGION, "region.equals=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllClientsByRegionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where region in
        defaultClientFiltering("region.in=" + DEFAULT_REGION + "," + UPDATED_REGION, "region.in=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllClientsByRegionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where region is not null
        defaultClientFiltering("region.specified=true", "region.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByRegionContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where region contains
        defaultClientFiltering("region.contains=" + DEFAULT_REGION, "region.contains=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllClientsByRegionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        // Get all the clientList where region does not contain
        defaultClientFiltering("region.doesNotContain=" + UPDATED_REGION, "region.doesNotContain=" + DEFAULT_REGION);
    }

    @Test
    @Transactional
    void getAllClientsBySizeIsEqualToSomething() throws Exception {
        ClientSize size;
        if (TestUtil.findAll(em, ClientSize.class).isEmpty()) {
            clientRepository.saveAndFlush(client);
            size = ClientSizeResourceIT.createEntity();
        } else {
            size = TestUtil.findAll(em, ClientSize.class).get(0);
        }
        em.persist(size);
        em.flush();
        client.setSize(size);
        clientRepository.saveAndFlush(client);
        Long sizeId = size.getId();
        // Get all the clientList where size equals to sizeId
        defaultClientShouldBeFound("sizeId.equals=" + sizeId);

        // Get all the clientList where size equals to (sizeId + 1)
        defaultClientShouldNotBeFound("sizeId.equals=" + (sizeId + 1));
    }

    @Test
    @Transactional
    void getAllClientsByClientTypeIsEqualToSomething() throws Exception {
        ClientType clientType;
        if (TestUtil.findAll(em, ClientType.class).isEmpty()) {
            clientRepository.saveAndFlush(client);
            clientType = ClientTypeResourceIT.createEntity();
        } else {
            clientType = TestUtil.findAll(em, ClientType.class).get(0);
        }
        em.persist(clientType);
        em.flush();
        client.setClientType(clientType);
        clientRepository.saveAndFlush(client);
        Long clientTypeId = clientType.getId();
        // Get all the clientList where clientType equals to clientTypeId
        defaultClientShouldBeFound("clientTypeId.equals=" + clientTypeId);

        // Get all the clientList where clientType equals to (clientTypeId + 1)
        defaultClientShouldNotBeFound("clientTypeId.equals=" + (clientTypeId + 1));
    }

    @Test
    @Transactional
    void getAllClientsByCountryIsEqualToSomething() throws Exception {
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            clientRepository.saveAndFlush(client);
            country = CountryResourceIT.createEntity();
        } else {
            country = TestUtil.findAll(em, Country.class).get(0);
        }
        em.persist(country);
        em.flush();
        client.setCountry(country);
        clientRepository.saveAndFlush(client);
        Long countryId = country.getId();
        // Get all the clientList where country equals to countryId
        defaultClientShouldBeFound("countryId.equals=" + countryId);

        // Get all the clientList where country equals to (countryId + 1)
        defaultClientShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    private void defaultClientFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClientShouldBeFound(shouldBeFound);
        defaultClientShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientShouldBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].clientLogo").value(hasItem(DEFAULT_CLIENT_LOGO)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].mainContactName").value(hasItem(DEFAULT_MAIN_CONTACT_NAME)))
            .andExpect(jsonPath("$.[*].mainContactEmail").value(hasItem(DEFAULT_MAIN_CONTACT_EMAIL)))
            .andExpect(jsonPath("$.[*].currentCardHolderNumber").value(hasItem(DEFAULT_CURRENT_CARD_HOLDER_NUMBER)))
            .andExpect(jsonPath("$.[*].currentBruncheNumber").value(hasItem(DEFAULT_CURRENT_BRUNCHE_NUMBER)))
            .andExpect(jsonPath("$.[*].currentCustomersNumber").value(hasItem(DEFAULT_CURRENT_CUSTOMERS_NUMBER)))
            .andExpect(jsonPath("$.[*].mainContactPhoneNumber").value(hasItem(DEFAULT_MAIN_CONTACT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].countryName").value(hasItem(DEFAULT_COUNTRY_NAME)))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)));

        // Check, that the count call also returns 1
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientShouldNotBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClient() throws Exception {
        // Get the client
        restClientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClient() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClient are not directly saved in db
        em.detach(updatedClient);
        updatedClient
            .clientLogo(UPDATED_CLIENT_LOGO)
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .mainContactName(UPDATED_MAIN_CONTACT_NAME)
            .mainContactEmail(UPDATED_MAIN_CONTACT_EMAIL)
            .currentCardHolderNumber(UPDATED_CURRENT_CARD_HOLDER_NUMBER)
            .currentBruncheNumber(UPDATED_CURRENT_BRUNCHE_NUMBER)
            .currentCustomersNumber(UPDATED_CURRENT_CUSTOMERS_NUMBER)
            .mainContactPhoneNumber(UPDATED_MAIN_CONTACT_PHONE_NUMBER)
            .url(UPDATED_URL)
            .address(UPDATED_ADDRESS)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES)
            .countryName(UPDATED_COUNTRY_NAME)
            .region(UPDATED_REGION);

        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientToMatchAllProperties(updatedClient);
    }

    @Test
    @Transactional
    void putNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(put(ENTITY_API_URL_ID, client.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(client))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(client)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientWithPatch() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .mainContactName(UPDATED_MAIN_CONTACT_NAME)
            .mainContactEmail(UPDATED_MAIN_CONTACT_EMAIL)
            .currentCardHolderNumber(UPDATED_CURRENT_CARD_HOLDER_NUMBER)
            .url(UPDATED_URL)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClient, client), getPersistedClient(client));
    }

    @Test
    @Transactional
    void fullUpdateClientWithPatch() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient
            .clientLogo(UPDATED_CLIENT_LOGO)
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .mainContactName(UPDATED_MAIN_CONTACT_NAME)
            .mainContactEmail(UPDATED_MAIN_CONTACT_EMAIL)
            .currentCardHolderNumber(UPDATED_CURRENT_CARD_HOLDER_NUMBER)
            .currentBruncheNumber(UPDATED_CURRENT_BRUNCHE_NUMBER)
            .currentCustomersNumber(UPDATED_CURRENT_CUSTOMERS_NUMBER)
            .mainContactPhoneNumber(UPDATED_MAIN_CONTACT_PHONE_NUMBER)
            .url(UPDATED_URL)
            .address(UPDATED_ADDRESS)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES)
            .countryName(UPDATED_COUNTRY_NAME)
            .region(UPDATED_REGION);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientUpdatableFieldsEquals(partialUpdatedClient, getPersistedClient(partialUpdatedClient));
    }

    @Test
    @Transactional
    void patchNonExistingClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, client.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(client))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(client))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        client.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(client)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClient() throws Exception {
        // Initialize the database
        insertedClient = clientRepository.saveAndFlush(client);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the client
        restClientMockMvc
            .perform(delete(ENTITY_API_URL_ID, client.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Client getPersistedClient(Client client) {
        return clientRepository.findById(client.getId()).orElseThrow();
    }

    protected void assertPersistedClientToMatchAllProperties(Client expectedClient) {
        assertClientAllPropertiesEquals(expectedClient, getPersistedClient(expectedClient));
    }

    protected void assertPersistedClientToMatchUpdatableProperties(Client expectedClient) {
        assertClientAllUpdatablePropertiesEquals(expectedClient, getPersistedClient(expectedClient));
    }
}
