package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InfraComponentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static InfraComponent getInfraComponentSample1() {
        return new InfraComponent().id(1L).name("name1").vendor("vendor1");
    }

    public static InfraComponent getInfraComponentSample2() {
        return new InfraComponent().id(2L).name("name2").vendor("vendor2");
    }

    public static InfraComponent getInfraComponentRandomSampleGenerator() {
        return new InfraComponent().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).vendor(UUID.randomUUID().toString());
    }
}
