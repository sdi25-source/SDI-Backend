package com.sdi.domain;

import static com.sdi.domain.ClientTestSamples.*;
import static com.sdi.domain.CustomisationLevelTestSamples.*;
import static com.sdi.domain.ModuleVersionTestSamples.*;
import static com.sdi.domain.ProductVersionTestSamples.*;
import static com.sdi.domain.RequestOfChangeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RequestOfChangeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RequestOfChange.class);
        RequestOfChange requestOfChange1 = getRequestOfChangeSample1();
        RequestOfChange requestOfChange2 = new RequestOfChange();
        assertThat(requestOfChange1).isNotEqualTo(requestOfChange2);

        requestOfChange2.setId(requestOfChange1.getId());
        assertThat(requestOfChange1).isEqualTo(requestOfChange2);

        requestOfChange2 = getRequestOfChangeSample2();
        assertThat(requestOfChange1).isNotEqualTo(requestOfChange2);
    }

    @Test
    void productVersionTest() {
        RequestOfChange requestOfChange = getRequestOfChangeRandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        requestOfChange.setProductVersion(productVersionBack);
        assertThat(requestOfChange.getProductVersion()).isEqualTo(productVersionBack);

        requestOfChange.productVersion(null);
        assertThat(requestOfChange.getProductVersion()).isNull();
    }

    @Test
    void clientTest() {
        RequestOfChange requestOfChange = getRequestOfChangeRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        requestOfChange.setClient(clientBack);
        assertThat(requestOfChange.getClient()).isEqualTo(clientBack);

        requestOfChange.client(null);
        assertThat(requestOfChange.getClient()).isNull();
    }

    @Test
    void moduleVersionTest() {
        RequestOfChange requestOfChange = getRequestOfChangeRandomSampleGenerator();
        ModuleVersion moduleVersionBack = getModuleVersionRandomSampleGenerator();

        requestOfChange.addModuleVersion(moduleVersionBack);
        assertThat(requestOfChange.getModuleVersions()).containsOnly(moduleVersionBack);

        requestOfChange.removeModuleVersion(moduleVersionBack);
        assertThat(requestOfChange.getModuleVersions()).doesNotContain(moduleVersionBack);

        requestOfChange.moduleVersions(new HashSet<>(Set.of(moduleVersionBack)));
        assertThat(requestOfChange.getModuleVersions()).containsOnly(moduleVersionBack);

        requestOfChange.setModuleVersions(new HashSet<>());
        assertThat(requestOfChange.getModuleVersions()).doesNotContain(moduleVersionBack);
    }

    @Test
    void customisationLevelTest() {
        RequestOfChange requestOfChange = getRequestOfChangeRandomSampleGenerator();
        CustomisationLevel customisationLevelBack = getCustomisationLevelRandomSampleGenerator();

        requestOfChange.setCustomisationLevel(customisationLevelBack);
        assertThat(requestOfChange.getCustomisationLevel()).isEqualTo(customisationLevelBack);

        requestOfChange.customisationLevel(null);
        assertThat(requestOfChange.getCustomisationLevel()).isNull();
    }
}
