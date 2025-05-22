package com.sdi.domain;

import static com.sdi.domain.ComponentTypeTestSamples.*;
import static com.sdi.domain.InfraComponentTestSamples.*;
import static com.sdi.domain.ProductVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InfraComponentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InfraComponent.class);
        InfraComponent infraComponent1 = getInfraComponentSample1();
        InfraComponent infraComponent2 = new InfraComponent();
        assertThat(infraComponent1).isNotEqualTo(infraComponent2);

        infraComponent2.setId(infraComponent1.getId());
        assertThat(infraComponent1).isEqualTo(infraComponent2);

        infraComponent2 = getInfraComponentSample2();
        assertThat(infraComponent1).isNotEqualTo(infraComponent2);
    }

    @Test
    void componentTypeTest() {
        InfraComponent infraComponent = getInfraComponentRandomSampleGenerator();
        ComponentType componentTypeBack = getComponentTypeRandomSampleGenerator();

        infraComponent.setComponentType(componentTypeBack);
        assertThat(infraComponent.getComponentType()).isEqualTo(componentTypeBack);

        infraComponent.componentType(null);
        assertThat(infraComponent.getComponentType()).isNull();
    }

    @Test
    void productVersionTest() {
        InfraComponent infraComponent = getInfraComponentRandomSampleGenerator();
        ProductVersion productVersionBack = getProductVersionRandomSampleGenerator();

        infraComponent.addProductVersion(productVersionBack);
        assertThat(infraComponent.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getInfraComponents()).containsOnly(infraComponent);

        infraComponent.removeProductVersion(productVersionBack);
        assertThat(infraComponent.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getInfraComponents()).doesNotContain(infraComponent);

        infraComponent.productVersions(new HashSet<>(Set.of(productVersionBack)));
        assertThat(infraComponent.getProductVersions()).containsOnly(productVersionBack);
        assertThat(productVersionBack.getInfraComponents()).containsOnly(infraComponent);

        infraComponent.setProductVersions(new HashSet<>());
        assertThat(infraComponent.getProductVersions()).doesNotContain(productVersionBack);
        assertThat(productVersionBack.getInfraComponents()).doesNotContain(infraComponent);
    }
}
