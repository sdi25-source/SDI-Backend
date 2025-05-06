package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HATestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static HA getHASample1() {
        return new HA().id(1L).name("name1");
    }

    public static HA getHASample2() {
        return new HA().id(2L).name("name2");
    }

    public static HA getHARandomSampleGenerator() {
        return new HA().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
