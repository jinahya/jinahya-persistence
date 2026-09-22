package com.github.jinahya.persistence.more.temporalinterval.test;

import com.github.jinahya.persistence.more.temporalinterval.__MappedInstantInterval;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Tests {@link __MappedInstantInterval} against what every interval is expected to do.
 */
class __MappedInstantInterval_Test extends ___MappedTemporalInterval_Test<__MappedInstantInterval_Test.Iv, Instant> {

    static final class Iv extends __MappedInstantInterval {

    }

    __MappedInstantInterval_Test() {
        super(Iv.class, Instant.EPOCH, Instant.EPOCH.plusSeconds(3600L),
              ChronoUnit.SECONDS, 3600L);
    }
}
