package com.sdi.domain;

import static com.sdi.domain.ClientTestSamples.*;
import static com.sdi.domain.ProductDeployementTestSamples.*;
import static com.sdi.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductDeployementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductDeployement.class);
        ProductDeployement productDeployement1 = getProductDeployementSample1();
        ProductDeployement productDeployement2 = new ProductDeployement();
        assertThat(productDeployement1).isNotEqualTo(productDeployement2);

        productDeployement2.setId(productDeployement1.getId());
        assertThat(productDeployement1).isEqualTo(productDeployement2);

        productDeployement2 = getProductDeployementSample2();
        assertThat(productDeployement1).isNotEqualTo(productDeployement2);
    }

    @Test
    void productTest() {
        ProductDeployement productDeployement = getProductDeployementRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productDeployement.setProduct(productBack);
        assertThat(productDeployement.getProduct()).isEqualTo(productBack);

        productDeployement.product(null);
        assertThat(productDeployement.getProduct()).isNull();
    }

    @Test
    void clientTest() {
        ProductDeployement productDeployement = getProductDeployementRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        productDeployement.setClient(clientBack);
        assertThat(productDeployement.getClient()).isEqualTo(clientBack);

        productDeployement.client(null);
        assertThat(productDeployement.getClient()).isNull();
    }
}
