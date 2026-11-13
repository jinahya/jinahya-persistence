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
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * A utility class for {@link EntityType}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote The methods here look a type up across an {@link Iterable} of entity manager factories, and take the
 *         first match. None memoizes: a lookup which took the first match found for a class, whichever factories a
 *         later caller passed, is not a cache but a wrong answer waiting for a second persistence unit.
 * @see JinahyaManagedTypeUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaEntityTypeUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the {@link ManagedType} of the specified entity class.
     *
     * @param entityClass            the entity class whose {@link ManagedType} is returned.
     * @param entityManagerFactories an iterable of entity manager factories.
     * @param <X>                    represented entity type
     * @return the {@link ManagedType} of the {@code entityClass}.
     * @deprecated A managed type is not an entity type, and this class is about entity types; use
     *         {@link JinahyaManagedTypeUtils#getManagedType(Class, Iterable)}, which this now merely calls.
     */
    @Deprecated(forRemoval = true)
    public static <X> ManagedType<X> getManagedType(
            final Class<X> entityClass,
            final Iterable<? extends EntityManagerFactory> entityManagerFactories) {
        // Delegates rather than duplicating. This used to carry its own copy of the walk, and its own
        // cache, and the copy was wrong: Metamodel.managedType(Class) THROWS for a class it does not
        // manage -- it never returns null -- so the `.filter(Objects::nonNull)` was dead and the first
        // factory which did not know the class aborted the whole lookup instead of falling through.
        return JinahyaManagedTypeUtils.getManagedType(entityClass, entityManagerFactories);
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
     * Returns the entity name of the specified entity class, in the metamodel of the specified entity manager factory.
     *
     * @param entityClass          the entity class whose entity name is returned.
     * @param entityManagerFactory the entity manager factory to look the {@code entityClass} up in.
     * @return the entity name of the {@code entityClass}; the name a query refers to it by.
     * @throws IllegalArgumentException when the {@code entityManagerFactory} does not map the {@code entityClass} as an
     *                                  entity.
     * @see #getEntityType(Class, EntityManagerFactory)
     * @see EntityType#getName()
     */
    public static String getEntityName(final Class<?> entityClass,
                                       final EntityManagerFactory entityManagerFactory) {
        return getEntityType(entityClass, entityManagerFactory).getName();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the single identifier attribute, either an {@link jakarta.persistence.Id @Id} or an
     * {@link jakarta.persistence.EmbeddedId @EmbeddedId}, of the specified entity type.
     *
     * @param entityType the entity type whose identifier attribute is returned.
     * @param <X>        represented entity type
     * @return the single identifier attribute of the {@code entityType}.
     * @throws IllegalArgumentException when the {@code entityType} does not have a single identifier attribute; e.g.
     *                                  it has an {@link jakarta.persistence.IdClass @IdClass}.
     * @apiNote {@link EntityType#getId(Class)} is not used, for providers disagree on the type it expects for a
     *         primitive identifier; EclipseLink, e.g., reports {@link Long} as the {@link EntityType#getIdType() id type}
     *         of a {@code long} identifier, and then rejects it.
     * @see EntityType#hasSingleIdAttribute()
     */
    public static <X> SingularAttribute<? super X, ?> getSingleIdAttribute(final EntityType<X> entityType) {
        Objects.requireNonNull(entityType, "entityType is null");
        if (!entityType.hasSingleIdAttribute()) {
            throw new IllegalArgumentException("no single id attribute in " + entityType);
        }
        return entityType.getSingularAttributes().stream()
                .filter(SingularAttribute::isId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no single id attribute in " + entityType));
    }

    /**
     * Returns the path, from the specified path to an entity, of the value which the {@link jakarta.persistence.IdClass
     * @IdClass} holds for the specified identifier attribute; the attribute itself, or, for an association (a derived
     * identity), the identifier of the associated entity.
     *
     * @param metamodel        the metamodel to look the associated entity up in.
     * @param path             the path to an entity; e.g. a {@link jakarta.persistence.criteria.Root}.
     * @param idClassAttribute one of the {@link EntityType#getIdClassAttributes() id class attributes} of the entity.
     * @return the path of the value of the {@code idClassAttribute}.
     * @throws IllegalArgumentException when the {@code idClassAttribute} is an association to an entity which does not
     *                                  have a single identifier attribute.
     * @see EntityType#getIdClassAttributes()
     */
    public static Path<?> getIdClassAttributePath(final Metamodel metamodel, final Path<?> path,
                                                  final SingularAttribute<?, ?> idClassAttribute) {
        Objects.requireNonNull(metamodel, "metamodel is null");
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(idClassAttribute, "idClassAttribute is null");
        final var attributePath = path.get(idClassAttribute.getName());
        if (!idClassAttribute.isAssociation()) {
            return attributePath;
        }
        final var associatedType = metamodel.entity(idClassAttribute.getJavaType());
        if (!associatedType.hasSingleIdAttribute()) {
            throw new IllegalArgumentException(
                    "the entity, " + associatedType.getJavaType() + ", associated by an identifier attribute, "
                    + idClassAttribute + ", does not have a single identifier attribute"
            );
        }
        return attributePath.get(getSingleIdAttribute(associatedType).getName());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private JinahyaEntityTypeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
