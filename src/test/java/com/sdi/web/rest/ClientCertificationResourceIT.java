package com.sdi.web.rest;

import static com.sdi.domain.ClientCertificationAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ClientCertification;
import com.sdi.repository.ClientCertificationRepository;
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
 * Integration tests for the {@link ClientCertificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClientCertificationResourceIT {

    private static final String DEFAULT_CERTIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_CERTIFICATION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CERTIFICATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CERTIFICATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-certifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientCertificationRepository clientCertificationRepository;

    @Mock
    private ClientCertificationRepository clientCertificationRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientCertificationMockMvc;

    private ClientCertification clientCertification;

    private ClientCertification insertedClientCertification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientCertification createEntity() {
        return new ClientCertification()
            .certification(DEFAULT_CERTIFICATION)
            .certificationDate(DEFAULT_CERTIFICATION_DATE)
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
    public static ClientCertification createUpdatedEntity() {
        return new ClientCertification()
            .certification(UPDATED_CERTIFICATION)
            .certificationDate(UPDATED_CERTIFICATION_DATE)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        clientCertification = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedClientCertification != null) {
            clientCertificationRepository.delete(insertedClientCertification);
            insertedClientCertification = null;
        }
    }

    @Test
    @Transactional
    void createClientCertification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClientCertification
        var returnedClientCertification = om.readValue(
            restClientCertificationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientCertification)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientCertification.class
        );

        // Validate the ClientCertification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientCertificationUpdatableFieldsEquals(
            returnedClientCertification,
            getPersistedClientCertification(returnedClientCertification)
        );

        insertedClientCertification = returnedClientCertification;
    }

    @Test
    @Transactional
    void createClientCertificationWithExistingId() throws Exception {
        // Create the ClientCertification with an existing ID
        clientCertification.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientCertificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientCertification)))
            .andExpect(status().isBadRequest());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCertificationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientCertification.setCertification(null);

        // Create the ClientCertification, which fails.

        restClientCertificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientCertification)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientCertifications() throws Exception {
        // Initialize the database
        insertedClientCertification = clientCertificationRepository.saveAndFlush(clientCertification);

        // Get all the clientCertificationList
        restClientCertificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientCertification.getId().intValue())))
            .andExpect(jsonPath("$.[*].certification").value(hasItem(DEFAULT_CERTIFICATION)))
            .andExpect(jsonPath("$.[*].certificationDate").value(hasItem(DEFAULT_CERTIFICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientCertificationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(clientCertificationRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientCertificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clientCertificationRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientCertificationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(clientCertificationRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientCertificationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(clientCertificationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClientCertification() throws Exception {
        // Initialize the database
        insertedClientCertification = clientCertificationRepository.saveAndFlush(clientCertification);

        // Get the clientCertification
        restClientCertificationMockMvc
            .perform(get(ENTITY_API_URL_ID, clientCertification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientCertification.getId().intValue()))
            .andExpect(jsonPath("$.certification").value(DEFAULT_CERTIFICATION))
            .andExpect(jsonPath("$.certificationDate").value(DEFAULT_CERTIFICATION_DATE.toString()))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingClientCertification() throws Exception {
        // Get the clientCertification
        restClientCertificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientCertification() throws Exception {
        // Initialize the database
        insertedClientCertification = clientCertificationRepository.saveAndFlush(clientCertification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientCertification
        ClientCertification updatedClientCertification = clientCertificationRepository.findById(clientCertification.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientCertification are not directly saved in db
        em.detach(updatedClientCertification);
        updatedClientCertification
            .certification(UPDATED_CERTIFICATION)
            .certificationDate(UPDATED_CERTIFICATION_DATE)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restClientCertificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientCertification.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClientCertification))
            )
            .andExpect(status().isOk());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientCertificationToMatchAllProperties(updatedClientCertification);
    }

    @Test
    @Transactional
    void putNonExistingClientCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientCertification.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientCertificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientCertification.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientCertification))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientCertification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientCertificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientCertification))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientCertification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientCertificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientCertification)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientCertificationWithPatch() throws Exception {
        // Initialize the database
        insertedClientCertification = clientCertificationRepository.saveAndFlush(clientCertification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientCertification using partial update
        ClientCertification partialUpdatedClientCertification = new ClientCertification();
        partialUpdatedClientCertification.setId(clientCertification.getId());

        partialUpdatedClientCertification.certification(UPDATED_CERTIFICATION).createDate(UPDATED_CREATE_DATE);

        restClientCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientCertification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientCertification))
            )
            .andExpect(status().isOk());

        // Validate the ClientCertification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientCertificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClientCertification, clientCertification),
            getPersistedClientCertification(clientCertification)
        );
    }

    @Test
    @Transactional
    void fullUpdateClientCertificationWithPatch() throws Exception {
        // Initialize the database
        insertedClientCertification = clientCertificationRepository.saveAndFlush(clientCertification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientCertification using partial update
        ClientCertification partialUpdatedClientCertification = new ClientCertification();
        partialUpdatedClientCertification.setId(clientCertification.getId());

        partialUpdatedClientCertification
            .certification(UPDATED_CERTIFICATION)
            .certificationDate(UPDATED_CERTIFICATION_DATE)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restClientCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientCertification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientCertification))
            )
            .andExpect(status().isOk());

        // Validate the ClientCertification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientCertificationUpdatableFieldsEquals(
            partialUpdatedClientCertification,
            getPersistedClientCertification(partialUpdatedClientCertification)
        );
    }

    @Test
    @Transactional
    void patchNonExistingClientCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientCertification.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientCertification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientCertification))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientCertification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientCertification))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientCertification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientCertificationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clientCertification)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientCertification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientCertification() throws Exception {
        // Initialize the database
        insertedClientCertification = clientCertificationRepository.saveAndFlush(clientCertification);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clientCertification
        restClientCertificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientCertification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientCertificationRepository.count();
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

    protected ClientCertification getPersistedClientCertification(ClientCertification clientCertification) {
        return clientCertificationRepository.findById(clientCertification.getId()).orElseThrow();
    }

    protected void assertPersistedClientCertificationToMatchAllProperties(ClientCertification expectedClientCertification) {
        assertClientCertificationAllPropertiesEquals(
            expectedClientCertification,
            getPersistedClientCertification(expectedClientCertification)
        );
    }

    protected void assertPersistedClientCertificationToMatchUpdatableProperties(ClientCertification expectedClientCertification) {
        assertClientCertificationAllUpdatablePropertiesEquals(
            expectedClientCertification,
            getPersistedClientCertification(expectedClientCertification)
        );
    }
}
