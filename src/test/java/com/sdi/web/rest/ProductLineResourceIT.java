package com.sdi.web.rest;

import static com.sdi.domain.ProductLineAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ProductLine;
import com.sdi.repository.ProductLineRepository;
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
 * Integration tests for the {@link ProductLineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductLineResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductLineRepository productLineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductLineMockMvc;

    private ProductLine productLine;

    private ProductLine insertedProductLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductLine createEntity() {
        return new ProductLine().name(DEFAULT_NAME).createDate(DEFAULT_CREATE_DATE).updateDate(DEFAULT_UPDATE_DATE).notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductLine createUpdatedEntity() {
        return new ProductLine().name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        productLine = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductLine != null) {
            productLineRepository.delete(insertedProductLine);
            insertedProductLine = null;
        }
    }

    @Test
    @Transactional
    void createProductLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductLine
        var returnedProductLine = om.readValue(
            restProductLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productLine)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductLine.class
        );

        // Validate the ProductLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProductLineUpdatableFieldsEquals(returnedProductLine, getPersistedProductLine(returnedProductLine));

        insertedProductLine = returnedProductLine;
    }

    @Test
    @Transactional
    void createProductLineWithExistingId() throws Exception {
        // Create the ProductLine with an existing ID
        productLine.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productLine)))
            .andExpect(status().isBadRequest());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productLine.setName(null);

        // Create the ProductLine, which fails.

        restProductLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductLines() throws Exception {
        // Initialize the database
        insertedProductLine = productLineRepository.saveAndFlush(productLine);

        // Get all the productLineList
        restProductLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @Test
    @Transactional
    void getProductLine() throws Exception {
        // Initialize the database
        insertedProductLine = productLineRepository.saveAndFlush(productLine);

        // Get the productLine
        restProductLineMockMvc
            .perform(get(ENTITY_API_URL_ID, productLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productLine.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingProductLine() throws Exception {
        // Get the productLine
        restProductLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductLine() throws Exception {
        // Initialize the database
        insertedProductLine = productLineRepository.saveAndFlush(productLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productLine
        ProductLine updatedProductLine = productLineRepository.findById(productLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductLine are not directly saved in db
        em.detach(updatedProductLine);
        updatedProductLine.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restProductLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductLine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProductLine))
            )
            .andExpect(status().isOk());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductLineToMatchAllProperties(updatedProductLine);
    }

    @Test
    @Transactional
    void putNonExistingProductLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productLine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productLine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productLine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductLineWithPatch() throws Exception {
        // Initialize the database
        insertedProductLine = productLineRepository.saveAndFlush(productLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productLine using partial update
        ProductLine partialUpdatedProductLine = new ProductLine();
        partialUpdatedProductLine.setId(productLine.getId());

        partialUpdatedProductLine.updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restProductLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductLine))
            )
            .andExpect(status().isOk());

        // Validate the ProductLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductLine, productLine),
            getPersistedProductLine(productLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductLineWithPatch() throws Exception {
        // Initialize the database
        insertedProductLine = productLineRepository.saveAndFlush(productLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productLine using partial update
        ProductLine partialUpdatedProductLine = new ProductLine();
        partialUpdatedProductLine.setId(productLine.getId());

        partialUpdatedProductLine.name(UPDATED_NAME).createDate(UPDATED_CREATE_DATE).updateDate(UPDATED_UPDATE_DATE).notes(UPDATED_NOTES);

        restProductLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductLine))
            )
            .andExpect(status().isOk());

        // Validate the ProductLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductLineUpdatableFieldsEquals(partialUpdatedProductLine, getPersistedProductLine(partialUpdatedProductLine));
    }

    @Test
    @Transactional
    void patchNonExistingProductLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productLine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productLine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductLine() throws Exception {
        // Initialize the database
        insertedProductLine = productLineRepository.saveAndFlush(productLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productLine
        restProductLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, productLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productLineRepository.count();
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

    protected ProductLine getPersistedProductLine(ProductLine productLine) {
        return productLineRepository.findById(productLine.getId()).orElseThrow();
    }

    protected void assertPersistedProductLineToMatchAllProperties(ProductLine expectedProductLine) {
        assertProductLineAllPropertiesEquals(expectedProductLine, getPersistedProductLine(expectedProductLine));
    }

    protected void assertPersistedProductLineToMatchUpdatableProperties(ProductLine expectedProductLine) {
        assertProductLineAllUpdatablePropertiesEquals(expectedProductLine, getPersistedProductLine(expectedProductLine));
    }
}
