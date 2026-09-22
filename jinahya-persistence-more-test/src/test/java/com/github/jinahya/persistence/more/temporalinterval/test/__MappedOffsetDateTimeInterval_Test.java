package com.github.jinahya.persistence.more.temporalinterval.test;

import com.github.jinahya.persistence.more.temporalinterval.__MappedOffsetDateTimeInterval;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Tests {@link __MappedOffsetDateTimeInterval} against what every interval is expected to do.
 */
class __MappedOffsetDateTimeInterval_Test extends ___MappedTemporalInterval_Test<__MappedOffsetDateTimeInterval_Test.Iv, OffsetDateTime> {

    static final class Iv extends __MappedOffsetDateTimeInterval {

    }

    __MappedOffsetDateTimeInterval_Test() {
        super(Iv.class, OffsetDateTime.of(2026, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC), OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
              ChronoUnit.HOURS, 3L);
    }
}
