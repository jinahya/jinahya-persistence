package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JakartaPersistenceTestUtils;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assumptions.assumeThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntityPersistenceTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___MappedEntityTest<ENTITY, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityPersistenceTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
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
    protected <R> R applyEntityManagerFactory(final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(entityManagerFactory);
    }

    protected void acceptEntityManagerFactory(final Consumer<? super EntityManagerFactory> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerFactory(emf -> {
            consumer.accept(emf);
            return null;
        });
    }

    // -------=------------------------------------------------------------------------------------------- entityManager
    protected <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        return function.apply(entityManager);
    }

    protected void acceptEntityManager(final Consumer<? super EntityManager> consumer) {
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    //    protected
    private <R> R applyEntityManagerInTransaction(final Function<? super EntityManager, ? extends R> function,
                                                  final boolean rollback) {
        return applyEntityManager(em -> {
            return __JakartaPersistenceTestUtils.applyEntityManagerInTransaction(em, function, rollback);
        });
    }

    //    protected
    private void acceptEntityManagerInTransaction(final Consumer<? super EntityManager> consumer,
                                                  final boolean rollback) {
        applyEntityManagerInTransaction(
                em -> {
                    consumer.accept(em);
                    return null;
                },
                rollback
        );
    }

    protected <R> R applyEntityManagerInTransactionAndRollback(
            final Function<? super EntityManager, ? extends R> function) {
        return applyEntityManagerInTransaction(function, true);
    }

    protected void acceptEntityManagerInTransactionAndRollback(
            final Consumer<? super EntityManager> consumer) {
        applyEntityManagerInTransactionAndRollback(rm -> {
            consumer.accept(rm);
            return null;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private EntityManagerFactory entityManagerFactory;

    @Inject
    private EntityManager entityManager;
}
