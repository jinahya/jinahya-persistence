package com.github.jinahya.persistence.mapped.tests.util;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;

public final class __OrgHibernateOrmTestUtils {

    public static <R> R applyConnection(@Nonnull final EntityManager entityManager,
                                        @Nonnull final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        try {
            final Class<?> sessionClass = Class.forName("org.hibernate.Session");
            final Object sessionInstance = entityManager.unwrap(sessionClass);
            final Class<?> returningWorkClass = Class.forName("org.hibernate.jdbc.ReturningWork");
            final Method executeMethod = returningWorkClass.getMethod("execute", Connection.class);
            final Object workProxy = Proxy.newProxyInstance(
                    MethodHandles.lookup().lookupClass().getClassLoader(),
                    new Class[]{returningWorkClass},
                    (p, m, a) -> {
                        if (m.equals(executeMethod)) {
                            final Connection connection = (Connection) a[0];
                            return function.apply(connection);
                        }
                        return null;
                    });
            final Method doReturningWorkMethod = sessionClass.getMethod("doReturningWork", returningWorkClass);
            return (R) doReturningWorkMethod.invoke(sessionInstance, workProxy);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to work with hibernate", roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __OrgHibernateOrmTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
