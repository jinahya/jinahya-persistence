package com.github.jinahya.persistence.mapped.tests;

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

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link ___Instantiator}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___Instantiator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___InstantiatorUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static <T> Optional<Class<?>> getInstantiatorClassOf(final Class<T> type) {
        return Optional.ofNullable(
                ___JavaLangUtils.forAnyPostfixes(type, ___Instantiator.class, "Instantiator", "_Instantiator")
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Instantiator<T>> newInstantiatorInstanceOf(final Class<T> type) {
        return getInstantiatorClassOf(type)
                .map(___JavaLangReflectUtils::newInstanceOf)
                .map(i -> (___Instantiator<T>) i);
    }

    /**
     * Creates a new instance of the specified class, which implements {@link ___Instantiator}, and has a postfix of
     * either {@code "Instantiator"} or {@code "_Instantiator"}, after the fully qualified name of the specified class.
     * <p>
     * {@snippet lang = "java":
     * class MyPojo {
     * }
     *
     * class MyPojoInstantiator implements Instantiator<MyPojo> {
     *
     *     @Override
     *     public MyPojo get() {
     *         return new MyPojo();
     *     }
     * }
     *}
     *
     * @param type the class to be initialized.
     * @param <T>  class type parameter
     * @return an optional of the initialized instance; {@link Optional#empty() empty} when no instantiator found.
     */
    public static <T> Optional<T> newInstantiatedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return newInstantiatorInstanceOf(type)
                .map(___Instantiator::get);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___InstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
