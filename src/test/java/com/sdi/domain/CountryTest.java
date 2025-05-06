package com.sdi.domain;

import static com.sdi.domain.ClientTestSamples.*;
import static com.sdi.domain.CountryTestSamples.*;
import static com.sdi.domain.RegionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sdi.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Country.class);
        Country country1 = getCountrySample1();
        Country country2 = new Country();
        assertThat(country1).isNotEqualTo(country2);

        country2.setId(country1.getId());
        assertThat(country1).isEqualTo(country2);

        country2 = getCountrySample2();
        assertThat(country1).isNotEqualTo(country2);
    }

    @Test
    void clientTest() {
        Country country = getCountryRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        country.addClient(clientBack);
        assertThat(country.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getCountry()).isEqualTo(country);

        country.removeClient(clientBack);
        assertThat(country.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getCountry()).isNull();

        country.clients(new HashSet<>(Set.of(clientBack)));
        assertThat(country.getClients()).containsOnly(clientBack);
        assertThat(clientBack.getCountry()).isEqualTo(country);

        country.setClients(new HashSet<>());
        assertThat(country.getClients()).doesNotContain(clientBack);
        assertThat(clientBack.getCountry()).isNull();
    }

    @Test
    void regionTest() {
        Country country = getCountryRandomSampleGenerator();
        Region regionBack = getRegionRandomSampleGenerator();

        country.setRegion(regionBack);
        assertThat(country.getRegion()).isEqualTo(regionBack);

        country.region(null);
        assertThat(country.getRegion()).isNull();
    }
}
