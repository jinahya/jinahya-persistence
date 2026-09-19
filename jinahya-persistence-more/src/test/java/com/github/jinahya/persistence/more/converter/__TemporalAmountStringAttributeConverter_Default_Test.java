package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for what {@link __TemporalAmountStringAttributeConverter} itself does, for a subtype which overrides neither
 * direction.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This one does not extend {@link __TemporalAmountStringAttributeConverter_Test}: its subject is an
 *         anonymous subclass, which no {@code Class} literal can name.
 */
@DisplayName("the default read finds a static parse(CharSequence)")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __TemporalAmountStringAttributeConverter_Default_Test {

    private final __TemporalAmountStringAttributeConverter<_Ticks> converter =
            new __TemporalAmountStringAttributeConverter<>(_Ticks.class) {
            };

    @DisplayName("a type with a static parse(CharSequence) round-trips, with nothing overridden")
    @Test
    void __roundTrip() {
        final var ticks = new _Ticks(42L);
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(ticks)))
                .isEqualTo(ticks);
    }

    @DisplayName("null <-> null, without touching the parse method")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("what parse throws is thrown unchanged, not wrapped in a reflection exception")
    @Test
    void __parseFailure() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("not a number"))
                .isInstanceOf(NumberFormatException.class);
    }

    @DisplayName("a type without a static parse(CharSequence) fails only when the default read is used")
    @Test
    void __noParseMethod() {
        // constructing it is fine, and so is the write; a subclass overriding the read never needs a parse method
        final var noParse = new __TemporalAmountStringAttributeConverter<_Opaque>(_Opaque.class) {
        };
        assertThat(noParse.convertToDatabaseColumn(null)).isNull();
        assertThat(noParse.convertToEntityAttribute(null)).isNull();
        assertThatThrownBy(() -> noParse.convertToEntityAttribute("anything"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("parse(CharSequence)");
    }

    @DisplayName("the two forms overlap, which is why there is no converter for TemporalAmount itself")
    @Test
    void __overlap() {
        // 'P2D' is a valid spelling of both, and the two are not the same amount
        assertThat(Duration.parse("P2D")).isEqualTo(Duration.ofHours(48L));
        assertThat(Period.parse("P2D")).isEqualTo(Period.ofDays(2));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A temporal amount which is neither a {@link Duration} nor a {@link Period}, and which spells itself in a form its
     * own {@code parse(CharSequence)} reads back &mdash; the shape the default read is for.
     *
     * @param seconds the number of seconds.
     */
    public record _Ticks(long seconds) implements TemporalAmount {

        /**
         * Reads back what {@link #toString()} wrote.
         *
         * @param text the text to parse.
         * @return a new instance.
         */
        public static _Ticks parse(final CharSequence text) {
            return new _Ticks(Long.parseLong(text.toString()));
        }

        @Override
        public String toString() {
            return Long.toString(seconds);
        }

        @Override
        public long get(final TemporalUnit unit) {
            return Duration.ofSeconds(seconds).get(unit);
        }

        @Override
        public List<TemporalUnit> getUnits() {
            return Duration.ofSeconds(seconds).getUnits();
        }

        @Override
        public Temporal addTo(final Temporal temporal) {
            return Duration.ofSeconds(seconds).addTo(temporal);
        }

        @Override
        public Temporal subtractFrom(final Temporal temporal) {
            return Duration.ofSeconds(seconds).subtractFrom(temporal);
        }
    }

    /**
     * A temporal amount which declares no {@code parse(CharSequence)} at all.
     */
    public record _Opaque() implements TemporalAmount {

        @Override
        public long get(final TemporalUnit unit) {
            return 0L;
        }

        @Override
        public List<TemporalUnit> getUnits() {
            return List.of();
        }

        @Override
        public Temporal addTo(final Temporal temporal) {
            return temporal;
        }

        @Override
        public Temporal subtractFrom(final Temporal temporal) {
            return temporal;
        }
    }
}
