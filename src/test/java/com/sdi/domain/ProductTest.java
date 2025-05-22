package com.sdi.domain;

import static com.sdi.domain.CertificationTestSamples.*;
import static com.sdi.domain.ModuleTestSamples.*;
import static com.sdi.domain.ProductLineTestSamples.*;
import static com.sdi.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void productLineTest() {
        Product product = getProductRandomSampleGenerator();
        ProductLine productLineBack = getProductLineRandomSampleGenerator();

        product.addProductLine(productLineBack);
        assertThat(product.getProductLines()).containsOnly(productLineBack);

        product.removeProductLine(productLineBack);
        assertThat(product.getProductLines()).doesNotContain(productLineBack);

        product.productLines(new HashSet<>(Set.of(productLineBack)));
        assertThat(product.getProductLines()).containsOnly(productLineBack);

        product.setProductLines(new HashSet<>());
        assertThat(product.getProductLines()).doesNotContain(productLineBack);
    }

    @Test
    void certificationTest() {
        Product product = getProductRandomSampleGenerator();
        Certification certificationBack = getCertificationRandomSampleGenerator();

        product.addCertification(certificationBack);
        assertThat(product.getCertifications()).containsOnly(certificationBack);

        product.removeCertification(certificationBack);
        assertThat(product.getCertifications()).doesNotContain(certificationBack);

        product.certifications(new HashSet<>(Set.of(certificationBack)));
        assertThat(product.getCertifications()).containsOnly(certificationBack);

        product.setCertifications(new HashSet<>());
        assertThat(product.getCertifications()).doesNotContain(certificationBack);
    }

    @Test
    void moduleTest() {
        Product product = getProductRandomSampleGenerator();
        Module moduleBack = getModuleRandomSampleGenerator();

        product.addModule(moduleBack);
        assertThat(product.getModules()).containsOnly(moduleBack);

        product.removeModule(moduleBack);
        assertThat(product.getModules()).doesNotContain(moduleBack);

        product.modules(new HashSet<>(Set.of(moduleBack)));
        assertThat(product.getModules()).containsOnly(moduleBack);

        product.setModules(new HashSet<>());
        assertThat(product.getModules()).doesNotContain(moduleBack);
    }
}
