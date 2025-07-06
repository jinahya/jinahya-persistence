package com.github.jinahya.persistence.more;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;

final class __MappedRgbTestUtils {

    static int randomComponent() {
        final var value = ThreadLocalRandom.current().nextInt(
                __MappedRgbConstants.MAX_COMPONENT + 1
        );
        assert value >= __MappedRgbConstants.MIN_COMPONENT;
        assert value <= __MappedRgbConstants.MAX_COMPONENT;
        return value;
    }

    static <R> R applyRandomComponents(
            final IntFunction<
                    ? extends IntFunction<
                            ? extends IntFunction<
                                    ? extends IntFunction<? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(randomComponent()) // r
                .apply(randomComponent()) // g
                .apply(randomComponent()) // b
                .apply(randomComponent()) // a
                ;
    }

    static double randomNormalizedComponent() {
        final var value = randomComponent() / (double) __MappedRgbConstants.MAX_COMPONENT;
        assert value >= __MappedRgbConstants.MIN_NORMALIZED_COMPONENT;
        assert value <= __MappedRgbConstants.MAX_NORMALIZED_COMPONENT;
        return value;
    }

    static <R> R applyRandomNormalizedComponents(
            final DoubleFunction<
                    ? extends DoubleFunction<
                            ? extends DoubleFunction<
                                    ? extends DoubleFunction<? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(randomNormalizedComponent()) // r
                .apply(randomNormalizedComponent()) // g
                .apply(randomNormalizedComponent()) // b
                .apply(randomNormalizedComponent()) // a
                ;
    }

    static <T extends __MappedRgb<T>> T randomizedInstance(final Supplier<? extends T> instantiator) {
        final var instance = Objects.requireNonNull(
                Objects.requireNonNull(instantiator, "instantiator is null").get(),
                "null got from the instantiator: " + instantiator
        );
        return applyRandomComponents(
                r -> g -> b -> a -> instance.red(r).green(g).blue(b).alpha(a)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedRgbTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
