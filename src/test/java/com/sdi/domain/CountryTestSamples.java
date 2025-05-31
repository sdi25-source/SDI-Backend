package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CountryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Country getCountrySample1() {
        return new Country()
            .id(1L)
            .countryname("countryname1")
            .countrycode("countrycode1")
            .countryFlagcode("countryFlagcode1")
            .countryFlag("countryFlag1");
    }

    public static Country getCountrySample2() {
        return new Country()
            .id(2L)
            .countryname("countryname2")
            .countrycode("countrycode2")
            .countryFlagcode("countryFlagcode2")
            .countryFlag("countryFlag2");
    }

    public static Country getCountryRandomSampleGenerator() {
        return new Country()
            .id(longCount.incrementAndGet())
            .countryname(UUID.randomUUID().toString())
            .countrycode(UUID.randomUUID().toString())
            .countryFlagcode(UUID.randomUUID().toString())
            .countryFlag(UUID.randomUUID().toString());
    }
}
