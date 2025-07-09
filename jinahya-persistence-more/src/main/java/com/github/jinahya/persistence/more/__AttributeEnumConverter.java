package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnumConverter<ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
        implements AttributeConverter<ENUM, ATTRIBUTE> {

    public abstract static class __OfString<ENUM extends Enum<ENUM> & __AttributeEnum.__OfString<ENUM>>
            extends __AttributeEnumConverter<ENUM, String> {

        protected __OfString(final Class<ENUM> enumClass) {
            super(enumClass, String.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified enum class.
     *
     * @param enumClass      a class of {@link ENUM}.
     * @param attributeClass a class of {@link ATTRIBUTE}
     * @see #enumClass
     * @see #attributeClass
     */
    protected __AttributeEnumConverter(final Class<ENUM> enumClass, final Class<ATTRIBUTE> attributeClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts specified entity attribute to the database column value.
     *
     * @param attribute the entity attribute value to be converted.
     * @return a database column value; {@code null} when {@code attribute} is {@code null}.
     */
    @Override
    public ATTRIBUTE convertToDatabaseColumn(final ENUM attribute) {
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
    public ENUM convertToEntityAttribute(final ATTRIBUTE dbData) {
        if (dbData == null) {
            return null;
        }
        return __AttributeEnumUtils.valueOfAttributeValue(enumClass, dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENUM}.
     */
    protected final Class<ENUM> enumClass;

    /**
     * The class of {@link ATTRIBUTE}.
     */
    protected final Class<ATTRIBUTE> attributeClass;
}
