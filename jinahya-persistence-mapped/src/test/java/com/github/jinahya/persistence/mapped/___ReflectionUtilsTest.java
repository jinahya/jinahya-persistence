package com.github.jinahya.persistence.mapped;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ___ReflectionUtilsTest {

    static class Some {

        boolean b = true;

        int i = 1;

        long l = 1L;

        char c = 'c';

        float f = .1f;

        double d = .1d;

        String name = "name";
    }

    @Nested
    class ResetTest {

        @Test
        void __() {
            final var some = new Some();
            assertThatCode(() -> {
                ___ReflectionUtils.reset(some);
            }).doesNotThrowAnyException();
            assertThat(some.b).isFalse();
            assertThat(some.c).isEqualTo('\0');
            assertThat(some.i).isZero();
            assertThat(some.l).isZero();
            assertThat(some.f).isZero();
            assertThat(some.d).isZero();
            assertThat(some.name).isNull();
        }
    }
}
