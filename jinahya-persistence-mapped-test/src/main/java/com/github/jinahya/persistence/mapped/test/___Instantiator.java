package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.annotation.Nonnull;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.Objects;

/**
 * An abstract class for initializing a new instance of a specific class.
 *
 * @param <T> class type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___InstantiatorUtils
 * @see ___Randomizer
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class ___Instantiator<T> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for instantiating the specified class.
     *
     * @param targetClass the class to be instantiated.
     * @see #targetClass
     */
    protected ___Instantiator(final @Nonnull Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Instantiates {@link #targetClass}.
     *
     * @return a new instance of {@link #targetClass}.
     * @see ReflectionUtils#newInstance(Class, Object...)
     */
    @Nonnull
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
    public T get() {
        return ReflectionUtils.newInstance(targetClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The target type to instantiate.
     */
    protected final Class<T> targetClass;
}
