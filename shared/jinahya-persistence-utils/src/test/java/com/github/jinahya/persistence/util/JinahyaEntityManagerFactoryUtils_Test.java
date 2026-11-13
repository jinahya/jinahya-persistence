package com.github.jinahya.persistence.util;

import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JinahyaEntityManagerFactoryUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JinahyaEntityManagerFactoryUtils_Test {

    private EntityManagerFactory factory;

    private PersistenceUnitUtil persistenceUnitUtil;

    @BeforeEach
    void mockFactory() {
        factory = mock(EntityManagerFactory.class);
        persistenceUnitUtil = mock(PersistenceUnitUtil.class);
        when(factory.getPersistenceUnitUtil()).thenReturn(persistenceUnitUtil);
    }

    @DisplayName("getEntityIdentifier(factory, entity)")
    @Nested
    class GetEntityIdentifier_Test {

        @DisplayName("the identifier comes back cast to the caller's type")
        @Test
        void __casts() {
            final var entity = new Object();
            when(persistenceUnitUtil.getIdentifier(entity)).thenReturn(42L);

            final Long identifier = JinahyaEntityManagerFactoryUtils.getEntityIdentifier(factory, entity);

            assertThat(identifier).isEqualTo(42L);
        }

        @DisplayName("an entity with no identifier yet reads back as null")
        @Test
        void __nullWhenNotAssignedYet() {
            final var entity = new Object();
            when(persistenceUnitUtil.getIdentifier(entity)).thenReturn(null);

            assertThat(JinahyaEntityManagerFactoryUtils.<Long>getEntityIdentifier(factory, entity)).isNull();
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> JinahyaEntityManagerFactoryUtils.getEntityIdentifier(null, new Object()))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("factory");
            assertThatThrownBy(() -> JinahyaEntityManagerFactoryUtils.getEntityIdentifier(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("entity");
        }
    }

    @DisplayName("getEntityVersion(factory, entity)")
    @Nested
    class GetEntityVersion_Test {

        @DisplayName("the version comes back cast to the caller's type")
        @Test
        void __casts() {
            final var entity = new Object();
            when(persistenceUnitUtil.getVersion(entity)).thenReturn(7);

            final Integer version = JinahyaEntityManagerFactoryUtils.getEntityVersion(factory, entity);

            assertThat(version).isEqualTo(7);
        }

        @DisplayName("an entity with no version attribute reads back as null")
        @Test
        void __nullWhenThereIsNoVersionAttribute() {
            final var entity = new Object();
            when(persistenceUnitUtil.getVersion(entity)).thenReturn(null);

            assertThat(JinahyaEntityManagerFactoryUtils.<Integer>getEntityVersion(factory, entity)).isNull();
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> JinahyaEntityManagerFactoryUtils.getEntityVersion(null, new Object()))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("factory");
            assertThatThrownBy(() -> JinahyaEntityManagerFactoryUtils.getEntityVersion(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("entity");
        }
    }

    @DisplayName("applyEntityManager(factory, function)")
    @Nested
    class ApplyEntityManager_Test {

        @DisplayName("the function gets a new entity manager, which is closed afterwards")
        @Test
        void __appliedAndClosed() {
            final var manager = mock(EntityManager.class);
            when(factory.createEntityManager()).thenReturn(manager);

            final EntityManager applied = JinahyaEntityManagerFactoryUtils.applyEntityManager(factory, m -> m);

            assertThat(applied).isSameAs(manager);
            org.mockito.Mockito.verify(manager).close();
        }

        @DisplayName("the entity manager is closed when the function throws")
        @Test
        void __closedWhenThrown() {
            final var manager = mock(EntityManager.class);
            when(factory.createEntityManager()).thenReturn(manager);
            final var thrown = new IllegalStateException("thrown");

            assertThatThrownBy(() -> JinahyaEntityManagerFactoryUtils.applyEntityManager(factory, m -> {
                throw thrown;
            })).isSameAs(thrown);
            org.mockito.Mockito.verify(manager).close();
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> JinahyaEntityManagerFactoryUtils.applyEntityManager(null, m -> m))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("factory");
            assertThatThrownBy(() -> JinahyaEntityManagerFactoryUtils.applyEntityManager(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("function");
        }
    }

    @DisplayName("apply|acceptConnectionInTransactionAndRollback|Commit(factory, function|action)")
    @Nested
    class ApplyConnectionInTransaction_Test {

        private EntityManager manager;

        private EntityTransaction transaction;

        private Connection connection;

        @BeforeEach
        void mockManager() {
            manager = mock(EntityManager.class);
            transaction = mock(EntityTransaction.class);
            connection = mock(Connection.class);
            when(factory.createEntityManager()).thenReturn(manager);
            when(manager.getTransaction()).thenReturn(transaction);
            // the way a provider does: the function is invoked with the connection
            when(manager.callWithConnection(any())).thenAnswer(
                    i -> i.<ConnectionFunction<Connection, ?>>getArgument(0).apply(connection));
            doAnswer(i -> {
                i.<ConnectionConsumer<Connection>>getArgument(0).accept(connection);
                return null;
            }).when(manager).runWithConnection(any());
        }

        @DisplayName("the function gets the connection in a transaction, which is rolled back")
        @Test
        void __rolledBack() {
            final Connection applied = JinahyaEntityManagerFactoryUtils.applyConnectionInTransactionAndRollback(
                    factory, c -> c);

            assertThat(applied).isSameAs(connection);
            verify(transaction).begin();
            verify(transaction).rollback();
            verify(transaction, never()).commit();
            verify(manager).close();
        }

        @DisplayName("the function gets the connection in a transaction, which is committed")
        @Test
        void __committed() {
            final Connection applied = JinahyaEntityManagerFactoryUtils.applyConnectionInTransactionAndCommit(
                    factory, c -> c);

            assertThat(applied).isSameAs(connection);
            verify(transaction).begin();
            verify(transaction).commit();
            verify(transaction, never()).rollback();
            verify(manager).close();
        }

        @DisplayName("the action gets the connection in a transaction, which is rolled back")
        @Test
        void __acceptedAndRolledBack() {
            final var accepted = new AtomicReference<Connection>();

            JinahyaEntityManagerFactoryUtils.acceptConnectionInTransactionAndRollback(factory, accepted::set);

            assertThat(accepted).hasValue(connection);
            verify(transaction).begin();
            verify(transaction).rollback();
            verify(transaction, never()).commit();
            verify(manager).close();
        }

        @DisplayName("the action gets the connection in a transaction, which is committed")
        @Test
        void __acceptedAndCommitted() {
            final var accepted = new AtomicReference<Connection>();

            JinahyaEntityManagerFactoryUtils.acceptConnectionInTransactionAndCommit(factory, accepted::set);

            assertThat(accepted).hasValue(connection);
            verify(transaction).begin();
            verify(transaction).commit();
            verify(transaction, never()).rollback();
            verify(manager).close();
        }

        @DisplayName("null arguments are rejected by name, before any entity manager is created")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(
                    () -> JinahyaEntityManagerFactoryUtils.applyConnectionInTransactionAndRollback(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("function");
            assertThatThrownBy(
                    () -> JinahyaEntityManagerFactoryUtils.applyConnectionInTransactionAndCommit(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("function");
            assertThatThrownBy(
                    () -> JinahyaEntityManagerFactoryUtils.acceptConnectionInTransactionAndRollback(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("action");
            assertThatThrownBy(
                    () -> JinahyaEntityManagerFactoryUtils.acceptConnectionInTransactionAndCommit(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("action");
            verify(factory, never()).createEntityManager();
        }
    }
}
