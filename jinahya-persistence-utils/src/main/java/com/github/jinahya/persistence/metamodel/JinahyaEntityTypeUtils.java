package com.github.jinahya.persistence.metamodel;

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

import com.github.jinahya.persistence.JinahyaEntityManagerFactoryUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.StreamSupport;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaEntityTypeUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<?>, ManagedType<?>> MANAGED_TYPES = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Returns the {@link ManagedType} of the specified entity class.
     *
     * @param entityClass            the entity class whose {@link ManagedType} is returned.
     * @param entityManagerFactories an iterable of entity manager factories.
     * @param <X>                    represented entity type
     * @return the {@link ManagedType} of the {@code entityClass}.
     */
    public static <X> ManagedType<X> getManagedType(
            final @Nonnull Class<X> entityClass,
            final @Nonnull Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(entityManagerFactories, "entityManagerFactories is null");
        @SuppressWarnings({"unchecked"})
        final var managedType = (ManagedType<X>) MANAGED_TYPES.computeIfAbsent(
                entityClass,
                k -> {
                    return StreamSupport.stream(entityManagerFactories.spliterator(), false)
//                            .map(EntityManagerFactory::getMetamodel)
                            .map(JinahyaEntityManagerFactoryUtils::getMetamodel)
                            .map(m -> m.managedType(entityClass))
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElseThrow(
                                    () -> new IllegalArgumentException(
                                            "no entity type found for entity class: " + entityClass
                                    )
                            );
                }
        );
        return managedType;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<?>, EntityType<?>> ENTITY_TYPES = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Returns the {@link EntityType} of the specified entity class.
     *
     * @param entityClass            the entity class whose {@link EntityType} is returned.
     * @param entityManagerFactories an iterable of entity manager factories.
     * @param <X>                    represented entity type
     * @return the {@link EntityType} of the {@code entityClass}.
     */
    public static <X> EntityType<X> getEntityType(
            final @Nonnull Class<X> entityClass,
            final @Nonnull Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(entityManagerFactories, "entityManagerFactories is null");
        @SuppressWarnings({"unchecked"})
        final var entityType = (EntityType<X>) ENTITY_TYPES.computeIfAbsent(
                entityClass,
                k -> {
                    try {
                        final var managedType =
                                JinahyaManagedTypeUtils.getManagedType(entityClass, entityManagerFactories);
                        if (managedType instanceof EntityType<?> et && et.getJavaType() == entityClass) {
                            return et;
                        }
                    } catch (final IllegalArgumentException iae) {
                        // empty
                    }
                    return StreamSupport.stream(entityManagerFactories.spliterator(), false)
//                            .map(EntityManagerFactory::getMetamodel)
                            .map(JinahyaEntityManagerFactoryUtils::getMetamodel)
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
                                    () -> new IllegalArgumentException(
                                            "no entity type found for entity class: " + entityClass
                                    )
                            );
                }
        );
        return entityType;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaEntityTypeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
