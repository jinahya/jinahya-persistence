package com.github.jinahya.persistence.test.util;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for {@link __Randomizer}.
 * <p>
 * A randomizer is found for a target class by the {@link #locateStandard(Class) naming convention}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __RandomizerUtils {

    /**
     * Locates, by the naming convention, the randomizer class of the specified target class.
     * <p>
     * The randomizer class is a sibling of the {@code target} -- declared in the same package, beside it -- which
     * extends {@link __Randomizer} and has a postfix of either {@code "Randomizer"} or {@code "_Randomizer"}. For a
     * target class {@code Foo}, that is {@code FooRandomizer}, and then {@code Foo_Randomizer}.
     * <p>
     * The convention spans source sets: a {@code Foo} declared in {@code main} and a {@code FooRandomizer} declared in
     * {@code test} are the same package, and both are on the test classpath.
     *
     * @param target the target class whose randomizer class is located.
     * @return the randomizer class of the {@code target}; {@code null} when not found.
     * @implNote A class which is not a sibling of the {@code target} is never located: neither a local nor an
     *         anonymous class, which can not carry the required name, nor a class nested inside the {@code target},
     *         which would have to be declared in the source of the {@code target} itself. A class nested in a target
     *         class of {@code main} therefore has to be declared as a top-level class to be randomizable here. Note
     *         that a subclass of a class which has a randomizer is located by the convention, and not by the randomizer
     *         of its superclass, which could not produce instances of the subclass anyway.
     */
    @Nullable
    static Class<?> locateStandard(final Class<?> target) {
        assert target != null;
        return ___Utils.siblingClassForPostfixes(target, "Randomizer", "_Randomizer");
    }

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

    /**
     * Returns, an optional of, a randomized instance of the specified target class, using the randomizer located for it
     * by the {@link #locateStandard(Class) naming convention}.
     * <p>
     * An empty optional means one thing: no randomizer is located for the {@code target}. When one <em>is</em> located,
     * it is used, and anything wrong with it -- it does not extend {@link __Randomizer}, it can not be instantiated, or
     * it produces something which is not an instance of the {@code target} -- fails, rather than being reported as an
     * absence: a class named by the convention is one the developer meant to be used.
     *
     * @param target the target class.
     * @param <T>    target type parameter
     * @return an optional of randomized instance of the {@code target}; {@code empty} when no randomizer applies.
     * @throws NullPointerException when the {@code target} is {@code null}.
     * @throws RuntimeException     when the located randomizer is unusable, or produces nothing, or produces something
     *                              which is not a {@code target}; and when it throws.
     * @see #locateStandard(Class)
     * @see ___Utils#produced(Class, Object, Object)
     */
    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> target) {
        Objects.requireNonNull(target, "target is null");
        final Class<?> located = locateStandard(target);
        if (located == null) {
            return Optional.empty();
        }
        // one is provided, so it is meant to be used; from here, anything wrong with it is a fault
        ___Utils.requireSubtype(located, __Randomizer.class, target);
        final var randomizer = (__Randomizer<?>) ___Utils.newInstance(located);
        return Optional.of(___Utils.produced(target, randomizer, randomizer.get()));
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private __RandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
