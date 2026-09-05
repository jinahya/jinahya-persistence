package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
     * @implSpec A value is stored as the {@link BigDecimal#toPlainString() plain string} of the attribute, which keeps the value free of an exponent.
     */
    @Converter(autoApply = false)
    public static class OfBigDecimal extends OfNumber<BigDecimal> {

        /**
         * Creates a new instance.
         */
        protected OfBigDecimal() {
            super();
        }

        @Override
        public String convertToDatabaseColumn(final BigDecimal attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toPlainString();
        }

        @Override
        public BigDecimal convertToEntityAttribute(final String dbData) {
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
        protected OfInteger() {
            super();
        }

        @Override
        public String convertToDatabaseColumn(final Integer attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toString();
        }

        @Override
        public Integer convertToEntityAttribute(final String dbData) {
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
        protected OfLong() {
            super();
        }

        @Override
        public String convertToDatabaseColumn(final Long attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toString();
        }

        @Override
        public Long convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return Long.valueOf(dbData);
        }
    }

    /**
     * A converter for an entity attribute of {@link Float}.
     *
     * @implSpec A value is widened to a {@link BigDecimal} and delegated to {@link OfBigDecimal}, so that it is stored
     * without an exponent; note that the round-trip is that of {@link BigDecimal#valueOf(double)}, and is not
     * bit-exact for every value.
     */
    @Converter(autoApply = false)
    public static class OfFloat extends OfNumber<Float> {

        /**
         * Creates a new instance.
         */
        protected OfFloat() {
            super();
            delegate = new OfBigDecimal();
        }

        @Override
        public String convertToDatabaseColumn(final Float attribute) {
            if (attribute == null) {
                return null;
            }
            return delegate.convertToDatabaseColumn(BigDecimal.valueOf(attribute));
        }

        @Override
        public Float convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return delegate.convertToEntityAttribute(dbData).floatValue();
        }

        private final OfBigDecimal delegate;
    }

    /**
     * A converter for an entity attribute of {@link Double}.
     *
     * @implSpec A value is widened to a {@link BigDecimal} and delegated to {@link OfBigDecimal}, so that it is stored
     * without an exponent; note that the round-trip is that of {@link BigDecimal#valueOf(double)}, and is not
     * bit-exact for every value.
     */
    @Converter(autoApply = false)
    public static class OfDouble extends OfNumber<Double> {

        /**
         * Creates a new instance.
         */
        protected OfDouble() {
            super();
            delegate = new OfBigDecimal();
        }

        @Override
        public String convertToDatabaseColumn(final Double attribute) {
            if (attribute == null) {
                return null;
            }
            return delegate.convertToDatabaseColumn(BigDecimal.valueOf(attribute));
        }

        @Override
        public Double convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return delegate.convertToEntityAttribute(dbData).doubleValue();
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
