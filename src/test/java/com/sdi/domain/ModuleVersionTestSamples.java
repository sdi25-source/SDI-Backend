package com.sdi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ModuleVersionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ModuleVersion getModuleVersionSample1() {
        return new ModuleVersion().id(1L).version("version1");
    }

    public static ModuleVersion getModuleVersionSample2() {
        return new ModuleVersion().id(2L).version("version2");
    }

    public static ModuleVersion getModuleVersionRandomSampleGenerator() {
        return new ModuleVersion().id(longCount.incrementAndGet()).version(UUID.randomUUID().toString());
    }
}
