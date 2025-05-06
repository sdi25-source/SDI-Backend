package com.sdi.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClientCriteriaTest {

    @Test
    void newClientCriteriaHasAllFiltersNullTest() {
        var clientCriteria = new ClientCriteria();
        assertThat(clientCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void clientCriteriaFluentMethodsCreatesFiltersTest() {
        var clientCriteria = new ClientCriteria();

        setAllFilters(clientCriteria);

        assertThat(clientCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void clientCriteriaCopyCreatesNullFilterTest() {
        var clientCriteria = new ClientCriteria();
        var copy = clientCriteria.copy();

        assertThat(clientCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(clientCriteria)
        );
    }

    @Test
    void clientCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var clientCriteria = new ClientCriteria();
        setAllFilters(clientCriteria);

        var copy = clientCriteria.copy();

        assertThat(clientCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(clientCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var clientCriteria = new ClientCriteria();

        assertThat(clientCriteria).hasToString("ClientCriteria{}");
    }

    private static void setAllFilters(ClientCriteria clientCriteria) {
        clientCriteria.id();
        clientCriteria.clientLogo();
        clientCriteria.name();
        clientCriteria.code();
        clientCriteria.mainContactName();
        clientCriteria.mainContactEmail();
        clientCriteria.currentCardHolderNumber();
        clientCriteria.currentBruncheNumber();
        clientCriteria.currentCustomersNumber();
        clientCriteria.mainContactPhoneNumber();
        clientCriteria.url();
        clientCriteria.address();
        clientCriteria.createDate();
        clientCriteria.updateDate();
        clientCriteria.productDeployementId();
        clientCriteria.countryId();
        clientCriteria.sizeId();
        clientCriteria.clientTypeId();
        clientCriteria.certifId();
        clientCriteria.distinct();
    }

    private static Condition<ClientCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getClientLogo()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getMainContactName()) &&
                condition.apply(criteria.getMainContactEmail()) &&
                condition.apply(criteria.getCurrentCardHolderNumber()) &&
                condition.apply(criteria.getCurrentBruncheNumber()) &&
                condition.apply(criteria.getCurrentCustomersNumber()) &&
                condition.apply(criteria.getMainContactPhoneNumber()) &&
                condition.apply(criteria.getUrl()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getCreateDate()) &&
                condition.apply(criteria.getUpdateDate()) &&
                condition.apply(criteria.getProductDeployementId()) &&
                condition.apply(criteria.getCountryId()) &&
                condition.apply(criteria.getSizeId()) &&
                condition.apply(criteria.getClientTypeId()) &&
                condition.apply(criteria.getCertifId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ClientCriteria> copyFiltersAre(ClientCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getClientLogo(), copy.getClientLogo()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getMainContactName(), copy.getMainContactName()) &&
                condition.apply(criteria.getMainContactEmail(), copy.getMainContactEmail()) &&
                condition.apply(criteria.getCurrentCardHolderNumber(), copy.getCurrentCardHolderNumber()) &&
                condition.apply(criteria.getCurrentBruncheNumber(), copy.getCurrentBruncheNumber()) &&
                condition.apply(criteria.getCurrentCustomersNumber(), copy.getCurrentCustomersNumber()) &&
                condition.apply(criteria.getMainContactPhoneNumber(), copy.getMainContactPhoneNumber()) &&
                condition.apply(criteria.getUrl(), copy.getUrl()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getCreateDate(), copy.getCreateDate()) &&
                condition.apply(criteria.getUpdateDate(), copy.getUpdateDate()) &&
                condition.apply(criteria.getProductDeployementId(), copy.getProductDeployementId()) &&
                condition.apply(criteria.getCountryId(), copy.getCountryId()) &&
                condition.apply(criteria.getSizeId(), copy.getSizeId()) &&
                condition.apply(criteria.getClientTypeId(), copy.getClientTypeId()) &&
                condition.apply(criteria.getCertifId(), copy.getCertifId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
