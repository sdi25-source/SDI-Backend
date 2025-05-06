package com.sdi.web.rest;

import static com.sdi.domain.HAAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.HA;
import com.sdi.repository.HARepository;
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
 * Integration tests for the {@link HAResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HAResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/has";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HARepository hARepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHAMockMvc;

    private HA hA;

    private HA insertedHA;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HA createEntity() {
        return new HA().name(DEFAULT_NAME).createDate(DEFAULT_CREATE_DATE).updateDate(DEFAULT_UPDATE_DATE).notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HA createUpdatedEntity() {
        return new HA().name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        hA = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedHA != null) {
            hARepository.delete(insertedHA);
            insertedHA = null;
        }
    }

    @Test
    @Transactional
    void createHA() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HA
        var returnedHA = om.readValue(
            restHAMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hA)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HA.class
        );

        // Validate the HA in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertHAUpdatableFieldsEquals(returnedHA, getPersistedHA(returnedHA));

        insertedHA = returnedHA;
    }

    @Test
    @Transactional
    void createHAWithExistingId() throws Exception {
        // Create the HA with an existing ID
        hA.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHAMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hA)))
            .andExpect(status().isBadRequest());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        hA.setName(null);

        // Create the HA, which fails.

        restHAMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hA)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHAS() throws Exception {
        // Initialize the database
        insertedHA = hARepository.saveAndFlush(hA);

        // Get all the hAList
        restHAMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hA.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getHA() throws Exception {
        // Initialize the database
        insertedHA = hARepository.saveAndFlush(hA);

        // Get the hA
        restHAMockMvc
            .perform(get(ENTITY_API_URL_ID, hA.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hA.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingHA() throws Exception {
        // Get the hA
        restHAMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHA() throws Exception {
        // Initialize the database
        insertedHA = hARepository.saveAndFlush(hA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hA
        HA updatedHA = hARepository.findById(hA.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHA are not directly saved in db
        em.detach(updatedHA);
        updatedHA.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restHAMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHA.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(updatedHA))
            )
            .andExpect(status().isOk());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHAToMatchAllProperties(updatedHA);
    }

    @Test
    @Transactional
    void putNonExistingHA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hA.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHAMockMvc
            .perform(put(ENTITY_API_URL_ID, hA.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hA)))
            .andExpect(status().isBadRequest());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHAMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(hA))
            )
            .andExpect(status().isBadRequest());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHAMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(hA)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHAWithPatch() throws Exception {
        // Initialize the database
        insertedHA = hARepository.saveAndFlush(hA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hA using partial update
        HA partialUpdatedHA = new HA();
        partialUpdatedHA.setId(hA.getId());

        partialUpdatedHA.createDate(UPDATED_CREATE_DATE);

        restHAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHA.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHA))
            )
            .andExpect(status().isOk());

        // Validate the HA in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHAUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHA, hA), getPersistedHA(hA));
    }

    @Test
    @Transactional
    void fullUpdateHAWithPatch() throws Exception {
        // Initialize the database
        insertedHA = hARepository.saveAndFlush(hA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hA using partial update
        HA partialUpdatedHA = new HA();
        partialUpdatedHA.setId(hA.getId());

        partialUpdatedHA.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restHAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHA.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHA))
            )
            .andExpect(status().isOk());

        // Validate the HA in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHAUpdatableFieldsEquals(partialUpdatedHA, getPersistedHA(partialUpdatedHA));
    }

    @Test
    @Transactional
    void patchNonExistingHA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hA.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHAMockMvc
            .perform(patch(ENTITY_API_URL_ID, hA.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(hA)))
            .andExpect(status().isBadRequest());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(hA))
            )
            .andExpect(status().isBadRequest());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHAMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(hA)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHA() throws Exception {
        // Initialize the database
        insertedHA = hARepository.saveAndFlush(hA);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the hA
        restHAMockMvc.perform(delete(ENTITY_API_URL_ID, hA.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return hARepository.count();
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

    protected HA getPersistedHA(HA hA) {
        return hARepository.findById(hA.getId()).orElseThrow();
    }

    protected void assertPersistedHAToMatchAllProperties(HA expectedHA) {
        assertHAAllPropertiesEquals(expectedHA, getPersistedHA(expectedHA));
    }

    protected void assertPersistedHAToMatchUpdatableProperties(HA expectedHA) {
        assertHAAllUpdatablePropertiesEquals(expectedHA, getPersistedHA(expectedHA));
    }
}
