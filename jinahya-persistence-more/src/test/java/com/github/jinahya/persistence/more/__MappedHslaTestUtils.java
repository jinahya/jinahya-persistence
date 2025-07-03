package com.github.jinahya.persistence.more;

import java.util.concurrent.ThreadLocalRandom;

final class __MappedHslaTestUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static int randomHue() {
        return ThreadLocalRandom.current().nextInt(___MappedColorConstants.HSL_MAX_HUE + 1);
    }

    static double randomNormalizedHue() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.HSL_MAX_HUE_NORMALIZED + Double.MIN_VALUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static int randomSaturation() {
        return ThreadLocalRandom.current().nextInt(___MappedColorConstants.HSL_MAX_SATURATION + 1);
    }

    static double randomNormalizedSaturation() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.HSL_MAX_SATURATION_NORMALIZED + Double.MIN_VALUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static int randomLightness() {
        return ThreadLocalRandom.current().nextInt(___MappedColorConstants.HSL_MAX_LIGHTNESS + 1);
    }

    static double randomNormalizedLightness() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.HSL_MAX_LIGHTNESS_NORMALIZED + Double.MIN_VALUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
//    static int randomAlpha() {
//        return ThreadLocalRandom.current().nextInt(___MappedColorConstants.HSL_MAX_ALPHA + 1);
//    }

    static double randomNormalizedAlpha() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.HSL_MAX_ALPHA_NORMALIZED + Double.MIN_VALUE);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedHslaTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}