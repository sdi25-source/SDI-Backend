package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CertificationVersionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CertificationVersion getCertificationVersionSample1() {
        return new CertificationVersion().id(1L).version("version1").description("description1");
    }

    public static CertificationVersion getCertificationVersionSample2() {
        return new CertificationVersion().id(2L).version("version2").description("description2");
    }

    public static CertificationVersion getCertificationVersionRandomSampleGenerator() {
        return new CertificationVersion()
            .id(longCount.incrementAndGet())
            .version(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
