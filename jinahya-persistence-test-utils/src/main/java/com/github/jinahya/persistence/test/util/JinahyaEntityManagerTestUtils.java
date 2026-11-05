package com.github.jinahya.persistence.test.util;

import com.github.jinahya.persistence.metamodel.util.JinahyaEntityTypeUtils;
import com.github.jinahya.persistence.util.JinahyaEntityManagerUtils;
import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for testing with an {@link EntityManager}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaEntityManagerTestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    private static <T> Optional<T> selectEntityAt(final EntityManager entityManager, final Class<T> entityClass,
                                                  final int firstResult) {
        final var builder = entityManager.getCriteriaBuilder();
        final var query = builder.createQuery(entityClass);
        final var root = query.from(entityClass);
        query.select(root);
        return Optional.ofNullable(
                entityManager.createQuery(query)
                        .setFirstResult(firstResult)
                        .setMaxResults(1)
                        .getSingleResultOrNull()
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Selects a randomly chosen instance of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class whose instance is selected.
     * @param <T>           entity type parameter
     * @return an optional of a randomly chosen instance of the {@code entityClass}; {@code empty} when there is none.
     * @throws NullPointerException when either argument is {@code null}.
     * @implNote The offset is chosen from the first {@link Integer#MAX_VALUE} instances only, for
     *         {@link jakarta.persistence.TypedQuery#setFirstResult(int)} takes an {@code int}.
     */
    public static <T> Optional<T> selectRandomEntity(final EntityManager entityManager, final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var index = JinahyaEntityManagerUtils.getSelectableEntityIndex(entityManager, entityClass);
        if (index.isEmpty()) {
            return Optional.empty();
        }
        final var firstResult = index.getAsInt();
        return selectEntityAt(entityManager, entityClass, firstResult);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Selects a reference to a randomly chosen instance of the specified entity class.
     * <p>
     * For a single identifier attribute, either an {@link jakarta.persistence.Id @Id} or an
     * {@link jakarta.persistence.EmbeddedId @EmbeddedId}, only the identifier of a randomly chosen instance is
     * selected, and its {@link EntityManager#getReference(Class, Object) reference} is returned. For an
     * {@link jakarta.persistence.IdClass @IdClass}, whose identifier no single path holds, a randomly chosen instance
     * itself is selected and returned.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class whose instance is referenced.
     * @param <T>           entity type parameter
     * @return an optional of a reference to a randomly chosen instance of the {@code entityClass}; {@code empty} when
     *         there is none.
     * @throws NullPointerException     when either argument is {@code null}.
     * @throws IllegalArgumentException when the {@code entityClass} is not an entity.
     * @apiNote A reference's state may be fetched lazily, so it is to be used, e.g. as the target of an
     *         association, while the {@code entityManager} is open.
     * @implNote The offset is chosen from the first {@link Integer#MAX_VALUE} instances only, for
     *         {@link jakarta.persistence.TypedQuery#setFirstResult(int)} takes an {@code int}.
     * @see EntityManager#getReference(Class, Object)
     */
    public static <T> Optional<T> selectRandomEntityReference(final EntityManager entityManager,
                                                              final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var entityType = entityManager.getMetamodel().entity(entityClass);
        final var index = JinahyaEntityManagerUtils.getSelectableEntityIndex(entityManager, entityClass);
        if (index.isEmpty()) {
            return Optional.empty();
        }
        final var firstResult = index.getAsInt();
        if (!entityType.hasSingleIdAttribute()) {
            return selectEntityAt(entityManager, entityClass, firstResult);
        }
        final var builder = entityManager.getCriteriaBuilder();
        final var query = builder.createQuery(Object.class);
        final var root = query.from(entityType);
        query.select(root.get(JinahyaEntityTypeUtils.getSingleIdAttribute(entityType)));
        return Optional.ofNullable(
                entityManager.createQuery(query)
                        .setFirstResult(firstResult)
                        .setMaxResults(1)
                        .getSingleResultOrNull()
        ).map(id -> entityManager.getReference(entityClass, id));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Selects the identifier of a randomly chosen instance of the specified entity class; the
     * {@link jakarta.persistence.PersistenceUnitUtil#getIdentifier(Object) identifier} of the
     * {@link #selectRandomEntityReference(EntityManager, Class) randomly chosen reference}.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class whose identifier is selected.
     * @param <T>           entity type parameter
     * @param <U>           identifier type parameter
     * @return an optional of the identifier of a randomly chosen instance of the {@code entityClass}; {@code empty}
     *         when there is none.
     * @throws NullPointerException     when either argument is {@code null}.
     * @throws IllegalArgumentException when the {@code entityClass} is not an entity.
     * @apiNote The {@code U} is inferred from the call site, e.g.
     *         {@code Optional<Long> id = selectRandomEntityId(em, Some.class)}, and is not checked against the type of the
     *         identifier; a wrong one fails with a {@link ClassCastException} where the value is used, not here. The
     *         identifier of a primitive type comes as its wrapper.
     * @implNote The offset is chosen from the first {@link Integer#MAX_VALUE} instances only, for
     *         {@link jakarta.persistence.TypedQuery#setFirstResult(int)} takes an {@code int}.
     * @see #selectRandomEntityReference(EntityManager, Class)
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Optional<U> selectRandomEntityId(final EntityManager entityManager,
                                                          final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        return selectRandomEntityReference(entityManager, entityClass)
                .map(r -> (U) persistenceUnitUtil.getIdentifier(r));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaEntityManagerTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
