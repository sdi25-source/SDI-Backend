package com.sdi.web.rest;

import static com.sdi.domain.DeployementTypeAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.DeployementType;
import com.sdi.repository.DeployementTypeRepository;
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
 * Integration tests for the {@link DeployementTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeployementTypeResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/deployement-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeployementTypeRepository deployementTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeployementTypeMockMvc;

    private DeployementType deployementType;

    private DeployementType insertedDeployementType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeployementType createEntity() {
        return new DeployementType()
            .type(DEFAULT_TYPE)
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
    public static DeployementType createUpdatedEntity() {
        return new DeployementType()
            .type(UPDATED_TYPE)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        deployementType = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDeployementType != null) {
            deployementTypeRepository.delete(insertedDeployementType);
            insertedDeployementType = null;
        }
    }

    @Test
    @Transactional
    void createDeployementType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DeployementType
        var returnedDeployementType = om.readValue(
            restDeployementTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deployementType)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeployementType.class
        );

        // Validate the DeployementType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDeployementTypeUpdatableFieldsEquals(returnedDeployementType, getPersistedDeployementType(returnedDeployementType));

        insertedDeployementType = returnedDeployementType;
    }

    @Test
    @Transactional
    void createDeployementTypeWithExistingId() throws Exception {
        // Create the DeployementType with an existing ID
        deployementType.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeployementTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deployementType)))
            .andExpect(status().isBadRequest());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDeployementTypes() throws Exception {
        // Initialize the database
        insertedDeployementType = deployementTypeRepository.saveAndFlush(deployementType);

        // Get all the deployementTypeList
        restDeployementTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deployementType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getDeployementType() throws Exception {
        // Initialize the database
        insertedDeployementType = deployementTypeRepository.saveAndFlush(deployementType);

        // Get the deployementType
        restDeployementTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, deployementType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(deployementType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingDeployementType() throws Exception {
        // Get the deployementType
        restDeployementTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDeployementType() throws Exception {
        // Initialize the database
        insertedDeployementType = deployementTypeRepository.saveAndFlush(deployementType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deployementType
        DeployementType updatedDeployementType = deployementTypeRepository.findById(deployementType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDeployementType are not directly saved in db
        em.detach(updatedDeployementType);
        updatedDeployementType.type(UPDATED_TYPE).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restDeployementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDeployementType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDeployementType))
            )
            .andExpect(status().isOk());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeployementTypeToMatchAllProperties(updatedDeployementType);
    }

    @Test
    @Transactional
    void putNonExistingDeployementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployementType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeployementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deployementType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deployementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDeployementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeployementTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deployementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDeployementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeployementTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deployementType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeployementTypeWithPatch() throws Exception {
        // Initialize the database
        insertedDeployementType = deployementTypeRepository.saveAndFlush(deployementType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deployementType using partial update
        DeployementType partialUpdatedDeployementType = new DeployementType();
        partialUpdatedDeployementType.setId(deployementType.getId());

        partialUpdatedDeployementType.updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restDeployementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeployementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeployementType))
            )
            .andExpect(status().isOk());

        // Validate the DeployementType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeployementTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDeployementType, deployementType),
            getPersistedDeployementType(deployementType)
        );
    }

    @Test
    @Transactional
    void fullUpdateDeployementTypeWithPatch() throws Exception {
        // Initialize the database
        insertedDeployementType = deployementTypeRepository.saveAndFlush(deployementType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the deployementType using partial update
        DeployementType partialUpdatedDeployementType = new DeployementType();
        partialUpdatedDeployementType.setId(deployementType.getId());

        partialUpdatedDeployementType
            .type(UPDATED_TYPE)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restDeployementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeployementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDeployementType))
            )
            .andExpect(status().isOk());

        // Validate the DeployementType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeployementTypeUpdatableFieldsEquals(
            partialUpdatedDeployementType,
            getPersistedDeployementType(partialUpdatedDeployementType)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDeployementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployementType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeployementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deployementType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deployementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDeployementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeployementTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deployementType))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDeployementType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        deployementType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeployementTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(deployementType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeployementType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDeployementType() throws Exception {
        // Initialize the database
        insertedDeployementType = deployementTypeRepository.saveAndFlush(deployementType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the deployementType
        restDeployementTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, deployementType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deployementTypeRepository.count();
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

    protected DeployementType getPersistedDeployementType(DeployementType deployementType) {
        return deployementTypeRepository.findById(deployementType.getId()).orElseThrow();
    }

    protected void assertPersistedDeployementTypeToMatchAllProperties(DeployementType expectedDeployementType) {
        assertDeployementTypeAllPropertiesEquals(expectedDeployementType, getPersistedDeployementType(expectedDeployementType));
    }

    protected void assertPersistedDeployementTypeToMatchUpdatableProperties(DeployementType expectedDeployementType) {
        assertDeployementTypeAllUpdatablePropertiesEquals(expectedDeployementType, getPersistedDeployementType(expectedDeployementType));
    }
}
