package com.sdi.web.rest;

import static com.sdi.domain.ProductVersionAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ProductVersion;
import com.sdi.repository.ProductVersionRepository;
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
 * Integration tests for the {@link ProductVersionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductVersionResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductVersionRepository productVersionRepository;

    @Mock
    private ProductVersionRepository productVersionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductVersionMockMvc;

    private ProductVersion productVersion;

    private ProductVersion insertedProductVersion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductVersion createEntity() {
        return new ProductVersion()
            .version(DEFAULT_VERSION)
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
    public static ProductVersion createUpdatedEntity() {
        return new ProductVersion()
            .version(UPDATED_VERSION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        productVersion = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductVersion != null) {
            productVersionRepository.delete(insertedProductVersion);
            insertedProductVersion = null;
        }
    }

    @Test
    @Transactional
    void createProductVersion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductVersion
        var returnedProductVersion = om.readValue(
            restProductVersionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVersion)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductVersion.class
        );

        // Validate the ProductVersion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProductVersionUpdatableFieldsEquals(returnedProductVersion, getPersistedProductVersion(returnedProductVersion));

        insertedProductVersion = returnedProductVersion;
    }

    @Test
    @Transactional
    void createProductVersionWithExistingId() throws Exception {
        // Create the ProductVersion with an existing ID
        productVersion.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVersion)))
            .andExpect(status().isBadRequest());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productVersion.setVersion(null);

        // Create the ProductVersion, which fails.

        restProductVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVersion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductVersions() throws Exception {
        // Initialize the database
        insertedProductVersion = productVersionRepository.saveAndFlush(productVersion);

        // Get all the productVersionList
        restProductVersionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productVersion.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVersionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productVersionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductVersionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productVersionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductVersionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productVersionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductVersion() throws Exception {
        // Initialize the database
        insertedProductVersion = productVersionRepository.saveAndFlush(productVersion);

        // Get the productVersion
        restProductVersionMockMvc
            .perform(get(ENTITY_API_URL_ID, productVersion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productVersion.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingProductVersion() throws Exception {
        // Get the productVersion
        restProductVersionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductVersion() throws Exception {
        // Initialize the database
        insertedProductVersion = productVersionRepository.saveAndFlush(productVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVersion
        ProductVersion updatedProductVersion = productVersionRepository.findById(productVersion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductVersion are not directly saved in db
        em.detach(updatedProductVersion);
        updatedProductVersion.version(UPDATED_VERSION).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restProductVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProductVersion))
            )
            .andExpect(status().isOk());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductVersionToMatchAllProperties(updatedProductVersion);
    }

    @Test
    @Transactional
    void putNonExistingProductVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVersionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductVersionWithPatch() throws Exception {
        // Initialize the database
        insertedProductVersion = productVersionRepository.saveAndFlush(productVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVersion using partial update
        ProductVersion partialUpdatedProductVersion = new ProductVersion();
        partialUpdatedProductVersion.setId(productVersion.getId());

        partialUpdatedProductVersion.version(UPDATED_VERSION).updateDate(UPDATED_UPDATE_DATE);

        restProductVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductVersion))
            )
            .andExpect(status().isOk());

        // Validate the ProductVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductVersionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductVersion, productVersion),
            getPersistedProductVersion(productVersion)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductVersionWithPatch() throws Exception {
        // Initialize the database
        insertedProductVersion = productVersionRepository.saveAndFlush(productVersion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productVersion using partial update
        ProductVersion partialUpdatedProductVersion = new ProductVersion();
        partialUpdatedProductVersion.setId(productVersion.getId());

        partialUpdatedProductVersion
            .version(UPDATED_VERSION)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restProductVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductVersion))
            )
            .andExpect(status().isOk());

        // Validate the ProductVersion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductVersionUpdatableFieldsEquals(partialUpdatedProductVersion, getPersistedProductVersion(partialUpdatedProductVersion));
    }

    @Test
    @Transactional
    void patchNonExistingProductVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVersion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productVersion))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductVersion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productVersion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductVersionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productVersion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductVersion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductVersion() throws Exception {
        // Initialize the database
        insertedProductVersion = productVersionRepository.saveAndFlush(productVersion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productVersion
        restProductVersionMockMvc
            .perform(delete(ENTITY_API_URL_ID, productVersion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productVersionRepository.count();
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

    protected ProductVersion getPersistedProductVersion(ProductVersion productVersion) {
        return productVersionRepository.findById(productVersion.getId()).orElseThrow();
    }

    protected void assertPersistedProductVersionToMatchAllProperties(ProductVersion expectedProductVersion) {
        assertProductVersionAllPropertiesEquals(expectedProductVersion, getPersistedProductVersion(expectedProductVersion));
    }

    protected void assertPersistedProductVersionToMatchUpdatableProperties(ProductVersion expectedProductVersion) {
        assertProductVersionAllUpdatablePropertiesEquals(expectedProductVersion, getPersistedProductVersion(expectedProductVersion));
    }
}
