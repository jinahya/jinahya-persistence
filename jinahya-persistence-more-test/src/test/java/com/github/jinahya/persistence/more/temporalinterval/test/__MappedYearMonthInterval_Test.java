package com.github.jinahya.persistence.more.temporalinterval.test;

import com.github.jinahya.persistence.more.temporalinterval.__MappedYearMonthInterval;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

/**
 * Tests {@link __MappedYearMonthInterval} against the closed form as well: this axis steps.
 */
class __MappedYearMonthInterval_Test extends ___DiscreteInterval_Test<__MappedYearMonthInterval_Test.Iv, YearMonth> {

    static final class Iv extends __MappedYearMonthInterval {

    }

    __MappedYearMonthInterval_Test() {
        super(Iv.class, YearMonth.of(2026, 1), YearMonth.of(2027, 3),
              ChronoUnit.MONTHS, 14L);
    }
}
