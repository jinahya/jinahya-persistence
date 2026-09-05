package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * An abstract class for persisting a specific class.
 * <p>
 * The default {@link #apply(EntityManager, Object) apply(entityManager, entityInstance)} method simply persists the
 * instance; override it for an entity whose required associations must be persisted first, calling
 * {@code super.apply(...)} for the instance itself.
 *
 * @param <T> the entity type to persist.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __PersisterLocator#STANDARD
 * @see __PersisterUtils#newPersistedInstanceOf(EntityManager, Class)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __Persister<T> implements BiFunction<EntityManager, T, T> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

//SEP:CONSTRUCTORS

    /**
     * Creates a new instance for persisting specified class.
     *
     * @param entityClass the class to persist.
     * @throws NullPointerException when the {@code entityClass} is {@code null}.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies the {@code entityClass}, for
     *         that is how a located persister class is instantiated.
     * @see #entityClass
     */
    protected __Persister(final @Nonnull Class<T> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Persists specified instance of {@code T}, using specified entity manager, and returns the instance.
     *
     * @param entityManager  the entity manager to use.
     * @param entityInstance the instance of {@code T} to persist.
     * @return the {@code entityInstance}.
     * @throws NullPointerException when either argument is {@code null}.
     * @apiNote This method does not flush the specified entity manager.
     * @implSpec An override should return the very {@code entityInstance} it was given; callers, including
     *         {@link __PersisterUtils}, rely on the returned instance being the one they passed in.
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

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to persist.
     */
    protected final Class<T> entityClass;
}
