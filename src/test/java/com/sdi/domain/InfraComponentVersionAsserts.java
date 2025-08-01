package com.sdi.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class InfraComponentVersionAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfraComponentVersionAllPropertiesEquals(InfraComponentVersion expected, InfraComponentVersion actual) {
        assertInfraComponentVersionAutoGeneratedPropertiesEquals(expected, actual);
        assertInfraComponentVersionAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfraComponentVersionAllUpdatablePropertiesEquals(
        InfraComponentVersion expected,
        InfraComponentVersion actual
    ) {
        assertInfraComponentVersionUpdatableFieldsEquals(expected, actual);
        assertInfraComponentVersionUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfraComponentVersionAutoGeneratedPropertiesEquals(
        InfraComponentVersion expected,
        InfraComponentVersion actual
    ) {
        assertThat(actual)
            .as("Verify InfraComponentVersion auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfraComponentVersionUpdatableFieldsEquals(InfraComponentVersion expected, InfraComponentVersion actual) {
        assertThat(actual)
            .as("Verify InfraComponentVersion relevant properties")
            .satisfies(a -> assertThat(a.getVersion()).as("check version").isEqualTo(expected.getVersion()))
            .satisfies(a -> assertThat(a.getDescription()).as("check description").isEqualTo(expected.getDescription()))
            .satisfies(a -> assertThat(a.getCreateDate()).as("check createDate").isEqualTo(expected.getCreateDate()))
            .satisfies(a -> assertThat(a.getUpdateDate()).as("check updateDate").isEqualTo(expected.getUpdateDate()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfraComponentVersionUpdatableRelationshipsEquals(
        InfraComponentVersion expected,
        InfraComponentVersion actual
    ) {
        assertThat(actual)
            .as("Verify InfraComponentVersion relationships")
            .satisfies(a -> assertThat(a.getInfraComponent()).as("check infraComponent").isEqualTo(expected.getInfraComponent()))
            .satisfies(a -> assertThat(a.getProductVersions()).as("check productVersions").isEqualTo(expected.getProductVersions()))
            .satisfies(a ->
                assertThat(a.getProductDeployementDetails())
                    .as("check productDeployementDetails")
                    .isEqualTo(expected.getProductDeployementDetails())
            );
    }
}
