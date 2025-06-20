package com.sdi.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ModuleVersionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertModuleVersionAllPropertiesEquals(ModuleVersion expected, ModuleVersion actual) {
        assertModuleVersionAutoGeneratedPropertiesEquals(expected, actual);
        assertModuleVersionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertModuleVersionAllUpdatablePropertiesEquals(ModuleVersion expected, ModuleVersion actual) {
        assertModuleVersionUpdatableFieldsEquals(expected, actual);
        assertModuleVersionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertModuleVersionAutoGeneratedPropertiesEquals(ModuleVersion expected, ModuleVersion actual) {
        assertThat(actual)
            .as("Verify ModuleVersion auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertModuleVersionUpdatableFieldsEquals(ModuleVersion expected, ModuleVersion actual) {
        assertThat(actual)
            .as("Verify ModuleVersion relevant properties")
            .satisfies(a -> assertThat(a.getVersion()).as("check version").isEqualTo(expected.getVersion()))
            .satisfies(a -> assertThat(a.getCreateDate()).as("check createDate").isEqualTo(expected.getCreateDate()))
            .satisfies(a -> assertThat(a.getUpdateDate()).as("check updateDate").isEqualTo(expected.getUpdateDate()))
            .satisfies(a -> assertThat(a.getNotes()).as("check notes").isEqualTo(expected.getNotes()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertModuleVersionUpdatableRelationshipsEquals(ModuleVersion expected, ModuleVersion actual) {
        assertThat(actual)
            .as("Verify ModuleVersion relationships")
            .satisfies(a -> assertThat(a.getModule()).as("check module").isEqualTo(expected.getModule()))
            .satisfies(a -> assertThat(a.getFeatures()).as("check features").isEqualTo(expected.getFeatures()))
            .satisfies(a -> assertThat(a.getDomaine()).as("check domaine").isEqualTo(expected.getDomaine()))
            .satisfies(a -> assertThat(a.getRoot()).as("check root").isEqualTo(expected.getRoot()))
            .satisfies(a -> assertThat(a.getProductVersions()).as("check productVersions").isEqualTo(expected.getProductVersions()))
            .satisfies(a ->
                assertThat(a.getProductDeployementDetails())
                    .as("check productDeployementDetails")
                    .isEqualTo(expected.getProductDeployementDetails())
            )
            .satisfies(a -> assertThat(a.getRequestOfChanges()).as("check requestOfChanges").isEqualTo(expected.getRequestOfChanges()));
    }
}
