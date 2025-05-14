package com.sdi.web.rest;

import static com.sdi.domain.RequestOfChangeAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.RequestOfChange;
import com.sdi.domain.enumeration.RequestStatus;
import com.sdi.repository.RequestOfChangeRepository;
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
 * Integration tests for the {@link RequestOfChangeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RequestOfChangeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBBBBBBB";

    private static final RequestStatus DEFAULT_STATUS = RequestStatus.PENDING;
    private static final RequestStatus UPDATED_STATUS = RequestStatus.APPROVED;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/request-of-changes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RequestOfChangeRepository requestOfChangeRepository;

    @Mock
    private RequestOfChangeRepository requestOfChangeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRequestOfChangeMockMvc;

    private RequestOfChange requestOfChange;

    private RequestOfChange insertedRequestOfChange;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RequestOfChange createEntity() {
        return new RequestOfChange()
            .title(DEFAULT_TITLE)
            .keywords(DEFAULT_KEYWORDS)
            .status(DEFAULT_STATUS)
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
    public static RequestOfChange createUpdatedEntity() {
        return new RequestOfChange()
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);
    }

    @BeforeEach
    public void initTest() {
        requestOfChange = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRequestOfChange != null) {
            requestOfChangeRepository.delete(insertedRequestOfChange);
            insertedRequestOfChange = null;
        }
    }

    @Test
    @Transactional
    void createRequestOfChange() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RequestOfChange
        var returnedRequestOfChange = om.readValue(
            restRequestOfChangeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestOfChange)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RequestOfChange.class
        );

        // Validate the RequestOfChange in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRequestOfChangeUpdatableFieldsEquals(returnedRequestOfChange, getPersistedRequestOfChange(returnedRequestOfChange));

        insertedRequestOfChange = returnedRequestOfChange;
    }

    @Test
    @Transactional
    void createRequestOfChangeWithExistingId() throws Exception {
        // Create the RequestOfChange with an existing ID
        requestOfChange.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRequestOfChangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestOfChange)))
            .andExpect(status().isBadRequest());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        requestOfChange.setTitle(null);

        // Create the RequestOfChange, which fails.

        restRequestOfChangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestOfChange)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        requestOfChange.setStatus(null);

        // Create the RequestOfChange, which fails.

        restRequestOfChangeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestOfChange)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRequestOfChanges() throws Exception {
        // Initialize the database
        insertedRequestOfChange = requestOfChangeRepository.saveAndFlush(requestOfChange);

        // Get all the requestOfChangeList
        restRequestOfChangeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requestOfChange.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRequestOfChangesWithEagerRelationshipsIsEnabled() throws Exception {
        when(requestOfChangeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRequestOfChangeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(requestOfChangeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRequestOfChangesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(requestOfChangeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRequestOfChangeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(requestOfChangeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRequestOfChange() throws Exception {
        // Initialize the database
        insertedRequestOfChange = requestOfChangeRepository.saveAndFlush(requestOfChange);

        // Get the requestOfChange
        restRequestOfChangeMockMvc
            .perform(get(ENTITY_API_URL_ID, requestOfChange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(requestOfChange.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRequestOfChange() throws Exception {
        // Get the requestOfChange
        restRequestOfChangeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRequestOfChange() throws Exception {
        // Initialize the database
        insertedRequestOfChange = requestOfChangeRepository.saveAndFlush(requestOfChange);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requestOfChange
        RequestOfChange updatedRequestOfChange = requestOfChangeRepository.findById(requestOfChange.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRequestOfChange are not directly saved in db
        em.detach(updatedRequestOfChange);
        updatedRequestOfChange
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restRequestOfChangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRequestOfChange.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRequestOfChange))
            )
            .andExpect(status().isOk());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRequestOfChangeToMatchAllProperties(updatedRequestOfChange);
    }

    @Test
    @Transactional
    void putNonExistingRequestOfChange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestOfChange.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequestOfChangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, requestOfChange.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(requestOfChange))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRequestOfChange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestOfChange.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestOfChangeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(requestOfChange))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRequestOfChange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestOfChange.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestOfChangeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestOfChange)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRequestOfChangeWithPatch() throws Exception {
        // Initialize the database
        insertedRequestOfChange = requestOfChangeRepository.saveAndFlush(requestOfChange);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requestOfChange using partial update
        RequestOfChange partialUpdatedRequestOfChange = new RequestOfChange();
        partialUpdatedRequestOfChange.setId(requestOfChange.getId());

        partialUpdatedRequestOfChange.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).createDate(UPDATED_CREATE_DATE);

        restRequestOfChangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequestOfChange.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRequestOfChange))
            )
            .andExpect(status().isOk());

        // Validate the RequestOfChange in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestOfChangeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRequestOfChange, requestOfChange),
            getPersistedRequestOfChange(requestOfChange)
        );
    }

    @Test
    @Transactional
    void fullUpdateRequestOfChangeWithPatch() throws Exception {
        // Initialize the database
        insertedRequestOfChange = requestOfChangeRepository.saveAndFlush(requestOfChange);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requestOfChange using partial update
        RequestOfChange partialUpdatedRequestOfChange = new RequestOfChange();
        partialUpdatedRequestOfChange.setId(requestOfChange.getId());

        partialUpdatedRequestOfChange
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restRequestOfChangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequestOfChange.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRequestOfChange))
            )
            .andExpect(status().isOk());

        // Validate the RequestOfChange in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestOfChangeUpdatableFieldsEquals(
            partialUpdatedRequestOfChange,
            getPersistedRequestOfChange(partialUpdatedRequestOfChange)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRequestOfChange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestOfChange.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequestOfChangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, requestOfChange.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(requestOfChange))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRequestOfChange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestOfChange.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestOfChangeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(requestOfChange))
            )
            .andExpect(status().isBadRequest());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRequestOfChange() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requestOfChange.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestOfChangeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(requestOfChange)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RequestOfChange in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRequestOfChange() throws Exception {
        // Initialize the database
        insertedRequestOfChange = requestOfChangeRepository.saveAndFlush(requestOfChange);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the requestOfChange
        restRequestOfChangeMockMvc
            .perform(delete(ENTITY_API_URL_ID, requestOfChange.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return requestOfChangeRepository.count();
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

    protected RequestOfChange getPersistedRequestOfChange(RequestOfChange requestOfChange) {
        return requestOfChangeRepository.findById(requestOfChange.getId()).orElseThrow();
    }

    protected void assertPersistedRequestOfChangeToMatchAllProperties(RequestOfChange expectedRequestOfChange) {
        assertRequestOfChangeAllPropertiesEquals(expectedRequestOfChange, getPersistedRequestOfChange(expectedRequestOfChange));
    }

    protected void assertPersistedRequestOfChangeToMatchUpdatableProperties(RequestOfChange expectedRequestOfChange) {
        assertRequestOfChangeAllUpdatablePropertiesEquals(expectedRequestOfChange, getPersistedRequestOfChange(expectedRequestOfChange));
    }
}
