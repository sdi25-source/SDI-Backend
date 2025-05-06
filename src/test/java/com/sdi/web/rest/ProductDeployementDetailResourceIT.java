package com.sdi.web.rest;

import static com.sdi.domain.ProductDeployementDetailAsserts.*;
import static com.sdi.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdi.IntegrationTest;
import com.sdi.domain.ProductDeployementDetail;
import com.sdi.repository.ProductDeployementDetailRepository;
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
 * Integration tests for the {@link ProductDeployementDetailResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductDeployementDetailResourceIT {

    private static final LocalDate DEFAULT_START_DEPLOYEMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DEPLOYEMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DEPLOYEMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DEPLOYEMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-deployement-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductDeployementDetailRepository productDeployementDetailRepository;

    @Mock
    private ProductDeployementDetailRepository productDeployementDetailRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductDeployementDetailMockMvc;

    private ProductDeployementDetail productDeployementDetail;

    private ProductDeployementDetail insertedProductDeployementDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductDeployementDetail createEntity() {
        return new ProductDeployementDetail()
            .startDeployementDate(DEFAULT_START_DEPLOYEMENT_DATE)
            .endDeployementDate(DEFAULT_END_DEPLOYEMENT_DATE)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductDeployementDetail createUpdatedEntity() {
        return new ProductDeployementDetail()
            .startDeployementDate(UPDATED_START_DEPLOYEMENT_DATE)
            .endDeployementDate(UPDATED_END_DEPLOYEMENT_DATE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        productDeployementDetail = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProductDeployementDetail != null) {
            productDeployementDetailRepository.delete(insertedProductDeployementDetail);
            insertedProductDeployementDetail = null;
        }
    }

    @Test
    @Transactional
    void createProductDeployementDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductDeployementDetail
        var returnedProductDeployementDetail = om.readValue(
            restProductDeployementDetailMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDeployementDetail))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProductDeployementDetail.class
        );

        // Validate the ProductDeployementDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProductDeployementDetailUpdatableFieldsEquals(
            returnedProductDeployementDetail,
            getPersistedProductDeployementDetail(returnedProductDeployementDetail)
        );

        insertedProductDeployementDetail = returnedProductDeployementDetail;
    }

    @Test
    @Transactional
    void createProductDeployementDetailWithExistingId() throws Exception {
        // Create the ProductDeployementDetail with an existing ID
        productDeployementDetail.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductDeployementDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDeployementDetail)))
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProductDeployementDetails() throws Exception {
        // Initialize the database
        insertedProductDeployementDetail = productDeployementDetailRepository.saveAndFlush(productDeployementDetail);

        // Get all the productDeployementDetailList
        restProductDeployementDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productDeployementDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDeployementDate").value(hasItem(DEFAULT_START_DEPLOYEMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDeployementDate").value(hasItem(DEFAULT_END_DEPLOYEMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductDeployementDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(productDeployementDetailRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductDeployementDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(productDeployementDetailRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProductDeployementDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(productDeployementDetailRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProductDeployementDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(productDeployementDetailRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProductDeployementDetail() throws Exception {
        // Initialize the database
        insertedProductDeployementDetail = productDeployementDetailRepository.saveAndFlush(productDeployementDetail);

        // Get the productDeployementDetail
        restProductDeployementDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, productDeployementDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productDeployementDetail.getId().intValue()))
            .andExpect(jsonPath("$.startDeployementDate").value(DEFAULT_START_DEPLOYEMENT_DATE.toString()))
            .andExpect(jsonPath("$.endDeployementDate").value(DEFAULT_END_DEPLOYEMENT_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingProductDeployementDetail() throws Exception {
        // Get the productDeployementDetail
        restProductDeployementDetailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductDeployementDetail() throws Exception {
        // Initialize the database
        insertedProductDeployementDetail = productDeployementDetailRepository.saveAndFlush(productDeployementDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productDeployementDetail
        ProductDeployementDetail updatedProductDeployementDetail = productDeployementDetailRepository
            .findById(productDeployementDetail.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedProductDeployementDetail are not directly saved in db
        em.detach(updatedProductDeployementDetail);
        updatedProductDeployementDetail
            .startDeployementDate(UPDATED_START_DEPLOYEMENT_DATE)
            .endDeployementDate(UPDATED_END_DEPLOYEMENT_DATE)
            .notes(UPDATED_NOTES);

        restProductDeployementDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProductDeployementDetail.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProductDeployementDetail))
            )
            .andExpect(status().isOk());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductDeployementDetailToMatchAllProperties(updatedProductDeployementDetail);
    }

    @Test
    @Transactional
    void putNonExistingProductDeployementDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployementDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductDeployementDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDeployementDetail.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productDeployementDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductDeployementDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployementDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(productDeployementDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductDeployementDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployementDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementDetailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(productDeployementDetail)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductDeployementDetailWithPatch() throws Exception {
        // Initialize the database
        insertedProductDeployementDetail = productDeployementDetailRepository.saveAndFlush(productDeployementDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productDeployementDetail using partial update
        ProductDeployementDetail partialUpdatedProductDeployementDetail = new ProductDeployementDetail();
        partialUpdatedProductDeployementDetail.setId(productDeployementDetail.getId());

        partialUpdatedProductDeployementDetail.endDeployementDate(UPDATED_END_DEPLOYEMENT_DATE).notes(UPDATED_NOTES);

        restProductDeployementDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductDeployementDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductDeployementDetail))
            )
            .andExpect(status().isOk());

        // Validate the ProductDeployementDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductDeployementDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductDeployementDetail, productDeployementDetail),
            getPersistedProductDeployementDetail(productDeployementDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateProductDeployementDetailWithPatch() throws Exception {
        // Initialize the database
        insertedProductDeployementDetail = productDeployementDetailRepository.saveAndFlush(productDeployementDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productDeployementDetail using partial update
        ProductDeployementDetail partialUpdatedProductDeployementDetail = new ProductDeployementDetail();
        partialUpdatedProductDeployementDetail.setId(productDeployementDetail.getId());

        partialUpdatedProductDeployementDetail
            .startDeployementDate(UPDATED_START_DEPLOYEMENT_DATE)
            .endDeployementDate(UPDATED_END_DEPLOYEMENT_DATE)
            .notes(UPDATED_NOTES);

        restProductDeployementDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductDeployementDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProductDeployementDetail))
            )
            .andExpect(status().isOk());

        // Validate the ProductDeployementDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductDeployementDetailUpdatableFieldsEquals(
            partialUpdatedProductDeployementDetail,
            getPersistedProductDeployementDetail(partialUpdatedProductDeployementDetail)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProductDeployementDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployementDetail.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductDeployementDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDeployementDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDeployementDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductDeployementDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployementDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(productDeployementDetail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductDeployementDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productDeployementDetail.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductDeployementDetailMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(productDeployementDetail))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductDeployementDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductDeployementDetail() throws Exception {
        // Initialize the database
        insertedProductDeployementDetail = productDeployementDetailRepository.saveAndFlush(productDeployementDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productDeployementDetail
        restProductDeployementDetailMockMvc
            .perform(delete(ENTITY_API_URL_ID, productDeployementDetail.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productDeployementDetailRepository.count();
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

    protected ProductDeployementDetail getPersistedProductDeployementDetail(ProductDeployementDetail productDeployementDetail) {
        return productDeployementDetailRepository.findById(productDeployementDetail.getId()).orElseThrow();
    }

    protected void assertPersistedProductDeployementDetailToMatchAllProperties(ProductDeployementDetail expectedProductDeployementDetail) {
        assertProductDeployementDetailAllPropertiesEquals(
            expectedProductDeployementDetail,
            getPersistedProductDeployementDetail(expectedProductDeployementDetail)
        );
    }

    protected void assertPersistedProductDeployementDetailToMatchUpdatableProperties(
        ProductDeployementDetail expectedProductDeployementDetail
    ) {
        assertProductDeployementDetailAllUpdatablePropertiesEquals(
            expectedProductDeployementDetail,
            getPersistedProductDeployementDetail(expectedProductDeployementDetail)
        );
    }
}
