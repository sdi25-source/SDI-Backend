package com.sdi.web.rest;

import static com.sdi.domain.FeatureDeployementAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.FeatureDeployement;
import com.sdi.repository.FeatureDeployementRepository;
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
 * Integration tests for the {@link FeatureDeployementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FeatureDeployementResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/feature-deployements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FeatureDeployementRepository featureDeployementRepository;

    @Mock
    private FeatureDeployementRepository featureDeployementRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFeatureDeployementMockMvc;

    private FeatureDeployement featureDeployement;

    private FeatureDeployement insertedFeatureDeployement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeatureDeployement createEntity() {
        return new FeatureDeployement()
            .code(DEFAULT_CODE)
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
    public static FeatureDeployement createUpdatedEntity() {
        return new FeatureDeployement()
            .code(UPDATED_CODE)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        featureDeployement = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFeatureDeployement != null) {
            featureDeployementRepository.delete(insertedFeatureDeployement);
            insertedFeatureDeployement = null;
        }
    }

    @Test
    @Transactional
    void createFeatureDeployement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FeatureDeployement
        var returnedFeatureDeployement = om.readValue(
            restFeatureDeployementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(featureDeployement)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FeatureDeployement.class
        );

        // Validate the FeatureDeployement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFeatureDeployementUpdatableFieldsEquals(
            returnedFeatureDeployement,
            getPersistedFeatureDeployement(returnedFeatureDeployement)
        );

        insertedFeatureDeployement = returnedFeatureDeployement;
    }

    @Test
    @Transactional
    void createFeatureDeployementWithExistingId() throws Exception {
        // Create the FeatureDeployement with an existing ID
        featureDeployement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeatureDeployementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(featureDeployement)))
            .andExpect(status().isBadRequest());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        featureDeployement.setCode(null);

        // Create the FeatureDeployement, which fails.

        restFeatureDeployementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(featureDeployement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFeatureDeployements() throws Exception {
        // Initialize the database
        insertedFeatureDeployement = featureDeployementRepository.saveAndFlush(featureDeployement);

        // Get all the featureDeployementList
        restFeatureDeployementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(featureDeployement.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFeatureDeployementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(featureDeployementRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFeatureDeployementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(featureDeployementRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFeatureDeployementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(featureDeployementRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFeatureDeployementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(featureDeployementRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFeatureDeployement() throws Exception {
        // Initialize the database
        insertedFeatureDeployement = featureDeployementRepository.saveAndFlush(featureDeployement);

        // Get the featureDeployement
        restFeatureDeployementMockMvc
            .perform(get(ENTITY_API_URL_ID, featureDeployement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(featureDeployement.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingFeatureDeployement() throws Exception {
        // Get the featureDeployement
        restFeatureDeployementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFeatureDeployement() throws Exception {
        // Initialize the database
        insertedFeatureDeployement = featureDeployementRepository.saveAndFlush(featureDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the featureDeployement
        FeatureDeployement updatedFeatureDeployement = featureDeployementRepository.findById(featureDeployement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFeatureDeployement are not directly saved in db
        em.detach(updatedFeatureDeployement);
        updatedFeatureDeployement.code(UPDATED_CODE).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restFeatureDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFeatureDeployement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFeatureDeployement))
            )
            .andExpect(status().isOk());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFeatureDeployementToMatchAllProperties(updatedFeatureDeployement);
    }

    @Test
    @Transactional
    void putNonExistingFeatureDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        featureDeployement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeatureDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, featureDeployement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(featureDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFeatureDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        featureDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(featureDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFeatureDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        featureDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureDeployementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(featureDeployement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFeatureDeployementWithPatch() throws Exception {
        // Initialize the database
        insertedFeatureDeployement = featureDeployementRepository.saveAndFlush(featureDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the featureDeployement using partial update
        FeatureDeployement partialUpdatedFeatureDeployement = new FeatureDeployement();
        partialUpdatedFeatureDeployement.setId(featureDeployement.getId());

        partialUpdatedFeatureDeployement.code(UPDATED_CODE);

        restFeatureDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeatureDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeatureDeployement))
            )
            .andExpect(status().isOk());

        // Validate the FeatureDeployement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeatureDeployementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFeatureDeployement, featureDeployement),
            getPersistedFeatureDeployement(featureDeployement)
        );
    }

    @Test
    @Transactional
    void fullUpdateFeatureDeployementWithPatch() throws Exception {
        // Initialize the database
        insertedFeatureDeployement = featureDeployementRepository.saveAndFlush(featureDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the featureDeployement using partial update
        FeatureDeployement partialUpdatedFeatureDeployement = new FeatureDeployement();
        partialUpdatedFeatureDeployement.setId(featureDeployement.getId());

        partialUpdatedFeatureDeployement
            .code(UPDATED_CODE)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restFeatureDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeatureDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeatureDeployement))
            )
            .andExpect(status().isOk());

        // Validate the FeatureDeployement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeatureDeployementUpdatableFieldsEquals(
            partialUpdatedFeatureDeployement,
            getPersistedFeatureDeployement(partialUpdatedFeatureDeployement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingFeatureDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        featureDeployement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeatureDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, featureDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(featureDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFeatureDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        featureDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(featureDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFeatureDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        featureDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeatureDeployementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(featureDeployement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FeatureDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFeatureDeployement() throws Exception {
        // Initialize the database
        insertedFeatureDeployement = featureDeployementRepository.saveAndFlush(featureDeployement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the featureDeployement
        restFeatureDeployementMockMvc
            .perform(delete(ENTITY_API_URL_ID, featureDeployement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return featureDeployementRepository.count();
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

    protected FeatureDeployement getPersistedFeatureDeployement(FeatureDeployement featureDeployement) {
        return featureDeployementRepository.findById(featureDeployement.getId()).orElseThrow();
    }

    protected void assertPersistedFeatureDeployementToMatchAllProperties(FeatureDeployement expectedFeatureDeployement) {
        assertFeatureDeployementAllPropertiesEquals(expectedFeatureDeployement, getPersistedFeatureDeployement(expectedFeatureDeployement));
    }

    protected void assertPersistedFeatureDeployementToMatchUpdatableProperties(FeatureDeployement expectedFeatureDeployement) {
        assertFeatureDeployementAllUpdatablePropertiesEquals(
            expectedFeatureDeployement,
            getPersistedFeatureDeployement(expectedFeatureDeployement)
        );
    }
}
