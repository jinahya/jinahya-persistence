package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __NumberStringAttributeConverters.OfFloat}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfFloat")
class __NumberStringAttributeConverters_OfFloat_Test
        extends __NumberStringAttributeConverter_Test<__NumberStringAttributeConverters.OfFloat, Float> {

    __NumberStringAttributeConverters_OfFloat_Test() {
        super(__NumberStringAttributeConverters.OfFloat.class, Float.class);
    }

    private final __NumberStringAttributeConverters.OfFloat converter = new __NumberStringAttributeConverters.OfFloat();

    @DisplayName("an ordinary value round-trips")
    @ParameterizedTest
    @ValueSource(floats = {0.0f, 1.0f, -1.0f, 0.5f, 1234.5f})
    void __ordinary(final float value) {
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(value)))
                .isEqualTo(value);
    }

    @DisplayName("NaN and the infinities round-trip instead of throwing")
    @ParameterizedTest
    @ValueSource(floats = {Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY})
    void __nonFinite(final float value) {
        final var column = converter.convertToDatabaseColumn(value);
        // boxed on purpose: AssertJ compares a primitive expected with ==, and NaN != NaN
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(Float.valueOf(value));
    }
}
