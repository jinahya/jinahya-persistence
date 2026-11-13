package com.github.jinahya.persistence.more.temporalinterval.test;

import com.github.jinahya.persistence.more.temporalinterval.__MappedYearInterval;

import java.time.Year;
import java.time.temporal.ChronoUnit;

/**
 * Tests {@link __MappedYearInterval} against the closed form as well: this axis steps.
 */
class __MappedYearInterval_Test extends ___DiscreteInterval_Test<__MappedYearInterval_Test.Iv, Year> {

    static final class Iv extends __MappedYearInterval {

    }

    __MappedYearInterval_Test() {
        super(Iv.class, Year.of(2026), Year.of(2029),
              ChronoUnit.YEARS, 3L);
    }
}
