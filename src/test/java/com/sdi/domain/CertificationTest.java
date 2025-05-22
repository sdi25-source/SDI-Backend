package com.sdi.domain;

import static com.sdi.domain.CertificationTestSamples.*;
import static com.sdi.domain.ClientCertificationTestSamples.*;
import static com.sdi.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CertificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Certification.class);
        Certification certification1 = getCertificationSample1();
        Certification certification2 = new Certification();
        assertThat(certification1).isNotEqualTo(certification2);

        certification2.setId(certification1.getId());
        assertThat(certification1).isEqualTo(certification2);

        certification2 = getCertificationSample2();
        assertThat(certification1).isNotEqualTo(certification2);
    }

    @Test
    void clientCertificationTest() {
        Certification certification = getCertificationRandomSampleGenerator();
        ClientCertification clientCertificationBack = getClientCertificationRandomSampleGenerator();

        certification.addClientCertification(clientCertificationBack);
        assertThat(certification.getClientCertifications()).containsOnly(clientCertificationBack);
        assertThat(clientCertificationBack.getCertif()).isEqualTo(certification);

        certification.removeClientCertification(clientCertificationBack);
        assertThat(certification.getClientCertifications()).doesNotContain(clientCertificationBack);
        assertThat(clientCertificationBack.getCertif()).isNull();

        certification.clientCertifications(new HashSet<>(Set.of(clientCertificationBack)));
        assertThat(certification.getClientCertifications()).containsOnly(clientCertificationBack);
        assertThat(clientCertificationBack.getCertif()).isEqualTo(certification);

        certification.setClientCertifications(new HashSet<>());
        assertThat(certification.getClientCertifications()).doesNotContain(clientCertificationBack);
        assertThat(clientCertificationBack.getCertif()).isNull();
    }

    @Test
    void productTest() {
        Certification certification = getCertificationRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        certification.addProduct(productBack);
        assertThat(certification.getProducts()).containsOnly(productBack);
        assertThat(productBack.getCertifications()).containsOnly(certification);

        certification.removeProduct(productBack);
        assertThat(certification.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getCertifications()).doesNotContain(certification);

        certification.products(new HashSet<>(Set.of(productBack)));
        assertThat(certification.getProducts()).containsOnly(productBack);
        assertThat(productBack.getCertifications()).containsOnly(certification);

        certification.setProducts(new HashSet<>());
        assertThat(certification.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getCertifications()).doesNotContain(certification);
    }
}
