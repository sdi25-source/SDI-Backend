package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DomaineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Domaine getDomaineSample1() {
        return new Domaine().id(1L).name("name1");
    }

    public static Domaine getDomaineSample2() {
        return new Domaine().id(2L).name("name2");
    }

    public static Domaine getDomaineRandomSampleGenerator() {
        return new Domaine().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
