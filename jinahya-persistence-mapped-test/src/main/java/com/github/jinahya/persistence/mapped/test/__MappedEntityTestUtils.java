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

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public class __MappedEntityTestUtils {

    static <ENTITY extends __MappedEntity<ENTITY, ?>>
    Table getTableAnnotation(@Nonnull final Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return ___JavaLangReflectTestUtils.findAnnotation(entityClass, Table.class)
                .orElseThrow(
                        () -> new IllegalArgumentException("unable to get @Table from " + entityClass)
                );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
