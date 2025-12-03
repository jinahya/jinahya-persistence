package com.github.jinahya.persistence;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.System.Logger.Level;

/**
 * A utility class for {@link EntityManager}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaEntityManagerUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * .
     *
     * @param manager .
     * @param entity  .
     * @param <Y>     .
     * @return .
     * @see JinahyaEntityManagerFactoryUtils#getIdentifier(EntityManagerFactory, Object)
     */
    @Deprecated(forRemoval = true)
    public static <Y> @Nullable Y getIdentifier(final @Nonnull EntityManager manager,
                                                final @Nonnull Object entity) {
        Objects.requireNonNull(manager, "manager is null");
        return JinahyaEntityManagerFactoryUtils.getIdentifier(
                manager.getEntityManagerFactory(),
                entity
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Starts a resource-level transaction of the specified entity manager, and returns the result of the specified
     * supplier.
     *
     * @param manager  the entity manager.
     * @param supplier the supplier to be applied to the {@code manager}.
     * @param rollback a flag for rolling-back; {@code true} for rolling-back; {@code false} for committing.
     * @param <R>      result type parameter
     * @return the result of the {@code supplier}.
     * @throws IllegalArgumentException when the {@code manager} is already joined to a transaction.
     */
    public static <R> R getInTransaction(final @Nonnull EntityManager manager,
                                         final @Nonnull Supplier<? extends R> supplier,
                                         final boolean rollback) {
        Objects.requireNonNull(manager, "manager is null");
        if (manager.isJoinedToTransaction()) {
            throw new IllegalArgumentException("manager is already joined to a transaction");
        }
        Objects.requireNonNull(supplier, "supplier is null");
        final var transaction = manager.getTransaction();
        transaction.begin();
        try {
            final R result = supplier.get();
            if (rollback) {
                logger.log(Level.DEBUG, "rolling back...");
                transaction.rollback();
            } else {
                logger.log(Level.WARNING, "committing...");
                transaction.commit();
            }
            return result;
        } catch (final Exception e) {
            transaction.rollback();
            throw new RuntimeException(
                    "failed to get, in transaction" +
                    "; manager: " + manager +
                    "; supplier: " + supplier +
                    "; rollback: " + rollback,
                    e
            );
        }
    }

    public static <R> R getInTransactionAndRollback(final @Nonnull EntityManager manager,
                                                    final @Nonnull Supplier<? extends R> supplier) {
        return getInTransaction(
                manager,
                supplier,
                true
        );
    }

    /**
     * Starts a resource-level transaction of the specified entity manager, applies it to the specified function, and
     * returns the result.
     *
     * @param manager  the entity manager.
     * @param function the function to be applied to the {@code manager}.
     * @param rollback a flag for rolling-back; {@code true} for rolling-back; {@code false} for committing.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when the {@code manager} is already joined to a transaction.
     * @see #applyUnwrappedConnectionInTransactionAndRollback(EntityManager, Function)
     */
    public static <R> R applyInTransaction(final @Nonnull EntityManager manager,
                                           final @Nonnull Function<? super EntityManager, ? extends R> function,
                                           final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return getInTransaction(
                manager,
                () -> function.apply(manager),
                rollback
        );
    }

    public static <R> R applyInTransactionAndRollback(
            final @Nonnull EntityManager manager,
            final @Nonnull Function<? super EntityManager, ? extends R> function) {
        return applyInTransaction(
                manager,
                function,
                true
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies a {@link Connection}, unwrapped from the specified entity manager, to the specified function, and returns
     * the result.
     *
     * @param manager  the entity manager.
     * @param function the function to be applied to a connection unwrapped from the {@code manager}.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when the {@code manager} is already joined to a transaction.
     * @apiNote this method does not close the unwrapped connection.
     */
    public static <R> R applyUnwrappedConnection(final @Nonnull EntityManager manager,
                                                 final @Nonnull Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(manager, "manager is null");
        if (!manager.isJoinedToTransaction()) {
            logger.log(Level.WARNING, "not joined to a transaction; " + manager);
        }
        Objects.requireNonNull(function, "function is null");
        try {
            final var connection = manager.unwrap(Connection.class);
            if (connection == null) {
                throw new RuntimeException("null unwrapped from " + manager);
            }
            logger.log(Level.DEBUG, "unwrapped connection: {0}", connection);
            return function.apply(connection);
        } catch (final Exception e1) {
            logger.log(Level.DEBUG, "failed to unwrap connection from " + manager, e1);
            try {
                return JinahyaHibernateUtils.applyConnection(
                        manager,
                        function
                );
            } catch (final Exception e2) {
                throw new RuntimeException("failed to unwrap connection from " + manager, e2);
            }
        }
    }

    public static <R> R applyUnwrappedConnectionInTransaction(
            final @Nonnull EntityManager manager,
            final @Nonnull Function<? super Connection, ? extends R> function,
            final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return getInTransaction(
                manager,
                () -> applyUnwrappedConnection(manager, function),
                rollback
        );
    }

    public static <R> R applyUnwrappedConnectionInTransactionAndRollback(
            final @Nonnull EntityManager manager,
            final @Nonnull Function<? super Connection, ? extends R> function) {
        return applyUnwrappedConnectionInTransaction(
                manager,
                function,
                true
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaEntityManagerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
