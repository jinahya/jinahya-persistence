package com.github.jinahya.persistence.test.util;

import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * A skeletal implementation of {@link EntityPersister}, for persisting a specific entity class.
 * <p>
 * The {@link #apply(EntityManager, Object) apply(entityManager, entityInstance)} method simply persists the instance;
 * override it for an entity whose required associations have to be persisted first, calling {@code super.apply(...)}
 * for the instance itself.
 * <p>
 * The {@link #entityClass} an instance is constructed with is what
 * {@link EntityPersisterUtils#newPersisterInstanceOf(Class)} checks a located persister against; a persister which
 * implements {@link EntityPersister} directly declares nothing of the kind, and so is taken as it is.
 *
 * @param <T> the entity type to persist.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityPersisterUtils#newPersistedInstanceOf(EntityManager, Class)
 */
public abstract class AbstractEntityPersister<T>
        implements EntityPersister<T> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for persisting the specified entity class.
     *
     * @param entityClass the entity class to persist.
     * @throws NullPointerException when the {@code entityClass} is {@code null}.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies the
     *         {@code entityClass}, for that is how a located persister class is instantiated.
     * @see #entityClass
     */
    protected AbstractEntityPersister(final Class<T> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }


    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityManager  {@inheritDoc}
     * @param entityInstance {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException when either argument is {@code null}.
     * @implSpec The default implementation {@link EntityManager#persist(Object) persists} the
     *         {@code entityInstance}, and returns it; it does not flush.
     */
    @Override
    public T apply(final EntityManager entityManager, final T entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        logger.log(System.Logger.Level.TRACE, "persisting {0}", entityInstance);
        entityManager.persist(entityInstance);
        logger.log(System.Logger.Level.TRACE, "persisted {0}", entityInstance);
        return entityInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to persist.
     */
    protected final Class<T> entityClass;
}
