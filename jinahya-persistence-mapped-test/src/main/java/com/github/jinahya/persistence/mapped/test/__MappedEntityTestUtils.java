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

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Table;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utilities for {@link __MappedEntityTest}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 *
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public class __MappedEntityTestUtils {

    static <ENTITY extends __MappedEntity<?>>
    Table getTableAnnotation(@Nonnull final Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return ___JavaLangReflectTestUtils.findAnnotation(entityClass, Table.class)
                .orElseThrow(
                        () -> new IllegalArgumentException("unable to get @Table from " + entityClass)
                );
    }

    public static <
            ENTITY extends __MappedEntity<?>
            >
    ENTITY setFieldValue(@Nonnull final Class<ENTITY> entityClass,
                         @Nonnull final ENTITY entityInstance,
                         @Nonnull final Predicate<Field> fieldPredicate,
                         final Object fieldValue) {
        final var field = ReflectionUtils
                .findFields(entityClass, fieldPredicate, ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP).stream()
                .findFirst()
                .orElseThrow();
        if (!field.canAccess(entityInstance)) {
            field.setAccessible(true);
        }
        try {
            field.set(entityInstance, fieldValue);
        } catch (final IllegalAccessException iae) {
            throw new RuntimeException("failed to set " + field + " on " + entityInstance + " with " + fieldValue, iae);
        }
        return entityInstance;
    }

    public static <
            ENTITY extends __MappedEntity<?>
            >
    ENTITY setFieldValue(@Nonnull final Class<ENTITY> entityClass,
                         @Nonnull final ENTITY entityInstance,
                         @Nonnull final String fieldName,
                         final Object fieldValue) {
        Objects.requireNonNull(fieldName, "fieldName is null");
        return setFieldValue(
                entityClass,
                entityInstance,
                f -> {
                    return fieldName.equals(f.getName());
                },
                fieldValue
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
