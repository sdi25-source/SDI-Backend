package com.sdi.domain;

import static com.sdi.domain.DeployementTypeTestSamples.*;
import static com.sdi.domain.InfraComponentVersionTestSamples.*;
import static com.sdi.domain.ModuleDeployementTestSamples.*;
import static com.sdi.domain.ModuleVersionTestSamples.*;
import static com.sdi.domain.ProductDeployementDetailTestSamples.*;
import static com.sdi.domain.ProductDeployementTestSamples.*;
import static com.sdi.domain.ProductVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductDeployementDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductDeployementDetail.class);
        ProductDeployementDetail productDeployementDetail1 = getProductDeployementDetailSample1();
        ProductDeployementDetail productDeployementDetail2 = new ProductDeployementDetail();
        assertThat(productDeployementDetail1).isNotEqualTo(productDeployementDetail2);

        productDeployementDetail2.setId(productDeployementDetail1.getId());
        assertThat(productDeployementDetail1).isEqualTo(productDeployementDetail2);

        productDeployementDetail2 = getProductDeployementDetailSample2();
        assertThat(productDeployementDetail1).isNotEqualTo(productDeployementDetail2);
    }

    @Test
    void moduleDeployementTest() {
        ProductDeployementDetail productDeployementDetail = getProductDeployementDetailRandomSampleGenerator();
        ModuleDeployement moduleDeployementBack = getModuleDeployementRandomSampleGenerator();

        productDeployementDetail.addModuleDeployement(moduleDeployementBack);
        assertThat(productDeployementDetail.getModuleDeployements()).containsOnly(moduleDeployementBack);
        assertThat(moduleDeployementBack.getProductDeployementDetail()).isEqualTo(productDeployementDetail);

        productDeployementDetail.removeModuleDeployement(moduleDeployementBack);
        assertThat(productDeployementDetail.getModuleDeployements()).doesNotContain(moduleDeployementBack);
        assertThat(moduleDeployementBack.getProductDeployementDetail()).isNull();

        productDeployementDetail.moduleDeployements(new HashSet<>(Set.of(moduleDeployementBack)));
        assertThat(productDeployementDetail.getModuleDeployements()).containsOnly(moduleDeployementBack);
        assertThat(moduleDeployementBack.getProductDeployementDetail()).isEqualTo(productDeployementDetail);

        productDeployementDetail.setModuleDeployements(new HashSet<>());
        assertThat(productDeployementDetail.getModuleDeployements()).doesNotContain(moduleDeployementBack);
        assertThat(moduleDeployementBack.getProductDeployementDetail()).isNull();
    }

    @Test
    void productDeployementTest() {
        ProductDeployementDetail productDeployementDetail = getProductDeployementDetailRandomSampleGenerator();
        ProductDeployement productDeployementBack = getProductDeployementRandomSampleGenerator();

        productDeployementDetail.setProductDeployement(productDeployementBack);
        assertThat(productDeployementDetail.getProductDeployement()).isEqualTo(productDeployementBack);

        productDeployementDetail.productDeployement(null);
        assertThat(productDeployementDetail.getProductDeployement()).isNull();
    }

    @Test
    void infraComponentVersionTest() {
        ProductDeployementDetail productDeployementDetail = getProductDeployementDetailRandomSampleGenerator();
        InfraComponentVersion infraComponentVersionBack = getInfraComponentVersionRandomSampleGenerator();

        productDeployementDetail.addInfraComponentVersion(infraComponentVersionBack);
        assertThat(productDeployementDetail.getInfraComponentVersions()).containsOnly(infraComponentVersionBack);

        productDeployementDetail.removeInfraComponentVersion(infraComponentVersionBack);
        assertThat(productDeployementDetail.getInfraComponentVersions()).doesNotContain(infraComponentVersionBack);

        productDeployementDetail.infraComponentVersions(new HashSet<>(Set.of(infraComponentVersionBack)));
        assertThat(productDeployementDetail.getInfraComponentVersions()).containsOnly(infraComponentVersionBack);

        productDeployementDetail.setInfraComponentVersions(new HashSet<>());
        assertThat(productDeployementDetail.getInfraComponentVersions()).doesNotContain(infraComponentVersionBack);
    }

    @Test
    void allowedModuleVersionTest() {
        ProductDeployementDetail productDeployementDetail = getProductDeployementDetailRandomSampleGenerator();
        ModuleVersion moduleVersionBack = getModuleVersionRandomSampleGenerator();

        productDeployementDetail.addAllowedModuleVersion(moduleVersionBack);
        assertThat(productDeployementDetail.getAllowedModuleVersions()).containsOnly(moduleVersionBack);

        productDeployementDetail.removeAllowedModuleVersion(moduleVersionBack);
        assertThat(productDeployementDetail.getAllowedModuleVersions()).doesNotContain(moduleVersionBack);

        productDeployementDetail.allowedModuleVersions(new HashSet<>(Set.of(moduleVersionBack)));
        assertThat(productDeployementDetail.getAllowedModuleVersions()).containsOnly(moduleVersionBack);

        productDeployementDetail.setAllowedModuleVersions(new HashSet<>());
        assertThat(productDeployementDetail.getAllowedModuleVersions()).doesNotContain(moduleVersionBack);
    }

    @Test
    void productVersionTest() {
        ProductDeployementDetail productDeployementDetail = getProductDeployementDetailRandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        productDeployementDetail.setProductVersion(productVersionBack);
        assertThat(productDeployementDetail.getProductVersion()).isEqualTo(productVersionBack);

        productDeployementDetail.productVersion(null);
        assertThat(productDeployementDetail.getProductVersion()).isNull();
    }

    @Test
    void deployementTypeTest() {
        ProductDeployementDetail productDeployementDetail = getProductDeployementDetailRandomSampleGenerator();
        DeployementType deployementTypeBack = getDeployementTypeRandomSampleGenerator();

        productDeployementDetail.setDeployementType(deployementTypeBack);
        assertThat(productDeployementDetail.getDeployementType()).isEqualTo(deployementTypeBack);

        productDeployementDetail.deployementType(null);
        assertThat(productDeployementDetail.getDeployementType()).isNull();
    }
}
