package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InfraComponentVersionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static InfraComponentVersion getInfraComponentVersionSample1() {
        return new InfraComponentVersion().id(1L).version("version1");
    }

    public static InfraComponentVersion getInfraComponentVersionSample2() {
        return new InfraComponentVersion().id(2L).version("version2");
    }

    public static InfraComponentVersion getInfraComponentVersionRandomSampleGenerator() {
        return new InfraComponentVersion().id(longCount.incrementAndGet()).version(UUID.randomUUID().toString());
    }
}
