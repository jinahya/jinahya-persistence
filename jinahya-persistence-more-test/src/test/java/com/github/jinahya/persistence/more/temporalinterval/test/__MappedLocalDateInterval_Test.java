package com.github.jinahya.persistence.more.temporalinterval.test;

import com.github.jinahya.persistence.more.temporalinterval.__MappedLocalDateInterval;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tests {@link __MappedLocalDateInterval} against the closed form as well: this axis steps.
 */
class __MappedLocalDateInterval_Test extends ___DiscreteInterval_Test<__MappedLocalDateInterval_Test.Iv, LocalDate> {

    static final class Iv extends __MappedLocalDateInterval {

    }

    __MappedLocalDateInterval_Test() {
        super(Iv.class, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 5),
              ChronoUnit.DAYS, 4L);
    }
}
