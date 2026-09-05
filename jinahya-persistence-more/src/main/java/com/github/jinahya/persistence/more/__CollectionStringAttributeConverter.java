package com.github.jinahya.persistence.more;

import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * An abstract class for converting a {@link Collection} of entity attribute elements to a single delimited
 * {@code String} db data, and vice versa.
 * <p>
 * Each element is converted by an element converter, joined with a delimiter on the way out, and split with a regex on
 * the way in; the collection itself is created by a supplier, which decides the collection type and, with it, whether
 * duplicates and order are kept.
 *
 * @param <C> collection type parameter
 * @param <X> element type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __CollectionStringAttributeConverter<C extends Collection<X>, X>
        extends __StringAttributeConverter<C> {

    /**
     * An abstract class for converting a {@link Collection} of {@link String} elements, which requires no element
     * conversion.
     *
     * @param <C> collection type parameter
     */
    public abstract static class __OfStrings<C extends Collection<String>>
            extends __CollectionStringAttributeConverter<C, String> {

        /**
         * Creates a new instance which joins and splits elements with the specified delimiter.
         *
         * @param delimiter          a delimiter, used both for joining and, as a regex, for splitting.
         * @param collectionSupplier a supplier for creating a collection.
         */
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
    /**
     * Converts the specified collection to a single delimited string.
     *
     * @param attribute the collection to convert.
     * @return a string of the converted elements, joined with the delimiter; {@code null} when the {@code attribute} is
     *         {@code null}.
     */
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

    /**
     * Decides whether the specified element, converted from db data, is kept in the resulting collection.
     *
     * @param element the element to test; may be {@code null}.
     * @return {@code true} for keeping the {@code element}; {@code false} for discarding it.
     * @implSpec The default implementation returns {@code true} for every element.
     */
    protected boolean filterDatabaseColumnElement(@Nullable final X element) {
        return true;
    }

    /**
     * Converts the specified delimited string to a collection of elements.
     *
     * @param dbData the string to convert.
     * @return a collection, created by the collection supplier, of the converted elements; {@code null} when the
     *         {@code dbData} is {@code null}.
     */
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

    /**
     * Decides whether the specified element, converted from an entity attribute, is joined into the db data.
     *
     * @param element the converted element to test; may be {@code null}.
     * @return {@code true} for joining the {@code element}; {@code false} for discarding it.
     * @implSpec The default implementation returns {@code true} for every element.
     */
    protected boolean filterEntityAttributeElement(@Nullable final String element) {
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final String joiningDelimiter;

    private final String splittingRegex;

    private final AttributeConverter<X, String> elementConverter;

    private final Supplier<? extends C> collectionSupplier;
}
