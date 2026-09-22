package com.github.jinahya.persistence.more.temporalinterval.test;

import com.github.jinahya.persistence.more.temporalinterval.__MappedLocalDateTimeInterval;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Tests {@link __MappedLocalDateTimeInterval} against what every interval is expected to do.
 */
class __MappedLocalDateTimeInterval_Test extends ___MappedTemporalInterval_Test<__MappedLocalDateTimeInterval_Test.Iv, LocalDateTime> {

    static final class Iv extends __MappedLocalDateTimeInterval {

    }

    __MappedLocalDateTimeInterval_Test() {
        super(Iv.class, LocalDateTime.of(2026, 1, 1, 9, 0), LocalDateTime.of(2026, 1, 1, 17, 0),
              ChronoUnit.HOURS, 8L);
    }
}
