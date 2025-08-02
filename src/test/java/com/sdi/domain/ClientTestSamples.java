package com.sdi.domain;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Client getClientSample1() {
        return new Client()
            .id(1L)
            .name("name1")
            .code("code1")
            .mainContactName("mainContactName1")
            .mainContactEmail("mainContactEmail1")
            .currentCardHolderNumber(BigInteger.valueOf(1))
            .currentBruncheNumber(1)
            .currentCustomersNumber(1)
            .mainContactPhoneNumber("mainContactPhoneNumber1")
            .url("url1")
            .address("address1");
    }

    public static Client getClientSample2() {
        return new Client()
            .id(2L)
            .name("name2")
            .code("code2")
            .mainContactName("mainContactName2")
            .mainContactEmail("mainContactEmail2")
            .currentCardHolderNumber(BigInteger.valueOf(2))
            .currentBruncheNumber(2)
            .currentCustomersNumber(2)
            .mainContactPhoneNumber("mainContactPhoneNumber2")
            .url("url2")
            .address("address2");
    }

    public static Client getClientRandomSampleGenerator() {
        return new Client()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .mainContactName(UUID.randomUUID().toString())
            .mainContactEmail(UUID.randomUUID().toString())
            .currentCardHolderNumber(BigInteger.valueOf(intCount.incrementAndGet()))
            .currentBruncheNumber(intCount.incrementAndGet())
            .currentCustomersNumber(intCount.incrementAndGet())
            .mainContactPhoneNumber(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
