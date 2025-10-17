package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __CollectionStringAttributeConverter<C extends Collection<X>, X>
        extends __StringAttributeConverter<C> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __CollectionStringAttributeConverter(final String joiningDelimiter, final String splittingRegex,
                                                   final AttributeConverter<X, String> converter,
                                                   final Supplier<? extends C> supplier) {
        super();
        this.joiningDelimiter = Objects.requireNonNull(joiningDelimiter, "joiningDelimiter is null");
        this.splittingRegex = Objects.requireNonNull(splittingRegex, "splittingRegex is null");
        this.converter = Objects.requireNonNull(converter, "converter is null");
        this.supplier = Objects.requireNonNull(supplier, "supplier is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String convertToDatabaseColumn(final C attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.stream()
                .map(converter::convertToDatabaseColumn)
                .collect(Collectors.joining(joiningDelimiter));
    }

    @Override
    public C convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return Arrays.stream(dbData.split(splittingRegex))
                .map(converter::convertToEntityAttribute)
                .collect(Collectors.toCollection(supplier));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final String joiningDelimiter;

    private final String splittingRegex;

    private final AttributeConverter<X, String> converter;

    private final Supplier<? extends C> supplier;
}
