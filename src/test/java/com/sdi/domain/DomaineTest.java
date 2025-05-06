package com.sdi.domain;

import static com.sdi.domain.DomaineTestSamples.*;
import static com.sdi.domain.ModuleVersionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DomaineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Domaine.class);
        Domaine domaine1 = getDomaineSample1();
        Domaine domaine2 = new Domaine();
        assertThat(domaine1).isNotEqualTo(domaine2);

        domaine2.setId(domaine1.getId());
        assertThat(domaine1).isEqualTo(domaine2);

        domaine2 = getDomaineSample2();
        assertThat(domaine1).isNotEqualTo(domaine2);
    }

    @Test
    void moduleVersionTest() {
        Domaine domaine = getDomaineRandomSampleGenerator();
        ModuleVersion moduleVersionBack = getModuleVersionRandomSampleGenerator();

        domaine.addModuleVersion(moduleVersionBack);
        assertThat(domaine.getModuleVersions()).containsOnly(moduleVersionBack);
        assertThat(moduleVersionBack.getDomaine()).isEqualTo(domaine);

        domaine.removeModuleVersion(moduleVersionBack);
        assertThat(domaine.getModuleVersions()).doesNotContain(moduleVersionBack);
        assertThat(moduleVersionBack.getDomaine()).isNull();

        domaine.moduleVersions(new HashSet<>(Set.of(moduleVersionBack)));
        assertThat(domaine.getModuleVersions()).containsOnly(moduleVersionBack);
        assertThat(moduleVersionBack.getDomaine()).isEqualTo(domaine);

        domaine.setModuleVersions(new HashSet<>());
        assertThat(domaine.getModuleVersions()).doesNotContain(moduleVersionBack);
        assertThat(moduleVersionBack.getDomaine()).isNull();
    }
}
