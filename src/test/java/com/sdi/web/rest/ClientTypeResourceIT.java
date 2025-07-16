package com.sdi.web.rest;

import static com.sdi.domain.ClientTypeAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ClientType;
import com.sdi.repository.ClientTypeRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientTypeResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientTypeRepository clientTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientTypeMockMvc;

    private ClientType clientType;

    private ClientType insertedClientType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientType createEntity() {
        return new ClientType().type(DEFAULT_TYPE).createDate(DEFAULT_CREATE_DATE).updateDate(DEFAULT_UPDATE_DATE).notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientType createUpdatedEntity() {
        return new ClientType().type(UPDATED_TYPE).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);
    }

    @BeforeEach
    void initTest() {
        clientType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClientType != null) {
            clientTypeRepository.delete(insertedClientType);
            insertedClientType = null;
        }
    }

    @Test
    @Transactional
    void createClientType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClientType
        var returnedClientType = om.readValue(
            restClientTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientType)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientType.class
        );

        // Validate the ClientType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientTypeUpdatableFieldsEquals(returnedClientType, getPersistedClientType(returnedClientType));

        insertedClientType = returnedClientType;
    }

    @Test
    @Transactional
    void createClientTypeWithExistingId() throws Exception {
        // Create the ClientType with an existing ID
        clientType.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientType)))
            .andExpect(status().isBadRequest());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientType.setType(null);

        // Create the ClientType, which fails.

        restClientTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientType)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientTypes() throws Exception {
        // Initialize the database
        insertedClientType = clientTypeRepository.saveAndFlush(clientType);

        // Get all the clientTypeList
        restClientTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getClientType() throws Exception {
        // Initialize the database
        insertedClientType = clientTypeRepository.saveAndFlush(clientType);

        // Get the clientType
        restClientTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, clientType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingClientType() throws Exception {
        // Get the clientType
        restClientTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientType() throws Exception {
        // Initialize the database
        insertedClientType = clientTypeRepository.saveAndFlush(clientType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientType
        ClientType updatedClientType = clientTypeRepository.findById(clientType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientType are not directly saved in db
        em.detach(updatedClientType);
        updatedClientType.type(UPDATED_TYPE).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restClientTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClientType))
            )
            .andExpect(status().isOk());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientTypeToMatchAllProperties(updatedClientType);
    }

    @Test
    @Transactional
    void putNonExistingClientType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientType.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientTypeWithPatch() throws Exception {
        // Initialize the database
        insertedClientType = clientTypeRepository.saveAndFlush(clientType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientType using partial update
        ClientType partialUpdatedClientType = new ClientType();
        partialUpdatedClientType.setId(clientType.getId());

        partialUpdatedClientType.createDate(UPDATED_CREATE_DATE);

        restClientTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientType))
            )
            .andExpect(status().isOk());

        // Validate the ClientType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClientType, clientType),
            getPersistedClientType(clientType)
        );
    }

    @Test
    @Transactional
    void fullUpdateClientTypeWithPatch() throws Exception {
        // Initialize the database
        insertedClientType = clientTypeRepository.saveAndFlush(clientType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientType using partial update
        ClientType partialUpdatedClientType = new ClientType();
        partialUpdatedClientType.setId(clientType.getId());

        partialUpdatedClientType.type(UPDATED_TYPE).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restClientTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientType))
            )
            .andExpect(status().isOk());

        // Validate the ClientType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientTypeUpdatableFieldsEquals(partialUpdatedClientType, getPersistedClientType(partialUpdatedClientType));
    }

    @Test
    @Transactional
    void patchNonExistingClientType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clientType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientType() throws Exception {
        // Initialize the database
        insertedClientType = clientTypeRepository.saveAndFlush(clientType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clientType
        restClientTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientTypeRepository.count();
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

    protected ClientType getPersistedClientType(ClientType clientType) {
        return clientTypeRepository.findById(clientType.getId()).orElseThrow();
    }

    protected void assertPersistedClientTypeToMatchAllProperties(ClientType expectedClientType) {
        assertClientTypeAllPropertiesEquals(expectedClientType, getPersistedClientType(expectedClientType));
    }

    protected void assertPersistedClientTypeToMatchUpdatableProperties(ClientType expectedClientType) {
        assertClientTypeAllUpdatablePropertiesEquals(expectedClientType, getPersistedClientType(expectedClientType));
    }
}
