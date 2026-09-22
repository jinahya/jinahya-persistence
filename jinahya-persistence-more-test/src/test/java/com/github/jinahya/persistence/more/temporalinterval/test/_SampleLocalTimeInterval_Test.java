package com.github.jinahya.persistence.more.temporalinterval.test;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

class _SampleLocalTimeInterval_Test
        extends ___MappedTemporalInterval_Test<_SampleLocalTimeInterval, LocalTime> {

    _SampleLocalTimeInterval_Test() {
        super(_SampleLocalTimeInterval.class, LocalTime.of(9, 0), LocalTime.of(17, 0), ChronoUnit.MINUTES, 480L);
    }
}
