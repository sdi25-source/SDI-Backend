package com.sdi.web.rest;

import static com.sdi.domain.ClientEventAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ClientEvent;
import com.sdi.repository.ClientEventRepository;
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
 * Integration tests for the {@link ClientEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClientEventResourceIT {

    private static final String DEFAULT_EVENT = "AAAAAAAAAA";
    private static final String UPDATED_EVENT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_EVENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EVENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClientEventRepository clientEventRepository;

    @Mock
    private ClientEventRepository clientEventRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientEventMockMvc;

    private ClientEvent clientEvent;

    private ClientEvent insertedClientEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientEvent createEntity() {
        return new ClientEvent().event(DEFAULT_EVENT).description(DEFAULT_DESCRIPTION).eventDate(DEFAULT_EVENT_DATE).notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientEvent createUpdatedEntity() {
        return new ClientEvent().event(UPDATED_EVENT).description(UPDATED_DESCRIPTION).eventDate(UPDATED_EVENT_DATE).notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        clientEvent = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedClientEvent != null) {
            clientEventRepository.delete(insertedClientEvent);
            insertedClientEvent = null;
        }
    }

    @Test
    @Transactional
    void createClientEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClientEvent
        var returnedClientEvent = om.readValue(
            restClientEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEvent)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClientEvent.class
        );

        // Validate the ClientEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertClientEventUpdatableFieldsEquals(returnedClientEvent, getPersistedClientEvent(returnedClientEvent));

        insertedClientEvent = returnedClientEvent;
    }

    @Test
    @Transactional
    void createClientEventWithExistingId() throws Exception {
        // Create the ClientEvent with an existing ID
        clientEvent.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEvent)))
            .andExpect(status().isBadRequest());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEventIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clientEvent.setEvent(null);

        // Create the ClientEvent, which fails.

        restClientEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEvent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientEvents() throws Exception {
        // Initialize the database
        insertedClientEvent = clientEventRepository.saveAndFlush(clientEvent);

        // Get all the clientEventList
        restClientEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].event").value(hasItem(DEFAULT_EVENT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(clientEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clientEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClientEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(clientEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClientEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(clientEventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClientEvent() throws Exception {
        // Initialize the database
        insertedClientEvent = clientEventRepository.saveAndFlush(clientEvent);

        // Get the clientEvent
        restClientEventMockMvc
            .perform(get(ENTITY_API_URL_ID, clientEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientEvent.getId().intValue()))
            .andExpect(jsonPath("$.event").value(DEFAULT_EVENT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.eventDate").value(DEFAULT_EVENT_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingClientEvent() throws Exception {
        // Get the clientEvent
        restClientEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientEvent() throws Exception {
        // Initialize the database
        insertedClientEvent = clientEventRepository.saveAndFlush(clientEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientEvent
        ClientEvent updatedClientEvent = clientEventRepository.findById(clientEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientEvent are not directly saved in db
        em.detach(updatedClientEvent);
        updatedClientEvent.event(UPDATED_EVENT).description(UPDATED_DESCRIPTION).eventDate(UPDATED_EVENT_DATE).notes(UPDATED_NOTES);

        restClientEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedClientEvent))
            )
            .andExpect(status().isOk());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClientEventToMatchAllProperties(updatedClientEvent);
    }

    @Test
    @Transactional
    void putNonExistingClientEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEvent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clientEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clientEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientEventWithPatch() throws Exception {
        // Initialize the database
        insertedClientEvent = clientEventRepository.saveAndFlush(clientEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientEvent using partial update
        ClientEvent partialUpdatedClientEvent = new ClientEvent();
        partialUpdatedClientEvent.setId(clientEvent.getId());

        partialUpdatedClientEvent.description(UPDATED_DESCRIPTION).eventDate(UPDATED_EVENT_DATE);

        restClientEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientEvent))
            )
            .andExpect(status().isOk());

        // Validate the ClientEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClientEvent, clientEvent),
            getPersistedClientEvent(clientEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateClientEventWithPatch() throws Exception {
        // Initialize the database
        insertedClientEvent = clientEventRepository.saveAndFlush(clientEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clientEvent using partial update
        ClientEvent partialUpdatedClientEvent = new ClientEvent();
        partialUpdatedClientEvent.setId(clientEvent.getId());

        partialUpdatedClientEvent.event(UPDATED_EVENT).description(UPDATED_DESCRIPTION).eventDate(UPDATED_EVENT_DATE).notes(UPDATED_NOTES);

        restClientEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClientEvent))
            )
            .andExpect(status().isOk());

        // Validate the ClientEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClientEventUpdatableFieldsEquals(partialUpdatedClientEvent, getPersistedClientEvent(partialUpdatedClientEvent));
    }

    @Test
    @Transactional
    void patchNonExistingClientEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEvent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clientEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clientEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clientEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientEvent() throws Exception {
        // Initialize the database
        insertedClientEvent = clientEventRepository.saveAndFlush(clientEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clientEvent
        restClientEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clientEventRepository.count();
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

    protected ClientEvent getPersistedClientEvent(ClientEvent clientEvent) {
        return clientEventRepository.findById(clientEvent.getId()).orElseThrow();
    }

    protected void assertPersistedClientEventToMatchAllProperties(ClientEvent expectedClientEvent) {
        assertClientEventAllPropertiesEquals(expectedClientEvent, getPersistedClientEvent(expectedClientEvent));
    }

    protected void assertPersistedClientEventToMatchUpdatableProperties(ClientEvent expectedClientEvent) {
        assertClientEventAllUpdatablePropertiesEquals(expectedClientEvent, getPersistedClientEvent(expectedClientEvent));
    }
}
