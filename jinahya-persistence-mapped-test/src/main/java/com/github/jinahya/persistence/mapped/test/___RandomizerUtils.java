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
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link ___Randomizer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___RandomizerUtils {

    public static String[] mergeExcludedFields(final String[] excludedFields, final String... moreExcludedFields) {
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        Objects.requireNonNull(moreExcludedFields, "moreExcludedFields is null");
        final var result = new String[excludedFields.length + moreExcludedFields.length];
        System.arraycopy(excludedFields, 0, result, 0, excludedFields.length);
        System.arraycopy(moreExcludedFields, 0, result, excludedFields.length, moreExcludedFields.length);
        return result;
    }

    @Nonnull
    private static Optional<Class<?>> getRandomizerClassOf(final @Nonnull Class<?> target) {
        assert target != null;
        {
            final var annotation = AnnotationUtils.findAnnotation(target, ___RandomizerClass.class, false);
            if (annotation.isPresent()) {
                return Optional.of(annotation.get().value());
            }
        }
        // if enclosed,
        // try to find [enclosingClass_Randomizer$targetClass_Randomizer]
        {
            final Optional<Class<?>> optionalEnclosedRandomizerClass =
                    Optional.ofNullable(target.getEnclosingClass())
                            .flatMap(___RandomizerUtils::getRandomizerClassOf)
                            .map(enclosingRandomizerClass -> {
                                return ___JavaLang_TestUtils.siblingClassForPostfix(
                                        enclosingRandomizerClass,
                                        ___Randomizer.class,
                                        "$" + target.getSimpleName() + "Randomizer",
                                        "$" + target.getSimpleName() + "_Randomizer"
                                );
                            });
            if (optionalEnclosedRandomizerClass.isPresent()) {
                return optionalEnclosedRandomizerClass;
            }
        }
        return Optional.ofNullable(
                ___JavaLang_TestUtils.siblingClassForPostfix(
                        target,
                        ___Randomizer.class,
                        "Randomizer",
                        "_Randomizer"
                )
        );
    }

    @Nonnull
    @SuppressWarnings({
            "unchecked"
    })
    private static <T> Optional<___Randomizer<T>> newRandomizerInstanceOf(final @Nonnull Class<T> target) {
        assert target != null;
        return getRandomizerClassOf(target)
                .map(ReflectionUtils::newInstance)
                .filter(i -> target.isAssignableFrom(((___Randomizer<?>) i).target))
                .map(i -> (___Randomizer<T>) i);
    }

    @Nonnull
    public static <T> Optional<T> newRandomizedInstanceOf(final @Nonnull Class<T> target) {
        Objects.requireNonNull(target, "target is null");
        return newRandomizerInstanceOf(target).map(___Randomizer::get);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___RandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
