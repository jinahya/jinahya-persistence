package com.github.jinahya.persistence.more;

import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __CollectionStringAttributeConverter<C extends Collection<X>, X>
        extends __StringAttributeConverter<C> {

    public abstract static class __OfStrings<C extends Collection<String>>
            extends __CollectionStringAttributeConverter<C, String> {

        protected __OfStrings(final String delimiter, final Supplier<? extends C> collectionSupplier) {
            super(delimiter, delimiter,
                  __AttributeConverterUtils.using(UnaryOperator.identity(), UnaryOperator.identity()),
                  collectionSupplier);
        }
    }

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with specified parameters.
     *
     * @param joiningDelimiter   a delimiter for joining elements into a string.
     * @param splittingRegex     a regex for splitting elements from a string.
     * @param elementConverter   an attribute converter for converting elements.
     * @param collectionSupplier a supplier for creating a collection.
     */
    protected __CollectionStringAttributeConverter(final String joiningDelimiter, final String splittingRegex,
                                                   final AttributeConverter<X, String> elementConverter,
                                                   final Supplier<? extends C> collectionSupplier) {
        super();
        this.joiningDelimiter = Objects.requireNonNull(joiningDelimiter, "joiningDelimiter is null");
        this.splittingRegex = Objects.requireNonNull(splittingRegex, "splittingRegex is null");
        this.elementConverter = Objects.requireNonNull(elementConverter, "elementConverter is null");
        this.collectionSupplier = Objects.requireNonNull(collectionSupplier, "collectionSupplier is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public String convertToDatabaseColumn(@Nullable final C attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.stream()
                .map(elementConverter::convertToDatabaseColumn)
                .filter(this::filterEntityAttributeElement)
                .collect(Collectors.joining(joiningDelimiter));
    }

    protected boolean filterDatabaseColumnElement(@Nullable final X element) {
        return true;
    }

    @Nullable
    @Override
    @SuppressWarnings({
            "java:S1168" // Empty arrays and collections should be returned instead of null
    })
    public C convertToEntityAttribute(@Nullable final String dbData) {
        if (dbData == null) {
            return null;
        }
        return Arrays.stream(dbData.split(splittingRegex))
                .map(elementConverter::convertToEntityAttribute)
                .filter(this::filterDatabaseColumnElement)
                .collect(Collectors.toCollection(collectionSupplier));
    }

    protected boolean filterEntityAttributeElement(@Nullable final String element) {
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final String joiningDelimiter;

    private final String splittingRegex;

    private final AttributeConverter<X, String> elementConverter;

    private final Supplier<? extends C> collectionSupplier;
}
