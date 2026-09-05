package com.github.jinahya.persistence.test;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link __Instantiator}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Instantiator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __InstantiatorUtils {

    // -----------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({
            "unchecked"
    })
    private static <T> Optional<__Instantiator<T>> newInstantiatorInstanceOf(
            final Class<T> target, final __InstantiatorLocator locator) {
        assert target != null;
        assert locator != null;
        return Optional.ofNullable(locator.apply(target))
                .filter(__Instantiator.class::isAssignableFrom)
                .map(___Utils::newInstance)
                .filter(i -> target.isAssignableFrom(((__Instantiator<?>) i).targetClass))
                .map(i -> (__Instantiator<T>) i);
    }

    /**
     * Instantiates a new instance of the specified class, using an instantiator located by the specified locator.
     * <p>
     * When the {@code locator} locates no instantiator for the {@code target}, the {@code target} itself is
     * instantiated, using its no-argument constructor.
     *
     * @param target  the class to be instantiated.
     * @param locator the locator for locating the instantiator class of the {@code target}.
     * @param <T>     class target parameter
     * @return a new instance of the {@code target}.
     * @see __InstantiatorLocator#STANDARD
     */
    public static <T> @Nonnull T newInstantiatedInstanceOf(final @Nonnull Class<T> target,
                                                           final @Nonnull __InstantiatorLocator locator) {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(locator, "locator is null");
        return newInstantiatorInstanceOf(target, locator)
                .orElseGet(() -> new __Instantiator<>(target) {
                })
                .get();
    }

    /**
     * Instantiates a new instance of the specified class, who may have a sibling instantiator class which extends
     * {@link __Instantiator}, and has a postfix of either {@code "Instantiator"} or {@code "_Instantiator"}.
     * <p>
     * When no such instantiator class is found, the {@code target} itself is instantiated, using its no-argument
     * constructor.
     *
     * @param target the class to be instantiated.
     * @param <T>    class target parameter
     * @return a new instance of the {@code target}.
     * @see #newInstantiatedInstanceOf(Class, __InstantiatorLocator)
     * @see __InstantiatorLocator#STANDARD
     */
    public static <T> @Nonnull T newInstantiatedInstanceOf(final @Nonnull Class<T> target) {
        return newInstantiatedInstanceOf(target, __InstantiatorLocator.STANDARD);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __InstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
