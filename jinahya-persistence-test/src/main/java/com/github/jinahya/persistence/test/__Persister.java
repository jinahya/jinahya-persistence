package com.github.jinahya.persistence.test;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * An abstract class for persisting a specific class.
 *
 * @param <T> entity type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __PersisterUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __Persister<T> implements BiFunction<EntityManager, T, T> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());


    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for persisting specified class.
     *
     * @param entityClass the class to persist.
     */
    protected __Persister(final @Nonnull Class<T> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists specified instance of {@code T}, using specified entity manager, and returns the instance.
     *
     * @param entityManager  the entity manager to use.
     * @param entityInstance the instance of {@code T} to persist.
     * @return the {@code entityInstance}.
     * @apiNote This method does not flush the specified entity manager.
     * @see EntityManager#persist(Object)
     */
    @Nonnull
    @Override
    public T apply(final @Nonnull EntityManager entityManager, final @Nonnull T entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        logger.log(System.Logger.Level.TRACE, "persisting {0}", entityInstance);
        entityManager.persist(entityInstance);
        return entityInstance;
    }

    // ----------------------------------------------------------------------------------------------------- entityClass


    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to persist.
     */
    protected final Class<T> entityClass;
}
