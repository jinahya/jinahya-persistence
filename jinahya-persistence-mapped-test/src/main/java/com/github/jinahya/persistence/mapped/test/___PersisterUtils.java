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
import jakarta.persistence.EntityManager;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class ___PersisterUtils {

    static <T> Optional<Class<?>> getPersisterClassOf(final Class<T> entityClass) {
        return Optional.ofNullable(
                ___JavaLangTestUtils.forAnyPostfixes(
                        entityClass,
                        __MappedEntityPersister.class,
                        "Persister",
                        "_Persister"
                )
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Persister<T>> newPersisterInstanceOf(final Class<T> entityClass) {
        return getPersisterClassOf(entityClass)
                .map(___JavaLangReflectTestUtils::newInstanceOf)
                .map(i -> (___Persister<T>) i);
    }

    @Nonnull
    public static <T> T newPersistedInstanceOf(final EntityManager entityManager, final Class<T> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        final T entityInstance = ___RandomizerUtils.newRandomizedInstanceOf(entityClass);
        newPersisterInstanceOf(entityClass)
                .orElseThrow(() -> new RuntimeException("empty persister instance of " + entityClass))
                .persist(entityManager, entityInstance);
        return entityInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___PersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
