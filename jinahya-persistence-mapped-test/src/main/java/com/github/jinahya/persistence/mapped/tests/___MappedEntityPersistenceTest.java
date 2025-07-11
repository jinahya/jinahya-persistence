package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JakartaPersistenceTestUtils;
import com.github.jinahya.persistence.mapped.tests.util.__OrgHibernateOrmTestUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assumptions.assumeThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
abstract class ___MappedEntityPersistenceTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___MappedEntityTest<ENTITY, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    ___MappedEntityPersistenceTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    protected void persistEntityInstance() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            final var entityInstanceOptional = newRandomizedEntityInstance();
            assumeThat(entityInstanceOptional)
                    .as("optional of randomized entity instance of %s", entityClass)
                    .isNotEmpty();
            final ENTITY entityInstance = entityInstanceOptional.get();
            persistingEntityInstance(entityInstance);
            em.persist(entityInstance);
            em.flush();
            persistedEntityInstance(entityInstance);
        });
    }

    protected void persistingEntityInstance(final ENTITY entityInstance) {
        // empty
    }

    protected void persistedEntityInstance(final ENTITY entityInstance) {
        // empty
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory
    protected final <R> R applyEntityManagerFactory(
            final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getEntityManagerFactory());
    }

    protected final void acceptEntityManagerFactory(final Consumer<? super EntityManagerFactory> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerFactory(emf -> {
            consumer.accept(emf);
            return null;
        });
    }

    protected final Map<String, Object> getEntityManagerProperties() {
        return applyEntityManagerFactory(EntityManagerFactory::getProperties);
    }

    // -------=------------------------------------------------------------------------------------------- entityManager
    protected final <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        return function.apply(getEntityManager());
    }

    protected final void acceptEntityManager(final Consumer<? super EntityManager> consumer) {
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    protected final <R> R applyEntityManagerInTransaction(final Function<? super EntityManager, ? extends R> function,
                                                          final boolean rollback) {
        return applyEntityManager(em -> {
            return __JakartaPersistenceTestUtils.applyEntityManagerInTransaction(em, function, rollback);
        });
    }

    protected final void acceptEntityManagerInTransaction(final Consumer<? super EntityManager> consumer,
                                                          final boolean rollback) {
        applyEntityManagerInTransaction(
                em -> {
                    consumer.accept(em);
                    return null;
                },
                rollback
        );
    }

    protected final <R> R applyEntityManagerInTransactionAndRollback(
            final Function<? super EntityManager, ? extends R> function) {
        return applyEntityManagerInTransaction(function, true);
    }

    protected final void acceptEntityManagerInTransactionAndRollback(
            final Consumer<? super EntityManager> consumer) {
        applyEntityManagerInTransactionAndRollback(rm -> {
            consumer.accept(rm);
            return null;
        });
    }

    /**
     * Applies a connection, unwrapped from the {@link #applyEntityManager(Function) entity manager}, to the specified
     * function, and returns the result.
     *
     * @param function the function.
     * @param rollback {@code true} for rollback; {@code false} otherwise.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    protected final <R> R applyConnectionInTransaction(
            @Nonnull final Function<? super Connection, ? extends R> function,
            final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransaction(
                em -> {
                    try {
                        final var connection = em.unwrap(Connection.class);
                        return function.apply(connection);
                    } catch (final PersistenceException pe) {
                        // empty
                    }
                    return __OrgHibernateOrmTestUtils.applyConnection(em, function);
                },
                rollback
        );
    }

    protected final void acceptConnectionInTransaction(@Nonnull final Consumer<? super Connection> consumer,
                                                       final boolean rollback) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyConnectionInTransaction(
                c -> {
                    consumer.accept(c);
                    return null;
                },
                rollback
        );
    }

    protected final <R> R applyConnectionInTransactionAndRollback(
            @Nonnull final Function<? super Connection, ? extends R> function) {
        return applyConnectionInTransaction(function, true);
    }

    protected final void acceptConnectionInTransactionAndRollback(
            @Nonnull final Consumer<? super Connection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyConnectionInTransactionAndRollback(
                c -> {
                    consumer.accept(c);
                    return null;
                }
        );
    }

    //        @Disabled
    @Test
    void __() {
        acceptConnectionInTransactionAndRollback(c -> {
            try {
                final var m = c.getMetaData();
                try (var r = m.getCatalogs()) {
                    while (r.next()) {
                        System.out.println("TABLE_CAT: " + r.getString("TABLE_CAT"));
                    }
                }
                try (var r = m.getSchemas()) {
                    while (r.next()) {
                        System.out.println("TABLE_SCHEM: " + r.getString("TABLE_SCHEM"));
                    }
                }
                try (var r = m.getTables(null, null, "%", null)) {
                    while (r.next()) {
                        System.out.println("TABLE_CAT: " + r.getString("TABLE_CAT"));
                        System.out.println("TABLE_SCHEM: " + r.getString("TABLE_SCHEM"));
                        System.out.println("TABLE_NAME: " + r.getString("TABLE_NAME"));
                    }
                }
                try (var r = m.getColumns(null, null, "%", "%")) {
                    while (r.next()) {
                        System.out.println("TABLE_CAT: " + r.getString("TABLE_CAT"));
                        System.out.println("TABLE_SCHEM: " + r.getString("TABLE_SCHEM"));
                        System.out.println("TABLE_NAME: " + r.getString("TABLE_NAME"));
                        System.out.println("COLUMN_NAME: " + r.getString("COLUMN_NAME"));
                        System.out.println();
                    }
                }
            } catch (final SQLException sqle) {

            }
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    abstract EntityManagerFactory getEntityManagerFactory();

    abstract EntityManager getEntityManager();
}
