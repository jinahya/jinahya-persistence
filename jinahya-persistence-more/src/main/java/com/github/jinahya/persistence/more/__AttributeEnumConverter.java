package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnumConverter<E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE>
        implements AttributeConverter<E, ATTRIBUTE> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified enum class.
     *
     * @param enumClass the enum class.
     * @see #enumClass
     */
    protected __AttributeEnumConverter(final Class<E> enumClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts specified entity attribute to the database column value.
     *
     * @param attribute the entity attribute value to be converted.
     * @return a database column value; {@code null} when {@code attribute} is {@code null}.
     */
    @Override
    public ATTRIBUTE convertToDatabaseColumn(final E attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.attributeValue();
    }

    /**
     * Converts specified db data to an entity attribute.
     *
     * @param dbData the data from the database column to be converted.
     * @return an entity attribute; {@code null} when {@code dbData} is {@code null}.
     */
    @Override
    public E convertToEntityAttribute(final ATTRIBUTE dbData) {
        if (dbData == null) {
            return null;
        }
        return __AttributeEnumUtils.valueOfAttributeValue(enumClass, dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The enum class.
     */
    protected final Class<E> enumClass;
}
