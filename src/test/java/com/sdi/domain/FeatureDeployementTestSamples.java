package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FeatureDeployementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FeatureDeployement getFeatureDeployementSample1() {
        return new FeatureDeployement().id(1L).code("code1");
    }

    public static FeatureDeployement getFeatureDeployementSample2() {
        return new FeatureDeployement().id(2L).code("code2");
    }

    public static FeatureDeployement getFeatureDeployementRandomSampleGenerator() {
        return new FeatureDeployement().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
