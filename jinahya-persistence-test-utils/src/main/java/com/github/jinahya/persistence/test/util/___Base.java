package com.github.jinahya.persistence.test.util;

import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
abstract class ___Base<T> {

    ___Base(final Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The target class.
     */
    protected final Class<T> targetClass;
}
