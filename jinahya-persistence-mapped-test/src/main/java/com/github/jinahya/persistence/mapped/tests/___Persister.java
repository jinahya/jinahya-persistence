package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.tests.util.__JakartaValidationTestUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class ___Persister<ENTITY> {

    protected ___Persister(@Nonnull final Class<ENTITY> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists specified instance of {@link ENTITY} using specified entity manager.
     *
     * @param entityManager  the entity manager to use.
     * @param entityInstance the instance of {@link ENTITY} to persist.
     * @return given {@code entityInstance}.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    public ENTITY persist(final EntityManager entityManager, final ENTITY entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        __JakartaValidationTestUtils.requireValid(entityInstance);
        entityManager.persist(entityInstance);
        entityManager.flush(); // required?
        __JakartaValidationTestUtils.requireValid(entityInstance);
        return entityInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENTITY}.
     */
    protected final Class<ENTITY> entityClass;
}
