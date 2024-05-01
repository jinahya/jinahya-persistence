package com.github.jinahya.persistence.metamodel;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;

import java.util.Objects;

/**
 * Utilities for {@link Metamodel}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaMetamodelUtil {

    /**
     * Returns the {@link EntityType#getName() name} of specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class.
     * @return the {@link EntityType#getName() name} of the {@code entityClass}.
     * @see EntityManager#getMetamodel()
     * @see Metamodel#entity(Class)
     * @see EntityType#getName()
     */
    public static String getEntityName(final @Nonnull EntityManager entityManager,
                                       final @Nonnull Class<?> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var metamodel = entityManager.getMetamodel();
        final var entityType = metamodel.entity(entityClass); // IllegalArgumentException
        return entityType.getName();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaMetamodelUtil() {
        throw new AssertionError("instantiation is not allowed");
    }
}
