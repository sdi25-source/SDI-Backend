package com.sdi.domain;

import static com.sdi.domain.ClientSizeTestSamples.*;
import static com.sdi.domain.ClientTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClientSizeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientSize.class);
        ClientSize clientSize1 = getClientSizeSample1();
        ClientSize clientSize2 = new ClientSize();
        assertThat(clientSize1).isNotEqualTo(clientSize2);

        clientSize2.setId(clientSize1.getId());
        assertThat(clientSize1).isEqualTo(clientSize2);

        clientSize2 = getClientSizeSample2();
        assertThat(clientSize1).isNotEqualTo(clientSize2);
    }

    @Test
    void clientTest() {
        ClientSize clientSize = getClientSizeRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        clientSize.addClient(clientBack);
        assertThat(clientSize.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getSize()).isEqualTo(clientSize);

        clientSize.removeClient(clientBack);
        assertThat(clientSize.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getSize()).isNull();

        clientSize.clients(new HashSet<>(Set.of(clientBack)));
        assertThat(clientSize.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getSize()).isEqualTo(clientSize);

        clientSize.setClients(new HashSet<>());
        assertThat(clientSize.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getSize()).isNull();
    }
}
