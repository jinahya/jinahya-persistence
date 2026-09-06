package com.github.jinahya.persistence;

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

import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;

/**
 * A utility class for working with Hibernate ORM, reflectively.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Every Hibernate type is resolved with {@link Class#forName(String)}, so that this class, and the
 *         module it belongs to, do not require Hibernate ORM at compile-time nor at runtime. Methods here throw a
 *         {@link RuntimeException} when Hibernate is not the provider in use.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___HibernateUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Return the result of the specified function applied to a connection unwrapped from the specified entity manager.
     *
     * @param manager  the entity manager from which the connection is unwrapped.
     * @param function the function to be applied with the connection.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    @SuppressWarnings({"unchecked"})
    // https://stackoverflow.com/a/44214469/330457
    static <R> R applyConnection(final EntityManager manager,
                                 final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(manager, "manager is null");
        Objects.requireNonNull(function, "function is null");
        try {
            final Class<?> sessionClass = Class.forName("org.hibernate.Session");
            final Object sessionInstance = manager.unwrap(sessionClass);
            final Class<?> returningWorkClass = Class.forName("org.hibernate.jdbc.ReturningWork");
            final Method executeMethod = returningWorkClass.getMethod("execute", Connection.class);
            final Object returningWorkProxy = Proxy.newProxyInstance(
                    returningWorkClass.getClassLoader(),
                    new Class[]{returningWorkClass},
                    (p, m, a) -> {
                        if (m.equals(executeMethod)) {
                            final var connection = (Connection) a[0];
                            logger.log(System.Logger.Level.DEBUG, "connection: {0}", connection);
                            return function.apply(connection);
                        }
                        return null;
                    }
            );
            final var doReturningWorkMethod = sessionClass.getMethod("doReturningWork", returningWorkClass);
            return (R) doReturningWorkMethod.invoke(sessionInstance, returningWorkProxy);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to work with hibernate", roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private ___HibernateUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
