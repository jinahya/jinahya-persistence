package com.github.jinahya.persistence.more.converter;

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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Concrete {@link __NumberStringAttributeConverter}s, one per {@link Number} type which has an exact decimal spelling
 * and reads back from it.
 * <p>
 * Every one of them keeps the digits rather than the bits: a value is written as text and parsed back, so what the
 * column holds is what a person reading the table sees. {@link OfFloat} and {@link OfDouble} are the two which cannot
 * promise that entirely, and say so.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __NumberStringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __NumberStringAttributeConverters {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The three {@code double}/{@code float} values which {@link BigDecimal} cannot represent, in the exact spelling
     * {@link Double#toString(double)} and {@link Float#toString(float)} both produce, and which
     * {@link Double#valueOf(String)} and {@link Float#valueOf(String)} both read back.
     */
    private static final Set<String> NON_FINITE = Set.of("NaN", "Infinity", "-Infinity");

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link BigDecimal}.
     *
     * @implSpec A value is stored as the {@link BigDecimal#toPlainString() plain string} of the attribute,
     *         which keeps the value free of an exponent.
     */
    @Converter(autoApply = false)
    public static class OfBigDecimal extends __NumberStringAttributeConverter<BigDecimal>
            implements AttributeConverter<BigDecimal, String> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor. It also
         *         lets a caller use this converter directly, without subclassing a leaf class.
         */
        public OfBigDecimal() {
            super();
        }

        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable BigDecimal attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toPlainString();
        }

        @Override
        public @Nullable BigDecimal convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            return new BigDecimal(dbData);
        }
    }

    /**
     * A converter for an entity attribute of {@link Integer}.
     *
     * @implSpec A value is stored as the {@link Integer#toString() decimal string} of the attribute.
     */
    @Converter(autoApply = false)
    public static class OfInteger extends __NumberStringAttributeConverter<Integer>
            implements AttributeConverter<Integer, String> {

        /**
         * Creates a new instance.
         */
        public OfInteger() {
            super();
        }

        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable Integer attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toString();
        }

        @Override
        public @Nullable Integer convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            return Integer.valueOf(dbData);
        }
    }

    /**
     * A converter for an entity attribute of {@link Long}.
     *
     * @implSpec A value is stored as the {@link Long#toString() decimal string} of the attribute.
     */
    @Converter(autoApply = false)
    public static class OfLong extends __NumberStringAttributeConverter<Long>
            implements AttributeConverter<Long, String> {

        /**
         * Creates a new instance.
         */
        public OfLong() {
            super();
        }

        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable Long attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toString();
        }

        @Override
        public @Nullable Long convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            return Long.valueOf(dbData);
        }
    }

    /**
     * A converter for an entity attribute of {@link Float}.
     *
     * @implSpec A value is widened to a {@link BigDecimal} and delegated to {@link OfBigDecimal}, so that it is
     *         stored without an exponent; note that the round-trip is that of {@link BigDecimal#valueOf(double)}, and
     *         is not bit-exact for every value. {@link Double#NaN NaN} and the two infinities have no
     *         {@link BigDecimal} form, so they bypass the delegate and are stored as {@code "NaN"}, {@code "Infinity"}
     *         and {@code "-Infinity"}; they round-trip exactly. Negative zero does not: {@link BigDecimal} has no
     *         signed zero, so {@code -0.0} reads back as {@code 0.0}.
     */
    @Converter(autoApply = false)
    public static class OfFloat extends __NumberStringAttributeConverter<Float>
            implements AttributeConverter<Float, String> {

        /**
         * Creates a new instance.
         */
        public OfFloat() {
            super();
            delegate = new OfBigDecimal();
        }

        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable Float attribute) {
            if (attribute == null) {
                return null;
            }
            if (!Float.isFinite(attribute)) {
                // BigDecimal has no representation for these, so routing them through it threw
                // NumberFormatException on a perfectly ordinary float
                return attribute.toString();
            }
            return delegate.convertToDatabaseColumn(BigDecimal.valueOf(attribute));
        }

        @Override
        public @Nullable Float convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            if (NON_FINITE.contains(dbData)) {
                return Float.valueOf(dbData);
            }
            final var value = delegate.convertToEntityAttribute(dbData);
            return value == null ? null : value.floatValue();
        }

        private final OfBigDecimal delegate;
    }

    /**
     * A converter for an entity attribute of {@link Double}.
     *
     * @implSpec A value is widened to a {@link BigDecimal} and delegated to {@link OfBigDecimal}, so that it is
     *         stored without an exponent; note that the round-trip is that of {@link BigDecimal#valueOf(double)}, and
     *         is not bit-exact for every value. {@link Double#NaN NaN} and the two infinities have no
     *         {@link BigDecimal} form, so they bypass the delegate and are stored as {@code "NaN"}, {@code "Infinity"}
     *         and {@code "-Infinity"}; they round-trip exactly. Negative zero does not: {@link BigDecimal} has no
     *         signed zero, so {@code -0.0} reads back as {@code 0.0}.
     */
    @Converter(autoApply = false)
    public static class OfDouble extends __NumberStringAttributeConverter<Double>
            implements AttributeConverter<Double, String> {

        /**
         * Creates a new instance.
         */
        public OfDouble() {
            super();
            delegate = new OfBigDecimal();
        }

        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable Double attribute) {
            if (attribute == null) {
                return null;
            }
            if (!Double.isFinite(attribute)) {
                // BigDecimal has no representation for these, so routing them through it threw
                // NumberFormatException on a perfectly ordinary double
                return attribute.toString();
            }
            return delegate.convertToDatabaseColumn(BigDecimal.valueOf(attribute));
        }

        @Override
        public @Nullable Double convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            if (NON_FINITE.contains(dbData)) {
                return Double.valueOf(dbData);
            }
            final var value = delegate.convertToEntityAttribute(dbData);
            return value == null ? null : value.doubleValue();
        }

        private final OfBigDecimal delegate;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __NumberStringAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
