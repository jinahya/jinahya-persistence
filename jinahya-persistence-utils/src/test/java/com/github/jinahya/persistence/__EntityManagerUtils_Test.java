package com.github.jinahya.persistence;

import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link __EntityManagerUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __EntityManagerUtils_Test {

    @DisplayName("getInTransaction(manager, supplier, rollback)")
    @Nested
    class GetInTransaction_Test {

        private EntityManager manager;

        private EntityTransaction transaction;

        @BeforeEach
        void mockManager() {
            manager = mock(EntityManager.class);
            transaction = mock(EntityTransaction.class);
            when(manager.isJoinedToTransaction()).thenReturn(false);
            when(manager.getTransaction()).thenReturn(transaction);
        }

        @DisplayName("a failing commit is not masked by a rollback of the dead transaction")
        @Test
        void __failedCommitIsNotMaskedByCleanup() {
            final var cause = new OptimisticLockException("someone else won");
            doThrow(cause).when(transaction).commit();
            // a transaction whose commit failed is no longer active, and rolling it back then throws
            when(transaction.isActive()).thenReturn(false);
            doThrow(new IllegalStateException("transaction is not active")).when(transaction).rollback();

            final var thrown =
                    catchThrowable(() -> __EntityManagerUtils.getInTransaction(manager, () -> "v", false));

            assertThat(thrown)
                    .as("the commit failure is what the caller needs to see")
                    .isSameAs(cause);
        }

        @DisplayName("a caller can still catch the provider's own exception type")
        @Test
        void __originalExceptionTypeSurvives() {
            final var cause = new OptimisticLockException("someone else won");
            when(transaction.isActive()).thenReturn(true);

            final var thrown = catchThrowable(() -> __EntityManagerUtils.getInTransaction(manager, () -> {
                throw cause;
            }, true));

            assertThat(thrown).isSameAs(cause);
        }

        @DisplayName("an Error still rolls the transaction back")
        @Test
        void __errorStillRollsBack() {
            when(transaction.isActive()).thenReturn(true);
            final var boom = new AssertionError("a failing assertion inside a test");

            final var thrown = catchThrowable(() -> __EntityManagerUtils.getInTransaction(manager, () -> {
                throw boom;
            }, true));

            assertThat(thrown).isSameAs(boom);
            verify(transaction).rollback();
        }

        @DisplayName("an inactive transaction is not rolled back at all")
        @Test
        void __inactiveTransactionIsNotRolledBack() {
            when(transaction.isActive()).thenReturn(false);

            catchThrowable(() -> __EntityManagerUtils.getInTransaction(manager, () -> {
                throw new IllegalStateException("boom");
            }, true));

            verify(transaction, never()).rollback();
        }

        @DisplayName("a failure while rolling back is suppressed, not promoted")
        @Test
        void __cleanupFailureIsSuppressed() {
            final var cause = new IllegalStateException("boom");
            final var cleanup = new PersistenceException("the connection died");
            when(transaction.isActive()).thenReturn(true);
            doThrow(cleanup).when(transaction).rollback();

            final var thrown = catchThrowable(() -> __EntityManagerUtils.getInTransaction(manager, () -> {
                throw cause;
            }, true));

            assertThat(thrown).isSameAs(cause);
            assertThat(thrown.getSuppressed()).containsExactly(cleanup);
        }

        @DisplayName("isActive() itself throwing is suppressed too")
        @Test
        void __isActiveFailureIsSuppressed() {
            final var cause = new IllegalStateException("boom");
            final var cleanup = new PersistenceException("cannot even tell");
            when(transaction.isActive()).thenThrow(cleanup);

            final var thrown = catchThrowable(() -> __EntityManagerUtils.getInTransaction(manager, () -> {
                throw cause;
            }, true));

            assertThat(thrown).isSameAs(cause);
            assertThat(thrown.getSuppressed()).containsExactly(cleanup);
        }

        @DisplayName("an active transaction is still rolled back on failure")
        @Test
        void __activeTransactionIsRolledBack() {
            when(transaction.isActive()).thenReturn(true);

            catchThrowable(() -> __EntityManagerUtils.getInTransaction(manager, () -> {
                throw new IllegalStateException("boom");
            }, false));

            verify(transaction).begin();
            verify(transaction).rollback();
        }

        @DisplayName("the happy path commits and returns")
        @Test
        void __commits() {
            assertThat(__EntityManagerUtils.getInTransaction(manager, () -> "v", false)).isEqualTo("v");
            verify(transaction).begin();
            verify(transaction).commit();
        }

        @DisplayName("rollback=true rolls back and returns")
        @Test
        void __rollsBack() {
            assertThat(__EntityManagerUtils.getInTransaction(manager, () -> "v", true)).isEqualTo("v");
            verify(transaction).rollback();
        }
    }


    @DisplayName("the void transaction variants")
    @Nested
    class VoidTransactionVariants_Test {

        private EntityManager manager;

        private EntityTransaction transaction;

        @BeforeEach
        void mockManager() {
            manager = mock(EntityManager.class);
            transaction = mock(EntityTransaction.class);
            when(manager.isJoinedToTransaction()).thenReturn(false);
            when(manager.getTransaction()).thenReturn(transaction);
        }

        @DisplayName("runInTransaction(manager, runnable, false) runs and commits")
        @Test
        void __runCommits() {
            final var ran = new AtomicInteger();

            __EntityManagerUtils.runInTransaction(manager, ran::incrementAndGet, false);

            assertThat(ran).hasValue(1);
            verify(transaction).begin();
            verify(transaction).commit();
            verify(transaction, never()).rollback();
        }

        @DisplayName("runInTransactionAndRollback(manager, runnable) runs and rolls back")
        @Test
        void __runRollsBack() {
            final var ran = new AtomicInteger();

            __EntityManagerUtils.runInTransactionAndRollback(manager, ran::incrementAndGet);

            assertThat(ran).hasValue(1);
            verify(transaction).rollback();
            verify(transaction, never()).commit();
        }

        @DisplayName("acceptInTransaction(manager, consumer, false) is handed the manager, and commits")
        @Test
        void __acceptCommits() {
            final var seen = new AtomicReference<EntityManager>();

            __EntityManagerUtils.acceptInTransaction(manager, seen::set, false);

            assertThat(seen).hasValue(manager);
            verify(transaction).commit();
        }

        @DisplayName("acceptInTransactionAndRollback(manager, consumer) is handed the manager, and rolls back")
        @Test
        void __acceptRollsBack() {
            final var seen = new AtomicReference<EntityManager>();

            __EntityManagerUtils.acceptInTransactionAndRollback(manager, seen::set);

            assertThat(seen).hasValue(manager);
            verify(transaction).rollback();
        }

        @DisplayName("a failing runnable still rolls the transaction back, unchanged")
        @Test
        void __failureStillRollsBack() {
            when(transaction.isActive()).thenReturn(true);
            final var boom = new IllegalStateException("boom");

            final var thrown = catchThrowable(() -> __EntityManagerUtils.runInTransaction(manager, () -> {
                throw boom;
            }, false));

            assertThat(thrown).isSameAs(boom);
            verify(transaction).rollback();
        }

        @DisplayName("null actions are rejected by name, before anything is begun")
        @Test
        void _NullPointerException_ForNullActions() {
            assertThatThrownBy(() -> __EntityManagerUtils.runInTransaction(manager, null, false))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("runnable");
            assertThatThrownBy(() -> __EntityManagerUtils.acceptInTransaction(manager, null, false))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("consumer");
            verify(transaction, never()).begin();
        }
    }

    @DisplayName("applyUnwrappedConnection(manager, function)")
    @Nested
    class ApplyUnwrappedConnection_Test {

        private EntityManager manager;

        private Connection connection;

        @BeforeEach
        void mockManager() {
            manager = mock(EntityManager.class);
            connection = mock(Connection.class);
            when(manager.isJoinedToTransaction()).thenReturn(true);
        }

        /**
         * Wires {@code callWithConnection} the way a well-behaved provider does: the function is invoked with the
         * connection, and whatever it throws comes back wrapped &mdash; both providers wrap <em>every</em> exception,
         * not only the checked ones the specification mentions.
         */
        private void providerHandingOut(final Connection connection) {
            doAnswer(i -> {
                final ConnectionFunction<Connection, Object> function = i.getArgument(0);
                try {
                    return function.apply(connection);
                } catch (final Exception e) {
                    throw new PersistenceException("wrapped by the provider", e);
                }
            }).when(manager).callWithConnection(any());
        }

        @DisplayName("the function is handed the provider's connection, and its result returned")
        @Test
        void __returnsResult() {
            providerHandingOut(connection);
            final var seen = new AtomicReference<Connection>();

            final String result = __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                seen.set(c);
                return "ok";
            });

            assertThat(result).isEqualTo("ok");
            assertThat(seen).hasValue(connection);
        }

        @DisplayName("the function is applied exactly once")
        @Test
        void __functionAppliedOnce() {
            providerHandingOut(connection);
            final var calls = new AtomicInteger();

            __EntityManagerUtils.applyUnwrappedConnection(manager, c -> calls.incrementAndGet());

            // a function handed a Connection usually writes; applying it twice would repeat that
            assertThat(calls).hasValue(1);
        }

        @DisplayName("the function's own exception survives the provider's wrapper, by identity")
        @Test
        void __callersExceptionIsNotWrapped() {
            providerHandingOut(connection);
            final var boom = new IllegalStateException("boom");

            // Hibernate ORM wraps this in a bare RuntimeException and EclipseLink in a PersistenceException,
            // so a caller could no longer catch its own failure by type. Peel it back off.
            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                throw boom;
            })).isSameAs(boom);
        }

        @DisplayName("an Error from the function survives the provider's wrapper too")
        @Test
        void __callersErrorIsNotWrapped() {
            providerHandingOut(connection);
            final var boom = new AssertionError("a failing assertion inside a test");

            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                throw boom;
            })).isSameAs(boom);
        }

        @DisplayName("a provider which never reaches the function reports its own failure, unchanged")
        @Test
        void __providerFailureIsReportedAsIs() {
            final var failure = new PersistenceException("no connection to be had");
            doThrow(failure).when(manager).callWithConnection(any());
            final var calls = new AtomicInteger();

            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                calls.incrementAndGet();
                return "unreachable";
            })).isSameAs(failure);

            assertThat(calls).hasValue(0);
        }

        @DisplayName("a null connection is a failure, not a call with null")
        @Test
        void __nullConnectionIsAFailure() {
            // EclipseLink reads the accessor's connection, which outside a transaction may not exist
            providerHandingOut(null);
            final var calls = new AtomicInteger();

            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                calls.incrementAndGet();
                return "unreachable";
            }))
                    .isInstanceOf(PersistenceException.class)
                    // our own diagnosis, not the provider's wrapper around it
                    .hasMessageContaining("no connection from");

            assertThat(calls).hasValue(0);
        }

        @DisplayName("a manager not joined to a transaction is accepted, not refused")
        @Test
        void __notJoinedIsOnlyWarned() {
            when(manager.isJoinedToTransaction()).thenReturn(false);
            providerHandingOut(connection);

            final String result = __EntityManagerUtils.applyUnwrappedConnection(manager, c -> "ok");

            assertThat(result).isEqualTo("ok");
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(null, c -> "v"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("manager");
            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("function");
        }

        @DisplayName("acceptUnwrappedConnection(manager, consumer) is handed the connection")
        @Test
        void __acceptIsHandedTheConnection() {
            providerHandingOut(connection);
            final var seen = new AtomicReference<Connection>();

            __EntityManagerUtils.acceptUnwrappedConnection(manager, seen::set);

            assertThat(seen).hasValue(connection);
        }
    }
}
