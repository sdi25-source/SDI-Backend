package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RequestOfChangeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RequestOfChange getRequestOfChangeSample1() {
        return new RequestOfChange().id(1L).title("title1");
    }

    public static RequestOfChange getRequestOfChangeSample2() {
        return new RequestOfChange().id(2L).title("title2");
    }

    public static RequestOfChange getRequestOfChangeRandomSampleGenerator() {
        return new RequestOfChange().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
