package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientEventTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClientEventType getClientEventTypeSample1() {
        return new ClientEventType().id(1L).type("type1").description("description1");
    }

    public static ClientEventType getClientEventTypeSample2() {
        return new ClientEventType().id(2L).type("type2").description("description2");
    }

    public static ClientEventType getClientEventTypeRandomSampleGenerator() {
        return new ClientEventType()
            .id(longCount.incrementAndGet())
            .type(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
