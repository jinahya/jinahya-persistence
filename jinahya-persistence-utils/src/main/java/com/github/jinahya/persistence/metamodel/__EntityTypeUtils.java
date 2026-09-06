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

import com.github.jinahya.persistence.__EntityManagerFactoryUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;

import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * A utility class for {@link EntityType}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Both methods here look a type up across an {@link Iterable} of entity manager factories, and take
 *         the first match. Neither memoizes: a lookup which took the first match found for a class, whichever factories
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
     */
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
     * Returns the {@link EntityType} of the specified entity class.
     *
     * @param entityClass            the entity class whose {@link EntityType} is returned.
     * @param entityManagerFactories an iterable of entity manager factories.
     * @param <X>                    represented entity type
     * @return the {@link EntityType} of the {@code entityClass}.
     * @throws IllegalArgumentException when none of the {@code entityManagerFactories} maps the {@code entityClass} as
     *                                  an entity.
     */
    public static <X> EntityType<X> getEntityType(
            final Class<X> entityClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(entityManagerFactories, "entityManagerFactories is null");
        return StreamSupport.stream(entityManagerFactories.spliterator(), false)
                .map(__EntityManagerFactoryUtils::getMetamodel)
                .map(m -> {
                    try {
                        return m.entity(entityClass);
                    } catch (final IllegalArgumentException iae) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(
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
