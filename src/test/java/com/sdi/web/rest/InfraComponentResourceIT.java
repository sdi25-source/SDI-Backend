package com.sdi.web.rest;

import static com.sdi.domain.InfraComponentAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.InfraComponent;
import com.sdi.repository.InfraComponentRepository;
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
 * Integration tests for the {@link InfraComponentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InfraComponentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VENDOR = "AAAAAAAAAA";
    private static final String UPDATED_VENDOR = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/infra-components";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InfraComponentRepository infraComponentRepository;

    @Mock
    private InfraComponentRepository infraComponentRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInfraComponentMockMvc;

    private InfraComponent infraComponent;

    private InfraComponent insertedInfraComponent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InfraComponent createEntity() {
        return new InfraComponent().name(DEFAULT_NAME).vendor(DEFAULT_VENDOR).notes(DEFAULT_NOTES).createDate(DEFAULT_CREATE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InfraComponent createUpdatedEntity() {
        return new InfraComponent().name(UPDATED_NAME).vendor(UPDATED_VENDOR).notes(UPDATED_NOTES).createDate(UPDATED_CREATE_DATE);
    }

    @BeforeEach
    public void initTest() {
        infraComponent = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInfraComponent != null) {
            infraComponentRepository.delete(insertedInfraComponent);
            insertedInfraComponent = null;
        }
    }

    @Test
    @Transactional
    void createInfraComponent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InfraComponent
        var returnedInfraComponent = om.readValue(
            restInfraComponentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponent)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfraComponent.class
        );

        // Validate the InfraComponent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInfraComponentUpdatableFieldsEquals(returnedInfraComponent, getPersistedInfraComponent(returnedInfraComponent));

        insertedInfraComponent = returnedInfraComponent;
    }

    @Test
    @Transactional
    void createInfraComponentWithExistingId() throws Exception {
        // Create the InfraComponent with an existing ID
        infraComponent.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInfraComponentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponent)))
            .andExpect(status().isBadRequest());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        infraComponent.setName(null);

        // Create the InfraComponent, which fails.

        restInfraComponentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInfraComponents() throws Exception {
        // Initialize the database
        insertedInfraComponent = infraComponentRepository.saveAndFlush(infraComponent);

        // Get all the infraComponentList
        restInfraComponentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(infraComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].vendor").value(hasItem(DEFAULT_VENDOR)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInfraComponentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(infraComponentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInfraComponentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(infraComponentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInfraComponentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(infraComponentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInfraComponentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(infraComponentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInfraComponent() throws Exception {
        // Initialize the database
        insertedInfraComponent = infraComponentRepository.saveAndFlush(infraComponent);

        // Get the infraComponent
        restInfraComponentMockMvc
            .perform(get(ENTITY_API_URL_ID, infraComponent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(infraComponent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.vendor").value(DEFAULT_VENDOR))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingInfraComponent() throws Exception {
        // Get the infraComponent
        restInfraComponentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInfraComponent() throws Exception {
        // Initialize the database
        insertedInfraComponent = infraComponentRepository.saveAndFlush(infraComponent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infraComponent
        InfraComponent updatedInfraComponent = infraComponentRepository.findById(infraComponent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInfraComponent are not directly saved in db
        em.detach(updatedInfraComponent);
        updatedInfraComponent.name(UPDATED_NAME).vendor(UPDATED_VENDOR).notes(UPDATED_NOTES).createDate(UPDATED_CREATE_DATE);

        restInfraComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInfraComponent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInfraComponent))
            )
            .andExpect(status().isOk());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInfraComponentToMatchAllProperties(updatedInfraComponent);
    }

    @Test
    @Transactional
    void putNonExistingInfraComponent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInfraComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, infraComponent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(infraComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInfraComponent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(infraComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInfraComponent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infraComponent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInfraComponentWithPatch() throws Exception {
        // Initialize the database
        insertedInfraComponent = infraComponentRepository.saveAndFlush(infraComponent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infraComponent using partial update
        InfraComponent partialUpdatedInfraComponent = new InfraComponent();
        partialUpdatedInfraComponent.setId(infraComponent.getId());

        partialUpdatedInfraComponent.name(UPDATED_NAME).notes(UPDATED_NOTES).createDate(UPDATED_CREATE_DATE);

        restInfraComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInfraComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInfraComponent))
            )
            .andExpect(status().isOk());

        // Validate the InfraComponent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfraComponentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInfraComponent, infraComponent),
            getPersistedInfraComponent(infraComponent)
        );
    }

    @Test
    @Transactional
    void fullUpdateInfraComponentWithPatch() throws Exception {
        // Initialize the database
        insertedInfraComponent = infraComponentRepository.saveAndFlush(infraComponent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infraComponent using partial update
        InfraComponent partialUpdatedInfraComponent = new InfraComponent();
        partialUpdatedInfraComponent.setId(infraComponent.getId());

        partialUpdatedInfraComponent.name(UPDATED_NAME).vendor(UPDATED_VENDOR).notes(UPDATED_NOTES).createDate(UPDATED_CREATE_DATE);

        restInfraComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInfraComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInfraComponent))
            )
            .andExpect(status().isOk());

        // Validate the InfraComponent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfraComponentUpdatableFieldsEquals(partialUpdatedInfraComponent, getPersistedInfraComponent(partialUpdatedInfraComponent));
    }

    @Test
    @Transactional
    void patchNonExistingInfraComponent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInfraComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, infraComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(infraComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInfraComponent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(infraComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInfraComponent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        infraComponent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfraComponentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(infraComponent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InfraComponent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInfraComponent() throws Exception {
        // Initialize the database
        insertedInfraComponent = infraComponentRepository.saveAndFlush(infraComponent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the infraComponent
        restInfraComponentMockMvc
            .perform(delete(ENTITY_API_URL_ID, infraComponent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return infraComponentRepository.count();
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

    protected InfraComponent getPersistedInfraComponent(InfraComponent infraComponent) {
        return infraComponentRepository.findById(infraComponent.getId()).orElseThrow();
    }

    protected void assertPersistedInfraComponentToMatchAllProperties(InfraComponent expectedInfraComponent) {
        assertInfraComponentAllPropertiesEquals(expectedInfraComponent, getPersistedInfraComponent(expectedInfraComponent));
    }

    protected void assertPersistedInfraComponentToMatchUpdatableProperties(InfraComponent expectedInfraComponent) {
        assertInfraComponentAllUpdatablePropertiesEquals(expectedInfraComponent, getPersistedInfraComponent(expectedInfraComponent));
    }
}
