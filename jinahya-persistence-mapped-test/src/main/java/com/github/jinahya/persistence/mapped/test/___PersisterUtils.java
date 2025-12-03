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
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link ___Persister}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class ___PersisterUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    static <T> Optional<Class<?>> getPersisterClassOf(final Class<T> type) {
        return Optional.ofNullable(
                ___JavaLang_TestUtils.siblingClassForPostfix(
                        type,
                        __MappedEntity_Persister.class,
                        "Persister",
                        "_Persister"
                )
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Persister<T>> newPersisterInstanceOf(final Class<T> type) {
        return getPersisterClassOf(type)
//                .map(___JavaLangReflectTestUtils::newInstanceOf)
                .map(ReflectionUtils::newInstance)
                .map(i -> (___Persister<T>) i);
    }

    /**
     * Returns a new persisted instance of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   entity entityClass parameter.
     * @param <T>           entity entityClass parameter.
     * @return a new persisted instance of the {@code entityClass}.
     */
    @Nonnull
    public static <T> T newPersistedInstanceOf(final @Nonnull EntityManager entityManager,
                                               final @Nonnull Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final T entityInstance = ___RandomizerUtils.newRandomizedInstanceOf(entityClass)
                .orElseThrow(() -> new IllegalArgumentException("no randomized instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "entityInstance: {0}", entityInstance);
        final var persisterInstance = newPersisterInstanceOf(entityClass)
                .orElseThrow(() -> new IllegalArgumentException("no persister instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "persister instance: {0}", persisterInstance);
        persisterInstance.persist(entityManager, entityInstance);
        return entityInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___PersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
