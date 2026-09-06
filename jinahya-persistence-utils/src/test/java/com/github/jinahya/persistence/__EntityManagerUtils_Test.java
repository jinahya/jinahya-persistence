package com.github.jinahya.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;
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

    @DisplayName("applyUnwrappedConnection(manager, function)")
    @Nested
    class ApplyUnwrappedConnection_Test {

        @DisplayName("an exception from the function is not reported as an unwrap failure")
        @Test
        void __callersExceptionIsNotSwallowed() {
            final var manager = mock(EntityManager.class);
            final var connection = mock(Connection.class);
            when(manager.unwrap(Connection.class)).thenReturn(connection);
            final var boom = new IllegalStateException("boom");

            // the unwrap succeeded; only the caller's own function failed, so that is what the
            // caller has to see -- not "failed to unwrap connection", which is a wrong diagnosis
            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                throw boom;
            })).isSameAs(boom);
        }

        @DisplayName("the function is applied exactly once")
        @Test
        void __functionAppliedOnce() {
            final var manager = mock(EntityManager.class);
            final var connection = mock(Connection.class);
            when(manager.unwrap(Connection.class)).thenReturn(connection);
            final var calls = new AtomicInteger();

            // capture separately, so the count is asserted even when the type assertion fails
            Throwable thrown = null;
            try {
                __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                    calls.incrementAndGet();
                    throw new IllegalStateException("boom");
                });
            } catch (final Throwable t) {
                thrown = t;
            }

            // a function handed a Connection usually writes; applying it twice would repeat that
            assertThat(calls).hasValue(1);
            assertThat(thrown).isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("a successful function returns its result")
        @Test
        void __returnsResult() {
            final var manager = mock(EntityManager.class);
            final var connection = mock(Connection.class);
            when(manager.unwrap(Connection.class)).thenReturn(connection);

            final String result = __EntityManagerUtils.applyUnwrappedConnection(manager, c -> "ok");
            assertThat(result).isEqualTo("ok");
        }

        @DisplayName("a genuine unwrap failure still falls back, and reports an unwrap failure")
        @Test
        void __unwrapFailureStillFallsBack() {
            final var manager = mock(EntityManager.class);
            when(manager.unwrap(Connection.class)).thenThrow(new IllegalArgumentException("no Connection here"));
            final var calls = new AtomicInteger();

            // the fallback goes through ___HibernateUtils; Hibernate IS on this module's test
            // classpath (a root active-by-default test profile supplies it), but unwrap(Session.class)
            // is unstubbed and returns null, so the reflective call fails before reaching the function
            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                calls.incrementAndGet();
                return "unreachable";
            }))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("failed to unwrap connection");

            // the function was never reached: the unwrap failed, and the Hibernate path failed too
            assertThat(calls).hasValue(0);
        }

        @DisplayName("with a WORKING Hibernate fallback, the function still runs only once")
        @Test
        void __notAppliedTwiceEvenWhenTheFallbackWouldReachIt() throws Exception {
            // Hibernate is on the test classpath, so wire a Session whose doReturningWork actually
            // invokes the ReturningWork -- that is the path which used to apply the function a
            // SECOND time (___HibernateUtils.applyConnection) after the first one threw.
            final Class<?> sessionClass;
            try {
                sessionClass = Class.forName("org.hibernate.Session");
            } catch (final ClassNotFoundException cnfe) {
                Assumptions.abort("Hibernate is not on the test classpath");
                return;
            }
            final var workClass = Class.forName("org.hibernate.jdbc.ReturningWork");
            final var execute = workClass.getMethod("execute", Connection.class);
            final var fallbackConnection = mock(Connection.class);

            final var manager = mock(EntityManager.class);
            final var directConnection = mock(Connection.class);
            when(manager.unwrap(Connection.class)).thenReturn(directConnection);
            // doReturn(...).when(...) because unwrap(Class<?>) returns a captured type
            doReturn(
                    Proxy.newProxyInstance(
                            sessionClass.getClassLoader(),
                            new Class<?>[]{sessionClass},
                            (p, m, a) -> "doReturningWork".equals(m.getName())
                                    ? execute.invoke(a[0], fallbackConnection)
                                    : null
                    )
            ).when(manager).unwrap(sessionClass);

            final var calls = new AtomicInteger();
            Throwable thrown = null;
            try {
                __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                    calls.incrementAndGet();
                    throw new IllegalStateException("boom");
                });
            } catch (final Throwable t) {
                thrown = t;
            }

            assertThat(calls)
                    .as("the direct unwrap succeeded, so the fallback must not run the function again")
                    .hasValue(1);
            assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessage("boom");
        }

        @DisplayName("a genuine unwrap failure does reach the fallback, and its result is returned")
        @Test
        void __fallbackIsActuallyUsed() throws Exception {
            final Class<?> sessionClass;
            try {
                sessionClass = Class.forName("org.hibernate.Session");
            } catch (final ClassNotFoundException cnfe) {
                Assumptions.abort("Hibernate is not on the test classpath");
                return;
            }
            final var workClass = Class.forName("org.hibernate.jdbc.ReturningWork");
            final var execute = workClass.getMethod("execute", Connection.class);
            final var fallbackConnection = mock(Connection.class);

            final var manager = mock(EntityManager.class);
            when(manager.unwrap(Connection.class)).thenThrow(new IllegalArgumentException("no Connection here"));
            // doReturn(...).when(...) because unwrap(Class<?>) returns a captured type
            doReturn(
                    Proxy.newProxyInstance(
                            sessionClass.getClassLoader(),
                            new Class<?>[]{sessionClass},
                            (p, m, a) -> "doReturningWork".equals(m.getName())
                                    ? execute.invoke(a[0], fallbackConnection)
                                    : null
                    )
            ).when(manager).unwrap(sessionClass);

            final var seen = new AtomicInteger();
            final String result = __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                seen.incrementAndGet();
                assertThat(c).isSameAs(fallbackConnection);
                return "from-fallback";
            });

            assertThat(result).isEqualTo("from-fallback");
            assertThat(seen).hasValue(1);
        }

        @DisplayName("a null unwrapped connection is an unwrap failure, not a call with null")
        @Test
        void __nullConnectionIsAnUnwrapFailure() {
            final var manager = mock(EntityManager.class);
            when(manager.unwrap(Connection.class)).thenReturn(null);
            final var calls = new AtomicInteger();

            assertThatThrownBy(() -> __EntityManagerUtils.applyUnwrappedConnection(manager, c -> {
                calls.incrementAndGet();
                return "unreachable";
            })).isInstanceOf(RuntimeException.class);

            assertThat(calls).hasValue(0);
        }
    }
}
