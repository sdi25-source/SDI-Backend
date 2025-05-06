package com.sdi.web.rest;

import static com.sdi.domain.ComponentTypeAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ComponentType;
import com.sdi.repository.ComponentTypeRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ComponentTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ComponentTypeResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/component-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ComponentTypeRepository componentTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restComponentTypeMockMvc;

    private ComponentType componentType;

    private ComponentType insertedComponentType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ComponentType createEntity() {
        return new ComponentType().type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ComponentType createUpdatedEntity() {
        return new ComponentType().type(UPDATED_TYPE);
    }

    @BeforeEach
    public void initTest() {
        componentType = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedComponentType != null) {
            componentTypeRepository.delete(insertedComponentType);
            insertedComponentType = null;
        }
    }

    @Test
    @Transactional
    void createComponentType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ComponentType
        var returnedComponentType = om.readValue(
            restComponentTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(componentType)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ComponentType.class
        );

        // Validate the ComponentType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertComponentTypeUpdatableFieldsEquals(returnedComponentType, getPersistedComponentType(returnedComponentType));

        insertedComponentType = returnedComponentType;
    }

    @Test
    @Transactional
    void createComponentTypeWithExistingId() throws Exception {
        // Create the ComponentType with an existing ID
        componentType.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restComponentTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(componentType)))
            .andExpect(status().isBadRequest());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        componentType.setType(null);

        // Create the ComponentType, which fails.

        restComponentTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(componentType)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComponentTypes() throws Exception {
        // Initialize the database
        insertedComponentType = componentTypeRepository.saveAndFlush(componentType);

        // Get all the componentTypeList
        restComponentTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(componentType.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @Test
    @Transactional
    void getComponentType() throws Exception {
        // Initialize the database
        insertedComponentType = componentTypeRepository.saveAndFlush(componentType);

        // Get the componentType
        restComponentTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, componentType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(componentType.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingComponentType() throws Exception {
        // Get the componentType
        restComponentTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComponentType() throws Exception {
        // Initialize the database
        insertedComponentType = componentTypeRepository.saveAndFlush(componentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the componentType
        ComponentType updatedComponentType = componentTypeRepository.findById(componentType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedComponentType are not directly saved in db
        em.detach(updatedComponentType);
        updatedComponentType.type(UPDATED_TYPE);

        restComponentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedComponentType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedComponentType))
            )
            .andExpect(status().isOk());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedComponentTypeToMatchAllProperties(updatedComponentType);
    }

    @Test
    @Transactional
    void putNonExistingComponentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        componentType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComponentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, componentType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(componentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComponentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        componentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComponentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(componentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComponentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        componentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComponentTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(componentType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateComponentTypeWithPatch() throws Exception {
        // Initialize the database
        insertedComponentType = componentTypeRepository.saveAndFlush(componentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the componentType using partial update
        ComponentType partialUpdatedComponentType = new ComponentType();
        partialUpdatedComponentType.setId(componentType.getId());

        restComponentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComponentType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComponentType))
            )
            .andExpect(status().isOk());

        // Validate the ComponentType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComponentTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedComponentType, componentType),
            getPersistedComponentType(componentType)
        );
    }

    @Test
    @Transactional
    void fullUpdateComponentTypeWithPatch() throws Exception {
        // Initialize the database
        insertedComponentType = componentTypeRepository.saveAndFlush(componentType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the componentType using partial update
        ComponentType partialUpdatedComponentType = new ComponentType();
        partialUpdatedComponentType.setId(componentType.getId());

        partialUpdatedComponentType.type(UPDATED_TYPE);

        restComponentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedComponentType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedComponentType))
            )
            .andExpect(status().isOk());

        // Validate the ComponentType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComponentTypeUpdatableFieldsEquals(partialUpdatedComponentType, getPersistedComponentType(partialUpdatedComponentType));
    }

    @Test
    @Transactional
    void patchNonExistingComponentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        componentType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restComponentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, componentType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(componentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComponentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        componentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComponentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(componentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComponentType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        componentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restComponentTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(componentType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ComponentType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComponentType() throws Exception {
        // Initialize the database
        insertedComponentType = componentTypeRepository.saveAndFlush(componentType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the componentType
        restComponentTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, componentType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return componentTypeRepository.count();
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

    protected ComponentType getPersistedComponentType(ComponentType componentType) {
        return componentTypeRepository.findById(componentType.getId()).orElseThrow();
    }

    protected void assertPersistedComponentTypeToMatchAllProperties(ComponentType expectedComponentType) {
        assertComponentTypeAllPropertiesEquals(expectedComponentType, getPersistedComponentType(expectedComponentType));
    }

    protected void assertPersistedComponentTypeToMatchUpdatableProperties(ComponentType expectedComponentType) {
        assertComponentTypeAllUpdatablePropertiesEquals(expectedComponentType, getPersistedComponentType(expectedComponentType));
    }
}
