package com.sdi.domain;

import static com.sdi.domain.ModuleTestSamples.*;
import static com.sdi.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModuleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Module.class);
        Module module1 = getModuleSample1();
        Module module2 = new Module();
        assertThat(module1).isNotEqualTo(module2);

        module2.setId(module1.getId());
        assertThat(module1).isEqualTo(module2);

        module2 = getModuleSample2();
        assertThat(module1).isNotEqualTo(module2);
    }

    @Test
    void productTest() {
        Module module = getModuleRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        module.addProduct(productBack);
        assertThat(module.getProducts()).containsOnly(productBack);
        assertThat(productBack.getModules()).containsOnly(module);

        module.removeProduct(productBack);
        assertThat(module.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getModules()).doesNotContain(module);

        module.products(new HashSet<>(Set.of(productBack)));
        assertThat(module.getProducts()).containsOnly(productBack);
        assertThat(productBack.getModules()).containsOnly(module);

        module.setProducts(new HashSet<>());
        assertThat(module.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getModules()).doesNotContain(module);
    }
}
