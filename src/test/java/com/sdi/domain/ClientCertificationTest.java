package com.sdi.domain;

import static com.sdi.domain.CertificationTestSamples.*;
import static com.sdi.domain.ClientCertificationTestSamples.*;
import static com.sdi.domain.ClientTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientCertificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientCertification.class);
        ClientCertification clientCertification1 = getClientCertificationSample1();
        ClientCertification clientCertification2 = new ClientCertification();
        assertThat(clientCertification1).isNotEqualTo(clientCertification2);

        clientCertification2.setId(clientCertification1.getId());
        assertThat(clientCertification1).isEqualTo(clientCertification2);

        clientCertification2 = getClientCertificationSample2();
        assertThat(clientCertification1).isNotEqualTo(clientCertification2);
    }

    @Test
    void clientTest() {
        ClientCertification clientCertification = getClientCertificationRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        clientCertification.setClient(clientBack);
        assertThat(clientCertification.getClient()).isEqualTo(clientBack);

        clientCertification.client(null);
        assertThat(clientCertification.getClient()).isNull();
    }

    @Test
    void certifTest() {
        ClientCertification clientCertification = getClientCertificationRandomSampleGenerator();
        Certification certificationBack = getCertificationRandomSampleGenerator();

        clientCertification.setCertif(certificationBack);
        assertThat(clientCertification.getCertif()).isEqualTo(certificationBack);

        clientCertification.certif(null);
        assertThat(clientCertification.getCertif()).isNull();
    }
}
