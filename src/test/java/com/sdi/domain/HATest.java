package com.sdi.domain;

import static com.sdi.domain.HATestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HATest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HA.class);
        HA hA1 = getHASample1();
        HA hA2 = new HA();
        assertThat(hA1).isNotEqualTo(hA2);

        hA2.setId(hA1.getId());
        assertThat(hA1).isEqualTo(hA2);

        hA2 = getHASample2();
        assertThat(hA1).isNotEqualTo(hA2);
    }
}
