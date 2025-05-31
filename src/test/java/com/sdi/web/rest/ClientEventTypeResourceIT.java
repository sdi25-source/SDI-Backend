package com.sdi.web.rest;

import static com.sdi.domain.ClientEventTypeAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ClientEventType;
import com.sdi.repository.ClientEventTypeRepository;
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
 * Integration tests for the {@link ClientEventTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientEventTypeResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/client-event-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientEventTypeRepository clientEventTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientEventTypeMockMvc;

    private ClientEventType clientEventType;

    private ClientEventType insertedClientEventType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientEventType createEntity() {
        return new ClientEventType()
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .createDate(DEFAULT_CREATE_DATE)
            .updateDate(DEFAULT_UPDATE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientEventType createUpdatedEntity() {
        return new ClientEventType()
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);
    }

    @BeforeEach
    public void initTest() {
        clientEventType = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedClientEventType != null) {
            clientEventTypeRepository.delete(insertedClientEventType);
            insertedClientEventType = null;
        }
    }

    @Test
    @Transactional
    void createClientEventType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClientEventType
        var returnedClientEventType = om.readValue(
            restClientEventTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEventType)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientEventType.class
        );

        // Validate the ClientEventType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientEventTypeUpdatableFieldsEquals(returnedClientEventType, getPersistedClientEventType(returnedClientEventType));

        insertedClientEventType = returnedClientEventType;
    }

    @Test
    @Transactional
    void createClientEventTypeWithExistingId() throws Exception {
        // Create the ClientEventType with an existing ID
        clientEventType.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEventType)))
            .andExpect(status().isBadRequest());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientEventType.setType(null);

        // Create the ClientEventType, which fails.

        restClientEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEventType)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientEventTypes() throws Exception {
        // Initialize the database
        insertedClientEventType = clientEventTypeRepository.saveAndFlush(clientEventType);

        // Get all the clientEventTypeList
        restClientEventTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientEventType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())));
    }

    @Test
    @Transactional
    void getClientEventType() throws Exception {
        // Initialize the database
        insertedClientEventType = clientEventTypeRepository.saveAndFlush(clientEventType);

        // Get the clientEventType
        restClientEventTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, clientEventType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientEventType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingClientEventType() throws Exception {
        // Get the clientEventType
        restClientEventTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientEventType() throws Exception {
        // Initialize the database
        insertedClientEventType = clientEventTypeRepository.saveAndFlush(clientEventType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientEventType
        ClientEventType updatedClientEventType = clientEventTypeRepository.findById(clientEventType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientEventType are not directly saved in db
        em.detach(updatedClientEventType);
        updatedClientEventType
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restClientEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientEventType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClientEventType))
            )
            .andExpect(status().isOk());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientEventTypeToMatchAllProperties(updatedClientEventType);
    }

    @Test
    @Transactional
    void putNonExistingClientEventType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEventType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientEventType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientEventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientEventType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientEventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientEventType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEventType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientEventTypeWithPatch() throws Exception {
        // Initialize the database
        insertedClientEventType = clientEventTypeRepository.saveAndFlush(clientEventType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientEventType using partial update
        ClientEventType partialUpdatedClientEventType = new ClientEventType();
        partialUpdatedClientEventType.setId(clientEventType.getId());

        partialUpdatedClientEventType.description(UPDATED_DESCRIPTION).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE);

        restClientEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientEventType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientEventType))
            )
            .andExpect(status().isOk());

        // Validate the ClientEventType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientEventTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClientEventType, clientEventType),
            getPersistedClientEventType(clientEventType)
        );
    }

    @Test
    @Transactional
    void fullUpdateClientEventTypeWithPatch() throws Exception {
        // Initialize the database
        insertedClientEventType = clientEventTypeRepository.saveAndFlush(clientEventType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientEventType using partial update
        ClientEventType partialUpdatedClientEventType = new ClientEventType();
        partialUpdatedClientEventType.setId(clientEventType.getId());

        partialUpdatedClientEventType
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restClientEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientEventType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientEventType))
            )
            .andExpect(status().isOk());

        // Validate the ClientEventType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientEventTypeUpdatableFieldsEquals(
            partialUpdatedClientEventType,
            getPersistedClientEventType(partialUpdatedClientEventType)
        );
    }

    @Test
    @Transactional
    void patchNonExistingClientEventType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEventType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientEventType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientEventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientEventType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientEventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientEventType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clientEventType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientEventType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientEventType() throws Exception {
        // Initialize the database
        insertedClientEventType = clientEventTypeRepository.saveAndFlush(clientEventType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clientEventType
        restClientEventTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientEventType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientEventTypeRepository.count();
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

    protected ClientEventType getPersistedClientEventType(ClientEventType clientEventType) {
        return clientEventTypeRepository.findById(clientEventType.getId()).orElseThrow();
    }

    protected void assertPersistedClientEventTypeToMatchAllProperties(ClientEventType expectedClientEventType) {
        assertClientEventTypeAllPropertiesEquals(expectedClientEventType, getPersistedClientEventType(expectedClientEventType));
    }

    protected void assertPersistedClientEventTypeToMatchUpdatableProperties(ClientEventType expectedClientEventType) {
        assertClientEventTypeAllUpdatablePropertiesEquals(expectedClientEventType, getPersistedClientEventType(expectedClientEventType));
    }
}
