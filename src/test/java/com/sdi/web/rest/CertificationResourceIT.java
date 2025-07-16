package com.sdi.web.rest;

import static com.sdi.domain.CertificationAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.Certification;
import com.sdi.repository.CertificationRepository;
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
 * Integration tests for the {@link CertificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CertificationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/certifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificationMockMvc;

    private Certification certification;

    private Certification insertedCertification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certification createEntity() {
        return new Certification().name(DEFAULT_NAME).createDate(DEFAULT_CREATE_DATE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certification createUpdatedEntity() {
        return new Certification().name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        certification = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCertification != null) {
            certificationRepository.delete(insertedCertification);
            insertedCertification = null;
        }
    }

    @Test
    @Transactional
    void createCertification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Certification
        var returnedCertification = om.readValue(
            restCertificationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certification)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Certification.class
        );

        // Validate the Certification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCertificationUpdatableFieldsEquals(returnedCertification, getPersistedCertification(returnedCertification));

        insertedCertification = returnedCertification;
    }

    @Test
    @Transactional
    void createCertificationWithExistingId() throws Exception {
        // Create the Certification with an existing ID
        certification.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certification)))
            .andExpect(status().isBadRequest());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        certification.setName(null);

        // Create the Certification, which fails.

        restCertificationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certification)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCertifications() throws Exception {
        // Initialize the database
        insertedCertification = certificationRepository.saveAndFlush(certification);

        // Get all the certificationList
        restCertificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certification.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getCertification() throws Exception {
        // Initialize the database
        insertedCertification = certificationRepository.saveAndFlush(certification);

        // Get the certification
        restCertificationMockMvc
            .perform(get(ENTITY_API_URL_ID, certification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certification.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingCertification() throws Exception {
        // Get the certification
        restCertificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCertification() throws Exception {
        // Initialize the database
        insertedCertification = certificationRepository.saveAndFlush(certification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certification
        Certification updatedCertification = certificationRepository.findById(certification.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCertification are not directly saved in db
        em.detach(updatedCertification);
        updatedCertification.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).description(UPDATED_DESCRIPTION);

        restCertificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCertification.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCertification))
            )
            .andExpect(status().isOk());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCertificationToMatchAllProperties(updatedCertification);
    }

    @Test
    @Transactional
    void putNonExistingCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certification.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certification.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(certification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(certification)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCertificationWithPatch() throws Exception {
        // Initialize the database
        insertedCertification = certificationRepository.saveAndFlush(certification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certification using partial update
        Certification partialUpdatedCertification = new Certification();
        partialUpdatedCertification.setId(certification.getId());

        partialUpdatedCertification.createDate(UPDATED_CREATE_DATE);

        restCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertification))
            )
            .andExpect(status().isOk());

        // Validate the Certification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCertification, certification),
            getPersistedCertification(certification)
        );
    }

    @Test
    @Transactional
    void fullUpdateCertificationWithPatch() throws Exception {
        // Initialize the database
        insertedCertification = certificationRepository.saveAndFlush(certification);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the certification using partial update
        Certification partialUpdatedCertification = new Certification();
        partialUpdatedCertification.setId(certification.getId());

        partialUpdatedCertification.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).description(UPDATED_DESCRIPTION);

        restCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCertification))
            )
            .andExpect(status().isOk());

        // Validate the Certification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCertificationUpdatableFieldsEquals(partialUpdatedCertification, getPersistedCertification(partialUpdatedCertification));
    }

    @Test
    @Transactional
    void patchNonExistingCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certification.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, certification.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(certification))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCertification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        certification.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(certification)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCertification() throws Exception {
        // Initialize the database
        insertedCertification = certificationRepository.saveAndFlush(certification);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the certification
        restCertificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, certification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return certificationRepository.count();
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

    protected Certification getPersistedCertification(Certification certification) {
        return certificationRepository.findById(certification.getId()).orElseThrow();
    }

    protected void assertPersistedCertificationToMatchAllProperties(Certification expectedCertification) {
        assertCertificationAllPropertiesEquals(expectedCertification, getPersistedCertification(expectedCertification));
    }

    protected void assertPersistedCertificationToMatchUpdatableProperties(Certification expectedCertification) {
        assertCertificationAllUpdatablePropertiesEquals(expectedCertification, getPersistedCertification(expectedCertification));
    }
}
