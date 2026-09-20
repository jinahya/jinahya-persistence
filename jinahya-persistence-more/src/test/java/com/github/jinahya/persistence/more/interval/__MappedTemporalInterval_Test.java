package com.github.jinahya.persistence.more.interval;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests every mapped superclass in this package against one set of expectations.
 * <p>
 * The classes differ only in their point type and in the amount they measure in, so the behaviour worth pinning is the
 * same for all of them: what a fresh instance holds, what the order of the two points does and does not decide, and what the two derived
 * values answer when a bound is absent. {@link Case} states that once, against a point type it is told nothing about
 * beyond two ordered samples, and each nested class supplies the samples.
 * <p>
 * A subclass which adds behaviour of its own adds tests of its own beside the inherited ones — see
 * {@link OfOffsetDateTime}, the one class here which compares instants rather than natural order.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __MappedTemporalInterval_Test {

    private static ValidatorFactory validatorFactory;

    @BeforeAll
    static void openValidatorFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    private static Validator validator() {
        return validatorFactory.getValidator();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * What every mapped superclass in this package is expected to do, written once.
     *
     * @param <I> the mapped superclass under test
     * @param <P> its point type
     */
    abstract static class Case<I extends __MappedTemporalInterval<P>, P extends Temporal & Comparable<? super P>> {

        private final Supplier<I> instantiator;

        private final P earlier;

        private final P later;

        private final TemporalUnit unit;

        private final long length;

        /**
         * Creates a new instance.
         *
         * @param instantiator supplies a fresh instance of a concrete subclass.
         * @param earlier      a point.
         * @param later        a point strictly after {@code earlier}.
         * @param unit         a unit the point type supports.
         * @param length       the number of complete {@code unit}s from {@code earlier} to {@code later}.
         */
        Case(final Supplier<I> instantiator, final P earlier, final P later, final TemporalUnit unit,
             final long length) {
            this.instantiator = instantiator;
            this.earlier = earlier;
            this.later = later;
            this.unit = unit;
            this.length = length;
        }

        private I interval(final P start, final P end) {
            final I instance = instantiator.get();
            instance.setIntervalStart(start);
            instance.setIntervalEnd(end);
            return instance;
        }

        private boolean valid(final I instance) {
            return validator().validate(instance).isEmpty();
        }

        // ------------------------------------------------------------------------------------------------- the points

        @DisplayName("a new instance holds neither point")
        @Test
        void _bothAbsent_new() {
            final I instance = instantiator.get();
            assertThat(instance.getIntervalStart()).isNull();
            assertThat(instance.getIntervalEnd()).isNull();
        }

        @DisplayName("each point is returned as it was set, including back to null")
        @Test
        void _roundTrip_setThenGet() {
            final I instance = interval(earlier, later);
            assertThat(instance.getIntervalStart()).isEqualTo(earlier);
            assertThat(instance.getIntervalEnd()).isEqualTo(later);
            instance.setIntervalStart(null);
            instance.setIntervalEnd(null);
            assertThat(instance.getIntervalStart()).isNull();
            assertThat(instance.getIntervalEnd()).isNull();
        }

        // ------------------------------------------------------------------------------------------ the absent order

        @DisplayName("an absent bound cannot be out of order, so every unbounded shape is accepted")
        @Test
        void _accepted_anyBoundAbsent() {
            assertThat(valid(interval(null, null))).isTrue();
            assertThat(valid(interval(earlier, null))).isTrue();
            assertThat(valid(interval(null, later))).isTrue();
        }

        @DisplayName("a start before its end is accepted")
        @Test
        void _accepted_ordered() {
            assertThat(valid(interval(earlier, later))).isTrue();
        }

        @DisplayName("two points which compare equal are accepted: empty is a shape, not a violation")
        @Test
        void _accepted_empty() {
            assertThat(valid(interval(earlier, earlier))).isTrue();
        }

        @DisplayName("a start after its end is accepted too: the order is documented, not validated")
        @Test
        void _accepted_inverted() {
            assertThat(valid(interval(later, earlier))).isTrue();
        }

        // ---------------------------------------------------------------------------------- what the two points decide

        @DisplayName("an interval is bounded only when it holds both points")
        @Test
        void _bounded_bothPresent() {
            assertThat(interval(earlier, later).isBounded()).isTrue();
            assertThat(interval(earlier, null).isBounded()).isFalse();
            assertThat(interval(null, later).isBounded()).isFalse();
            assertThat(interval(null, null).isBounded()).isFalse();
        }

        @DisplayName("an interval is empty exactly when its two points coincide")
        @Test
        void _empty_pointsCoincide() {
            assertThat(interval(earlier, earlier).isEmpty()).isTrue();
            assertThat(interval(earlier, later).isEmpty()).isFalse();
            assertThat(interval(earlier, null).isEmpty()).isFalse();
            assertThat(interval(null, null).isEmpty()).isFalse();
        }

        @DisplayName("the start belongs to the interval and the end does not")
        @Test
        void _contains_halfOpen() {
            final I bounded = interval(earlier, later);
            assertThat(bounded.contains(earlier)).as("the start is inclusive").isTrue();
            assertThat(bounded.contains(later)).as("the end is exclusive").isFalse();
        }

        @DisplayName("an absent bound holds, so an unbounded interval contains everything")
        @Test
        void _contains_absentBoundHolds() {
            assertThat(interval(null, null).contains(earlier)).isTrue();
            assertThat(interval(null, null).contains(later)).isTrue();
            assertThat(interval(earlier, null).contains(later)).isTrue();
            assertThat(interval(null, later).contains(earlier)).isTrue();
            assertThat(interval(later, null).contains(earlier)).as("before an open-ended start").isFalse();
            assertThat(interval(null, earlier).contains(later)).as("after an open-ended end").isFalse();
        }

        @DisplayName("an empty interval contains no point at all")
        @Test
        void _contains_emptyContainsNothing() {
            assertThat(interval(earlier, earlier).contains(earlier)).isFalse();
        }

        @DisplayName("a null point is refused rather than answered")
        @Test
        void _contains_nullRefused() {
            assertThatThrownBy(() -> interval(earlier, later).contains(null))
                    .isInstanceOf(NullPointerException.class);
        }

        // ------------------------------------------------------------------------------------------------- the amount

        @DisplayName("the amount is absent unless both points are")
        @Test
        void _amountNull_anyBoundAbsent() {
            assertThat(interval(null, null).getTemporalAmount()).isNull();
            assertThat(interval(earlier, null).getTemporalAmount()).isNull();
            assertThat(interval(null, later).getTemporalAmount()).isNull();
        }

        @DisplayName("an empty interval measures zero in every unit its amount carries")
        @Test
        void _amountZero_empty() {
            final var amount = interval(earlier, earlier).getTemporalAmount();
            assertThat(amount).isNotNull();
            assertThat(amount.getUnits()).allSatisfy(u -> assertThat(amount.get(u)).isZero());
        }

        @DisplayName("a bounded interval measures something")
        @Test
        void _amountPresent_bounded() {
            assertThat(interval(earlier, later).getTemporalAmount()).isNotNull();
        }

        // ------------------------------------------------------------------------------------------------- the length

        @DisplayName("the length is empty unless both points are present")
        @Test
        void _lengthEmpty_anyBoundAbsent() {
            assertThat(interval(null, null).lengthIn(unit)).isEmpty();
            assertThat(interval(earlier, null).lengthIn(unit)).isEmpty();
            assertThat(interval(null, later).lengthIn(unit)).isEmpty();
        }

        @DisplayName("the length counts whole units from the start to the end")
        @Test
        void _lengthCounted_bounded() {
            assertThat(interval(earlier, later).lengthIn(unit)).hasValue(length);
            assertThat(interval(earlier, earlier).lengthIn(unit)).hasValue(0L);
        }

        // ------------------------------------------------------------------------------------------------- the string

        @DisplayName("toString writes the half-open form, with an absent bound as null")
        @Test
        void _halfOpenNotation_toString() {
            assertThat(interval(earlier, later)).hasToString('[' + earlier.toString() + ", " + later + ')');
            assertThat(interval(null, null)).hasToString("[null, null)");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nested
    class OfLocalDate extends Case<OfLocalDate.Iv, LocalDate> {

        static final class Iv extends __MappedLocalDateInterval {

        }

        OfLocalDate() {
            super(Iv::new, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 5), ChronoUnit.DAYS, 4L);
        }
    }

    @Nested
    class OfLocalTime extends Case<OfLocalTime.Iv, LocalTime> {

        static final class Iv extends __MappedLocalTimeInterval {

        }

        OfLocalTime() {
            super(Iv::new, LocalTime.of(9, 0), LocalTime.of(17, 0), ChronoUnit.MINUTES, 480L);
        }
    }

    @Nested
    class OfLocalDateTime extends Case<OfLocalDateTime.Iv, LocalDateTime> {

        static final class Iv extends __MappedLocalDateTimeInterval {

        }

        OfLocalDateTime() {
            super(Iv::new, LocalDateTime.of(2026, 1, 1, 9, 0), LocalDateTime.of(2026, 1, 1, 17, 0),
                  ChronoUnit.HOURS, 8L);
        }
    }

    @Nested
    class OfInstant extends Case<OfInstant.Iv, Instant> {

        static final class Iv extends __MappedInstantInterval {

        }

        OfInstant() {
            super(Iv::new, Instant.EPOCH, Instant.EPOCH.plusSeconds(3600L), ChronoUnit.SECONDS, 3600L);
        }
    }

    @Nested
    class OfYear extends Case<OfYear.Iv, Year> {

        static final class Iv extends __MappedYearInterval {

        }

        OfYear() {
            super(Iv::new, Year.of(2026), Year.of(2029), ChronoUnit.YEARS, 3L);
        }
    }

    @Nested
    class OfYearMonth extends Case<OfYearMonth.Iv, YearMonth> {

        static final class Iv extends __MappedYearMonthInterval {

        }

        OfYearMonth() {
            super(Iv::new, YearMonth.of(2026, 1), YearMonth.of(2027, 3), ChronoUnit.MONTHS, 14L);
        }
    }

    /**
     * What a discrete axis affords, and what a continuous one does not.
     */
    @Nested
    class DiscreteAxisTest {

        @DisplayName("the last point contained is one step before the exclusive end")
        @Test
        void _endInclusive_oneStepBack() {
            final var dates = new OfLocalDate.Iv();
            dates.setIntervalStart(LocalDate.of(2026, 1, 1));
            dates.setIntervalEnd(LocalDate.of(2026, 1, 5));
            assertThat(dates.getIntervalEndInclusive()).isEqualTo(LocalDate.of(2026, 1, 4));

            final var years = new OfYear.Iv();
            years.setIntervalStart(Year.of(2026));
            years.setIntervalEnd(Year.of(2029));
            assertThat(years.getIntervalEndInclusive()).isEqualTo(Year.of(2028));

            final var months = new OfYearMonth.Iv();
            months.setIntervalStart(YearMonth.of(2026, 1));
            months.setIntervalEnd(YearMonth.of(2027, 3));
            assertThat(months.getIntervalEndInclusive()).isEqualTo(YearMonth.of(2027, 2));
        }

        @DisplayName("the granularity is the step, so a length counted in it is a count of steps")
        @Test
        void _granularity_countsSteps() {
            final var dates = new OfLocalDate.Iv();
            dates.setIntervalStart(LocalDate.of(2026, 1, 1));
            dates.setIntervalEnd(LocalDate.of(2026, 1, 5));
            assertThat(dates.getGranularity()).isEqualTo(ChronoUnit.DAYS);
            assertThat(dates.lengthIn(dates.getGranularity())).hasValue(4L);

            final var years = new OfYear.Iv();
            years.setIntervalStart(Year.of(2026));
            years.setIntervalEnd(Year.of(2029));
            assertThat(years.getGranularity()).isEqualTo(ChronoUnit.YEARS);
            assertThat(years.lengthIn(years.getGranularity())).hasValue(3L);

            final var months = new OfYearMonth.Iv();
            months.setIntervalStart(YearMonth.of(2026, 1));
            months.setIntervalEnd(YearMonth.of(2027, 3));
            assertThat(months.getGranularity()).isEqualTo(ChronoUnit.MONTHS);
            assertThat(months.lengthIn(months.getGranularity())).hasValue(14L);
        }

        @DisplayName("one step back from the end is the last point, and one step on from it is the end")
        @Test
        void _endInclusive_roundTripsByOneStep() {
            final var months = new OfYearMonth.Iv();
            months.setIntervalStart(YearMonth.of(2026, 1));
            months.setIntervalEnd(YearMonth.of(2027, 3));
            final var last = months.getIntervalEndInclusive();
            assertThat(last).isNotNull();
            assertThat(last.plus(1L, months.getGranularity()))
                    .as("the exclusive end is exactly one step past the last point contained")
                    .isEqualTo(months.getIntervalEnd());
            assertThat(months.contains(last)).isTrue();
        }

        @DisplayName("a continuous interval is not a discrete one, and the type says so")
        @Test
        void _notDiscrete_continuousTypes() {
            assertThat(new OfInstant.Iv()).isNotInstanceOf(__DiscreteInterval.class);
            assertThat(new OfLocalTime.Iv()).isNotInstanceOf(__DiscreteInterval.class);
            assertThat(new OfLocalDateTime.Iv()).isNotInstanceOf(__DiscreteInterval.class);
            assertThat(new OfOffsetDateTime.Iv()).isNotInstanceOf(__DiscreteInterval.class);
            assertThat(new OfLocalDate.Iv()).isInstanceOf(__DiscreteInterval.class);
            assertThat(new OfYear.Iv()).isInstanceOf(__DiscreteInterval.class);
            assertThat(new OfYearMonth.Iv()).isInstanceOf(__DiscreteInterval.class);
        }

        @DisplayName("there is no last point where there is no end, nor where the interval is empty")
        @Test
        void _endInclusiveNull_unboundedOrEmpty() {
            final var open = new OfLocalDate.Iv();
            open.setIntervalStart(LocalDate.of(2026, 1, 1));
            assertThat(open.getIntervalEndInclusive()).isNull();

            final var empty = new OfYearMonth.Iv();
            empty.setIntervalStart(YearMonth.of(2026, 1));
            empty.setIntervalEnd(YearMonth.of(2026, 1));
            assertThat(empty.getIntervalEndInclusive()).isNull();
        }
    }

    @Nested
    class OfOffsetDateTime extends Case<OfOffsetDateTime.Iv, OffsetDateTime> {

        static final class Iv extends __MappedOffsetDateTimeInterval {

        }

        OfOffsetDateTime() {
            super(Iv::new, OffsetDateTime.of(2026, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC),
                  OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC), ChronoUnit.HOURS, 3L);
        }

        // the two points below name one moment, written in two offsets. compareTo reads them as ordered, the column
        // they are stored in reads them as equal, and this class is the one which sides with the column.

        private static final OffsetDateTime AT_UTC = OffsetDateTime.of(2026, 9, 20, 8, 0, 0, 0, ZoneOffset.UTC);

        private static final OffsetDateTime SAME_MOMENT_AT_PLUS_TWO =
                OffsetDateTime.of(2026, 9, 20, 10, 0, 0, 0, ZoneOffset.ofHours(2));

        private Iv interval(final OffsetDateTime start, final OffsetDateTime end) {
            final Iv instance = new Iv();
            instance.setIntervalStart(start);
            instance.setIntervalEnd(end);
            return instance;
        }

        @DisplayName("the two samples really are one moment, and really do compare as distinct")
        @Test
        void _premise_sameMomentComparesDistinct() {
            assertThat(SAME_MOMENT_AT_PLUS_TWO.toInstant()).isEqualTo(AT_UTC.toInstant());
            assertThat(SAME_MOMENT_AT_PLUS_TWO.compareTo(AT_UTC)).isPositive();
        }

        @DisplayName("an interval whose ends name one moment in two offsets is empty, and is accepted")
        @Test
        void _accepted_emptyAcrossOffsets() {
            final Iv instance = interval(SAME_MOMENT_AT_PLUS_TWO, AT_UTC);
            assertThat(validator().validate(instance))
                    .as("nothing here validates the order of the two points")
                    .isEmpty();
            assertThat(instance.getTemporalAmount()).isEqualTo(Duration.ZERO);
        }

        @DisplayName("a genuinely inverted interval is accepted too, and is inert: it contains nothing")
        @Test
        void _inert_invertedAcrossOffsets() {
            final var inverted = interval(AT_UTC.plusHours(5L), SAME_MOMENT_AT_PLUS_TWO);
            assertThat(validator().validate(inverted)).isEmpty();
            assertThat(inverted.contains(AT_UTC)).isFalse();
            assertThat(inverted.contains(AT_UTC.plusHours(5L))).isFalse();
        }

        @DisplayName("an interval whose ends name one moment in two offsets is empty, not merely short")
        @Test
        void _empty_acrossOffsets() {
            assertThat(interval(SAME_MOMENT_AT_PLUS_TWO, AT_UTC).isEmpty())
                    .as("the inherited compareTo order would answer false here")
                    .isTrue();
        }

        @DisplayName("containment is decided on the instants, whatever offsets are written")
        @Test
        void _contains_onInstants() {
            final Iv iv = interval(AT_UTC, AT_UTC.plusHours(3L));
            assertThat(iv.contains(SAME_MOMENT_AT_PLUS_TWO))
                    .as("the start, spelled in another offset, is still the start and still included")
                    .isTrue();
            assertThat(iv.contains(SAME_MOMENT_AT_PLUS_TWO.plusHours(3L)))
                    .as("the end, spelled in another offset, is still the end and still excluded")
                    .isFalse();
        }

        @DisplayName("the length is measured on the instants, so the offsets do not affect it")
        @Test
        void _lengthOnInstants_acrossOffsets() {
            assertThat(interval(SAME_MOMENT_AT_PLUS_TWO, AT_UTC.plusHours(3L)).getTemporalAmount())
                    .isEqualTo(Duration.ofHours(3L));
        }
    }
}
