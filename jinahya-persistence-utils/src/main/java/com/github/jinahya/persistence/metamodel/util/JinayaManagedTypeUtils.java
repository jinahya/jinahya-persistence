package com.github.jinahya.persistence.metamodel.util;

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
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;

import java.util.Optional;

/**
 * A utility class for {@link ManagedType}, under its misspelled name.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @deprecated The name is misspelled; use {@link JinahyaManagedTypeUtils}, which each method of this class merely
 *         calls.
 */
@Deprecated(forRemoval = true)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinayaManagedTypeUtils {

    /**
     * Finds the {@link ManagedType} of the specified type class in the metamodel of the specified entity manager
     * factory.
     *
     * @param typeClass            the class whose {@link ManagedType} is returned.
     * @param entityManagerFactory the entity manager factory to look the {@code typeClass} up in.
     * @param <X>                  represented type
     * @return an optional of the {@link ManagedType} of the {@code typeClass}.
     * @deprecated use {@link JinahyaManagedTypeUtils#findManagedType(Class, EntityManagerFactory)}.
     */
    @Deprecated(forRemoval = true)
    public static <X> Optional<ManagedType<X>> findManagedType(final Class<X> typeClass,
                                                               final EntityManagerFactory entityManagerFactory) {
        return JinahyaManagedTypeUtils.findManagedType(typeClass, entityManagerFactory);
    }

    /**
     * Finds the {@link ManagedType} of the specified type class in the first of the specified entity manager factories
     * which manages it.
     *
     * @param typeClass              the class whose {@link ManagedType} is returned.
     * @param entityManagerFactories the entity manager factories to look the {@code typeClass} up in, in order.
     * @param <X>                    represented type
     * @return an optional of the {@link ManagedType} of the {@code typeClass}.
     * @deprecated use {@link JinahyaManagedTypeUtils#findManagedType(Class, Iterable)}.
     */
    @Deprecated(forRemoval = true)
    public static <X> Optional<ManagedType<X>> findManagedType(
            final Class<X> typeClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        return JinahyaManagedTypeUtils.findManagedType(typeClass, entityManagerFactories);
    }

    /**
     * Returns the {@link ManagedType} of the specified type class in the metamodel of the specified entity manager
     * factory.
     *
     * @param typeClass            the class whose {@link ManagedType} is returned.
     * @param entityManagerFactory the entity manager factory to look the {@code typeClass} up in.
     * @param <X>                  represented type
     * @return the {@link ManagedType} of the {@code typeClass}.
     * @deprecated use {@link JinahyaManagedTypeUtils#getManagedType(Class, EntityManagerFactory)}.
     */
    @Deprecated(forRemoval = true)
    public static <X> ManagedType<X> getManagedType(final Class<X> typeClass,
                                                    final EntityManagerFactory entityManagerFactory) {
        return JinahyaManagedTypeUtils.getManagedType(typeClass, entityManagerFactory);
    }

    /**
     * Returns the {@link ManagedType} of the specified type class in the first of the specified entity manager
     * factories which manages it.
     *
     * @param typeClass              the class whose {@link ManagedType} is returned.
     * @param entityManagerFactories the entity manager factories to look the {@code typeClass} up in, in order.
     * @param <X>                    represented type
     * @return the {@link ManagedType} of the {@code typeClass}.
     * @deprecated use {@link JinahyaManagedTypeUtils#getManagedType(Class, Iterable)}.
     */
    @Deprecated(forRemoval = true)
    public static <X> ManagedType<X> getManagedType(
            final Class<X> typeClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        return JinahyaManagedTypeUtils.getManagedType(typeClass, entityManagerFactories);
    }

    /**
     * Resolves the specified dotted attribute path against the specified managed type.
     *
     * @param managedType   the managed type the path starts from.
     * @param attributePath the dotted path of attribute names.
     * @return the attribute of the path's last segment.
     * @deprecated use {@link JinahyaManagedTypeUtils#resolveAttributePath(ManagedType, String)}.
     */
    @Deprecated(forRemoval = true)
    public static Attribute<?, ?> resolveAttributePath(final ManagedType<?> managedType,
                                                       final String attributePath) {
        return JinahyaManagedTypeUtils.resolveAttributePath(managedType, attributePath);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private JinayaManagedTypeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
