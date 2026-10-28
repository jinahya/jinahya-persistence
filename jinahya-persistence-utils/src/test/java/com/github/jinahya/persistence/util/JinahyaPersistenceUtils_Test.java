package com.github.jinahya.persistence.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JinahyaPersistenceUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JinahyaPersistenceUtils_Test {

    private static final String PERSISTENCE_UNIT_NAME = "unit";

    private MockedStatic<Persistence> persistence;

    private EntityManagerFactory factory;

    private EntityManager manager;

    private EntityTransaction transaction;

    @BeforeEach
    void mockPersistence() {
        factory = mock(EntityManagerFactory.class);
        manager = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);
        when(factory.createEntityManager()).thenReturn(manager);
        when(manager.getTransaction()).thenReturn(transaction);
        persistence = mockStatic(Persistence.class);
        persistence.when(() -> Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)).thenReturn(factory);
    }

    @AfterEach
    void closePersistence() {
        persistence.close();
    }

    @DisplayName("applyEntityManagerFactory(persistenceUnitName, function)")
    @Nested
    class ApplyEntityManagerFactory_Test {

        @DisplayName("the function gets the unit's factory, which is closed afterwards")
        @Test
        void __appliedAndClosed() {
            final var result = JinahyaPersistenceUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, f -> f);

            assertThat(result).isSameAs(factory);
            verify(factory).close();
        }

        @DisplayName("the factory is closed when the function throws")
        @Test
        void __closedWhenThrown() {
            final var thrown = new IllegalStateException("thrown");

            assertThatThrownBy(() -> JinahyaPersistenceUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, f -> {
                throw thrown;
            })).isSameAs(thrown);
            verify(factory).close();
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> JinahyaPersistenceUtils.applyEntityManagerFactory(null, f -> f))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("persistenceUnitName");
            assertThatThrownBy(() -> JinahyaPersistenceUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("function");
        }
    }

    @DisplayName("applyEntityManager(persistenceUnitName, function)")
    @Nested
    class ApplyEntityManager_Test {

        @DisplayName("the function gets an entity manager of the unit; both are closed afterwards")
        @Test
        void __appliedAndClosed() {
            final var result = JinahyaPersistenceUtils.applyEntityManager(PERSISTENCE_UNIT_NAME, m -> m);

            assertThat(result).isSameAs(manager);
            verify(manager).close();
            verify(factory).close();
        }
    }

    @DisplayName("applyEntityManagerInTransaction(persistenceUnitName, function, rollback)")
    @Nested
    class ApplyEntityManagerInTransaction_Test {

        @DisplayName("rollback true rolls the transaction back")
        @Test
        void __rolledBack() {
            final var result = JinahyaPersistenceUtils.applyEntityManagerInTransactionAndRollback(
                    PERSISTENCE_UNIT_NAME,
                    m -> m
            );

            assertThat(result).isSameAs(manager);
            verify(transaction).begin();
            verify(transaction).rollback();
            verify(transaction, never()).commit();
            verify(manager).close();
            verify(factory).close();
        }

        @DisplayName("rollback false commits the transaction")
        @Test
        void __committed() {
            final var result = JinahyaPersistenceUtils.applyEntityManagerInTransaction(
                    PERSISTENCE_UNIT_NAME,
                    m -> m,
                    false
            );

            assertThat(result).isSameAs(manager);
            verify(transaction).begin();
            verify(transaction).commit();
            verify(transaction, never()).rollback();
        }

        @DisplayName("rollback false still rolls back, without committing, when the function throws")
        @Test
        void __rolledBackWhenThrown() {
            final var thrown = new IllegalStateException("thrown");
            when(transaction.isActive()).thenReturn(true);

            assertThatThrownBy(() -> JinahyaPersistenceUtils.applyEntityManagerInTransaction(
                    PERSISTENCE_UNIT_NAME,
                    m -> {
                        throw thrown;
                    },
                    false
            )).isSameAs(thrown);
            verify(transaction).rollback();
            verify(transaction, never()).commit();
            verify(manager).close();
            verify(factory).close();
        }
    }
}
