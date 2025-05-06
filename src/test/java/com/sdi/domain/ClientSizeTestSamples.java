package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientSizeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClientSize getClientSizeSample1() {
        return new ClientSize().id(1L).sizeName("sizeName1").sizeCode("sizeCode1").sizeDescription("sizeDescription1");
    }

    public static ClientSize getClientSizeSample2() {
        return new ClientSize().id(2L).sizeName("sizeName2").sizeCode("sizeCode2").sizeDescription("sizeDescription2");
    }

    public static ClientSize getClientSizeRandomSampleGenerator() {
        return new ClientSize()
            .id(longCount.incrementAndGet())
            .sizeName(UUID.randomUUID().toString())
            .sizeCode(UUID.randomUUID().toString())
            .sizeDescription(UUID.randomUUID().toString());
    }
}
