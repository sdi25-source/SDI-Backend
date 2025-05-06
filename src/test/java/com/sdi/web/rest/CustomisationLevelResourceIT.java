package com.sdi.web.rest;

import static com.sdi.domain.CustomisationLevelAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.CustomisationLevel;
import com.sdi.repository.CustomisationLevelRepository;
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
 * Integration tests for the {@link CustomisationLevelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomisationLevelResourceIT {

    private static final String DEFAULT_LEVEL = "AAAAAAAAAA";
    private static final String UPDATED_LEVEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/customisation-levels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomisationLevelRepository customisationLevelRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomisationLevelMockMvc;

    private CustomisationLevel customisationLevel;

    private CustomisationLevel insertedCustomisationLevel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomisationLevel createEntity() {
        return new CustomisationLevel()
            .level(DEFAULT_LEVEL)
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
    public static CustomisationLevel createUpdatedEntity() {
        return new CustomisationLevel()
            .level(UPDATED_LEVEL)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        customisationLevel = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCustomisationLevel != null) {
            customisationLevelRepository.delete(insertedCustomisationLevel);
            insertedCustomisationLevel = null;
        }
    }

    @Test
    @Transactional
    void createCustomisationLevel() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CustomisationLevel
        var returnedCustomisationLevel = om.readValue(
            restCustomisationLevelMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customisationLevel)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CustomisationLevel.class
        );

        // Validate the CustomisationLevel in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCustomisationLevelUpdatableFieldsEquals(
            returnedCustomisationLevel,
            getPersistedCustomisationLevel(returnedCustomisationLevel)
        );

        insertedCustomisationLevel = returnedCustomisationLevel;
    }

    @Test
    @Transactional
    void createCustomisationLevelWithExistingId() throws Exception {
        // Create the CustomisationLevel with an existing ID
        customisationLevel.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomisationLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customisationLevel)))
            .andExpect(status().isBadRequest());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        customisationLevel.setLevel(null);

        // Create the CustomisationLevel, which fails.

        restCustomisationLevelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customisationLevel)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCustomisationLevels() throws Exception {
        // Initialize the database
        insertedCustomisationLevel = customisationLevelRepository.saveAndFlush(customisationLevel);

        // Get all the customisationLevelList
        restCustomisationLevelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customisationLevel.getId().intValue())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getCustomisationLevel() throws Exception {
        // Initialize the database
        insertedCustomisationLevel = customisationLevelRepository.saveAndFlush(customisationLevel);

        // Get the customisationLevel
        restCustomisationLevelMockMvc
            .perform(get(ENTITY_API_URL_ID, customisationLevel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customisationLevel.getId().intValue()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingCustomisationLevel() throws Exception {
        // Get the customisationLevel
        restCustomisationLevelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomisationLevel() throws Exception {
        // Initialize the database
        insertedCustomisationLevel = customisationLevelRepository.saveAndFlush(customisationLevel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customisationLevel
        CustomisationLevel updatedCustomisationLevel = customisationLevelRepository.findById(customisationLevel.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCustomisationLevel are not directly saved in db
        em.detach(updatedCustomisationLevel);
        updatedCustomisationLevel.level(UPDATED_LEVEL).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restCustomisationLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCustomisationLevel.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCustomisationLevel))
            )
            .andExpect(status().isOk());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomisationLevelToMatchAllProperties(updatedCustomisationLevel);
    }

    @Test
    @Transactional
    void putNonExistingCustomisationLevel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customisationLevel.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomisationLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customisationLevel.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customisationLevel))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomisationLevel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customisationLevel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomisationLevelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(customisationLevel))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomisationLevel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customisationLevel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomisationLevelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(customisationLevel)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomisationLevelWithPatch() throws Exception {
        // Initialize the database
        insertedCustomisationLevel = customisationLevelRepository.saveAndFlush(customisationLevel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customisationLevel using partial update
        CustomisationLevel partialUpdatedCustomisationLevel = new CustomisationLevel();
        partialUpdatedCustomisationLevel.setId(customisationLevel.getId());

        partialUpdatedCustomisationLevel.level(UPDATED_LEVEL).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE);

        restCustomisationLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomisationLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomisationLevel))
            )
            .andExpect(status().isOk());

        // Validate the CustomisationLevel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomisationLevelUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCustomisationLevel, customisationLevel),
            getPersistedCustomisationLevel(customisationLevel)
        );
    }

    @Test
    @Transactional
    void fullUpdateCustomisationLevelWithPatch() throws Exception {
        // Initialize the database
        insertedCustomisationLevel = customisationLevelRepository.saveAndFlush(customisationLevel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customisationLevel using partial update
        CustomisationLevel partialUpdatedCustomisationLevel = new CustomisationLevel();
        partialUpdatedCustomisationLevel.setId(customisationLevel.getId());

        partialUpdatedCustomisationLevel
            .level(UPDATED_LEVEL)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restCustomisationLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomisationLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCustomisationLevel))
            )
            .andExpect(status().isOk());

        // Validate the CustomisationLevel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomisationLevelUpdatableFieldsEquals(
            partialUpdatedCustomisationLevel,
            getPersistedCustomisationLevel(partialUpdatedCustomisationLevel)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCustomisationLevel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customisationLevel.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomisationLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customisationLevel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customisationLevel))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomisationLevel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customisationLevel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomisationLevelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(customisationLevel))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomisationLevel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customisationLevel.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomisationLevelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(customisationLevel)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomisationLevel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomisationLevel() throws Exception {
        // Initialize the database
        insertedCustomisationLevel = customisationLevelRepository.saveAndFlush(customisationLevel);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customisationLevel
        restCustomisationLevelMockMvc
            .perform(delete(ENTITY_API_URL_ID, customisationLevel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customisationLevelRepository.count();
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

    protected CustomisationLevel getPersistedCustomisationLevel(CustomisationLevel customisationLevel) {
        return customisationLevelRepository.findById(customisationLevel.getId()).orElseThrow();
    }

    protected void assertPersistedCustomisationLevelToMatchAllProperties(CustomisationLevel expectedCustomisationLevel) {
        assertCustomisationLevelAllPropertiesEquals(expectedCustomisationLevel, getPersistedCustomisationLevel(expectedCustomisationLevel));
    }

    protected void assertPersistedCustomisationLevelToMatchUpdatableProperties(CustomisationLevel expectedCustomisationLevel) {
        assertCustomisationLevelAllUpdatablePropertiesEquals(
            expectedCustomisationLevel,
            getPersistedCustomisationLevel(expectedCustomisationLevel)
        );
    }
}
