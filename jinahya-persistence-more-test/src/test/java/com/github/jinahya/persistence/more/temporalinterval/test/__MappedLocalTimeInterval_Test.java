package com.github.jinahya.persistence.more.temporalinterval.test;

import com.github.jinahya.persistence.more.temporalinterval.__MappedLocalTimeInterval;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Tests {@link __MappedLocalTimeInterval} against what every interval is expected to do.
 */
class __MappedLocalTimeInterval_Test extends ___MappedTemporalInterval_Test<__MappedLocalTimeInterval_Test.Iv, LocalTime> {

    static final class Iv extends __MappedLocalTimeInterval {

    }

    __MappedLocalTimeInterval_Test() {
        super(Iv.class, LocalTime.of(9, 0), LocalTime.of(17, 0),
              ChronoUnit.MINUTES, 480L);
    }
}
