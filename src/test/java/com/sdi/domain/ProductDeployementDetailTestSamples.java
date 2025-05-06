package com.sdi.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ProductDeployementDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductDeployementDetail getProductDeployementDetailSample1() {
        return new ProductDeployementDetail().id(1L);
    }

    public static ProductDeployementDetail getProductDeployementDetailSample2() {
        return new ProductDeployementDetail().id(2L);
    }

    public static ProductDeployementDetail getProductDeployementDetailRandomSampleGenerator() {
        return new ProductDeployementDetail().id(longCount.incrementAndGet());
    }
}
