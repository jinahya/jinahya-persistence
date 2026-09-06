package com.github.jinahya.persistence.more;

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

/**
 * An abstract class for converting {@code String} db data to a specific type of entity attribute, and vice versa.
 *
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __StringAttributeConverter<X> implements AttributeConverter<X, String> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An abstract class for converting {@code String} db data to an entity attribute of aspecific subtype of
     * {@link Number}, and vice versa.
     *
     * @param <N> number type parameter
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    /**
     * The three {@code double}/{@code float} values which {@link BigDecimal} cannot represent, in the exact spelling
     * {@link Double#toString(double)} and {@link Float#toString(float)} both produce, and which
     * {@link Double#valueOf(String)} and {@link Float#valueOf(String)} both read back.
     */
    private static final java.util.Set<String> NON_FINITE = java.util.Set.of("NaN", "Infinity", "-Infinity");

    public abstract static class OfNumber<N extends Number> extends __StringAttributeConverter<N> {

        /**
         * Creates a new instance.
         */
        protected OfNumber() {
            super();
        }
    }

    /**
     * A converter for an entity attribute of {@link BigDecimal}.
     *
     * @implSpec A value is stored as the {@link BigDecimal#toPlainString() plain string} of the attribute,
     *         which keeps the value free of an exponent.
     */
    @Converter(autoApply = false)
    public static class OfBigDecimal extends OfNumber<BigDecimal> {

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
    public static class OfInteger extends OfNumber<Integer> {

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
    public static class OfLong extends OfNumber<Long> {

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
    public static class OfFloat extends OfNumber<Float> {

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
    public static class OfDouble extends OfNumber<Double> {

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

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __StringAttributeConverter() {
        super();
    }
}
