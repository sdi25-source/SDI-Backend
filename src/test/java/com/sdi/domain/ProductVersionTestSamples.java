package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductVersionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductVersion getProductVersionSample1() {
        return new ProductVersion().id(1L).version("version1");
    }

    public static ProductVersion getProductVersionSample2() {
        return new ProductVersion().id(2L).version("version2");
    }

    public static ProductVersion getProductVersionRandomSampleGenerator() {
        return new ProductVersion().id(longCount.incrementAndGet()).version(UUID.randomUUID().toString());
    }
}
