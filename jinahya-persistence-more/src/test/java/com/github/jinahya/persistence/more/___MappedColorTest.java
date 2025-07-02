package com.github.jinahya.persistence.more;

import java.util.Objects;

abstract class ___MappedColorTest<COLOR extends ___MappedColor<COLOR>> {

    ___MappedColorTest(final Class<COLOR> colorClass) {
        super();
        this.colorClass = Objects.requireNonNull(colorClass, "colorClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    final Class<COLOR> colorClass;
}