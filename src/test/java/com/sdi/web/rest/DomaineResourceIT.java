package com.sdi.web.rest;

import static com.sdi.domain.DomaineAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.Domaine;
import com.sdi.repository.DomaineRepository;
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
 * Integration tests for the {@link DomaineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DomaineResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/domaines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DomaineRepository domaineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDomaineMockMvc;

    private Domaine domaine;

    private Domaine insertedDomaine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Domaine createEntity() {
        return new Domaine().name(DEFAULT_NAME).createDate(DEFAULT_CREATE_DATE).updateDate(DEFAULT_UPDATE_DATE).notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Domaine createUpdatedEntity() {
        return new Domaine().name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        domaine = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDomaine != null) {
            domaineRepository.delete(insertedDomaine);
            insertedDomaine = null;
        }
    }

    @Test
    @Transactional
    void createDomaine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Domaine
        var returnedDomaine = om.readValue(
            restDomaineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(domaine)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Domaine.class
        );

        // Validate the Domaine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDomaineUpdatableFieldsEquals(returnedDomaine, getPersistedDomaine(returnedDomaine));

        insertedDomaine = returnedDomaine;
    }

    @Test
    @Transactional
    void createDomaineWithExistingId() throws Exception {
        // Create the Domaine with an existing ID
        domaine.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDomaineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(domaine)))
            .andExpect(status().isBadRequest());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        domaine.setName(null);

        // Create the Domaine, which fails.

        restDomaineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(domaine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDomaines() throws Exception {
        // Initialize the database
        insertedDomaine = domaineRepository.saveAndFlush(domaine);

        // Get all the domaineList
        restDomaineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(domaine.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getDomaine() throws Exception {
        // Initialize the database
        insertedDomaine = domaineRepository.saveAndFlush(domaine);

        // Get the domaine
        restDomaineMockMvc
            .perform(get(ENTITY_API_URL_ID, domaine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(domaine.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingDomaine() throws Exception {
        // Get the domaine
        restDomaineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDomaine() throws Exception {
        // Initialize the database
        insertedDomaine = domaineRepository.saveAndFlush(domaine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the domaine
        Domaine updatedDomaine = domaineRepository.findById(domaine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDomaine are not directly saved in db
        em.detach(updatedDomaine);
        updatedDomaine.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restDomaineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDomaine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDomaine))
            )
            .andExpect(status().isOk());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDomaineToMatchAllProperties(updatedDomaine);
    }

    @Test
    @Transactional
    void putNonExistingDomaine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        domaine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDomaineMockMvc
            .perform(put(ENTITY_API_URL_ID, domaine.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(domaine)))
            .andExpect(status().isBadRequest());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDomaine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        domaine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDomaineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(domaine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDomaine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        domaine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDomaineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(domaine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDomaineWithPatch() throws Exception {
        // Initialize the database
        insertedDomaine = domaineRepository.saveAndFlush(domaine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the domaine using partial update
        Domaine partialUpdatedDomaine = new Domaine();
        partialUpdatedDomaine.setId(domaine.getId());

        partialUpdatedDomaine.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restDomaineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDomaine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDomaine))
            )
            .andExpect(status().isOk());

        // Validate the Domaine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDomaineUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDomaine, domaine), getPersistedDomaine(domaine));
    }

    @Test
    @Transactional
    void fullUpdateDomaineWithPatch() throws Exception {
        // Initialize the database
        insertedDomaine = domaineRepository.saveAndFlush(domaine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the domaine using partial update
        Domaine partialUpdatedDomaine = new Domaine();
        partialUpdatedDomaine.setId(domaine.getId());

        partialUpdatedDomaine.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restDomaineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDomaine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDomaine))
            )
            .andExpect(status().isOk());

        // Validate the Domaine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDomaineUpdatableFieldsEquals(partialUpdatedDomaine, getPersistedDomaine(partialUpdatedDomaine));
    }

    @Test
    @Transactional
    void patchNonExistingDomaine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        domaine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDomaineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, domaine.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(domaine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDomaine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        domaine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDomaineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(domaine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDomaine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        domaine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDomaineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(domaine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Domaine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDomaine() throws Exception {
        // Initialize the database
        insertedDomaine = domaineRepository.saveAndFlush(domaine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the domaine
        restDomaineMockMvc
            .perform(delete(ENTITY_API_URL_ID, domaine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return domaineRepository.count();
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

    protected Domaine getPersistedDomaine(Domaine domaine) {
        return domaineRepository.findById(domaine.getId()).orElseThrow();
    }

    protected void assertPersistedDomaineToMatchAllProperties(Domaine expectedDomaine) {
        assertDomaineAllPropertiesEquals(expectedDomaine, getPersistedDomaine(expectedDomaine));
    }

    protected void assertPersistedDomaineToMatchUpdatableProperties(Domaine expectedDomaine) {
        assertDomaineAllUpdatablePropertiesEquals(expectedDomaine, getPersistedDomaine(expectedDomaine));
    }
}
