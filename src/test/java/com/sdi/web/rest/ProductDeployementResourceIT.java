package com.sdi.web.rest;

import static com.sdi.domain.ProductDeployementAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ProductDeployement;
import com.sdi.repository.ProductDeployementRepository;
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
 * Integration tests for the {@link ProductDeployementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductDeployementResourceIT {

    private static final String DEFAULT_REF_CONTRACT = "AAAAAAAAAA";
    private static final String UPDATED_REF_CONTRACT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-deployements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductDeployementRepository productDeployementRepository;

    @Mock
    private ProductDeployementRepository productDeployementRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductDeployementMockMvc;

    private ProductDeployement productDeployement;

    private ProductDeployement insertedProductDeployement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductDeployement createEntity() {
        return new ProductDeployement()
            .refContract(DEFAULT_REF_CONTRACT)
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
    public static ProductDeployement createUpdatedEntity() {
        return new ProductDeployement()
            .refContract(UPDATED_REF_CONTRACT)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        productDeployement = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductDeployement != null) {
            productDeployementRepository.delete(insertedProductDeployement);
            insertedProductDeployement = null;
        }
    }

    @Test
    @Transactional
    void createProductDeployement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductDeployement
        var returnedProductDeployement = om.readValue(
            restProductDeployementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDeployement)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductDeployement.class
        );

        // Validate the ProductDeployement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProductDeployementUpdatableFieldsEquals(
            returnedProductDeployement,
            getPersistedProductDeployement(returnedProductDeployement)
        );

        insertedProductDeployement = returnedProductDeployement;
    }

    @Test
    @Transactional
    void createProductDeployementWithExistingId() throws Exception {
        // Create the ProductDeployement with an existing ID
        productDeployement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductDeployementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDeployement)))
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRefContractIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productDeployement.setRefContract(null);

        // Create the ProductDeployement, which fails.

        restProductDeployementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDeployement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProductDeployements() throws Exception {
        // Initialize the database
        insertedProductDeployement = productDeployementRepository.saveAndFlush(productDeployement);

        // Get all the productDeployementList
        restProductDeployementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productDeployement.getId().intValue())))
            .andExpect(jsonPath("$.[*].refContract").value(hasItem(DEFAULT_REF_CONTRACT)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductDeployementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productDeployementRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductDeployementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productDeployementRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductDeployementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productDeployementRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductDeployementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productDeployementRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductDeployement() throws Exception {
        // Initialize the database
        insertedProductDeployement = productDeployementRepository.saveAndFlush(productDeployement);

        // Get the productDeployement
        restProductDeployementMockMvc
            .perform(get(ENTITY_API_URL_ID, productDeployement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productDeployement.getId().intValue()))
            .andExpect(jsonPath("$.refContract").value(DEFAULT_REF_CONTRACT))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingProductDeployement() throws Exception {
        // Get the productDeployement
        restProductDeployementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductDeployement() throws Exception {
        // Initialize the database
        insertedProductDeployement = productDeployementRepository.saveAndFlush(productDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productDeployement
        ProductDeployement updatedProductDeployement = productDeployementRepository.findById(productDeployement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProductDeployement are not directly saved in db
        em.detach(updatedProductDeployement);
        updatedProductDeployement
            .refContract(UPDATED_REF_CONTRACT)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restProductDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductDeployement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProductDeployement))
            )
            .andExpect(status().isOk());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductDeployementToMatchAllProperties(updatedProductDeployement);
    }

    @Test
    @Transactional
    void putNonExistingProductDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDeployement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDeployement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductDeployementWithPatch() throws Exception {
        // Initialize the database
        insertedProductDeployement = productDeployementRepository.saveAndFlush(productDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productDeployement using partial update
        ProductDeployement partialUpdatedProductDeployement = new ProductDeployement();
        partialUpdatedProductDeployement.setId(productDeployement.getId());

        partialUpdatedProductDeployement.createDate(UPDATED_CREATE_DATE);

        restProductDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductDeployement))
            )
            .andExpect(status().isOk());

        // Validate the ProductDeployement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductDeployementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductDeployement, productDeployement),
            getPersistedProductDeployement(productDeployement)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductDeployementWithPatch() throws Exception {
        // Initialize the database
        insertedProductDeployement = productDeployementRepository.saveAndFlush(productDeployement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productDeployement using partial update
        ProductDeployement partialUpdatedProductDeployement = new ProductDeployement();
        partialUpdatedProductDeployement.setId(productDeployement.getId());

        partialUpdatedProductDeployement
            .refContract(UPDATED_REF_CONTRACT)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE)
            .notes(UPDATED_NOTES);

        restProductDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductDeployement))
            )
            .andExpect(status().isOk());

        // Validate the ProductDeployement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductDeployementUpdatableFieldsEquals(
            partialUpdatedProductDeployement,
            getPersistedProductDeployement(partialUpdatedProductDeployement)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProductDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDeployement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDeployement))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductDeployement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productDeployement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductDeployement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductDeployement() throws Exception {
        // Initialize the database
        insertedProductDeployement = productDeployementRepository.saveAndFlush(productDeployement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productDeployement
        restProductDeployementMockMvc
            .perform(delete(ENTITY_API_URL_ID, productDeployement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productDeployementRepository.count();
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

    protected ProductDeployement getPersistedProductDeployement(ProductDeployement productDeployement) {
        return productDeployementRepository.findById(productDeployement.getId()).orElseThrow();
    }

    protected void assertPersistedProductDeployementToMatchAllProperties(ProductDeployement expectedProductDeployement) {
        assertProductDeployementAllPropertiesEquals(expectedProductDeployement, getPersistedProductDeployement(expectedProductDeployement));
    }

    protected void assertPersistedProductDeployementToMatchUpdatableProperties(ProductDeployement expectedProductDeployement) {
        assertProductDeployementAllUpdatablePropertiesEquals(
            expectedProductDeployement,
            getPersistedProductDeployement(expectedProductDeployement)
        );
    }
}
