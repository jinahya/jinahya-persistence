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

        protected OfNumber() {
            super();
        }
    }

    @Converter(autoApply = false)
    public static class OfBigDecimal extends OfNumber<BigDecimal> {

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

    @Converter(autoApply = false)
    public static class OfInteger extends OfNumber<Integer> {

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

    @Converter(autoApply = false)
    public static class OfLong extends OfNumber<Long> {

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

    @Converter(autoApply = false)
    public static class OfFloat extends OfNumber<Float> {

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

    @Converter(autoApply = false)
    public static class OfDouble extends OfNumber<Double> {

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
    protected __StringAttributeConverter() {
        super();
    }
}
