package com.sdi.domain;

import static com.sdi.domain.HATestSamples.*;
import static com.sdi.domain.ProductVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    void productVersionTest() {
        HA hA = getHARandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        hA.addProductVersion(productVersionBack);
        assertThat(hA.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getHa()).isEqualTo(hA);

        hA.removeProductVersion(productVersionBack);
        assertThat(hA.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getHa()).isNull();

        hA.productVersions(new HashSet<>(Set.of(productVersionBack)));
        assertThat(hA.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getHa()).isEqualTo(hA);

        hA.setProductVersions(new HashSet<>());
        assertThat(hA.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getHa()).isNull();
    }
}
