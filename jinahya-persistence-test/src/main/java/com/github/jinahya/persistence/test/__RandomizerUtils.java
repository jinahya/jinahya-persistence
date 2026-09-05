package com.github.jinahya.persistence.test;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for {@link __Randomizer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __RandomizerUtils {

    /**
     * Merges specified iterables of excluded fields.
     *
     * @param excludedFields     the first iterable of excluded fields.
     * @param moreExcludedFields the second iterable of excluded fields.
     * @return an {@link Iterable} of merged excluded fields.
     */
    public static Iterable<String> moreExcludedFields(final @Nonnull Iterable<String> excludedFields,
                                                      final @Nonnull Iterable<String> moreExcludedFields) {
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        Objects.requireNonNull(moreExcludedFields, "moreExcludedFields is null");
        return Stream.concat(
                StreamSupport.stream(excludedFields.spliterator(), false),
                StreamSupport.stream(moreExcludedFields.spliterator(), false)
        ).toList();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @SuppressWarnings({
            "unchecked"
    })
    private static <T> Optional<__Randomizer<T>> newRandomizerInstanceOf(final @Nonnull Class<T> target,
                                                                         final @Nonnull __RandomizerLocator locator) {
        assert target != null;
        assert locator != null;
        return Optional.ofNullable(locator.apply(target))
                .filter(__Randomizer.class::isAssignableFrom)
                .map(___Utils::newInstance)
                .filter(i -> target.isAssignableFrom(((__Randomizer<?>) i).targetClass))
                .map(i -> (__Randomizer<T>) i);
    }

    /**
     * Returns, an optional of, a randomized instance of the specified target class, using a randomizer located by the
     * specified locator.
     *
     * @param target  the target class.
     * @param locator the locator for locating the randomizer class of the {@code target}.
     * @param <T>     target type parameter
     * @return an optional of randomized instance of the {@code target}; {@code empty} when the {@code locator} locates
     *         no randomizer.
     * @see __RandomizerLocator#STANDARD
     */
    @Nonnull
    public static <T> Optional<T> newRandomizedInstanceOf(final @Nonnull Class<T> target,
                                                          final @Nonnull __RandomizerLocator locator) {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(locator, "locator is null");
        return newRandomizerInstanceOf(target, locator)
                .map(__Randomizer::get);
    }

    /**
     * Returns, an optional of, a randomized instance of the specified target class.
     *
     * @param target the target class.
     * @param <T>    target type parameter
     * @return an optional of randomized instance of the {@code target}; {@code empty} when no randomizer found.
     * @see #newRandomizedInstanceOf(Class, __RandomizerLocator)
     * @see __RandomizerLocator#STANDARD
     */
    @Nonnull
    public static <T> Optional<T> newRandomizedInstanceOf(final @Nonnull Class<T> target) {
        return newRandomizedInstanceOf(target, __RandomizerLocator.STANDARD);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __RandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
