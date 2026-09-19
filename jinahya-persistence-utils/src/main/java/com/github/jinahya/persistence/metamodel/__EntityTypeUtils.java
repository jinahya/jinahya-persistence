package com.github.jinahya.persistence.metamodel;

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

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * A utility class for {@link EntityType}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote The methods here look a type up across an {@link Iterable} of entity manager factories, and take
 *         the first match. None memoizes: a lookup which took the first match found for a class, whichever factories
 *         a later caller passed, is not a cache but a wrong answer waiting for a second persistence unit.
 * @see __ManagedTypeUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __EntityTypeUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the {@link ManagedType} of the specified entity class.
     *
     * @param entityClass            the entity class whose {@link ManagedType} is returned.
     * @param entityManagerFactories an iterable of entity manager factories.
     * @param <X>                    represented entity type
     * @return the {@link ManagedType} of the {@code entityClass}.
     * @deprecated A managed type is not an entity type, and this class is about entity types; use
     *         {@link __ManagedTypeUtils#getManagedType(Class, Iterable)}, which this now merely calls.
     */
    @Deprecated(forRemoval = true)
    public static <X> ManagedType<X> getManagedType(
            final Class<X> entityClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        // Delegates rather than duplicating. This used to carry its own copy of the walk, and its own
        // cache, and the copy was wrong: Metamodel.managedType(Class) THROWS for a class it does not
        // manage -- it never returns null -- so the `.filter(Objects::nonNull)` was dead and the first
        // factory which did not know the class aborted the whole lookup instead of falling through.
        return __ManagedTypeUtils.getManagedType(entityClass, entityManagerFactories);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Finds the {@link EntityType} of the specified entity class in the metamodel of the specified entity manager
     * factory.
     *
     * @param entityClass          the entity class whose {@link EntityType} is returned.
     * @param entityManagerFactory the entity manager factory to look the {@code entityClass} up in.
     * @param <X>                  represented entity type
     * @return an optional of the {@link EntityType} of the {@code entityClass}; {@link Optional#empty() empty} when the
     *         {@code entityManagerFactory} does not map it as an entity.
     * @implNote {@link Metamodel#entity(Class)} <em>throws</em> for a class it does not map as an entity; it
     *         never returns {@code null}.
     * @see Metamodel#entity(Class)
     */
    public static <X> Optional<EntityType<X>> findEntityType(final Class<X> entityClass,
                                                             final EntityManagerFactory entityManagerFactory) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        try {
            return Optional.of(entityManagerFactory.getMetamodel().entity(entityClass));
        } catch (final IllegalArgumentException iae) {
            return Optional.empty();
        }
    }

    /**
     * Finds the {@link EntityType} of the specified entity class, in the first of the specified entity manager
     * factories which maps it as an entity.
     *
     * @param entityClass            the entity class whose {@link EntityType} is returned.
     * @param entityManagerFactories an iterable of entity manager factories.
     * @param <X>                    represented entity type
     * @return an optional of the {@link EntityType} of the {@code entityClass}; {@link Optional#empty() empty} when
     *         none of the {@code entityManagerFactories} maps it as an entity.
     * @see #findEntityType(Class, EntityManagerFactory)
     */
    public static <X> Optional<EntityType<X>> findEntityType(
            final Class<X> entityClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(entityManagerFactories, "entityManagerFactories is null");
        return StreamSupport.stream(entityManagerFactories.spliterator(), false)
                .map(f -> findEntityType(entityClass, f))
                .flatMap(Optional::stream)
                .findFirst();
    }

    /**
     * Returns the {@link EntityType} of the specified entity class, from the metamodel of the specified entity manager
     * factory.
     *
     * @param entityClass          the entity class whose {@link EntityType} is returned.
     * @param entityManagerFactory the entity manager factory to look the {@code entityClass} up in.
     * @param <X>                  represented entity type
     * @return the {@link EntityType} of the {@code entityClass}.
     * @throws IllegalArgumentException when the {@code entityManagerFactory} does not map the {@code entityClass} as an
     *                                  entity.
     * @see #findEntityType(Class, EntityManagerFactory)
     */
    public static <X> EntityType<X> getEntityType(final Class<X> entityClass,
                                                  final EntityManagerFactory entityManagerFactory) {
        return findEntityType(entityClass, entityManagerFactory).orElseThrow(
                () -> new IllegalArgumentException("no entity type found for entity class: " + entityClass)
        );
    }

    /**
     * Returns the {@link EntityType} of the specified entity class, from the first of the specified entity manager
     * factories which maps it as an entity.
     *
     * @param entityClass            the entity class whose {@link EntityType} is returned.
     * @param entityManagerFactories an iterable of entity manager factories.
     * @param <X>                    represented entity type
     * @return the {@link EntityType} of the {@code entityClass}.
     * @throws IllegalArgumentException when none of the {@code entityManagerFactories} maps the {@code entityClass} as
     *                                  an entity.
     * @see #findEntityType(Class, Iterable)
     */
    public static <X> EntityType<X> getEntityType(
            final Class<X> entityClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        return findEntityType(entityClass, entityManagerFactories).orElseThrow(
                () -> new IllegalArgumentException("no entity type found for entity class: " + entityClass)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __EntityTypeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
