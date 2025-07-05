package com.github.jinahya.persistence.more;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

final class __MappedColorTestUtils {

    static int randomHue() {
        return ThreadLocalRandom.current().nextInt(
                ___MappedColorConstants.HSL_MIN_HUE, ___MappedColorConstants.HSL_MAX_HUE + 1
        );
    }

    static double randomNormalizedComponent() {
        final var value = ThreadLocalRandom.current().nextDouble(
                ___MappedColorConstants.MAX_COMPONENT_NORMALIZED + Double.MIN_VALUE
        );
        if (false) {
            return BigDecimal.valueOf(value)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        return value;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedColorTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
