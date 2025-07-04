package com.github.jinahya.persistence.more;

import java.util.concurrent.ThreadLocalRandom;

final class __MappedColorTestUtils {

    static double randomComponent() {
        return ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedColorTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
