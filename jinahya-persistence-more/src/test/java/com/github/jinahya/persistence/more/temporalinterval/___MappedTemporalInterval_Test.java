package com.github.jinahya.persistence.more.temporalinterval;

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
import java.time.temporal.Temporal;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests what these mapped superclasses do which a downstream implementation is not also expected to do.
 * <p>
 * The interval contract itself — what a fresh instance holds, what the two points decide, what the derived values
 * answer when a bound is absent — is no longer stated here. It is
 * {@code com.github.jinahya.persistence.more.test}'s {@code ___MappedTemporalInterval_Test}, in
 * {@code jinahya-persistence-more-test}, which runs it against all seven classes of this package from that module's
 * own tests. Stating it in both places meant maintaining it in both, and the copy which a downstream also inherits is
 * the one worth keeping.
 * <p>
 * What stays is what that base deliberately cannot carry. {@link Case} keeps the assertions which need a
 * {@link jakarta.validation.Validator} — the published test layer takes no validation implementation — and the
 * {@code toString} form. {@link DiscreteAxisTest} keeps the structural claim about which point types are discrete,
 * which is a fact about this package rather than about any one interval. {@link OfOffsetDateTime} keeps its own,
 * being the one class here which compares instants rather than natural order.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class ___MappedTemporalInterval_Test {

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
    abstract static class Case<I extends ___MappedTemporalInterval<P>, P extends Temporal & Comparable<? super P>> {

        private final Supplier<I> instantiator;

        private final P earlier;

        private final P later;

        /**
         * Creates a new instance.
         *
         * @param instantiator supplies a fresh instance of a concrete subclass.
         * @param earlier      a point.
         * @param later        a point strictly after {@code earlier}.
         */
        Case(final Supplier<I> instantiator, final P earlier, final P later) {
            this.instantiator = instantiator;
            this.earlier = earlier;
            this.later = later;
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

        // ------------------------------------------------------------------------------------------------- the amount

        // ------------------------------------------------------------------------------------------------- the string

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
            super(Iv::new, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 5));
        }
    }

    @Nested
    class OfLocalTime extends Case<OfLocalTime.Iv, LocalTime> {

        static final class Iv extends __MappedLocalTimeInterval {

        }

        OfLocalTime() {
            super(Iv::new, LocalTime.of(9, 0), LocalTime.of(17, 0));
        }
    }

    @Nested
    class OfLocalDateTime extends Case<OfLocalDateTime.Iv, LocalDateTime> {

        static final class Iv extends __MappedLocalDateTimeInterval {

        }

        OfLocalDateTime() {
            super(Iv::new, LocalDateTime.of(2026, 1, 1, 9, 0), LocalDateTime.of(2026, 1, 1, 17, 0));
        }
    }

    @Nested
    class OfInstant extends Case<OfInstant.Iv, Instant> {

        static final class Iv extends __MappedInstantInterval {

        }

        OfInstant() {
            super(Iv::new, Instant.EPOCH, Instant.EPOCH.plusSeconds(3600L));
        }
    }

    @Nested
    class OfYear extends Case<OfYear.Iv, Year> {

        static final class Iv extends __MappedYearInterval {

        }

        OfYear() {
            super(Iv::new, Year.of(2026), Year.of(2029));
        }
    }

    @Nested
    class OfYearMonth extends Case<OfYearMonth.Iv, YearMonth> {

        static final class Iv extends __MappedYearMonthInterval {

        }

        OfYearMonth() {
            super(Iv::new, YearMonth.of(2026, 1), YearMonth.of(2027, 3));
        }
    }

    /**
     * What a discrete axis affords, and what a continuous one does not.
     */
    @Nested
    class DiscreteAxisTest {

        @DisplayName("a continuous interval is not a discrete one, and the type says so")
        @Test
        void _notDiscrete_continuousTypes() {
            assertThat(new OfInstant.Iv()).isNotInstanceOf(___DiscreteInterval.class);
            assertThat(new OfLocalTime.Iv()).isNotInstanceOf(___DiscreteInterval.class);
            assertThat(new OfLocalDateTime.Iv()).isNotInstanceOf(___DiscreteInterval.class);
            assertThat(new OfOffsetDateTime.Iv()).isNotInstanceOf(___DiscreteInterval.class);
            assertThat(new OfLocalDate.Iv()).isInstanceOf(___DiscreteInterval.class);
            assertThat(new OfYear.Iv()).isInstanceOf(___DiscreteInterval.class);
            assertThat(new OfYearMonth.Iv()).isInstanceOf(___DiscreteInterval.class);
        }
    }

    @Nested
    class OfOffsetDateTime extends Case<OfOffsetDateTime.Iv, OffsetDateTime> {

        static final class Iv extends __MappedOffsetDateTimeInterval {

        }

        OfOffsetDateTime() {
            super(Iv::new, OffsetDateTime.of(2026, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC),
                  OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC));
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
