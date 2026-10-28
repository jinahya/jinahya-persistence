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
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.Objects;
import java.util.Optional;
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
public final class JinayaManagedTypeUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Finds the {@link ManagedType} of the specified type class in the metamodel of the specified entity manager
     * factory.
     *
     * @param typeClass            the class whose {@link ManagedType} is returned; it does not have to be an entity
     *                             class.
     * @param entityManagerFactory the entity manager factory to look the {@code typeClass} up in.
     * @param <X>                  represented type
     * @return an optional of the {@link ManagedType} of the {@code typeClass}; {@link Optional#empty() empty} when the
     *         {@code entityManagerFactory} does not manage it.
     * @implNote {@link Metamodel#managedType(Class)} <em>throws</em> for a class it does not manage; it never
     *         returns {@code null}. That is what this method exists to turn into an answer a caller can branch on.
     * @see Metamodel#managedType(Class)
     */
    public static <X> Optional<ManagedType<X>> findManagedType(final Class<X> typeClass,
                                                               final EntityManagerFactory entityManagerFactory) {
        Objects.requireNonNull(typeClass, "typeClass is null");
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        try {
            return Optional.of(entityManagerFactory.getMetamodel().managedType(typeClass));
        } catch (final IllegalArgumentException iae) {
            return Optional.empty();
        }
    }

    /**
     * Finds the {@link ManagedType} of the specified type class, in the first of the specified entity manager factories
     * which knows it.
     *
     * @param typeClass              the class whose {@link ManagedType} is returned; it does not have to be an entity
     *                               class.
     * @param entityManagerFactories an iterable of entity manager factories to look the {@code typeClass} up in.
     * @param <X>                    represented type
     * @return an optional of the {@link ManagedType} of the {@code typeClass}; {@link Optional#empty() empty} when none
     *         of the {@code entityManagerFactories} manages it.
     * @see #findManagedType(Class, EntityManagerFactory)
     */
    public static <X> Optional<ManagedType<X>> findManagedType(
            final Class<X> typeClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        Objects.requireNonNull(typeClass, "typeClass is null");
        Objects.requireNonNull(entityManagerFactories, "entityManagerFactories is null");
        return StreamSupport.stream(entityManagerFactories.spliterator(), false)
                .map(f -> findManagedType(typeClass, f))
                .flatMap(Optional::stream)
                .findFirst();
    }

    /**
     * Returns the {@link ManagedType} of the specified type class, from the metamodel of the specified entity manager
     * factory.
     *
     * @param typeClass            the class whose {@link ManagedType} is returned; it does not have to be an entity
     *                             class.
     * @param entityManagerFactory the entity manager factory to look the {@code typeClass} up in.
     * @param <X>                  represented type
     * @return the {@link ManagedType} of the {@code typeClass}.
     * @throws IllegalArgumentException when the {@code entityManagerFactory} does not manage the {@code typeClass}.
     * @see #findManagedType(Class, EntityManagerFactory)
     */
    public static <X> ManagedType<X> getManagedType(final Class<X> typeClass,
                                                    final EntityManagerFactory entityManagerFactory) {
        return findManagedType(typeClass, entityManagerFactory).orElseThrow(
                () -> new IllegalArgumentException("no managed type found for type class: " + typeClass)
        );
    }

    /**
     * Returns the {@link ManagedType} of the specified type class, from the first of the specified entity manager
     * factories which knows it.
     *
     * @param typeClass              the class whose {@link ManagedType} is returned; it does not have to be an entity
     *                               class.
     * @param entityManagerFactories an iterable of entity manager factories to look the {@code typeClass} up in.
     * @param <X>                    represented type
     * @return the {@link ManagedType} of the {@code typeClass}.
     * @throws IllegalArgumentException when none of the {@code entityManagerFactories} manages the {@code typeClass}.
     * @implNote The result used to be memoized in a {@code static} map keyed by the {@code typeClass} alone.
     *         That cache answered a later call from a <em>different</em> set of factories with the first caller's
     *         factory, and, being keyed by a {@link Class} and never evicted, pinned the application's classes for the
     *         life of the JVM. The lookup it saved is a map lookup inside the provider's metamodel, so it is now
     *         performed on each call.
     * @see #findManagedType(Class, Iterable)
     */
    public static <X> ManagedType<X> getManagedType(
            final Class<X> typeClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        return findManagedType(typeClass, entityManagerFactories).orElseThrow(
                () -> new IllegalArgumentException("no managed type found for type class: " + typeClass)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Resolves the specified dotted attribute path, {@code id.promoId} say, from the specified managed type, and
     * returns the attribute the last segment names.
     * <p>
     * Each segment but the last has to name a singular attribute whose type is itself a managed type &mdash; an
     * embeddable or an entity &mdash; which the next segment is then resolved against. The last segment may name any
     * attribute.
     *
     * @param managedType   the managed type to start from.
     * @param attributePath the path to resolve; one or more attribute names joined by {@code .}.
     * @return the attribute the last segment of {@code attributePath} names.
     * @throws IllegalArgumentException when a segment names no attribute of the type it is resolved against, or when a
     *                                  segment but the last names an attribute whose type is not a managed type.
     * @see ManagedType#getAttribute(String)
     */
    public static Attribute<?, ?> resolveAttributePath(final ManagedType<?> managedType,
                                                       final String attributePath) {
        Objects.requireNonNull(managedType, "managedType is null");
        Objects.requireNonNull(attributePath, "attributePath is null");
        final var segments = attributePath.split("\\.", -1);
        ManagedType<?> current = managedType;
        Attribute<?, ?> attribute = null;
        for (int i = 0; i < segments.length; i++) {
            if (current == null) {
                throw new IllegalArgumentException(
                        "'" + segments[i - 1] + "' of '" + attributePath + "' is not of a managed type;"
                        + " cannot resolve '" + segments[i] + "' against it");
            }
            attribute = current.getAttribute(segments[i]); // IllegalArgumentException when absent
            current = attribute instanceof SingularAttribute<?, ?> singular
                      && singular.getType() instanceof ManagedType<?> next ? next : null;
        }
        return Objects.requireNonNull(attribute, "attribute is null"); // the split gives one segment at least
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private JinayaManagedTypeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
