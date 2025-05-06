package com.sdi.domain;

import static com.sdi.domain.FeatureDeployementTestSamples.*;
import static com.sdi.domain.FeatureTestSamples.*;
import static com.sdi.domain.ModuleDeployementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FeatureDeployementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeatureDeployement.class);
        FeatureDeployement featureDeployement1 = getFeatureDeployementSample1();
        FeatureDeployement featureDeployement2 = new FeatureDeployement();
        assertThat(featureDeployement1).isNotEqualTo(featureDeployement2);

        featureDeployement2.setId(featureDeployement1.getId());
        assertThat(featureDeployement1).isEqualTo(featureDeployement2);

        featureDeployement2 = getFeatureDeployementSample2();
        assertThat(featureDeployement1).isNotEqualTo(featureDeployement2);
    }

    @Test
    void featureTest() {
        FeatureDeployement featureDeployement = getFeatureDeployementRandomSampleGenerator();
        Feature featureBack = getFeatureRandomSampleGenerator();

        featureDeployement.setFeature(featureBack);
        assertThat(featureDeployement.getFeature()).isEqualTo(featureBack);

        featureDeployement.feature(null);
        assertThat(featureDeployement.getFeature()).isNull();
    }

    @Test
    void moduleDeployementTest() {
        FeatureDeployement featureDeployement = getFeatureDeployementRandomSampleGenerator();
        ModuleDeployement moduleDeployementBack = getModuleDeployementRandomSampleGenerator();

        featureDeployement.setModuleDeployement(moduleDeployementBack);
        assertThat(featureDeployement.getModuleDeployement()).isEqualTo(moduleDeployementBack);

        featureDeployement.moduleDeployement(null);
        assertThat(featureDeployement.getModuleDeployement()).isNull();
    }
}
