package com.sdi.domain;

import static com.sdi.domain.DomaineTestSamples.*;
import static com.sdi.domain.FeatureTestSamples.*;
import static com.sdi.domain.ModuleDeployementTestSamples.*;
import static com.sdi.domain.ModuleTestSamples.*;
import static com.sdi.domain.ModuleVersionTestSamples.*;
import static com.sdi.domain.ModuleVersionTestSamples.*;
import static com.sdi.domain.ProductDeployementDetailTestSamples.*;
import static com.sdi.domain.ProductVersionTestSamples.*;
import static com.sdi.domain.RequestOfChangeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModuleVersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModuleVersion.class);
        ModuleVersion moduleVersion1 = getModuleVersionSample1();
        ModuleVersion moduleVersion2 = new ModuleVersion();
        assertThat(moduleVersion1).isNotEqualTo(moduleVersion2);

        moduleVersion2.setId(moduleVersion1.getId());
        assertThat(moduleVersion1).isEqualTo(moduleVersion2);

        moduleVersion2 = getModuleVersionSample2();
        assertThat(moduleVersion1).isNotEqualTo(moduleVersion2);
    }

    @Test
    void moduleDeployementTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        ModuleDeployement moduleDeployementBack = getModuleDeployementRandomSampleGenerator();

        moduleVersion.addModuleDeployement(moduleDeployementBack);
        assertThat(moduleVersion.getModuleDeployements()).containsOnly(moduleDeployementBack);
        assertThat(moduleDeployementBack.getModuleVersion()).isEqualTo(moduleVersion);

        moduleVersion.removeModuleDeployement(moduleDeployementBack);
        assertThat(moduleVersion.getModuleDeployements()).doesNotContain(moduleDeployementBack);
        assertThat(moduleDeployementBack.getModuleVersion()).isNull();

        moduleVersion.moduleDeployements(new HashSet<>(Set.of(moduleDeployementBack)));
        assertThat(moduleVersion.getModuleDeployements()).containsOnly(moduleDeployementBack);
        assertThat(moduleDeployementBack.getModuleVersion()).isEqualTo(moduleVersion);

        moduleVersion.setModuleDeployements(new HashSet<>());
        assertThat(moduleVersion.getModuleDeployements()).doesNotContain(moduleDeployementBack);
        assertThat(moduleDeployementBack.getModuleVersion()).isNull();
    }

    @Test
    void moduleVersionTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        ModuleVersion moduleVersionBack = getModuleVersionRandomSampleGenerator();

        moduleVersion.addModuleVersion(moduleVersionBack);
        assertThat(moduleVersion.getModuleVersions()).containsOnly(moduleVersionBack);
        assertThat(moduleVersionBack.getRoot()).isEqualTo(moduleVersion);

        moduleVersion.removeModuleVersion(moduleVersionBack);
        assertThat(moduleVersion.getModuleVersions()).doesNotContain(moduleVersionBack);
        assertThat(moduleVersionBack.getRoot()).isNull();

        moduleVersion.moduleVersions(new HashSet<>(Set.of(moduleVersionBack)));
        assertThat(moduleVersion.getModuleVersions()).containsOnly(moduleVersionBack);
        assertThat(moduleVersionBack.getRoot()).isEqualTo(moduleVersion);

        moduleVersion.setModuleVersions(new HashSet<>());
        assertThat(moduleVersion.getModuleVersions()).doesNotContain(moduleVersionBack);
        assertThat(moduleVersionBack.getRoot()).isNull();
    }

    @Test
    void moduleTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        Module moduleBack = getModuleRandomSampleGenerator();

        moduleVersion.setModule(moduleBack);
        assertThat(moduleVersion.getModule()).isEqualTo(moduleBack);

        moduleVersion.module(null);
        assertThat(moduleVersion.getModule()).isNull();
    }

    @Test
    void featureTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        Feature featureBack = getFeatureRandomSampleGenerator();

        moduleVersion.addFeature(featureBack);
        assertThat(moduleVersion.getFeatures()).containsOnly(featureBack);

        moduleVersion.removeFeature(featureBack);
        assertThat(moduleVersion.getFeatures()).doesNotContain(featureBack);

        moduleVersion.features(new HashSet<>(Set.of(featureBack)));
        assertThat(moduleVersion.getFeatures()).containsOnly(featureBack);

        moduleVersion.setFeatures(new HashSet<>());
        assertThat(moduleVersion.getFeatures()).doesNotContain(featureBack);
    }

    @Test
    void domaineTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        Domaine domaineBack = getDomaineRandomSampleGenerator();

        moduleVersion.setDomaine(domaineBack);
        assertThat(moduleVersion.getDomaine()).isEqualTo(domaineBack);

        moduleVersion.domaine(null);
        assertThat(moduleVersion.getDomaine()).isNull();
    }

    @Test
    void rootTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        ModuleVersion moduleVersionBack = getModuleVersionRandomSampleGenerator();

        moduleVersion.setRoot(moduleVersionBack);
        assertThat(moduleVersion.getRoot()).isEqualTo(moduleVersionBack);

        moduleVersion.root(null);
        assertThat(moduleVersion.getRoot()).isNull();
    }

    @Test
    void productVersionTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        moduleVersion.addProductVersion(productVersionBack);
        assertThat(moduleVersion.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getModuleVersions()).containsOnly(moduleVersion);

        moduleVersion.removeProductVersion(productVersionBack);
        assertThat(moduleVersion.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getModuleVersions()).doesNotContain(moduleVersion);

        moduleVersion.productVersions(new HashSet<>(Set.of(productVersionBack)));
        assertThat(moduleVersion.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getModuleVersions()).containsOnly(moduleVersion);

        moduleVersion.setProductVersions(new HashSet<>());
        assertThat(moduleVersion.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getModuleVersions()).doesNotContain(moduleVersion);
    }

    @Test
    void productDeployementDetailTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        ProductDeployementDetail productDeployementDetailBack = getProductDeployementDetailRandomSampleGenerator();

        moduleVersion.addProductDeployementDetail(productDeployementDetailBack);
        assertThat(moduleVersion.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getAllowedModuleVersions()).containsOnly(moduleVersion);

        moduleVersion.removeProductDeployementDetail(productDeployementDetailBack);
        assertThat(moduleVersion.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getAllowedModuleVersions()).doesNotContain(moduleVersion);

        moduleVersion.productDeployementDetails(new HashSet<>(Set.of(productDeployementDetailBack)));
        assertThat(moduleVersion.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getAllowedModuleVersions()).containsOnly(moduleVersion);

        moduleVersion.setProductDeployementDetails(new HashSet<>());
        assertThat(moduleVersion.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getAllowedModuleVersions()).doesNotContain(moduleVersion);
    }

    @Test
    void requestOfChangeTest() {
        ModuleVersion moduleVersion = getModuleVersionRandomSampleGenerator();
        RequestOfChange requestOfChangeBack = getRequestOfChangeRandomSampleGenerator();

        moduleVersion.addRequestOfChange(requestOfChangeBack);
        assertThat(moduleVersion.getRequestOfChanges()).containsOnly(requestOfChangeBack);
        assertThat(requestOfChangeBack.getModuleVersions()).containsOnly(moduleVersion);

        moduleVersion.removeRequestOfChange(requestOfChangeBack);
        assertThat(moduleVersion.getRequestOfChanges()).doesNotContain(requestOfChangeBack);
        assertThat(requestOfChangeBack.getModuleVersions()).doesNotContain(moduleVersion);

        moduleVersion.requestOfChanges(new HashSet<>(Set.of(requestOfChangeBack)));
        assertThat(moduleVersion.getRequestOfChanges()).containsOnly(requestOfChangeBack);
        assertThat(requestOfChangeBack.getModuleVersions()).containsOnly(moduleVersion);

        moduleVersion.setRequestOfChanges(new HashSet<>());
        assertThat(moduleVersion.getRequestOfChanges()).doesNotContain(requestOfChangeBack);
        assertThat(requestOfChangeBack.getModuleVersions()).doesNotContain(moduleVersion);
    }
}
