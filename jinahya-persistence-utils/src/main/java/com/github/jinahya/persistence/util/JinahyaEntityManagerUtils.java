package com.github.jinahya.persistence.util;

/*-
 * #%L
 * jinahya-persistence-utils
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.System.Logger.Level;

/**
 * A utility class for {@link EntityManager}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote The transaction methods here drive a <em>resource-local</em> transaction, through
 *         {@link EntityManager#getTransaction()}; a container-managed JTA entity manager rejects that call, with an
 *         {@link IllegalStateException}, before any of this can help. For the committing case on a factory, Jakarta
 *         Persistence 3.2 has {@link EntityManagerFactory#callInTransaction(Function) callInTransaction} and
 *         {@link EntityManagerFactory#runInTransaction(Consumer) runInTransaction} of its own; what is added here is
 *         the rolling-back case, which the specification has no equivalent of.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaEntityManagerUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

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
     * @throws IllegalStateException    when the {@code manager} is a JTA entity manager, which has no resource-level
     *                                  transaction to start.
     * @implNote Whatever the {@code function}, the commit or the rollback throws propagates <em>unchanged</em>,
     *         so a caller can still catch {@link jakarta.persistence.OptimisticLockException} and friends by type. A
     *         failure while rolling back is attached to it as a
     *         {@linkplain Throwable#addSuppressed(Throwable) suppressed} exception rather than replacing it. Note that
     *         {@link EntityTransaction#begin() begin()} runs before this guard, so a failure there is not cleaned up
     *         here.
     *         <p>
     *         The guard reads {@link EntityManager#isJoinedToTransaction() isJoinedToTransaction()}, which the
     *         specification words for JTA. Both providers this project supports answer it, for a resource-local entity
     *         manager, with exactly {@code getTransaction().isActive()} &mdash; Hibernate ORM through
     *         {@code JdbcResourceLocalTransactionCoordinatorImpl.isJoined()}, EclipseLink through
     *         {@code EntityTransactionWrapper.isJoinedToTransaction(..)} &mdash; so it is the same question, asked in
     *         the one way that also answers correctly for a JTA entity manager which has been joined.
     */
    public static <R> R applyInTransaction(final EntityManager manager,
                                           final Function<? super EntityManager, ? extends R> function,
                                           final boolean rollback) {
        Objects.requireNonNull(manager, "manager is null");
        if (manager.isJoinedToTransaction()) {
            throw new IllegalArgumentException("manager is already joined to a transaction");
        }
        Objects.requireNonNull(function, "function is null");
        final var transaction = manager.getTransaction();
        transaction.begin();
        try {
            final R result = function.apply(manager);
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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the {@link Connection} underlying the specified entity manager to the specified function, and returns the
     * result.
     *
     * @param manager  the entity manager.
     * @param function the function to be applied with the connection of the {@code manager}.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws PersistenceException when no connection can be obtained from the {@code manager}.
     * @apiNote The connection is the provider's, and is only borrowed: the {@code function} must neither close
     *         it nor commit or roll back on it, and must not keep it beyond its own return. A {@code manager} which is
     *         not joined to a transaction is logged, at {@link System.Logger.Level#WARNING WARNING}, and otherwise
     *         accepted &mdash; note that a provider is free to hand out no connection at all outside a transaction.
     * @implNote This delegates to {@link EntityManager#callWithConnection(ConnectionFunction)}, standard since
     *         Jakarta Persistence 3.2. It used to call {@code manager.unwrap(Connection.class)} &mdash; which the
     *         specification never promised to answer &mdash; and fall back, on failure, to a reflective dance through
     *         {@code org.hibernate.Session#doReturningWork}, which reported EclipseLink's failures as Hibernate's and
     *         needed Hibernate to be resolvable from <em>this</em> class's loader.
     *         <p>
     *         Both providers wrap whatever the function throws: Hibernate ORM in a bare {@link RuntimeException},
     *         EclipseLink in a {@link PersistenceException}, and neither limits that to the checked exceptions the
     *         specification mentions. Since a caller has to be able to catch its own failure by type, the function's
     *         throwable is captured as it leaves and rethrown here in place of the wrapper.
     */
    public static <R> R applyUnwrappedConnection(final EntityManager manager,
                                                 final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(manager, "manager is null");
        Objects.requireNonNull(function, "function is null");
        if (!manager.isJoinedToTransaction()) {
            logger.log(Level.WARNING, () -> "not joined to a transaction; " + manager);
        }
        // the function's own failure, held so the provider's wrapper can be peeled back off below
        final var thrown = new AtomicReference<Throwable>();
        try {
            return manager.<Connection, R>callWithConnection(connection -> {
                try {
                    if (connection == null) {
                        // EclipseLink reads the accessor's connection, which outside a transaction may not
                        // exist; handing the function a null Connection would only move the failure
                        throw new PersistenceException("no connection from " + manager);
                    }
                    logger.log(Level.DEBUG, "connection: {0}", connection);
                    return function.apply(connection);
                } catch (final RuntimeException | Error e) {
                    thrown.set(e);
                    throw e;
                }
            });
        } catch (final RuntimeException | Error e) {
            final var caught = thrown.get();
            if (caught == null || caught == e) {
                throw e;
            }
            // the provider wrapped the function's failure; hand back what the function actually threw
            if (caught instanceof RuntimeException re) {
                throw re;
            }
            throw (Error) caught;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Selects the number of all instances of the specified entity class; {@code SELECT COUNT(e) FROM Entity e}.
     *
     * @param manager     the entity manager.
     * @param entityClass the entity class whose instances are counted.
     * @return the number of all instances of the {@code entityClass}; {@code 0} when there is none.
     * @throws IllegalArgumentException when the {@code entityClass} is not an entity of the {@code manager}'s
     *                                  persistence unit.
     * @implNote Built with the Criteria API, rooted at the class itself, so neither the entity name nor the
     *         class name goes into a query string. {@code COUNT} with no {@code GROUP BY} always yields exactly one row
     *         holding a number, so the result is neither {@code null} nor a
     *         {@link jakarta.persistence.NoResultException NoResultException}.
     */
    public static long selectEntityCount(final EntityManager manager, final Class<?> entityClass) {
        Objects.requireNonNull(manager, "manager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var builder = manager.getCriteriaBuilder();
        final var query = builder.createQuery(Long.class);
        final var root = query.from(entityClass);
        query.select(builder.count(root));
        return manager.createQuery(query).getSingleResult();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the id of the specified entity, from the persistence unit of the specified entity manager.
     *
     * @param entityManager the entity manager.
     * @param entity        the entity instance whose id is to be returned.
     * @param <Y>           identifier type parameter
     * @return the id of the {@code entity} cast as {@link Y}; {@code null} when the {@code entity} does not yet have an
     *         id.
     * @apiNote EclipseLink answers this by instantiating the identifier class &mdash; an
     *         {@link jakarta.persistence.EmbeddedId EmbeddedId} or an {@link jakarta.persistence.IdClass IdClass}
     *         &mdash; through {@link Class#getConstructor(Class[])}, which sees public constructors only; one with no
     *         {@code public} no-arg constructor fails with {@code EclipseLink-222}. The specification requires that
     *         constructor of a primary key class to be public (Jakarta Persistence 3.2, &sect;2.4).
     * @see JinahyaEntityManagerFactoryUtils#getEntityIdentifier(EntityManagerFactory, Object)
     */
    public static <Y> Y getEntityIdentifier(final EntityManager entityManager, final Object entity) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        return JinahyaEntityManagerFactoryUtils.getEntityIdentifier(entityManager.getEntityManagerFactory(), entity);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private JinahyaEntityManagerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
