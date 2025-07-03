package com.github.jinahya.persistence.more;

import java.util.concurrent.ThreadLocalRandom;

final class __MappedRgbaTestUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static int randomComponent() {
        return ThreadLocalRandom.current().nextInt(___MappedColorConstants.RGB_MAX_COMPONENT + 1);
    }

    static double randomNormalizedComponent() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.RGB_MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedRgbaTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}