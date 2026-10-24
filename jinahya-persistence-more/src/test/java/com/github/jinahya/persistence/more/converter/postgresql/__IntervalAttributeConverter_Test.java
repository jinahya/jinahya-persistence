package com.github.jinahya.persistence.more.converter.postgresql;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __IntervalAttributeConverter_Test {

    /**
     * {@code 1 year 2 mons 3 days 04:05:06.5}, the one value every style below prints.
     */
    private static final __IntervalAttributeConverter.Fields FULL = new __IntervalAttributeConverter.Fields(
            1L, 2L, BigDecimal.valueOf(3L), new BigDecimal("14706.5"));

    private static __IntervalAttributeConverter.Fields parse(final String text) {
        return __IntervalAttributeConverter.parse(text);
    }

    private static Duration duration(final Object dbData) {
        return new __IntervalAttributeConverters.OfDuration().convertToEntityAttribute(dbData);
    }

    private static Period period(final Object dbData) {
        return new __IntervalAttributeConverters.OfPeriod().convertToEntityAttribute(dbData);
    }

    private static boolean sameFields(final __IntervalAttributeConverter.Fields a,
                                      final __IntervalAttributeConverter.Fields b) {
        return a.years() == b.years() && a.months() == b.months()
               && a.days().compareTo(b.days()) == 0 && a.seconds().compareTo(b.seconds()) == 0;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("parse: every style the server prints, one value")
    @Nested
    class Parse_Styles_Test {

        @DisplayName("intervalstyle = postgres (the default)")
        @Test
        void __postgres() {
            assertThat(parse("1 year 2 mons 3 days 04:05:06.5")).matches(f -> sameFields(f, FULL));
        }

        @DisplayName("intervalstyle = postgres_verbose")
        @Test
        void __postgresVerbose() {
            assertThat(parse("@ 1 year 2 mons 3 days 4 hours 5 mins 6.5 secs")).matches(f -> sameFields(f, FULL));
        }

        @DisplayName("intervalstyle = sql_standard")
        @Test
        void __sqlStandard() {
            // a value holding both year-month and day-time fields is printed in the "non-standard" signed form
            assertThat(parse("+1-2 +3 +4:05:06.5")).matches(f -> sameFields(f, FULL));
        }

        @DisplayName("intervalstyle = iso_8601")
        @Test
        void __iso8601() {
            assertThat(parse("P1Y2M3DT4H5M6.5S")).matches(f -> sameFields(f, FULL));
        }

        @DisplayName("org.postgresql.util.PGInterval#toString(): only the non-zero fields, '0 secs' for none")
        @Test
        void __pgInterval() {
            assertThat(parse("1 years 2 mons 3 days 4 hours 5 mins 6.5 secs")).matches(f -> sameFields(f, FULL));
            assertThat(parse("0 secs")).matches(__IntervalAttributeConverter.Fields::isZero);
            assertThat(parse("-1 days 2 hours 5 mins")).matches(
                    f -> f.days().compareTo(BigDecimal.ONE.negate()) == 0
                         && f.seconds().compareTo(BigDecimal.valueOf(7500L)) == 0);
            assertThat(parse("1 days -2 hours -5 mins").seconds()).isEqualByComparingTo("-7500");
            assertThat(parse("-1.5 secs").seconds()).isEqualByComparingTo("-1.5");
            assertThat(parse("0.000001 secs").seconds()).isEqualByComparingTo("0.000001");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("parse: signs")
    @Nested
    class Parse_Signs_Test {

        @DisplayName("postgres: each field signed on its own, the time part with an explicit + when mixed")
        @Test
        void __postgresMixed() {
            final var fields = parse("-1 days +02:05:00");
            assertThat(fields.days()).isEqualByComparingTo("-1");
            assertThat(fields.seconds()).isEqualByComparingTo("7500");
            assertThat(parse("1 day -02:05:00").seconds()).isEqualByComparingTo("-7500");
            assertThat(parse("-1 days -02:05:00")).matches(
                    f -> f.days().compareTo(BigDecimal.ONE.negate()) == 0 && f.seconds().compareTo(
                            BigDecimal.valueOf(-7500L)) == 0);
        }

        @DisplayName("postgres_verbose: 'ago' negates every field")
        @Test
        void __verboseAgo() {
            assertThat(parse("@ 1 day 2 hours ago")).matches(f -> sameFields(f, new __IntervalAttributeConverter.Fields(
                    0L, 0L, BigDecimal.ONE.negate(), BigDecimal.valueOf(-7200L))));
            // the first non-zero field sets the direction, a later one of the other sign is printed negative
            assertThat(parse("@ 1 day -2 hours ago")).matches(f -> sameFields(f, new __IntervalAttributeConverter.Fields(
                    0L, 0L, BigDecimal.ONE.negate(), BigDecimal.valueOf(7200L))));
        }

        @DisplayName("sql_standard: one leading sign applies to every field")
        @Test
        void __sqlStandardOneSign() {
            assertThat(parse("-1-2")).matches(f -> f.years() == -1L && f.months() == -2L);
            assertThat(parse("-3 4:05:06")).matches(
                    f -> f.days().compareTo(BigDecimal.valueOf(-3L)) == 0
                         && f.seconds().compareTo(BigDecimal.valueOf(-14706L)) == 0);
            assertThat(parse("-4:05:06").seconds()).isEqualByComparingTo("-14706");
            assertThat(parse("0")).matches(__IntervalAttributeConverter.Fields::isZero);
        }

        @DisplayName("sql_standard: a sign per field when the signs are mixed")
        @Test
        void __sqlStandardMixed() {
            assertThat(parse("+1-2 -3 +4:05:06")).matches(
                    f -> f.years() == 1L && f.months() == 2L
                         && f.days().compareTo(BigDecimal.valueOf(-3L)) == 0
                         && f.seconds().compareTo(BigDecimal.valueOf(14706L)) == 0);
        }

        @DisplayName("iso_8601: a sign before any number, or before P")
        @Test
        void __iso8601Signs() {
            assertThat(parse("P-1Y-2M-3DT-4H-5M-6.5S")).matches(f -> sameFields(f, FULL.negated()));
            assertThat(parse("-P1Y2M3DT4H5M6.5S")).matches(f -> sameFields(f, FULL.negated()));
            assertThat(parse("PT-0.5S").seconds()).isEqualByComparingTo("-0.5");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("parse: the rest of what the server accepts")
    @Nested
    class Parse_Misc_Test {

        @Test
        void __weeks() {
            assertThat(parse("2 weeks").days()).isEqualByComparingTo("14");
            assertThat(parse("P2W").days()).isEqualByComparingTo("14");
        }

        @Test
        void __unitAttached() {
            assertThat(parse("2hours").seconds()).isEqualByComparingTo("7200");
        }

        @DisplayName("the spellings the server accepts but never prints")
        @Test
        void __shortUnits() {
            assertThat(parse("1h").seconds()).isEqualByComparingTo("3600");
            assertThat(parse("2m").seconds()).isEqualByComparingTo("120"); // a minute, not a month
            assertThat(parse("3s").seconds()).isEqualByComparingTo("3");
            assertThat(parse("4d").days()).isEqualByComparingTo("4");
            assertThat(parse("1w").days()).isEqualByComparingTo("7");
            assertThat(parse("1y").years()).isEqualTo(1L);
            assertThat(parse("1 m 1 mon")).matches(f -> f.months() == 1L && f.seconds().compareTo(
                    BigDecimal.valueOf(60L)) == 0);
            assertThat(parse("1500 ms").seconds()).isEqualByComparingTo("1.5");
            assertThat(parse("1500ms").seconds()).isEqualByComparingTo("1.5");
            assertThat(parse("2 milliseconds").seconds()).isEqualByComparingTo("0.002");
            assertThat(parse("7 us").seconds()).isEqualByComparingTo("0.000007");
            assertThat(parse("3 microseconds").seconds()).isEqualByComparingTo("0.000003");
        }

        @Test
        void __fractionalUnit() {
            assertThat(parse("1.5 days").days()).isEqualByComparingTo("1.5");
        }

        @Test
        void __caseInsensitive() {
            assertThat(parse("1 YEAR 2 Mons").months()).isEqualTo(2L);
            assertThat(parse("pt1h").seconds()).isEqualByComparingTo("3600");
        }

        @Test
        void __zero() {
            assertThat(parse("00:00:00")).matches(__IntervalAttributeConverter.Fields::isZero);
            assertThat(parse("PT0S")).matches(__IntervalAttributeConverter.Fields::isZero);
            assertThat(parse("P0D")).matches(__IntervalAttributeConverter.Fields::isZero);
            assertThat(parse("@ 0")).matches(__IntervalAttributeConverter.Fields::isZero);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "P", "PT", "@", "ago", "x", "1 x", "1 day x", "1-2-3", "1:2:3:4", "--1 day"})
        void _IllegalArgumentException_NotAnInterval(final String text) {
            assertThatThrownBy(() -> parse(text)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void _IllegalArgumentException_FractionalMonths() {
            assertThatThrownBy(() -> parse("1.5 mons")).isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("OfDuration")
    @Nested
    class OfDuration_Test {

        @Test
        void _IsoText_Write() {
            final var converter = new __IntervalAttributeConverters.OfDuration();
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(Duration.ofHours(26L).plusMinutes(5L))).isEqualTo("PT26H5M");
            assertThat(converter.convertToDatabaseColumn(Duration.ofMillis(-1500L))).isEqualTo("PT-1.5S");
        }

        @Test
        void _DaysFolded_Read() {
            assertThat(duration("1 day 02:05:00")).isEqualTo(Duration.ofHours(26L).plusMinutes(5L));
            assertThat(duration("26:05:00")).isEqualTo(Duration.ofHours(26L).plusMinutes(5L));
            assertThat(duration("-1 days +02:05:00")).isEqualTo(Duration.ofHours(-24L).plusMinutes(125L));
        }

        @Test
        void _Fraction_Read() {
            assertThat(duration("00:00:01.5")).isEqualTo(Duration.ofMillis(1500L));
            assertThat(duration("-00:00:01.5")).isEqualTo(Duration.ofMillis(-1500L));
            assertThat(duration("00:00:00.000001")).isEqualTo(Duration.ofNanos(1000L));
        }

        @Test
        void _Passthrough_Duration() {
            final var attribute = Duration.ofMinutes(3L);
            assertThat(duration(attribute)).isSameAs(attribute);
        }

        @Test
        void _Null_Null() {
            assertThat(duration(null)).isNull();
        }

        @DisplayName("years and months are refused, not guessed at")
        @Test
        void _IllegalArgumentException_YearsOrMonths() {
            assertThatThrownBy(() -> duration("1 mon")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> duration("P1Y")).isInstanceOf(IllegalArgumentException.class);
            // a zero count of either is no problem
            assertThat(duration("0 years 0 mons 1 days 2 hours 5 mins 0.0 secs"))
                    .isEqualTo(Duration.ofHours(26L).plusMinutes(5L));
            // the driver's own text, as EclipseLink hands it over
            assertThat(duration("1 days 2 hours 5 mins")).isEqualTo(Duration.ofHours(26L).plusMinutes(5L));
            assertThat(duration("0 secs")).isEqualTo(Duration.ZERO);
        }

        @Test
        void __roundTrip() {
            final var converter = new __IntervalAttributeConverters.OfDuration();
            for (final var attribute : new Duration[] {
                    Duration.ZERO, Duration.ofHours(26L), Duration.ofMillis(-1500L), Duration.ofNanos(123_456_000L)
            }) {
                assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(attribute)))
                        .isEqualTo(attribute);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("OfPeriod")
    @Nested
    class OfPeriod_Test {

        @Test
        void _IsoText_Write() {
            final var converter = new __IntervalAttributeConverters.OfPeriod();
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(Period.of(1, 2, 3))).isEqualTo("P1Y2M3D");
            assertThat(converter.convertToDatabaseColumn(Period.ofYears(-1))).isEqualTo("P-1Y");
        }

        @Test
        void _Fields_Read() {
            assertThat(period("1 year 2 mons 3 days")).isEqualTo(Period.of(1, 2, 3));
            assertThat(period("1-2")).isEqualTo(Period.of(1, 2, 0));
            assertThat(period("45 days")).isEqualTo(Period.ofDays(45));
            assertThat(period("P14M")).isEqualTo(Period.ofMonths(14));
            assertThat(period("@ 1 year ago")).isEqualTo(Period.ofYears(-1));
        }

        @Test
        void _IllegalArgumentException_OutOfRange() {
            assertThatThrownBy(() -> period("3000000000 years")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void _Passthrough_Period() {
            final var attribute = Period.ofDays(3);
            assertThat(period(attribute)).isSameAs(attribute);
        }

        @DisplayName("a time part is refused")
        @Test
        void _IllegalArgumentException_Seconds() {
            assertThatThrownBy(() -> period("1 day 00:00:01")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> period("PT1H")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> period("1.5 days")).isInstanceOf(IllegalArgumentException.class);
            // a zero time part is fine; the default style prints one for a value of days only when asked
            assertThat(period("3 days 00:00:00")).isEqualTo(Period.ofDays(3));
        }

        @Test
        void __roundTrip() {
            final var converter = new __IntervalAttributeConverters.OfPeriod();
            for (final var attribute : new Period[] {
                    Period.ZERO, Period.of(1, 2, 3), Period.ofMonths(14), Period.ofDays(-45)
            }) {
                assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(attribute)))
                        .isEqualTo(attribute);
            }
        }
    }
}
