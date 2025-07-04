package com.github.jinahya.persistence.more;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

final class __MappedHslaTestUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static int randomHue() {
        return ThreadLocalRandom.current().nextInt(
                ___MappedColorConstants.HSL_MAX_HUE + 1
        );
    }

    static double randomNormalizedHue() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static int randomSaturation() {
        return ThreadLocalRandom.current().nextInt(
                ___MappedColorConstants.HSL_MAX_SATURATION + 1
        );
    }

    static double randomNormalizedSaturation() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static int randomLightness() {
        return ThreadLocalRandom.current().nextInt(
                ___MappedColorConstants.HSL_MAX_LIGHTNESS + 1
        );
    }

    static double randomNormalizedLightness() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static double randomNormalizedAlpha() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static <T extends __MappedHsla<T>> T randomizedInstance(final Supplier<? extends T> instantiator) {
        final var instance = instantiator.get();
        instance.setHue(randomHue());
        instance.setSaturation(randomSaturation());
        instance.setLightness(randomLightness());
        instance.setNormalizedAlpha(randomNormalizedAlpha());
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedHslaTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
