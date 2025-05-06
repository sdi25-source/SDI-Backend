package com.sdi.domain;

import static com.sdi.domain.CustomisationLevelTestSamples.*;
import static com.sdi.domain.RequestOfChangeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomisationLevelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomisationLevel.class);
        CustomisationLevel customisationLevel1 = getCustomisationLevelSample1();
        CustomisationLevel customisationLevel2 = new CustomisationLevel();
        assertThat(customisationLevel1).isNotEqualTo(customisationLevel2);

        customisationLevel2.setId(customisationLevel1.getId());
        assertThat(customisationLevel1).isEqualTo(customisationLevel2);

        customisationLevel2 = getCustomisationLevelSample2();
        assertThat(customisationLevel1).isNotEqualTo(customisationLevel2);
    }

    @Test
    void requestOfChangeTest() {
        CustomisationLevel customisationLevel = getCustomisationLevelRandomSampleGenerator();
        RequestOfChange requestOfChangeBack = getRequestOfChangeRandomSampleGenerator();

        customisationLevel.addRequestOfChange(requestOfChangeBack);
        assertThat(customisationLevel.getRequestOfChanges()).containsOnly(requestOfChangeBack);
        assertThat(requestOfChangeBack.getCustomisationLevel()).isEqualTo(customisationLevel);

        customisationLevel.removeRequestOfChange(requestOfChangeBack);
        assertThat(customisationLevel.getRequestOfChanges()).doesNotContain(requestOfChangeBack);
        assertThat(requestOfChangeBack.getCustomisationLevel()).isNull();

        customisationLevel.requestOfChanges(new HashSet<>(Set.of(requestOfChangeBack)));
        assertThat(customisationLevel.getRequestOfChanges()).containsOnly(requestOfChangeBack);
        assertThat(requestOfChangeBack.getCustomisationLevel()).isEqualTo(customisationLevel);

        customisationLevel.setRequestOfChanges(new HashSet<>());
        assertThat(customisationLevel.getRequestOfChanges()).doesNotContain(requestOfChangeBack);
        assertThat(requestOfChangeBack.getCustomisationLevel()).isNull();
    }
}
