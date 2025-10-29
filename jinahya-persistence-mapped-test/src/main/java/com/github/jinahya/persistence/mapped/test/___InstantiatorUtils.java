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

import org.junit.platform.commons.util.ReflectionUtils;

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
    private static <T> Optional<Class<?>> getInstantiatorClassOf(final Class<T> target) {
        assert target != null;
        return Optional.ofNullable(
                ___JavaLang_TestUtils.siblingClassForPostfix(
                        target,
                        ___Instantiator.class,
                        "Instantiator",
                        "_Instantiator"
                )
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    private static <T> Optional<___Instantiator<T>> newInstantiatorInstanceOf(final Class<T> target) {
        assert target != null;
        return getInstantiatorClassOf(target)
                .map(ReflectionUtils::newInstance)
                .filter(i -> target.isAssignableFrom(((___Instantiator<?>) i).target))
                .map(i -> (___Instantiator<T>) i);
    }

    /**
     * Instantiates a new instance of the specified class, who has a sibling instantiator class which implements
     * {@link ___Instantiator}, and has a postfix of either {@code "Instantiator"} or {@code "_Instantiator"}.
     *
     * @param target the class to be instantiated.
     * @param <T>    class target parameter
     * @return an optional of the instantiated instance; {@link Optional#empty() empty} when no instantiator found.
     */
    public static <T> Optional<T> newInstantiatedInstanceOf(final Class<T> target) {
        Objects.requireNonNull(target, "target is null");
        return newInstantiatorInstanceOf(target)
                .map(___Instantiator::get);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___InstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
