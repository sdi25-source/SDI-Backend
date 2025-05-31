package com.sdi.web.rest;

import static com.sdi.domain.CertificationVersionAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.CertificationVersion;
import com.sdi.repository.CertificationVersionRepository;
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
 * Integration tests for the {@link CertificationVersionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CertificationVersionResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_EXPIRE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/certification-versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CertificationVersionRepository certificationVersionRepository;

    @Mock
    private CertificationVersionRepository certificationVersionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificationVersionMockMvc;

    private CertificationVersion certificationVersion;

    private CertificationVersion insertedCertificationVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CertificationVersion createEntity() {
        return new CertificationVersion()
            .version(DEFAULT_VERSION)
            .createDate(DEFAULT_CREATE_DATE)
            .expireDate(DEFAULT_EXPIRE_DATE)
            .description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CertificationVersion createUpdatedEntity() {
        return new CertificationVersion()
            .version(UPDATED_VERSION)
            .createDate(UPDATED_CREATE_DATE)
            .expireDate(UPDATED_EXPIRE_DATE)
            .description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        certificationVersion = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCertificationVersion != null) {
            certificationVersionRepository.delete(insertedCertificationVersion);
            insertedCertificationVersion = null;
        }
    }

    @Test
    @Transactional
    void createCertificationVersion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CertificationVersion
        var returnedCertificationVersion = om.readValue(
            restCertificationVersionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificationVersion)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CertificationVersion.class
        );

        // Validate the CertificationVersion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCertificationVersionUpdatableFieldsEquals(
            returnedCertificationVersion,
            getPersistedCertificationVersion(returnedCertificationVersion)
        );

        insertedCertificationVersion = returnedCertificationVersion;
    }

    @Test
    @Transactional
    void createCertificationVersionWithExistingId() throws Exception {
        // Create the CertificationVersion with an existing ID
        certificationVersion.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificationVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificationVersion)))
            .andExpect(status().isBadRequest());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certificationVersion.setVersion(null);

        // Create the CertificationVersion, which fails.

        restCertificationVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificationVersion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCertificationVersions() throws Exception {
        // Initialize the database
        insertedCertificationVersion = certificationVersionRepository.saveAndFlush(certificationVersion);

        // Get all the certificationVersionList
        restCertificationVersionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificationVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].expireDate").value(hasItem(DEFAULT_EXPIRE_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCertificationVersionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(certificationVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCertificationVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(certificationVersionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCertificationVersionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(certificationVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCertificationVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(certificationVersionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCertificationVersion() throws Exception {
        // Initialize the database
        insertedCertificationVersion = certificationVersionRepository.saveAndFlush(certificationVersion);

        // Get the certificationVersion
        restCertificationVersionMockMvc
            .perform(get(ENTITY_API_URL_ID, certificationVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certificationVersion.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.expireDate").value(DEFAULT_EXPIRE_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingCertificationVersion() throws Exception {
        // Get the certificationVersion
        restCertificationVersionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCertificationVersion() throws Exception {
        // Initialize the database
        insertedCertificationVersion = certificationVersionRepository.saveAndFlush(certificationVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificationVersion
        CertificationVersion updatedCertificationVersion = certificationVersionRepository
            .findById(certificationVersion.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCertificationVersion are not directly saved in db
        em.detach(updatedCertificationVersion);
        updatedCertificationVersion
            .version(UPDATED_VERSION)
            .createDate(UPDATED_CREATE_DATE)
            .expireDate(UPDATED_EXPIRE_DATE)
            .description(UPDATED_DESCRIPTION);

        restCertificationVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCertificationVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCertificationVersion))
            )
            .andExpect(status().isOk());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCertificationVersionToMatchAllProperties(updatedCertificationVersion);
    }

    @Test
    @Transactional
    void putNonExistingCertificationVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificationVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificationVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificationVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificationVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCertificationVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificationVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certificationVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCertificationVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificationVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationVersionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certificationVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCertificationVersionWithPatch() throws Exception {
        // Initialize the database
        insertedCertificationVersion = certificationVersionRepository.saveAndFlush(certificationVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificationVersion using partial update
        CertificationVersion partialUpdatedCertificationVersion = new CertificationVersion();
        partialUpdatedCertificationVersion.setId(certificationVersion.getId());

        partialUpdatedCertificationVersion.createDate(UPDATED_CREATE_DATE).description(UPDATED_DESCRIPTION);

        restCertificationVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificationVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificationVersion))
            )
            .andExpect(status().isOk());

        // Validate the CertificationVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificationVersionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCertificationVersion, certificationVersion),
            getPersistedCertificationVersion(certificationVersion)
        );
    }

    @Test
    @Transactional
    void fullUpdateCertificationVersionWithPatch() throws Exception {
        // Initialize the database
        insertedCertificationVersion = certificationVersionRepository.saveAndFlush(certificationVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certificationVersion using partial update
        CertificationVersion partialUpdatedCertificationVersion = new CertificationVersion();
        partialUpdatedCertificationVersion.setId(certificationVersion.getId());

        partialUpdatedCertificationVersion
            .version(UPDATED_VERSION)
            .createDate(UPDATED_CREATE_DATE)
            .expireDate(UPDATED_EXPIRE_DATE)
            .description(UPDATED_DESCRIPTION);

        restCertificationVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificationVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertificationVersion))
            )
            .andExpect(status().isOk());

        // Validate the CertificationVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificationVersionUpdatableFieldsEquals(
            partialUpdatedCertificationVersion,
            getPersistedCertificationVersion(partialUpdatedCertificationVersion)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCertificationVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificationVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificationVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, certificationVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificationVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCertificationVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificationVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certificationVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCertificationVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certificationVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationVersionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(certificationVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CertificationVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCertificationVersion() throws Exception {
        // Initialize the database
        insertedCertificationVersion = certificationVersionRepository.saveAndFlush(certificationVersion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the certificationVersion
        restCertificationVersionMockMvc
            .perform(delete(ENTITY_API_URL_ID, certificationVersion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return certificationVersionRepository.count();
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

    protected CertificationVersion getPersistedCertificationVersion(CertificationVersion certificationVersion) {
        return certificationVersionRepository.findById(certificationVersion.getId()).orElseThrow();
    }

    protected void assertPersistedCertificationVersionToMatchAllProperties(CertificationVersion expectedCertificationVersion) {
        assertCertificationVersionAllPropertiesEquals(
            expectedCertificationVersion,
            getPersistedCertificationVersion(expectedCertificationVersion)
        );
    }

    protected void assertPersistedCertificationVersionToMatchUpdatableProperties(CertificationVersion expectedCertificationVersion) {
        assertCertificationVersionAllUpdatablePropertiesEquals(
            expectedCertificationVersion,
            getPersistedCertificationVersion(expectedCertificationVersion)
        );
    }
}
