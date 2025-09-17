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
import jakarta.persistence.EntityManagerFactory;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
final class __PersistenceUnit_TestUtils {

    private static Optional<String> getProperty(@Nonnull final EntityManagerFactory entityManagerFactory,
                                                @Nonnull final String propertyName) {
        return Optional
                .ofNullable(
                        (String) entityManagerFactory.getProperties().get(propertyName)
                )
                .or(() -> {
                    try (final var entityManager = entityManagerFactory.createEntityManager()) {
                        return Optional.ofNullable((String) entityManager.getProperties().get(propertyName));
                    }
                });
    }

    static Optional<String> getJinahyaTableCatalog(final EntityManagerFactory entityManagerFactory) {
        return getProperty(
                entityManagerFactory,
                __PersistenceProducer_TestConstants.PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_CATALOG
        ).map(String::strip).filter(c -> !c.isBlank());
    }

    static Optional<String> getJinahyaTableSchema(final EntityManagerFactory entityManagerFactory) {
        return getProperty(
                entityManagerFactory,
                __PersistenceProducer_TestConstants.PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_SCHEMA
        ).map(String::strip).filter(s -> !s.isBlank());
    }

    static Optional<String[]> getJinahyaTableTypes(final EntityManagerFactory entityManagerFactory) {
        return getProperty(
                entityManagerFactory,
                __PersistenceProducer_TestConstants.PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_TYPES
        ).map(v -> Arrays.stream(v.split(","))
                        .map(String::strip)
                        .filter(t -> !t.isBlank())
                        .toArray(String[]::new))
                .filter(v -> v.length > 0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __PersistenceUnit_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
