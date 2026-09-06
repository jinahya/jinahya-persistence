package com.github.jinahya.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jspecify.annotations.Nullable;

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
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __EntityManagerUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the id of the specified entity, using the entity manager factory of the specified entity manager.
     *
     * @param manager the entity manager whose {@link EntityManager#getEntityManagerFactory() entityManagerFactory} is
     *                used.
     * @param entity  the entity instance whose id is returned.
     * @param <Y>     identifier type parameter
     * @return the id of the {@code entity}; {@code null} when the {@code entity} does not yet have an id.
     * @see __EntityManagerFactoryUtils#getIdentifier(EntityManagerFactory, Object)
     * @deprecated Use {@link __EntityManagerFactoryUtils#getIdentifier(EntityManagerFactory, Object)}, with the
     *         {@link EntityManager#getEntityManagerFactory() entityManagerFactory} of the {@code manager}, instead.
     */
    @Deprecated(forRemoval = true)
    public static <Y> @Nullable Y getIdentifier(final EntityManager manager,
                                                final Object entity) {
        Objects.requireNonNull(manager, "manager is null");
        return __EntityManagerFactoryUtils.getIdentifier(
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
     * @implNote Whatever the {@code supplier}, the commit or the rollback throws propagates <em>unchanged</em>,
     *         so a caller can still catch {@link jakarta.persistence.OptimisticLockException} and friends by type. A
     *         failure while rolling back is attached to it as a
     *         {@linkplain Throwable#addSuppressed(Throwable) suppressed} exception rather than replacing it. Note that
     *         {@link jakarta.persistence.EntityTransaction#begin() begin()} runs before this guard, so a failure there
     *         is not cleaned up here.
     */
    public static <R> R getInTransaction(final EntityManager manager,
                                         final Supplier<? extends R> supplier,
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
                logger.log(Level.DEBUG, "committing...");
                transaction.commit();
            }
            return result;
        } catch (final RuntimeException | Error e) {
            // Clean up without ever replacing what actually went wrong. The whole check-then-roll-back is
            // guarded, not just the rollback: EntityTransaction permits isActive() itself to throw. And the
            // rollback is conditional because a commit which failed has already rolled the transaction back,
            // so a second one would throw IllegalStateException -- which used to escape this catch and
            // replace the commit failure outright.
            try {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            } catch (final RuntimeException | Error cleanup) {
                if (cleanup != e) {
                    e.addSuppressed(cleanup);
                }
            }
            throw e;
        }
    }

    /**
     * Starts a resource-level transaction of the specified entity manager, returns the result of the specified
     * supplier, and rolls the transaction back.
     *
     * @param manager  the entity manager.
     * @param supplier the supplier to be applied to the {@code manager}.
     * @param <R>      result type parameter
     * @return the result of the {@code supplier}.
     * @throws IllegalArgumentException when the {@code manager} is already joined to a transaction.
     * @apiNote Nothing the {@code supplier} does is persisted; this is intended for tests.
     * @see #getInTransaction(EntityManager, Supplier, boolean)
     */
    public static <R> R getInTransactionAndRollback(final EntityManager manager,
                                                    final Supplier<? extends R> supplier) {
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
    public static <R> R applyInTransaction(final EntityManager manager,
                                           final Function<? super EntityManager, ? extends R> function,
                                           final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return getInTransaction(
                manager,
                () -> function.apply(manager),
                rollback
        );
    }

    /**
     * Starts a resource-level transaction of the specified entity manager, applies it to the specified function, and
     * rolls the transaction back.
     *
     * @param manager  the entity manager.
     * @param function the function to be applied to the {@code manager}.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when the {@code manager} is already joined to a transaction.
     * @apiNote Nothing the {@code function} does is persisted; this is intended for tests.
     * @see #applyInTransaction(EntityManager, Function, boolean)
     */
    public static <R> R applyInTransactionAndRollback(
            final EntityManager manager,
            final Function<? super EntityManager, ? extends R> function) {
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
     * @throws RuntimeException when no connection can be obtained from the {@code manager}, by either route.
     * @apiNote this method does not close the unwrapped connection. A {@code manager} which is not joined to a
     *         transaction is logged, at {@link System.Logger.Level#WARNING WARNING}, and otherwise accepted.
     * @implNote An exception thrown by the {@code function} itself propagates unchanged; only a failure to
     *         <em>obtain</em> the connection falls back to {@link ___HibernateUtils}. Note that the fallback route
     *         still acquires and applies in one step, so a {@code function} which throws there is wrapped rather than
     *         propagated.
     */
    public static <R> R applyUnwrappedConnection(final EntityManager manager,
                                                 final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(manager, "manager is null");
        if (!manager.isJoinedToTransaction()) {
            logger.log(Level.WARNING, "not joined to a transaction; " + manager);
        }
        Objects.requireNonNull(function, "function is null");
        // Only the ACQUISITION of the connection is guarded here. Applying the function used to sit inside this
        // try too, so a function which threw was reported as an unwrap failure and then run a second time
        // through the Hibernate path -- which, for a function handed a Connection, means the work was done twice.
        final Connection connection;
        try {
            final var unwrapped = manager.unwrap(Connection.class);
            if (unwrapped == null) {
                throw new RuntimeException("null unwrapped from " + manager);
            }
            connection = unwrapped;
        } catch (final Exception e1) {
            logger.log(Level.DEBUG, "failed to unwrap connection from " + manager, e1);
            try {
                return ___HibernateUtils.applyConnection(
                        manager,
                        function
                );
            } catch (final Exception e2) {
                // the fallback's failure is the cause -- it is why recovery did not work; the original
                // acquisition failure rides along as suppressed, and the wrapper is fresh so that neither
                // a shared instance nor a suppression-disabled throwable can bite
                final var wrapper = new RuntimeException("failed to unwrap connection from " + manager, e2);
                wrapper.addSuppressed(e1);
                throw wrapper;
            }
        }
        logger.log(Level.DEBUG, "unwrapped connection: {0}", connection);
        return function.apply(connection);
    }

    /**
     * Starts a resource-level transaction of the specified entity manager, applies a {@link Connection}, unwrapped from
     * the {@code manager}, to the specified function, and returns the result.
     *
     * @param manager  the entity manager.
     * @param function the function to be applied to a connection unwrapped from the {@code manager}.
     * @param rollback a flag for rolling-back; {@code true} for rolling-back; {@code false} for committing.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when the {@code manager} is already joined to a transaction.
     * @apiNote this method does not close the unwrapped connection.
     * @see #applyUnwrappedConnection(EntityManager, Function)
     * @see #getInTransaction(EntityManager, Supplier, boolean)
     */
    public static <R> R applyUnwrappedConnectionInTransaction(
            final EntityManager manager,
            final Function<? super Connection, ? extends R> function,
            final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return getInTransaction(
                manager,
                () -> applyUnwrappedConnection(manager, function),
                rollback
        );
    }

    /**
     * Starts a resource-level transaction of the specified entity manager, applies a {@link Connection}, unwrapped from
     * the {@code manager}, to the specified function, and rolls the transaction back.
     *
     * @param manager  the entity manager.
     * @param function the function to be applied to a connection unwrapped from the {@code manager}.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when the {@code manager} is already joined to a transaction.
     * @apiNote this method does not close the unwrapped connection, and nothing the {@code function} does is
     *         persisted; this is intended for tests.
     * @see #applyUnwrappedConnectionInTransaction(EntityManager, Function, boolean)
     */
    public static <R> R applyUnwrappedConnectionInTransactionAndRollback(
            final EntityManager manager,
            final Function<? super Connection, ? extends R> function) {
        return applyUnwrappedConnectionInTransaction(
                manager,
                function,
                true
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __EntityManagerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
