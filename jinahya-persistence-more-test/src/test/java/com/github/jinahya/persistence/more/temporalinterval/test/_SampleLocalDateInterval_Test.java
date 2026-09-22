package com.github.jinahya.persistence.more.temporalinterval.test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class _SampleLocalDateInterval_Test
        extends ___DiscreteInterval_Test<_SampleLocalDateInterval, LocalDate> {

    _SampleLocalDateInterval_Test() {
        super(_SampleLocalDateInterval.class, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 5),
              ChronoUnit.DAYS, 4L);
    }
}
