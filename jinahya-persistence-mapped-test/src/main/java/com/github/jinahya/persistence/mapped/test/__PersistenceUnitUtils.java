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

import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
final class __PersistenceUnitUtils {

    private static Optional<String> getProperty(final EntityManagerFactory entityManagerFactory, final String name) {
        return Optional
                .ofNullable(
                        (String) entityManagerFactory.getProperties().get(name)
                )
                .or(() -> {
                    try (final var entityManager = entityManagerFactory.createEntityManager()) {
                        return Optional.ofNullable((String) entityManager.getProperties().get(name));
                    }
                });
    }

    static Optional<String> getDefaultCatalog(final EntityManagerFactory entityManagerFactory) {
        return getProperty(
                entityManagerFactory,
                __PersistenceProducerConstants.PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_CATALOG
        );
    }

    static Optional<String> getDefaultSchema(final EntityManagerFactory entityManagerFactory) {
        return getProperty(
                entityManagerFactory,
                __PersistenceProducerConstants.PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_SCHEMA
        );
    }

    static Optional<String[]> getDefaultTypes(final EntityManagerFactory entityManagerFactory) {
        return getProperty(
                entityManagerFactory,
                __PersistenceProducerConstants.PERSISTENCE_UNIT_PROPERTY_JINAHYA_TABLE_TYPES
        ).map(v -> v.split(","));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __PersistenceUnitUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
