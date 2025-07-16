package com.sdi.web.rest;

import static com.sdi.domain.ClientSizeAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ClientSize;
import com.sdi.repository.ClientSizeRepository;
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
 * Integration tests for the {@link ClientSizeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientSizeResourceIT {

    private static final String DEFAULT_SIZE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SIZE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SIZE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SIZE_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SIZE_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SIZE_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-sizes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientSizeRepository clientSizeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientSizeMockMvc;

    private ClientSize clientSize;

    private ClientSize insertedClientSize;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientSize createEntity() {
        return new ClientSize()
            .sizeName(DEFAULT_SIZE_NAME)
            .sizeCode(DEFAULT_SIZE_CODE)
            .sizeDescription(DEFAULT_SIZE_DESCRIPTION)
            .createDate(DEFAULT_CREATE_DATE)
            .updateDate(DEFAULT_UPDATE_DATE)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientSize createUpdatedEntity() {
        return new ClientSize()
            .sizeName(UPDATED_SIZE_NAME)
            .sizeCode(UPDATED_SIZE_CODE)
            .sizeDescription(UPDATED_SIZE_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    void initTest() {
        clientSize = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClientSize != null) {
            clientSizeRepository.delete(insertedClientSize);
            insertedClientSize = null;
        }
    }

    @Test
    @Transactional
    void createClientSize() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClientSize
        var returnedClientSize = om.readValue(
            restClientSizeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientSize)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientSize.class
        );

        // Validate the ClientSize in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientSizeUpdatableFieldsEquals(returnedClientSize, getPersistedClientSize(returnedClientSize));

        insertedClientSize = returnedClientSize;
    }

    @Test
    @Transactional
    void createClientSizeWithExistingId() throws Exception {
        // Create the ClientSize with an existing ID
        clientSize.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientSizeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientSize)))
            .andExpect(status().isBadRequest());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSizeNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientSize.setSizeName(null);

        // Create the ClientSize, which fails.

        restClientSizeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientSize)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientSizes() throws Exception {
        // Initialize the database
        insertedClientSize = clientSizeRepository.saveAndFlush(clientSize);

        // Get all the clientSizeList
        restClientSizeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientSize.getId().intValue())))
            .andExpect(jsonPath("$.[*].sizeName").value(hasItem(DEFAULT_SIZE_NAME)))
            .andExpect(jsonPath("$.[*].sizeCode").value(hasItem(DEFAULT_SIZE_CODE)))
            .andExpect(jsonPath("$.[*].sizeDescription").value(hasItem(DEFAULT_SIZE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getClientSize() throws Exception {
        // Initialize the database
        insertedClientSize = clientSizeRepository.saveAndFlush(clientSize);

        // Get the clientSize
        restClientSizeMockMvc
            .perform(get(ENTITY_API_URL_ID, clientSize.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientSize.getId().intValue()))
            .andExpect(jsonPath("$.sizeName").value(DEFAULT_SIZE_NAME))
            .andExpect(jsonPath("$.sizeCode").value(DEFAULT_SIZE_CODE))
            .andExpect(jsonPath("$.sizeDescription").value(DEFAULT_SIZE_DESCRIPTION))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingClientSize() throws Exception {
        // Get the clientSize
        restClientSizeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientSize() throws Exception {
        // Initialize the database
        insertedClientSize = clientSizeRepository.saveAndFlush(clientSize);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientSize
        ClientSize updatedClientSize = clientSizeRepository.findById(clientSize.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientSize are not directly saved in db
        em.detach(updatedClientSize);
        updatedClientSize
            .sizeName(UPDATED_SIZE_NAME)
            .sizeCode(UPDATED_SIZE_CODE)
            .sizeDescription(UPDATED_SIZE_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restClientSizeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientSize.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClientSize))
            )
            .andExpect(status().isOk());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientSizeToMatchAllProperties(updatedClientSize);
    }

    @Test
    @Transactional
    void putNonExistingClientSize() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientSize.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientSizeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientSize.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientSize))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientSize() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientSize.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientSizeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientSize))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientSize() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientSize.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientSizeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientSize)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientSizeWithPatch() throws Exception {
        // Initialize the database
        insertedClientSize = clientSizeRepository.saveAndFlush(clientSize);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientSize using partial update
        ClientSize partialUpdatedClientSize = new ClientSize();
        partialUpdatedClientSize.setId(clientSize.getId());

        partialUpdatedClientSize.sizeName(UPDATED_SIZE_NAME).sizeCode(UPDATED_SIZE_CODE);

        restClientSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientSize.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientSize))
            )
            .andExpect(status().isOk());

        // Validate the ClientSize in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientSizeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClientSize, clientSize),
            getPersistedClientSize(clientSize)
        );
    }

    @Test
    @Transactional
    void fullUpdateClientSizeWithPatch() throws Exception {
        // Initialize the database
        insertedClientSize = clientSizeRepository.saveAndFlush(clientSize);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientSize using partial update
        ClientSize partialUpdatedClientSize = new ClientSize();
        partialUpdatedClientSize.setId(clientSize.getId());

        partialUpdatedClientSize
            .sizeName(UPDATED_SIZE_NAME)
            .sizeCode(UPDATED_SIZE_CODE)
            .sizeDescription(UPDATED_SIZE_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restClientSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientSize.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientSize))
            )
            .andExpect(status().isOk());

        // Validate the ClientSize in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientSizeUpdatableFieldsEquals(partialUpdatedClientSize, getPersistedClientSize(partialUpdatedClientSize));
    }

    @Test
    @Transactional
    void patchNonExistingClientSize() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientSize.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientSize.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientSize))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientSize() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientSize.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientSizeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientSize))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientSize() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientSize.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientSizeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clientSize)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientSize in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientSize() throws Exception {
        // Initialize the database
        insertedClientSize = clientSizeRepository.saveAndFlush(clientSize);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clientSize
        restClientSizeMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientSize.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientSizeRepository.count();
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

    protected ClientSize getPersistedClientSize(ClientSize clientSize) {
        return clientSizeRepository.findById(clientSize.getId()).orElseThrow();
    }

    protected void assertPersistedClientSizeToMatchAllProperties(ClientSize expectedClientSize) {
        assertClientSizeAllPropertiesEquals(expectedClientSize, getPersistedClientSize(expectedClientSize));
    }

    protected void assertPersistedClientSizeToMatchUpdatableProperties(ClientSize expectedClientSize) {
        assertClientSizeAllUpdatablePropertiesEquals(expectedClientSize, getPersistedClientSize(expectedClientSize));
    }
}
