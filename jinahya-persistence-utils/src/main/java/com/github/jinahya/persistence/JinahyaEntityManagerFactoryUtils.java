package com.github.jinahya.persistence;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.metamodel.Metamodel;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * A utility class for {@link EntityManagerFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaEntityManagerFactoryUtils {

    private static final Map<EntityManagerFactory, PersistenceUnitUtil> PERSISTENCE_UNIT_UTILS =
            Collections.synchronizedMap(new WeakHashMap<>());

    static @Nonnull PersistenceUnitUtil getPersistenceUnitUtil(final @Nonnull EntityManagerFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        return PERSISTENCE_UNIT_UTILS.computeIfAbsent(
                factory,
                EntityManagerFactory::getPersistenceUnitUtil
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
//    private static final Map<Object, Object> IDENTIFIERS = Collections.synchronizedMap(new WeakHashMap<>());

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
    public static <Y> @Nullable Y getIdentifier(final @Nonnull EntityManagerFactory factory,
                                                final @Nonnull Object entity) {
        Objects.requireNonNull(factory, "factory is null");
        Objects.requireNonNull(entity, "entity is null");
//        return (T) IDENTIFIERS.computeIfAbsent(
//                entity,
//                k -> getPersistenceUnitUtil(factory).getIdentifier(k)
//        );
        return (Y) getPersistenceUnitUtil(factory).getIdentifier(entity);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<EntityManagerFactory, Metamodel> METAMODELS =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static @Nonnull Metamodel getMetamodel(final @Nonnull EntityManagerFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        return METAMODELS.computeIfAbsent(
                factory,
                EntityManagerFactory::getMetamodel
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaEntityManagerFactoryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
