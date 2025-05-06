package com.sdi.domain;

import static com.sdi.domain.DeployementTypeTestSamples.*;
import static com.sdi.domain.ProductDeployementDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeployementTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeployementType.class);
        DeployementType deployementType1 = getDeployementTypeSample1();
        DeployementType deployementType2 = new DeployementType();
        assertThat(deployementType1).isNotEqualTo(deployementType2);

        deployementType2.setId(deployementType1.getId());
        assertThat(deployementType1).isEqualTo(deployementType2);

        deployementType2 = getDeployementTypeSample2();
        assertThat(deployementType1).isNotEqualTo(deployementType2);
    }

    @Test
    void productDeployementDetailTest() {
        DeployementType deployementType = getDeployementTypeRandomSampleGenerator();
        ProductDeployementDetail productDeployementDetailBack = getProductDeployementDetailRandomSampleGenerator();

        deployementType.addProductDeployementDetail(productDeployementDetailBack);
        assertThat(deployementType.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getDeployementType()).isEqualTo(deployementType);

        deployementType.removeProductDeployementDetail(productDeployementDetailBack);
        assertThat(deployementType.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getDeployementType()).isNull();

        deployementType.productDeployementDetails(new HashSet<>(Set.of(productDeployementDetailBack)));
        assertThat(deployementType.getProductDeployementDetails()).containsOnly(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getDeployementType()).isEqualTo(deployementType);

        deployementType.setProductDeployementDetails(new HashSet<>());
        assertThat(deployementType.getProductDeployementDetails()).doesNotContain(productDeployementDetailBack);
        assertThat(productDeployementDetailBack.getDeployementType()).isNull();
    }
}
