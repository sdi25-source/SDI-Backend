package com.sdi.domain;

import static com.sdi.domain.ProductLineTestSamples.*;
import static com.sdi.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductLine.class);
        ProductLine productLine1 = getProductLineSample1();
        ProductLine productLine2 = new ProductLine();
        assertThat(productLine1).isNotEqualTo(productLine2);

        productLine2.setId(productLine1.getId());
        assertThat(productLine1).isEqualTo(productLine2);

        productLine2 = getProductLineSample2();
        assertThat(productLine1).isNotEqualTo(productLine2);
    }

    @Test
    void productTest() {
        ProductLine productLine = getProductLineRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productLine.addProduct(productBack);
        assertThat(productLine.getProducts()).containsOnly(productBack);
        assertThat(productBack.getProductLines()).containsOnly(productLine);

        productLine.removeProduct(productBack);
        assertThat(productLine.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getProductLines()).doesNotContain(productLine);

        productLine.products(new HashSet<>(Set.of(productBack)));
        assertThat(productLine.getProducts()).containsOnly(productBack);
        assertThat(productBack.getProductLines()).containsOnly(productLine);

        productLine.setProducts(new HashSet<>());
        assertThat(productLine.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getProductLines()).doesNotContain(productLine);
    }
}
