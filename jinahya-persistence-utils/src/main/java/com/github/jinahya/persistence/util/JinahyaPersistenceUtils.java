package com.github.jinahya.persistence.util;

/*-
 * #%L
 * jinahya-persistence-utils
 * %%
 * Copyright (C) 2024 - 2026 Jinahya, Inc.
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Objects;
import java.util.function.Function;

/**
 * A utility class for {@link Persistence}, for working with a persistence unit by its name.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Each method here opens a new factory of the named persistence unit, and closes it once done.
 *         Opening a factory is what costs &mdash; the provider reads the descriptor, builds the metamodel and connects
 *         to the database &mdash; so these suit a test, or a one-off task, rather than a call made over and over.
 * @see JinahyaEntityManagerFactoryUtils
 * @see JinahyaEntityManagerUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaPersistenceUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies a new factory of the persistence unit of the specified name to the specified function, and closes the
     * factory.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to be applied with the factory.
     * @param <R>                 result type parameter
     * @return the result of the {@code function}.
     * @see Persistence#createEntityManagerFactory(String)
     */
    public static <R> R applyEntityManagerFactory(
            final String persistenceUnitName,
            final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(persistenceUnitName, "persistenceUnitName is null");
        Objects.requireNonNull(function, "function is null");
        try (var factory = Persistence.createEntityManagerFactory(persistenceUnitName)) {
            return function.apply(factory);
        }
    }

    /**
     * Applies a new entity manager, of a new factory of the persistence unit of the specified name, to the specified
     * function, and closes both.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to be applied with the entity manager.
     * @param <R>                 result type parameter
     * @return the result of the {@code function}.
     * @implNote The transaction is left to the {@code function}.
     * @see #applyEntityManagerFactory(String, Function)
     * @see JinahyaEntityManagerFactoryUtils#applyEntityManager(EntityManagerFactory, Function)
     */
    public static <R> R applyEntityManager(final String persistenceUnitName,
                                           final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerFactory(
                persistenceUnitName,
                f -> JinahyaEntityManagerFactoryUtils.applyEntityManager(f, function)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private JinahyaPersistenceUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}

