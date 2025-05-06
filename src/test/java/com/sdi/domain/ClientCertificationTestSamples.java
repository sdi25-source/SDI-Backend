package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientCertificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClientCertification getClientCertificationSample1() {
        return new ClientCertification().id(1L).certification("certification1");
    }

    public static ClientCertification getClientCertificationSample2() {
        return new ClientCertification().id(2L).certification("certification2");
    }

    public static ClientCertification getClientCertificationRandomSampleGenerator() {
        return new ClientCertification().id(longCount.incrementAndGet()).certification(UUID.randomUUID().toString());
    }
}
