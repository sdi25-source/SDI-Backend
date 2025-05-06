package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ComponentTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ComponentType getComponentTypeSample1() {
        return new ComponentType().id(1L).type("type1");
    }

    public static ComponentType getComponentTypeSample2() {
        return new ComponentType().id(2L).type("type2");
    }

    public static ComponentType getComponentTypeRandomSampleGenerator() {
        return new ComponentType().id(longCount.incrementAndGet()).type(UUID.randomUUID().toString());
    }
}
