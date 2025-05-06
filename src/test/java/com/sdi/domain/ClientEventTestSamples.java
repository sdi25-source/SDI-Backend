package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClientEvent getClientEventSample1() {
        return new ClientEvent().id(1L).event("event1").description("description1");
    }

    public static ClientEvent getClientEventSample2() {
        return new ClientEvent().id(2L).event("event2").description("description2");
    }

    public static ClientEvent getClientEventRandomSampleGenerator() {
        return new ClientEvent()
            .id(longCount.incrementAndGet())
            .event(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
