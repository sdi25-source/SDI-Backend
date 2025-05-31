package com.sdi.web.rest;

import static com.sdi.domain.ModuleDeployementAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ModuleDeployement;
import com.sdi.repository.ModuleDeployementRepository;
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
 * Integration tests for the {@link ModuleDeployementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ModuleDeployementResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/module-deployements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModuleDeployementRepository moduleDeployementRepository;

    @Mock
    private ModuleDeployementRepository moduleDeployementRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModuleDeployementMockMvc;

    private ModuleDeployement moduleDeployement;

    private ModuleDeployement insertedModuleDeployement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModuleDeployement createEntity() {
        return new ModuleDeployement()
            .code(DEFAULT_CODE)
            .notes(DEFAULT_NOTES)
            .createDate(DEFAULT_CREATE_DATE)
            .updateDate(DEFAULT_UPDATE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModuleDeployement createUpdatedEntity() {
        return new ModuleDeployement()
            .code(UPDATED_CODE)
            .notes(UPDATED_NOTES)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);
    }

    @BeforeEach
    public void initTest() {
        moduleDeployement = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedModuleDeployement != null) {
            moduleDeployementRepository.delete(insertedModuleDeployement);
            insertedModuleDeployement = null;
        }
    }

    @Test
    @Transactional
    void createModuleDeployement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ModuleDeployement
        var returnedModuleDeployement = om.readValue(
            restModuleDeployementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleDeployement)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ModuleDeployement.class
        );

        // Validate the ModuleDeployement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertModuleDeployementUpdatableFieldsEquals(returnedModuleDeployement, getPersistedModuleDeployement(returnedModuleDeployement));

        insertedModuleDeployement = returnedModuleDeployement;
    }

    @Test
    @Transactional
    void createModuleDeployementWithExistingId() throws Exception {
        // Create the ModuleDeployement with an existing ID
        moduleDeployement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModuleDeployementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleDeployement)))
            .andExpect(status().isBadRequest());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        moduleDeployement.setCode(null);

        // Create the ModuleDeployement, which fails.

        restModuleDeployementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleDeployement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllModuleDeployements() throws Exception {
        // Initialize the database
        insertedModuleDeployement = moduleDeployementRepository.saveAndFlush(moduleDeployement);

        // Get all the moduleDeployementList
        restModuleDeployementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(moduleDeployement.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllModuleDeployementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(moduleDeployementRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restModuleDeployementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(moduleDeployementRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllModuleDeployementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(moduleDeployementRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restModuleDeployementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(moduleDeployementRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getModuleDeployement() throws Exception {
        // Initialize the database
        insertedModuleDeployement = moduleDeployementRepository.saveAndFlush(moduleDeployement);

        // Get the moduleDeployement
        restModuleDeployementMockMvc
            .perform(get(ENTITY_API_URL_ID, moduleDeployement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(moduleDeployement.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingModuleDeployement() throws Exception {
        // Get the moduleDeployement
        restModuleDeployementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingModuleDeployement() throws Exception {
        // Initialize the database
        insertedModuleDeployement = moduleDeployementRepository.saveAndFlush(moduleDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moduleDeployement
        ModuleDeployement updatedModuleDeployement = moduleDeployementRepository.findById(moduleDeployement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedModuleDeployement are not directly saved in db
        em.detach(updatedModuleDeployement);
        updatedModuleDeployement.code(UPDATED_CODE).notes(UPDATED_NOTES).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE);

        restModuleDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedModuleDeployement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedModuleDeployement))
            )
            .andExpect(status().isOk());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedModuleDeployementToMatchAllProperties(updatedModuleDeployement);
    }

    @Test
    @Transactional
    void putNonExistingModuleDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleDeployement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModuleDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, moduleDeployement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(moduleDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchModuleDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(moduleDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModuleDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleDeployementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moduleDeployement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateModuleDeployementWithPatch() throws Exception {
        // Initialize the database
        insertedModuleDeployement = moduleDeployementRepository.saveAndFlush(moduleDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moduleDeployement using partial update
        ModuleDeployement partialUpdatedModuleDeployement = new ModuleDeployement();
        partialUpdatedModuleDeployement.setId(moduleDeployement.getId());

        partialUpdatedModuleDeployement.createDate(UPDATED_CREATE_DATE);

        restModuleDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModuleDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModuleDeployement))
            )
            .andExpect(status().isOk());

        // Validate the ModuleDeployement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModuleDeployementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedModuleDeployement, moduleDeployement),
            getPersistedModuleDeployement(moduleDeployement)
        );
    }

    @Test
    @Transactional
    void fullUpdateModuleDeployementWithPatch() throws Exception {
        // Initialize the database
        insertedModuleDeployement = moduleDeployementRepository.saveAndFlush(moduleDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moduleDeployement using partial update
        ModuleDeployement partialUpdatedModuleDeployement = new ModuleDeployement();
        partialUpdatedModuleDeployement.setId(moduleDeployement.getId());

        partialUpdatedModuleDeployement
            .code(UPDATED_CODE)
            .notes(UPDATED_NOTES)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restModuleDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModuleDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModuleDeployement))
            )
            .andExpect(status().isOk());

        // Validate the ModuleDeployement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModuleDeployementUpdatableFieldsEquals(
            partialUpdatedModuleDeployement,
            getPersistedModuleDeployement(partialUpdatedModuleDeployement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingModuleDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleDeployement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModuleDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, moduleDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(moduleDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModuleDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(moduleDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModuleDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moduleDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModuleDeployementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(moduleDeployement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModuleDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteModuleDeployement() throws Exception {
        // Initialize the database
        insertedModuleDeployement = moduleDeployementRepository.saveAndFlush(moduleDeployement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the moduleDeployement
        restModuleDeployementMockMvc
            .perform(delete(ENTITY_API_URL_ID, moduleDeployement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return moduleDeployementRepository.count();
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

    protected ModuleDeployement getPersistedModuleDeployement(ModuleDeployement moduleDeployement) {
        return moduleDeployementRepository.findById(moduleDeployement.getId()).orElseThrow();
    }

    protected void assertPersistedModuleDeployementToMatchAllProperties(ModuleDeployement expectedModuleDeployement) {
        assertModuleDeployementAllPropertiesEquals(expectedModuleDeployement, getPersistedModuleDeployement(expectedModuleDeployement));
    }

    protected void assertPersistedModuleDeployementToMatchUpdatableProperties(ModuleDeployement expectedModuleDeployement) {
        assertModuleDeployementAllUpdatablePropertiesEquals(
            expectedModuleDeployement,
            getPersistedModuleDeployement(expectedModuleDeployement)
        );
    }
}
