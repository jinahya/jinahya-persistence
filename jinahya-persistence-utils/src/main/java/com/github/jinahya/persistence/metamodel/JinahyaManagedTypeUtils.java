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

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.ManagedType;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

/**
 * A utility class for {@link ManagedType}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see JinahyaEntityTypeUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaManagedTypeUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    static final Map<Class<?>, ManagedType<?>> MANAGED_TYPES = new ConcurrentHashMap<>();

    /**
     * Returns the {@link ManagedType} of the specified type class, from the first of the specified entity manager
     * factories which knows it, caching the result against the {@code typeClass}.
     *
     * @param typeClass              the class whose {@link ManagedType} is returned; it does not have to be an entity
     *                               class.
     * @param entityManagerFactories an iterable of entity manager factories to look the {@code typeClass} up in.
     * @param <X>                    represented type
     * @return the {@link ManagedType} of the {@code typeClass}.
     * @throws IllegalArgumentException when none of the {@code entityManagerFactories} manages the {@code typeClass}.
     * @see jakarta.persistence.metamodel.Metamodel#managedType(Class)
     */
    public static <X> ManagedType<X> getManagedType(
            final @Nonnull Class<X> typeClass,
            final @Nonnull Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        Objects.requireNonNull(typeClass, "typeClass is null");
        Objects.requireNonNull(entityManagerFactories, "entityManagerFactories is null");
        @SuppressWarnings({"unchecked"})
        final var managedType = (ManagedType<X>) MANAGED_TYPES.computeIfAbsent(
                typeClass,
                k -> StreamSupport.stream(entityManagerFactories.spliterator(), false)
                        .map(EntityManagerFactory::getMetamodel)
                        .map(m -> {
                            try {
                                return m.managedType(typeClass);
                            } catch (final IllegalArgumentException iae) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "no entity type found for entity class: " + typeClass)
                        ));
        return managedType;
    }

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Creates a new instance, which is not allowed.
     */
    private JinahyaManagedTypeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
