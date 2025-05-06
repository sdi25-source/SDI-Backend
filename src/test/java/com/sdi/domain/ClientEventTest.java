package com.sdi.domain;

import static com.sdi.domain.ClientEventTestSamples.*;
import static com.sdi.domain.ClientEventTypeTestSamples.*;
import static com.sdi.domain.ClientTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientEvent.class);
        ClientEvent clientEvent1 = getClientEventSample1();
        ClientEvent clientEvent2 = new ClientEvent();
        assertThat(clientEvent1).isNotEqualTo(clientEvent2);

        clientEvent2.setId(clientEvent1.getId());
        assertThat(clientEvent1).isEqualTo(clientEvent2);

        clientEvent2 = getClientEventSample2();
        assertThat(clientEvent1).isNotEqualTo(clientEvent2);
    }

    @Test
    void clientTest() {
        ClientEvent clientEvent = getClientEventRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        clientEvent.setClient(clientBack);
        assertThat(clientEvent.getClient()).isEqualTo(clientBack);

        clientEvent.client(null);
        assertThat(clientEvent.getClient()).isNull();
    }

    @Test
    void clientEventTypeTest() {
        ClientEvent clientEvent = getClientEventRandomSampleGenerator();
        ClientEventType clientEventTypeBack = getClientEventTypeRandomSampleGenerator();

        clientEvent.setClientEventType(clientEventTypeBack);
        assertThat(clientEvent.getClientEventType()).isEqualTo(clientEventTypeBack);

        clientEvent.clientEventType(null);
        assertThat(clientEvent.getClientEventType()).isNull();
    }
}
