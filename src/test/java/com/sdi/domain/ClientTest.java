package com.sdi.domain;

import static com.sdi.domain.ClientCertificationTestSamples.*;
import static com.sdi.domain.ClientSizeTestSamples.*;
import static com.sdi.domain.ClientTestSamples.*;
import static com.sdi.domain.ClientTypeTestSamples.*;
import static com.sdi.domain.CountryTestSamples.*;
import static com.sdi.domain.ProductDeployementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Client.class);
        Client client1 = getClientSample1();
        Client client2 = new Client();
        assertThat(client1).isNotEqualTo(client2);

        client2.setId(client1.getId());
        assertThat(client1).isEqualTo(client2);

        client2 = getClientSample2();
        assertThat(client1).isNotEqualTo(client2);
    }

    @Test
    void productDeployementTest() {
        Client client = getClientRandomSampleGenerator();
        ProductDeployement productDeployementBack = getProductDeployementRandomSampleGenerator();

        client.addProductDeployement(productDeployementBack);
        assertThat(client.getProductDeployements()).containsOnly(productDeployementBack);
        assertThat(productDeployementBack.getClient()).isEqualTo(client);

        client.removeProductDeployement(productDeployementBack);
        assertThat(client.getProductDeployements()).doesNotContain(productDeployementBack);
        assertThat(productDeployementBack.getClient()).isNull();

        client.productDeployements(new HashSet<>(Set.of(productDeployementBack)));
        assertThat(client.getProductDeployements()).containsOnly(productDeployementBack);
        assertThat(productDeployementBack.getClient()).isEqualTo(client);

        client.setProductDeployements(new HashSet<>());
        assertThat(client.getProductDeployements()).doesNotContain(productDeployementBack);
        assertThat(productDeployementBack.getClient()).isNull();
    }

    @Test
    void sizeTest() {
        Client client = getClientRandomSampleGenerator();
        ClientSize clientSizeBack = getClientSizeRandomSampleGenerator();

        client.setSize(clientSizeBack);
        assertThat(client.getSize()).isEqualTo(clientSizeBack);

        client.size(null);
        assertThat(client.getSize()).isNull();
    }

    @Test
    void clientTypeTest() {
        Client client = getClientRandomSampleGenerator();
        ClientType clientTypeBack = getClientTypeRandomSampleGenerator();

        client.setClientType(clientTypeBack);
        assertThat(client.getClientType()).isEqualTo(clientTypeBack);

        client.clientType(null);
        assertThat(client.getClientType()).isNull();
    }

    @Test
    void countryTest() {
        Client client = getClientRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        client.setCountry(countryBack);
        assertThat(client.getCountry()).isEqualTo(countryBack);

        client.country(null);
        assertThat(client.getCountry()).isNull();
    }

    @Test
    void certifTest() {
        Client client = getClientRandomSampleGenerator();
        ClientCertification clientCertificationBack = getClientCertificationRandomSampleGenerator();

        client.addCertif(clientCertificationBack);
        assertThat(client.getCertifs()).containsOnly(clientCertificationBack);
        assertThat(clientCertificationBack.getClient()).isEqualTo(client);

        client.removeCertif(clientCertificationBack);
        assertThat(client.getCertifs()).doesNotContain(clientCertificationBack);
        assertThat(clientCertificationBack.getClient()).isNull();

        client.certifs(new HashSet<>(Set.of(clientCertificationBack)));
        assertThat(client.getCertifs()).containsOnly(clientCertificationBack);
        assertThat(clientCertificationBack.getClient()).isEqualTo(client);

        client.setCertifs(new HashSet<>());
        assertThat(client.getCertifs()).doesNotContain(clientCertificationBack);
        assertThat(clientCertificationBack.getClient()).isNull();
    }
}
