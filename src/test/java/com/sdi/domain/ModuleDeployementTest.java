package com.sdi.domain;

import static com.sdi.domain.FeatureDeployementTestSamples.*;
import static com.sdi.domain.ModuleDeployementTestSamples.*;
import static com.sdi.domain.ModuleVersionTestSamples.*;
import static com.sdi.domain.ProductDeployementDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModuleDeployementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModuleDeployement.class);
        ModuleDeployement moduleDeployement1 = getModuleDeployementSample1();
        ModuleDeployement moduleDeployement2 = new ModuleDeployement();
        assertThat(moduleDeployement1).isNotEqualTo(moduleDeployement2);

        moduleDeployement2.setId(moduleDeployement1.getId());
        assertThat(moduleDeployement1).isEqualTo(moduleDeployement2);

        moduleDeployement2 = getModuleDeployementSample2();
        assertThat(moduleDeployement1).isNotEqualTo(moduleDeployement2);
    }

    @Test
    void featureDeployementTest() {
        ModuleDeployement moduleDeployement = getModuleDeployementRandomSampleGenerator();
        FeatureDeployement featureDeployementBack = getFeatureDeployementRandomSampleGenerator();

        moduleDeployement.addFeatureDeployement(featureDeployementBack);
        assertThat(moduleDeployement.getFeatureDeployements()).containsOnly(featureDeployementBack);
        assertThat(featureDeployementBack.getModuleDeployement()).isEqualTo(moduleDeployement);

        moduleDeployement.removeFeatureDeployement(featureDeployementBack);
        assertThat(moduleDeployement.getFeatureDeployements()).doesNotContain(featureDeployementBack);
        assertThat(featureDeployementBack.getModuleDeployement()).isNull();

        moduleDeployement.featureDeployements(new HashSet<>(Set.of(featureDeployementBack)));
        assertThat(moduleDeployement.getFeatureDeployements()).containsOnly(featureDeployementBack);
        assertThat(featureDeployementBack.getModuleDeployement()).isEqualTo(moduleDeployement);

        moduleDeployement.setFeatureDeployements(new HashSet<>());
        assertThat(moduleDeployement.getFeatureDeployements()).doesNotContain(featureDeployementBack);
        assertThat(featureDeployementBack.getModuleDeployement()).isNull();
    }

    @Test
    void moduleVersionTest() {
        ModuleDeployement moduleDeployement = getModuleDeployementRandomSampleGenerator();
        ModuleVersion moduleVersionBack = getModuleVersionRandomSampleGenerator();

        moduleDeployement.setModuleVersion(moduleVersionBack);
        assertThat(moduleDeployement.getModuleVersion()).isEqualTo(moduleVersionBack);

        moduleDeployement.moduleVersion(null);
        assertThat(moduleDeployement.getModuleVersion()).isNull();
    }

    @Test
    void productDeployementDetailTest() {
        ModuleDeployement moduleDeployement = getModuleDeployementRandomSampleGenerator();
        ProductDeployementDetail productDeployementDetailBack = getProductDeployementDetailRandomSampleGenerator();

        moduleDeployement.setProductDeployementDetail(productDeployementDetailBack);
        assertThat(moduleDeployement.getProductDeployementDetail()).isEqualTo(productDeployementDetailBack);

        moduleDeployement.productDeployementDetail(null);
        assertThat(moduleDeployement.getProductDeployementDetail()).isNull();
    }
}
