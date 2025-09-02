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

import com.github.jinahya.persistence.mapped.__Mapped;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Table;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utilities for {@link __MappedEntity_Test}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 *
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public class __Mapped_TestUtils {

    static <MAPPED extends __Mapped> Table getTableAnnotation(@Nonnull final Class<MAPPED> mappedClass) {
        Objects.requireNonNull(mappedClass, "mappedClass is null");
        return ___JavaLangReflectTestUtils.findAnnotation(mappedClass, Table.class)
                .orElseThrow(
                        () -> new IllegalArgumentException("unable to get @Table from " + mappedClass)
                );
    }

    public static <MAPPED extends __Mapped> MAPPED setFieldValue(@Nonnull final Class<MAPPED> mappedClass,
                                                                 @Nonnull final MAPPED mappedInstance,
                                                                 @Nonnull final Predicate<Field> fieldPredicate,
                                                                 final Object fieldValue) {
        final var field = ReflectionUtils
                .findFields(mappedClass, fieldPredicate, ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP).stream()
                .findFirst()
                .orElseThrow();
        if (!field.canAccess(mappedInstance)) {
            field.setAccessible(true);
        }
        try {
            field.set(mappedInstance, fieldValue);
        } catch (final IllegalAccessException iae) {
            throw new RuntimeException("failed to set " + field + " on " + mappedInstance + " with " + fieldValue, iae);
        }
        return mappedInstance;
    }

    public static <MAPPED extends __Mapped> MAPPED setFieldValue(@Nonnull final Class<MAPPED> mappedClass,
                                                                 @Nonnull final MAPPED mappedInstance,
                                                                 @Nonnull final String fieldName,
                                                                 final Object fieldValue) {
        Objects.requireNonNull(fieldName, "fieldName is null");
        return setFieldValue(
                mappedClass,
                mappedInstance,
                f -> {
                    return fieldName.equals(f.getName());
                },
                fieldValue
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __Mapped_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
