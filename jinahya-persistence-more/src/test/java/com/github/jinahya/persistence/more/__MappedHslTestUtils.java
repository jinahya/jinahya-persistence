package com.github.jinahya.persistence.more;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

final class __MappedHslTestUtils {

    // ------------------------------------------------------------------------------------------------------------- hue
    static int randomHue() {
        return ThreadLocalRandom.current().nextInt(
                __MappedHsl.MAX_HUE + 1
        );
    }

    static double randomNormalizedHue() {
        return randomHue() / (double) __MappedHsl.MAX_HUE;
    }

    // -----------------------------------------------------------------------------------------------------------------
    static int randomSaturation() {
        return ThreadLocalRandom.current().nextInt(
                __MappedHsl.MAX_SATURATION + 1
        );
    }

    static double randomNormalizedSaturation() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static int randomLightness() {
        return ThreadLocalRandom.current().nextInt(
                __MappedHsl.MAX_LIGHTNESS + 1
        );
    }

    static double randomNormalizedLightness() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static int randomAlpha() {
        return ThreadLocalRandom.current().nextInt(
                __MappedHsl.MIN_ALPHA,
                __MappedHsl.MAX_ALPHA + 1
        );
    }

    static double randomNormalizedAlpha() {
        return randomAlpha() / (double) __MappedHsl.MAX_ALPHA;
    }

    // -----------------------------------------------------------------------------------------------------------------
    static <T extends __MappedHsl<T>> T randomizedInstance(final Supplier<? extends T> instantiator) {
        final var instance = instantiator.get();
        instance.setHue(randomHue());
        instance.setSaturation(randomSaturation());
        instance.setLightness(randomLightness());
        instance.setNormalizedAlpha(randomNormalizedAlpha());
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedHslTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
