package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DeployementTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DeployementType getDeployementTypeSample1() {
        return new DeployementType().id(1L).type("type1");
    }

    public static DeployementType getDeployementTypeSample2() {
        return new DeployementType().id(2L).type("type2");
    }

    public static DeployementType getDeployementTypeRandomSampleGenerator() {
        return new DeployementType().id(longCount.incrementAndGet()).type(UUID.randomUUID().toString());
    }
}
