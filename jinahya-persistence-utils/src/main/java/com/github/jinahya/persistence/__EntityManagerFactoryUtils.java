package com.github.jinahya.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.metamodel.Metamodel;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A utility class for {@link EntityManagerFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __EntityManagerFactoryUtils {

    /**
     * Returns the {@link PersistenceUnitUtil} of the specified entity manager factory.
     *
     * @param factory the entity manager factory whose {@link PersistenceUnitUtil} is returned.
     * @return the {@link PersistenceUnitUtil} of the {@code factory}.
     * @implNote This used to memoize the result in a {@code WeakHashMap} keyed by the {@code factory}. The
     *         memoization could never expire &mdash; a {@link PersistenceUnitUtil} holds its factory, so the value
     *         strongly reached its own key and the entry outlived the factory it was supposed to track &mdash; and it
     *         bought nothing, because both providers return an already-built instance from a field.
     * @see EntityManagerFactory#getPersistenceUnitUtil()
     */
    static PersistenceUnitUtil getPersistenceUnitUtil(final EntityManagerFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        return factory.getPersistenceUnitUtil();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the id of the specified entity.
     *
     * @param factory an entity manager factory.
     * @param entity  the entity instance whose id is to be returned.
     * @param <Y>     identifier type parameter
     * @return the id of the {@code entity} cast as {@link Y}; {@code null} when the {@code entity} does not yet have an
     *         id.
     * @see EntityManagerFactory#getPersistenceUnitUtil()
     * @see PersistenceUnitUtil#getIdentifier(Object)
     */
    @SuppressWarnings({"unchecked"})
    public static <Y> @Nullable Y getIdentifier(final EntityManagerFactory factory,
                                                final Object entity) {
        Objects.requireNonNull(factory, "factory is null");
        Objects.requireNonNull(entity, "entity is null");
        return (Y) getPersistenceUnitUtil(factory).getIdentifier(entity);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the {@link Metamodel} of the specified entity manager factory.
     *
     * @param factory the entity manager factory whose {@link Metamodel} is returned.
     * @return the {@link Metamodel} of the {@code factory}.
     * @implNote Memoized here until it turned out that the memoization both leaked and paid for nothing; see
     *         {@link #getPersistenceUnitUtil(EntityManagerFactory)}, whose cache had the same shape and was removed
     *         for the same reasons.
     * @see EntityManagerFactory#getMetamodel()
     */
    public static Metamodel getMetamodel(final EntityManagerFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        return factory.getMetamodel();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __EntityManagerFactoryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
