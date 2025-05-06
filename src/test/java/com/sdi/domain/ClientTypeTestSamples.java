package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClientType getClientTypeSample1() {
        return new ClientType().id(1L).type("type1");
    }

    public static ClientType getClientTypeSample2() {
        return new ClientType().id(2L).type("type2");
    }

    public static ClientType getClientTypeRandomSampleGenerator() {
        return new ClientType().id(longCount.incrementAndGet()).type(UUID.randomUUID().toString());
    }
}
