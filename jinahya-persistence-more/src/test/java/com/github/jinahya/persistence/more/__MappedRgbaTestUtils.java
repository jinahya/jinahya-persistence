package com.github.jinahya.persistence.more;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

final class __MappedRgbaTestUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static int randomComponent() {
        return ThreadLocalRandom.current().nextInt(___MappedColorConstants.RGB_MAX_COMPONENT + 1);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static <T extends __MappedRgba<T>> T randomizedInstance(final Supplier<? extends T> instantiator) {
        final var instance = instantiator.get();
        instance.setRed(__MappedRgbaTestUtils.randomComponent());
        instance.setGreen(__MappedRgbaTestUtils.randomComponent());
        instance.setBlue(__MappedRgbaTestUtils.randomComponent());
        instance.setAlpha(__MappedRgbaTestUtils.randomComponent());
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedRgbaTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
