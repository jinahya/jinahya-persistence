package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
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

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___OrgHibernateOrmTestUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Return the result of the specified function applied with a connection, unwrapped from the specified entity
     * manager.
     * <p>
     * Equivalent to:
     * {@snippet lang = "java":
     *     entityManager.unwrap(Session.class).doReturningWork(function::apply);
     *}
     *
     * @param entityManager an entity manager.
     * @param function      the function to be applied with a connection.
     * @param <R>           result type parameter
     * @return the result of the {@code function}.
     * @see <a
     *         href="https://docs.jboss.org/hibernate/orm/current/javadocs/org/hibernate/Session.html">org.hibernate.Session</a>
     * @see <a
     *         href="https://docs.jboss.org/hibernate/orm/current/javadocs/org/hibernate/jdbc/ReturningWork.html">org.hibernate.jdbc.ReturningWork</a>
     * @see <a
     *         href="https://docs.jboss.org/hibernate/orm/current/javadocs/org/hibernate/SharedSessionContract.html#doReturningWork(org.hibernate.jdbc.ReturningWork)">org.hibernate.SharedSessionContract#doReturningWork(org.hibernate.jdbc.ReturningWork)</a>
     */
    @SuppressWarnings({"unchecked"})
    // https://stackoverflow.com/a/44214469/330457
    static <R> R applyConnection(@Nonnull final EntityManager entityManager,
                                 @Nonnull final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        try {
            final Class<?> sessionClass = Class.forName("org.hibernate.Session");
            // [ final var session = entityManager.unwrap(Session.class); ]
            final Object sessionInstance = entityManager.unwrap(sessionClass);
            final Class<?> returningWorkClass = Class.forName("org.hibernate.jdbc.ReturningWork");
            final Method executeMethod = returningWorkClass.getMethod("execute", Connection.class);
            // [ final var returningWork = c -> function.apply(connection); ]
            final Object returningWorkProxy = Proxy.newProxyInstance(
//                    MethodHandles.lookup().lookupClass().getClassLoader(),
                    returningWorkClass.getClassLoader(),
                    new Class[]{returningWorkClass},
                    (p, m, a) -> {
                        if (m.equals(executeMethod)) {
                            final Connection connection = (Connection) a[0];
                            return function.apply(connection);
                        }
                        return null;
                    });
            final Method doReturningWorkMethod = sessionClass.getMethod("doReturningWork", returningWorkClass);
            // [return session.doReturningWork(returningWork);]
            return (R) doReturningWorkMethod.invoke(sessionInstance, returningWorkProxy);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to work with hibernate", roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___OrgHibernateOrmTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
