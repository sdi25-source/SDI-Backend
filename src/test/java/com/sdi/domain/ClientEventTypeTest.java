package com.sdi.domain;

import static com.sdi.domain.ClientEventTestSamples.*;
import static com.sdi.domain.ClientEventTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClientEventTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientEventType.class);
        ClientEventType clientEventType1 = getClientEventTypeSample1();
        ClientEventType clientEventType2 = new ClientEventType();
        assertThat(clientEventType1).isNotEqualTo(clientEventType2);

        clientEventType2.setId(clientEventType1.getId());
        assertThat(clientEventType1).isEqualTo(clientEventType2);

        clientEventType2 = getClientEventTypeSample2();
        assertThat(clientEventType1).isNotEqualTo(clientEventType2);
    }

    @Test
    void clientEventTest() {
        ClientEventType clientEventType = getClientEventTypeRandomSampleGenerator();
        ClientEvent clientEventBack = getClientEventRandomSampleGenerator();

        clientEventType.addClientEvent(clientEventBack);
        assertThat(clientEventType.getClientEvents()).containsOnly(clientEventBack);
        assertThat(clientEventBack.getClientEventType()).isEqualTo(clientEventType);

        clientEventType.removeClientEvent(clientEventBack);
        assertThat(clientEventType.getClientEvents()).doesNotContain(clientEventBack);
        assertThat(clientEventBack.getClientEventType()).isNull();

        clientEventType.clientEvents(new HashSet<>(Set.of(clientEventBack)));
        assertThat(clientEventType.getClientEvents()).containsOnly(clientEventBack);
        assertThat(clientEventBack.getClientEventType()).isEqualTo(clientEventType);

        clientEventType.setClientEvents(new HashSet<>());
        assertThat(clientEventType.getClientEvents()).doesNotContain(clientEventBack);
        assertThat(clientEventBack.getClientEventType()).isNull();
    }
}
