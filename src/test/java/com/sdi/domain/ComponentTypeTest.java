package com.sdi.domain;

import static com.sdi.domain.ComponentTypeTestSamples.*;
import static com.sdi.domain.InfraComponentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ComponentTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComponentType.class);
        ComponentType componentType1 = getComponentTypeSample1();
        ComponentType componentType2 = new ComponentType();
        assertThat(componentType1).isNotEqualTo(componentType2);

        componentType2.setId(componentType1.getId());
        assertThat(componentType1).isEqualTo(componentType2);

        componentType2 = getComponentTypeSample2();
        assertThat(componentType1).isNotEqualTo(componentType2);
    }

    @Test
    void infraComponentTest() {
        ComponentType componentType = getComponentTypeRandomSampleGenerator();
        InfraComponent infraComponentBack = getInfraComponentRandomSampleGenerator();

        componentType.addInfraComponent(infraComponentBack);
        assertThat(componentType.getInfraComponents()).containsOnly(infraComponentBack);
        assertThat(infraComponentBack.getComponentType()).isEqualTo(componentType);

        componentType.removeInfraComponent(infraComponentBack);
        assertThat(componentType.getInfraComponents()).doesNotContain(infraComponentBack);
        assertThat(infraComponentBack.getComponentType()).isNull();

        componentType.infraComponents(new HashSet<>(Set.of(infraComponentBack)));
        assertThat(componentType.getInfraComponents()).containsOnly(infraComponentBack);
        assertThat(infraComponentBack.getComponentType()).isEqualTo(componentType);

        componentType.setInfraComponents(new HashSet<>());
        assertThat(componentType.getInfraComponents()).doesNotContain(infraComponentBack);
        assertThat(infraComponentBack.getComponentType()).isNull();
    }
}
