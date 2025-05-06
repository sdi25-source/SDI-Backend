package com.sdi.domain;

import static com.sdi.domain.ComponentTypeTestSamples.*;
import static com.sdi.domain.InfraComponentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
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
}
