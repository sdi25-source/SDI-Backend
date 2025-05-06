package com.sdi.domain;

import static com.sdi.domain.ModuleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
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
}
