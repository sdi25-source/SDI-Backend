package com.sdi.web.rest;

import static com.sdi.domain.ModuleVersionAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ModuleVersion;
import com.sdi.repository.ModuleVersionRepository;
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
 * Integration tests for the {@link ModuleVersionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ModuleVersionResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/module-versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModuleVersionRepository moduleVersionRepository;

    @Mock
    private ModuleVersionRepository moduleVersionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModuleVersionMockMvc;

    private ModuleVersion moduleVersion;

    private ModuleVersion insertedModuleVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModuleVersion createEntity() {
        return new ModuleVersion()
            .version(DEFAULT_VERSION)
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
    public static ModuleVersion createUpdatedEntity() {
        return new ModuleVersion()
            .version(UPDATED_VERSION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        moduleVersion = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedModuleVersion != null) {
            moduleVersionRepository.delete(insertedModuleVersion);
            insertedModuleVersion = null;
        }
    }

    @Test
    @Transactional
    void createModuleVersion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ModuleVersion
        var returnedModuleVersion = om.readValue(
            restModuleVersionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleVersion)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ModuleVersion.class
        );

        // Validate the ModuleVersion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertModuleVersionUpdatableFieldsEquals(returnedModuleVersion, getPersistedModuleVersion(returnedModuleVersion));

        insertedModuleVersion = returnedModuleVersion;
    }

    @Test
    @Transactional
    void createModuleVersionWithExistingId() throws Exception {
        // Create the ModuleVersion with an existing ID
        moduleVersion.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModuleVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleVersion)))
            .andExpect(status().isBadRequest());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        moduleVersion.setVersion(null);

        // Create the ModuleVersion, which fails.

        restModuleVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleVersion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllModuleVersions() throws Exception {
        // Initialize the database
        insertedModuleVersion = moduleVersionRepository.saveAndFlush(moduleVersion);

        // Get all the moduleVersionList
        restModuleVersionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(moduleVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllModuleVersionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(moduleVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restModuleVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(moduleVersionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllModuleVersionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(moduleVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restModuleVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(moduleVersionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getModuleVersion() throws Exception {
        // Initialize the database
        insertedModuleVersion = moduleVersionRepository.saveAndFlush(moduleVersion);

        // Get the moduleVersion
        restModuleVersionMockMvc
            .perform(get(ENTITY_API_URL_ID, moduleVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(moduleVersion.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingModuleVersion() throws Exception {
        // Get the moduleVersion
        restModuleVersionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingModuleVersion() throws Exception {
        // Initialize the database
        insertedModuleVersion = moduleVersionRepository.saveAndFlush(moduleVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moduleVersion
        ModuleVersion updatedModuleVersion = moduleVersionRepository.findById(moduleVersion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedModuleVersion are not directly saved in db
        em.detach(updatedModuleVersion);
        updatedModuleVersion.version(UPDATED_VERSION).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restModuleVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedModuleVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedModuleVersion))
            )
            .andExpect(status().isOk());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedModuleVersionToMatchAllProperties(updatedModuleVersion);
    }

    @Test
    @Transactional
    void putNonExistingModuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModuleVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, moduleVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(moduleVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchModuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(moduleVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleVersionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateModuleVersionWithPatch() throws Exception {
        // Initialize the database
        insertedModuleVersion = moduleVersionRepository.saveAndFlush(moduleVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moduleVersion using partial update
        ModuleVersion partialUpdatedModuleVersion = new ModuleVersion();
        partialUpdatedModuleVersion.setId(moduleVersion.getId());

        restModuleVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModuleVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModuleVersion))
            )
            .andExpect(status().isOk());

        // Validate the ModuleVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModuleVersionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedModuleVersion, moduleVersion),
            getPersistedModuleVersion(moduleVersion)
        );
    }

    @Test
    @Transactional
    void fullUpdateModuleVersionWithPatch() throws Exception {
        // Initialize the database
        insertedModuleVersion = moduleVersionRepository.saveAndFlush(moduleVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moduleVersion using partial update
        ModuleVersion partialUpdatedModuleVersion = new ModuleVersion();
        partialUpdatedModuleVersion.setId(moduleVersion.getId());

        partialUpdatedModuleVersion
            .version(UPDATED_VERSION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restModuleVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModuleVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModuleVersion))
            )
            .andExpect(status().isOk());

        // Validate the ModuleVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModuleVersionUpdatableFieldsEquals(partialUpdatedModuleVersion, getPersistedModuleVersion(partialUpdatedModuleVersion));
    }

    @Test
    @Transactional
    void patchNonExistingModuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModuleVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, moduleVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(moduleVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(moduleVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModuleVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleVersionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(moduleVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModuleVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteModuleVersion() throws Exception {
        // Initialize the database
        insertedModuleVersion = moduleVersionRepository.saveAndFlush(moduleVersion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the moduleVersion
        restModuleVersionMockMvc
            .perform(delete(ENTITY_API_URL_ID, moduleVersion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return moduleVersionRepository.count();
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

    protected ModuleVersion getPersistedModuleVersion(ModuleVersion moduleVersion) {
        return moduleVersionRepository.findById(moduleVersion.getId()).orElseThrow();
    }

    protected void assertPersistedModuleVersionToMatchAllProperties(ModuleVersion expectedModuleVersion) {
        assertModuleVersionAllPropertiesEquals(expectedModuleVersion, getPersistedModuleVersion(expectedModuleVersion));
    }

    protected void assertPersistedModuleVersionToMatchUpdatableProperties(ModuleVersion expectedModuleVersion) {
        assertModuleVersionAllUpdatablePropertiesEquals(expectedModuleVersion, getPersistedModuleVersion(expectedModuleVersion));
    }
}
