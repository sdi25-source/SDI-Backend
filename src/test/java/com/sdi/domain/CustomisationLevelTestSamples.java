package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomisationLevelTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CustomisationLevel getCustomisationLevelSample1() {
        return new CustomisationLevel().id(1L).level("level1");
    }

    public static CustomisationLevel getCustomisationLevelSample2() {
        return new CustomisationLevel().id(2L).level("level2");
    }

    public static CustomisationLevel getCustomisationLevelRandomSampleGenerator() {
        return new CustomisationLevel().id(longCount.incrementAndGet()).level(UUID.randomUUID().toString());
    }
}
