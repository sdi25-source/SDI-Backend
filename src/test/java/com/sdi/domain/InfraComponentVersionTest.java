package com.sdi.domain;

import static com.sdi.domain.InfraComponentTestSamples.*;
import static com.sdi.domain.InfraComponentVersionTestSamples.*;
import static com.sdi.domain.ProductDeployementDetailTestSamples.*;
import static com.sdi.domain.ProductVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InfraComponentVersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InfraComponentVersion.class);
        InfraComponentVersion infraComponentVersion1 = getInfraComponentVersionSample1();
        InfraComponentVersion infraComponentVersion2 = new InfraComponentVersion();
        assertThat(infraComponentVersion1).isNotEqualTo(infraComponentVersion2);

        infraComponentVersion2.setId(infraComponentVersion1.getId());
        assertThat(infraComponentVersion1).isEqualTo(infraComponentVersion2);

        infraComponentVersion2 = getInfraComponentVersionSample2();
        assertThat(infraComponentVersion1).isNotEqualTo(infraComponentVersion2);
    }

    @Test
    void infraComponentTest() {
        InfraComponentVersion infraComponentVersion = getInfraComponentVersionRandomSampleGenerator();
        InfraComponent infraComponentBack = getInfraComponentRandomSampleGenerator();

        infraComponentVersion.setInfraComponent(infraComponentBack);
        assertThat(infraComponentVersion.getInfraComponent()).isEqualTo(infraComponentBack);

        infraComponentVersion.infraComponent(null);
        assertThat(infraComponentVersion.getInfraComponent()).isNull();
    }

    @Test
    void productVersionTest() {
        InfraComponentVersion infraComponentVersion = getInfraComponentVersionRandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        infraComponentVersion.addProductVersion(productVersionBack);
        assertThat(infraComponentVersion.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getInfraComponentVersions()).containsOnly(infraComponentVersion);

        infraComponentVersion.removeProductVersion(productVersionBack);
        assertThat(infraComponentVersion.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getInfraComponentVersions()).doesNotContain(infraComponentVersion);

        infraComponentVersion.productVersions(new HashSet<>(Set.of(productVersionBack)));
        assertThat(infraComponentVersion.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getInfraComponentVersions()).containsOnly(infraComponentVersion);

        infraComponentVersion.setProductVersions(new HashSet<>());
        assertThat(infraComponentVersion.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getInfraComponentVersions()).doesNotContain(infraComponentVersion);
    }

    @Test
    void productDeployementDetailTest() {
        InfraComponentVersion infraComponentVersion = getInfraComponentVersionRandomSampleGenerator();
        ProductDeployementDetail productDeployementDetailBack = getProductDeployementDetailRandomSampleGenerator();

        infraComponentVersion.addProductDeployementDetail(productDeployementDetailBack);
        assertThat(infraComponentVersion.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getInfraComponentVersions()).containsOnly(infraComponentVersion);

        infraComponentVersion.removeProductDeployementDetail(productDeployementDetailBack);
        assertThat(infraComponentVersion.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getInfraComponentVersions()).doesNotContain(infraComponentVersion);

        infraComponentVersion.productDeployementDetails(new HashSet<>(Set.of(productDeployementDetailBack)));
        assertThat(infraComponentVersion.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getInfraComponentVersions()).containsOnly(infraComponentVersion);

        infraComponentVersion.setProductDeployementDetails(new HashSet<>());
        assertThat(infraComponentVersion.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getInfraComponentVersions()).doesNotContain(infraComponentVersion);
    }
}
