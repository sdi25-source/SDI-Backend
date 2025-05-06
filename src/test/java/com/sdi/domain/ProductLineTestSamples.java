package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductLineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductLine getProductLineSample1() {
        return new ProductLine().id(1L).name("name1");
    }

    public static ProductLine getProductLineSample2() {
        return new ProductLine().id(2L).name("name2");
    }

    public static ProductLine getProductLineRandomSampleGenerator() {
        return new ProductLine().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
