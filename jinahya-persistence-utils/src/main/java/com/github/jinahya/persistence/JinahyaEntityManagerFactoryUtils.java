package com.github.jinahya.persistence;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

import java.util.Objects;
import java.util.Optional;

/**
 * A utility class for {@link EntityManagerFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaEntityManagerFactoryUtils {

    /**
     * Returns the id of the specified entity as the specified type.
     *
     * @param factory an entity manager factory.
     * @param entity  the entity instance whose id is to be returned.
     * @param type    the type of the id.
     * @param <ID>    identifier type parameter
     * @return the id of the {@code entity} cast as {@link ID}; {@code null} when the {@code entity} does not yet have
     *         an id.
     * @apiNote this method is just a shortcut for
     *         {@code factory.getPersistenceUnitUtil().getIdentifier(entity)}.
     * @see EntityManagerFactory#getPersistenceUnitUtil()
     * @see PersistenceUnitUtil#getIdentifier(Object)
     */
    public static <ID> @Nullable ID getIdentifier(final @Nonnull EntityManagerFactory factory,
                                                  final @Nonnull Object entity, final @Nonnull Class<ID> type) {
        Objects.requireNonNull(factory, "factory is null");
        Objects.requireNonNull(entity, "entity is null");
        Objects.requireNonNull(type, "type is null");
        return Optional
                .ofNullable(factory.getPersistenceUnitUtil().getIdentifier(entity))
                .map(type::cast)
                .orElse(null);
    }

    /**
     * Returns the id of the specified entity.
     *
     * @param factory an entity manager factory.
     * @param entity  the entity instance whose id is to be returned.
     * @param <ID>    identifier type parameter
     * @return the id of the {@code entity} cast as {@link ID}; {@code null} when the {@code entity} does not yet have
     *         an id.
     * @apiNote This method invokes
     *         {@link #getIdentifier(EntityManagerFactory, Object, Class) getIdentifier(factory, entity, type)} method
     *         with {@code factory}, {@code entity}, and {@code Object.class}, and returns the result cast as
     *         {@link ID}, which means this method is just a shortcut for
     *         {@code (ID) factory.getPersistenceUnitUtil().getIdentifier(entity)}.
     * @see EntityManagerFactory#getPersistenceUnitUtil()
     * @see PersistenceUnitUtil#getIdentifier(Object)
     */
    public static <ID> @Nullable ID getIdentifier(final @Nonnull EntityManagerFactory factory,
                                                  final @Nonnull Object entity) {
        @SuppressWarnings({"unchecked"})
        final var id = (ID) getIdentifier(factory, entity, Object.class);
        return id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaEntityManagerFactoryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
