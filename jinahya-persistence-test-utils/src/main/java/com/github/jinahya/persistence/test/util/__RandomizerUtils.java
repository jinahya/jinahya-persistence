package com.github.jinahya.persistence.test.util;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for {@link __Randomizer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Randomizer
 * @see __RandomizerLocator
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
     * @throws NullPointerException when either argument is {@code null}.
     * @apiNote This method is for a subclass which adds to the exclusions of the randomizer it extends.
     *         Elements are concatenated as they are, in order, with neither deduplication nor validation; the
     *         {@link __Randomizer#__Randomizer(Class, Iterable) randomizer constructor} strips them, drops the blank
     *         and the {@code null} ones, and deduplicates the rest.
     */
    public static Iterable<String> moreExcludedFields(final Iterable<String> excludedFields,
                                                      final Iterable<String> moreExcludedFields) {
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        Objects.requireNonNull(moreExcludedFields, "moreExcludedFields is null");
        return Stream.concat(
                StreamSupport.stream(excludedFields.spliterator(), false),
                StreamSupport.stream(moreExcludedFields.spliterator(), false)
        ).toList();
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({
            "unchecked"
    })
    private static <T> Optional<__Randomizer<T>> newRandomizerInstanceOf(final Class<T> target,
                                                                         final __RandomizerLocator locator) {
        assert target != null;
        assert locator != null;
        return Optional.ofNullable(locator.apply(target))
                .filter(__Randomizer.class::isAssignableFrom)
                .filter(c -> ___Utils.canInstantiate(c, target))
                .map(___Utils::newInstance)
                .filter(i -> ___Utils.canProduce(target, ((__Randomizer<?>) i).targetClass, i))
                .map(i -> (__Randomizer<T>) i);
    }

    /**
     * Returns, an optional of, a randomized instance of the specified target class, using a randomizer located by the
     * specified locator.
     * <p>
     * An empty optional is returned when the {@code locator} locates nothing, when the located class does not extend
     * {@link __Randomizer}, and when it is declared for a class of which the {@code target} is not a supertype; the
     * latter two are logged, at {@link System.Logger.Level#WARNING WARNING}, as they usually indicate a
     * misconfiguration.
     *
     * @param target  the target class.
     * @param locator the locator for locating the randomizer class of the {@code target}.
     * @param <T>     target type parameter
     * @return an optional of randomized instance of the {@code target}; {@code empty} when the {@code locator} locates
     *         no randomizer.
     * @throws NullPointerException when either argument is {@code null}.
     * @throws RuntimeException     when the located randomizer class declares no accessible no-argument constructor, or
     *                              when the randomizer itself throws.
     * @see __RandomizerLocator#STANDARD
     */
    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> target,
                                                          final __RandomizerLocator locator) {
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
     * @throws NullPointerException when the {@code target} is {@code null}.
     * @throws RuntimeException     when the located randomizer class declares no accessible no-argument constructor, or
     *                              when the randomizer itself throws.
     * @see #newRandomizedInstanceOf(Class, __RandomizerLocator)
     * @see __RandomizerLocator#STANDARD
     */
    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> target) {
        return newRandomizedInstanceOf(target, __RandomizerLocator.STANDARD);
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private __RandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
