package com.sdi.domain;

import static com.sdi.domain.InfraComponentTestSamples.*;
import static com.sdi.domain.InfraComponentVersionTestSamples.*;
import static com.sdi.domain.ModuleVersionTestSamples.*;
import static com.sdi.domain.ProductDeployementDetailTestSamples.*;
import static com.sdi.domain.ProductTestSamples.*;
import static com.sdi.domain.ProductVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductVersionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductVersion.class);
        ProductVersion productVersion1 = getProductVersionSample1();
        ProductVersion productVersion2 = new ProductVersion();
        assertThat(productVersion1).isNotEqualTo(productVersion2);

        productVersion2.setId(productVersion1.getId());
        assertThat(productVersion1).isEqualTo(productVersion2);

        productVersion2 = getProductVersionSample2();
        assertThat(productVersion1).isNotEqualTo(productVersion2);
    }

    @Test
    void productDeployementDetailTest() {
        ProductVersion productVersion = getProductVersionRandomSampleGenerator();
        ProductDeployementDetail productDeployementDetailBack = getProductDeployementDetailRandomSampleGenerator();

        productVersion.addProductDeployementDetail(productDeployementDetailBack);
        assertThat(productVersion.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getProductVersion()).isEqualTo(productVersion);

        productVersion.removeProductDeployementDetail(productDeployementDetailBack);
        assertThat(productVersion.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getProductVersion()).isNull();

        productVersion.productDeployementDetails(new HashSet<>(Set.of(productDeployementDetailBack)));
        assertThat(productVersion.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getProductVersion()).isEqualTo(productVersion);

        productVersion.setProductDeployementDetails(new HashSet<>());
        assertThat(productVersion.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getProductVersion()).isNull();
    }

    @Test
    void productVersionTest() {
        ProductVersion productVersion = getProductVersionRandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        productVersion.addProductVersion(productVersionBack);
        assertThat(productVersion.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getRoot()).isEqualTo(productVersion);

        productVersion.removeProductVersion(productVersionBack);
        assertThat(productVersion.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getRoot()).isNull();

        productVersion.productVersions(new HashSet<>(Set.of(productVersionBack)));
        assertThat(productVersion.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getRoot()).isEqualTo(productVersion);

        productVersion.setProductVersions(new HashSet<>());
        assertThat(productVersion.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getRoot()).isNull();
    }

    @Test
    void productTest() {
        ProductVersion productVersion = getProductVersionRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        productVersion.setProduct(productBack);
        assertThat(productVersion.getProduct()).isEqualTo(productBack);

        productVersion.product(null);
        assertThat(productVersion.getProduct()).isNull();
    }

    @Test
    void moduleVersionTest() {
        ProductVersion productVersion = getProductVersionRandomSampleGenerator();
        ModuleVersion moduleVersionBack = getModuleVersionRandomSampleGenerator();

        productVersion.addModuleVersion(moduleVersionBack);
        assertThat(productVersion.getModuleVersions()).containsOnly(moduleVersionBack);

        productVersion.removeModuleVersion(moduleVersionBack);
        assertThat(productVersion.getModuleVersions()).doesNotContain(moduleVersionBack);

        productVersion.moduleVersions(new HashSet<>(Set.of(moduleVersionBack)));
        assertThat(productVersion.getModuleVersions()).containsOnly(moduleVersionBack);

        productVersion.setModuleVersions(new HashSet<>());
        assertThat(productVersion.getModuleVersions()).doesNotContain(moduleVersionBack);
    }

    @Test
    void infraComponentVersionTest() {
        ProductVersion productVersion = getProductVersionRandomSampleGenerator();
        InfraComponentVersion infraComponentVersionBack = getInfraComponentVersionRandomSampleGenerator();

        productVersion.addInfraComponentVersion(infraComponentVersionBack);
        assertThat(productVersion.getInfraComponentVersions()).containsOnly(infraComponentVersionBack);

        productVersion.removeInfraComponentVersion(infraComponentVersionBack);
        assertThat(productVersion.getInfraComponentVersions()).doesNotContain(infraComponentVersionBack);

        productVersion.infraComponentVersions(new HashSet<>(Set.of(infraComponentVersionBack)));
        assertThat(productVersion.getInfraComponentVersions()).containsOnly(infraComponentVersionBack);

        productVersion.setInfraComponentVersions(new HashSet<>());
        assertThat(productVersion.getInfraComponentVersions()).doesNotContain(infraComponentVersionBack);
    }

    @Test
    void infraComponentTest() {
        ProductVersion productVersion = getProductVersionRandomSampleGenerator();
        InfraComponent infraComponentBack = getInfraComponentRandomSampleGenerator();

        productVersion.addInfraComponent(infraComponentBack);
        assertThat(productVersion.getInfraComponents()).containsOnly(infraComponentBack);

        productVersion.removeInfraComponent(infraComponentBack);
        assertThat(productVersion.getInfraComponents()).doesNotContain(infraComponentBack);

        productVersion.infraComponents(new HashSet<>(Set.of(infraComponentBack)));
        assertThat(productVersion.getInfraComponents()).containsOnly(infraComponentBack);

        productVersion.setInfraComponents(new HashSet<>());
        assertThat(productVersion.getInfraComponents()).doesNotContain(infraComponentBack);
    }

    @Test
    void rootTest() {
        ProductVersion productVersion = getProductVersionRandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        productVersion.setRoot(productVersionBack);
        assertThat(productVersion.getRoot()).isEqualTo(productVersionBack);

        productVersion.root(null);
        assertThat(productVersion.getRoot()).isNull();
    }
}
