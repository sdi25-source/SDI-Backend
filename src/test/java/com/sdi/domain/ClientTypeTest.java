package com.sdi.domain;

import static com.sdi.domain.ClientTestSamples.*;
import static com.sdi.domain.ClientTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClientTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientType.class);
        ClientType clientType1 = getClientTypeSample1();
        ClientType clientType2 = new ClientType();
        assertThat(clientType1).isNotEqualTo(clientType2);

        clientType2.setId(clientType1.getId());
        assertThat(clientType1).isEqualTo(clientType2);

        clientType2 = getClientTypeSample2();
        assertThat(clientType1).isNotEqualTo(clientType2);
    }

    @Test
    void clientTest() {
        ClientType clientType = getClientTypeRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        clientType.addClient(clientBack);
        assertThat(clientType.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getClientType()).isEqualTo(clientType);

        clientType.removeClient(clientBack);
        assertThat(clientType.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getClientType()).isNull();

        clientType.clients(new HashSet<>(Set.of(clientBack)));
        assertThat(clientType.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getClientType()).isEqualTo(clientType);

        clientType.setClients(new HashSet<>());
        assertThat(clientType.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getClientType()).isNull();
    }
}
