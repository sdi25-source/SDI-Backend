package com.sdi.service.criteria;

import java.math.BigInteger;
import tech.jhipster.service.filter.RangeFilter;

public class BigIntegerFilter extends RangeFilter<BigInteger> {

    public BigIntegerFilter() {}

    public BigIntegerFilter(BigIntegerFilter filter) {
        super(filter);
    }

    @Override
    public BigIntegerFilter copy() {
        return new BigIntegerFilter(this);
    }
}
