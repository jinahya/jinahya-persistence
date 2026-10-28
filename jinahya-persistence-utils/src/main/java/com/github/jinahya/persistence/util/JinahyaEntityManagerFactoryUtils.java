package com.github.jinahya.persistence.util;

/*-
 * #%L
 * jinahya-persistence-utils
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

import java.util.Objects;
import java.util.function.Function;

/**
 * A utility class for {@link EntityManagerFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaEntityManagerFactoryUtils {

    /**
     * Applies a new entity manager of the specified entity manager factory to the specified function, and closes the
     * entity manager.
     *
     * @param factory  the entity manager factory.
     * @param function the function to be applied with the entity manager.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @implNote The transaction is left to the {@code function}.
     * @see EntityManagerFactory#createEntityManager()
     */
    public static <R> R applyEntityManager(final EntityManagerFactory factory,
                                           final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(factory, "factory is null");
        Objects.requireNonNull(function, "function is null");
        try (var manager = factory.createEntityManager()) {
            return function.apply(manager);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the id of the specified entity.
     *
     * @param factory an entity manager factory.
     * @param entity  the entity instance whose id is to be returned.
     * @param <Y>     identifier type parameter
     * @return the id of the {@code entity} cast as {@link Y}; {@code null} when the {@code entity} does not yet have an
     *         id.
     * @see EntityManagerFactory#getPersistenceUnitUtil()
     * @see PersistenceUnitUtil#getIdentifier(Object)
     */
    @SuppressWarnings({"unchecked"})
    public static <Y> Y getEntityIdentifier(final EntityManagerFactory factory, final Object entity) {
        Objects.requireNonNull(factory, "factory is null");
        Objects.requireNonNull(entity, "entity is null");
        return (Y) factory.getPersistenceUnitUtil().getIdentifier(entity);
    }

    /**
     * Returns the version of the specified entity.
     *
     * @param factory an entity manager factory.
     * @param entity  the entity instance whose version is to be returned.
     * @param <Y>     version type parameter
     * @return the version of the {@code entity} cast as {@link Y}; {@code null} when the {@code entity} has no version
     *         attribute, or does not yet have a version.
     * @see PersistenceUnitUtil#getVersion(Object)
     */
    @SuppressWarnings({"unchecked"})
    public static <Y> Y getEntityVersion(final EntityManagerFactory factory, final Object entity) {
        Objects.requireNonNull(factory, "factory is null");
        Objects.requireNonNull(entity, "entity is null");
        return (Y) factory.getPersistenceUnitUtil().getVersion(entity);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private JinahyaEntityManagerFactoryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
