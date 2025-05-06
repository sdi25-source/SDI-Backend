package com.sdi.web.rest;

import static com.sdi.domain.InfraComponentVersionAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.InfraComponentVersion;
import com.sdi.repository.InfraComponentVersionRepository;
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
 * Integration tests for the {@link InfraComponentVersionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InfraComponentVersionResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/infra-component-versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InfraComponentVersionRepository infraComponentVersionRepository;

    @Mock
    private InfraComponentVersionRepository infraComponentVersionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInfraComponentVersionMockMvc;

    private InfraComponentVersion infraComponentVersion;

    private InfraComponentVersion insertedInfraComponentVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InfraComponentVersion createEntity() {
        return new InfraComponentVersion()
            .version(DEFAULT_VERSION)
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
    public static InfraComponentVersion createUpdatedEntity() {
        return new InfraComponentVersion()
            .version(UPDATED_VERSION)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);
    }

    @BeforeEach
    public void initTest() {
        infraComponentVersion = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInfraComponentVersion != null) {
            infraComponentVersionRepository.delete(insertedInfraComponentVersion);
            insertedInfraComponentVersion = null;
        }
    }

    @Test
    @Transactional
    void createInfraComponentVersion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InfraComponentVersion
        var returnedInfraComponentVersion = om.readValue(
            restInfraComponentVersionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponentVersion)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfraComponentVersion.class
        );

        // Validate the InfraComponentVersion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInfraComponentVersionUpdatableFieldsEquals(
            returnedInfraComponentVersion,
            getPersistedInfraComponentVersion(returnedInfraComponentVersion)
        );

        insertedInfraComponentVersion = returnedInfraComponentVersion;
    }

    @Test
    @Transactional
    void createInfraComponentVersionWithExistingId() throws Exception {
        // Create the InfraComponentVersion with an existing ID
        infraComponentVersion.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInfraComponentVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponentVersion)))
            .andExpect(status().isBadRequest());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        infraComponentVersion.setVersion(null);

        // Create the InfraComponentVersion, which fails.

        restInfraComponentVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponentVersion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInfraComponentVersions() throws Exception {
        // Initialize the database
        insertedInfraComponentVersion = infraComponentVersionRepository.saveAndFlush(infraComponentVersion);

        // Get all the infraComponentVersionList
        restInfraComponentVersionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(infraComponentVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInfraComponentVersionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(infraComponentVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInfraComponentVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(infraComponentVersionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInfraComponentVersionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(infraComponentVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInfraComponentVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(infraComponentVersionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInfraComponentVersion() throws Exception {
        // Initialize the database
        insertedInfraComponentVersion = infraComponentVersionRepository.saveAndFlush(infraComponentVersion);

        // Get the infraComponentVersion
        restInfraComponentVersionMockMvc
            .perform(get(ENTITY_API_URL_ID, infraComponentVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(infraComponentVersion.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingInfraComponentVersion() throws Exception {
        // Get the infraComponentVersion
        restInfraComponentVersionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInfraComponentVersion() throws Exception {
        // Initialize the database
        insertedInfraComponentVersion = infraComponentVersionRepository.saveAndFlush(infraComponentVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infraComponentVersion
        InfraComponentVersion updatedInfraComponentVersion = infraComponentVersionRepository
            .findById(infraComponentVersion.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedInfraComponentVersion are not directly saved in db
        em.detach(updatedInfraComponentVersion);
        updatedInfraComponentVersion
            .version(UPDATED_VERSION)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restInfraComponentVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInfraComponentVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInfraComponentVersion))
            )
            .andExpect(status().isOk());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInfraComponentVersionToMatchAllProperties(updatedInfraComponentVersion);
    }

    @Test
    @Transactional
    void putNonExistingInfraComponentVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponentVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInfraComponentVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, infraComponentVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(infraComponentVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInfraComponentVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponentVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(infraComponentVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInfraComponentVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponentVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentVersionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponentVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInfraComponentVersionWithPatch() throws Exception {
        // Initialize the database
        insertedInfraComponentVersion = infraComponentVersionRepository.saveAndFlush(infraComponentVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infraComponentVersion using partial update
        InfraComponentVersion partialUpdatedInfraComponentVersion = new InfraComponentVersion();
        partialUpdatedInfraComponentVersion.setId(infraComponentVersion.getId());

        partialUpdatedInfraComponentVersion.version(UPDATED_VERSION).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE);

        restInfraComponentVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInfraComponentVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInfraComponentVersion))
            )
            .andExpect(status().isOk());

        // Validate the InfraComponentVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfraComponentVersionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInfraComponentVersion, infraComponentVersion),
            getPersistedInfraComponentVersion(infraComponentVersion)
        );
    }

    @Test
    @Transactional
    void fullUpdateInfraComponentVersionWithPatch() throws Exception {
        // Initialize the database
        insertedInfraComponentVersion = infraComponentVersionRepository.saveAndFlush(infraComponentVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infraComponentVersion using partial update
        InfraComponentVersion partialUpdatedInfraComponentVersion = new InfraComponentVersion();
        partialUpdatedInfraComponentVersion.setId(infraComponentVersion.getId());

        partialUpdatedInfraComponentVersion
            .version(UPDATED_VERSION)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restInfraComponentVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInfraComponentVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInfraComponentVersion))
            )
            .andExpect(status().isOk());

        // Validate the InfraComponentVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfraComponentVersionUpdatableFieldsEquals(
            partialUpdatedInfraComponentVersion,
            getPersistedInfraComponentVersion(partialUpdatedInfraComponentVersion)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInfraComponentVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponentVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInfraComponentVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, infraComponentVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(infraComponentVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInfraComponentVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponentVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(infraComponentVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInfraComponentVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponentVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentVersionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(infraComponentVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InfraComponentVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInfraComponentVersion() throws Exception {
        // Initialize the database
        insertedInfraComponentVersion = infraComponentVersionRepository.saveAndFlush(infraComponentVersion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the infraComponentVersion
        restInfraComponentVersionMockMvc
            .perform(delete(ENTITY_API_URL_ID, infraComponentVersion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return infraComponentVersionRepository.count();
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

    protected InfraComponentVersion getPersistedInfraComponentVersion(InfraComponentVersion infraComponentVersion) {
        return infraComponentVersionRepository.findById(infraComponentVersion.getId()).orElseThrow();
    }

    protected void assertPersistedInfraComponentVersionToMatchAllProperties(InfraComponentVersion expectedInfraComponentVersion) {
        assertInfraComponentVersionAllPropertiesEquals(
            expectedInfraComponentVersion,
            getPersistedInfraComponentVersion(expectedInfraComponentVersion)
        );
    }

    protected void assertPersistedInfraComponentVersionToMatchUpdatableProperties(InfraComponentVersion expectedInfraComponentVersion) {
        assertInfraComponentVersionAllUpdatablePropertiesEquals(
            expectedInfraComponentVersion,
            getPersistedInfraComponentVersion(expectedInfraComponentVersion)
        );
    }
}
