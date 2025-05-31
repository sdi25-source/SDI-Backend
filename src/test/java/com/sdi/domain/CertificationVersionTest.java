package com.sdi.domain;

import static com.sdi.domain.CertificationTestSamples.*;
import static com.sdi.domain.CertificationVersionTestSamples.*;
import static com.sdi.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CertificationVersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificationVersion.class);
        CertificationVersion certificationVersion1 = getCertificationVersionSample1();
        CertificationVersion certificationVersion2 = new CertificationVersion();
        assertThat(certificationVersion1).isNotEqualTo(certificationVersion2);

        certificationVersion2.setId(certificationVersion1.getId());
        assertThat(certificationVersion1).isEqualTo(certificationVersion2);

        certificationVersion2 = getCertificationVersionSample2();
        assertThat(certificationVersion1).isNotEqualTo(certificationVersion2);
    }

    @Test
    void certificationTest() {
        CertificationVersion certificationVersion = getCertificationVersionRandomSampleGenerator();
        Certification certificationBack = getCertificationRandomSampleGenerator();

        certificationVersion.setCertification(certificationBack);
        assertThat(certificationVersion.getCertification()).isEqualTo(certificationBack);

        certificationVersion.certification(null);
        assertThat(certificationVersion.getCertification()).isNull();
    }

    @Test
    void productTest() {
        CertificationVersion certificationVersion = getCertificationVersionRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        certificationVersion.addProduct(productBack);
        assertThat(certificationVersion.getProducts()).containsOnly(productBack);
        assertThat(productBack.getCertifications()).containsOnly(certificationVersion);

        certificationVersion.removeProduct(productBack);
        assertThat(certificationVersion.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getCertifications()).doesNotContain(certificationVersion);

        certificationVersion.products(new HashSet<>(Set.of(productBack)));
        assertThat(certificationVersion.getProducts()).containsOnly(productBack);
        assertThat(productBack.getCertifications()).containsOnly(certificationVersion);

        certificationVersion.setProducts(new HashSet<>());
        assertThat(certificationVersion.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getCertifications()).doesNotContain(certificationVersion);
    }
}
