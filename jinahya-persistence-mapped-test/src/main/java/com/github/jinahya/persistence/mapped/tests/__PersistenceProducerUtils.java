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

import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
final class __PersistenceProducerUtils {

    static Optional<String> getDefaultCatalog(final EntityManagerFactory entityManagerFactory) {
        return Optional.ofNullable(
                (String) entityManagerFactory.getProperties().get(
                        __PersistenceProducerConstants.PERSISTENCE_UNIT_PROPERTY_DEFAULT_CATALOG
                )
        );
    }

    static Optional<String> getDefaultSchema(final EntityManagerFactory entityManagerFactory) {
        return Optional.ofNullable(
                (String) entityManagerFactory.getProperties().get(
                        __PersistenceProducerConstants.PERSISTENCE_UNIT_PROPERTY_DEFAULT_SCHEMA
                )
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __PersistenceProducerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
