package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeConverter;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class __StringAttributeConverter<X> implements AttributeConverter<X, String> {

    public abstract static class NumberConverter<N extends Number> extends __StringAttributeConverter<N> {

        protected NumberConverter(final Class<N> attributeClass) {
            super(attributeClass);
        }
    }

    public static class BigDecimalConverter extends NumberConverter<BigDecimal> {

        protected BigDecimalConverter() {
            super(BigDecimal.class);
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

    public static class DoubleConverter extends NumberConverter<Double> {

        protected DoubleConverter() {
            super(Double.class);
            delegate = new BigDecimalConverter();
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

        private final BigDecimalConverter delegate;
    }

    public static class FloatConverter extends NumberConverter<Float> {

        protected FloatConverter() {
            super(Float.class);
            delegate = new BigDecimalConverter();
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

        private final BigDecimalConverter delegate;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __StringAttributeConverter(final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The type of the entity attribute.
     */
    protected final Class<X> attributeClass;
}
