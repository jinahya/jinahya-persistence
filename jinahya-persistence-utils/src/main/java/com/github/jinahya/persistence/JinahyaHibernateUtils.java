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
import jakarta.persistence.EntityManagerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

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
final class JinahyaHibernateUtils {

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
     * Returns the names of all columns mapped by the specified entity class, identifier columns first.
     *
     * @param entityManagerFactory the entity manager factory whose
     *                             {@link EntityManagerFactory#getMetamodel() metamodel} is used; it should be
     *                             Hibernate's {@code org.hibernate.metamodel.MappingMetamodel}.
     * @param entityClass          the entity class whose column names are returned.
     * @return a list of distinct column names of the {@code entityClass}.
     * @throws RuntimeException when Hibernate is not the provider in use, or when any of the reflective calls fails.
     */
    static List<String> getEntityColumnNames(final EntityManagerFactory entityManagerFactory,
                                             final Class<?> entityClass) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        try {
            // ---------------------------------------------------------------------------------------------------------
            final var mappingMetamodelClass = Class.forName("org.hibernate.metamodel.MappingMetamodel");
            final var mappingMetamodel = mappingMetamodelClass.cast(entityManagerFactory.getMetamodel());
            // ---------------------------------------------------------------------------------------------------------
            final var getEntityDescriptorMethod = mappingMetamodelClass.getMethod("getEntityDescriptor", Class.class);
            final var entityDescriptor = getEntityDescriptorMethod.invoke(mappingMetamodel, entityClass);
            // ---------------------------------------------------------------------------------------------------------
            final var entityMappingTypeClass = Class.forName("org.hibernate.metamodel.mapping.EntityMappingType");
            final var getEntityPersisterMethod = entityMappingTypeClass.getMethod("getEntityPersister");
            final var entityPersister = getEntityPersisterMethod.invoke(entityDescriptor);
            // ---------------------------------------------------------------------------------------------------------
            final var abstractEntityPersisterClass =
                    Class.forName("org.hibernate.persister.entity.AbstractEntityPersister");
            final var abstractEntityPersister = abstractEntityPersisterClass.cast(entityPersister);
            // ---------------------------------------------------------------------------------------------------------
            final var getIdentifierColumNamesMethod =
                    abstractEntityPersisterClass.getMethod("getIdentifierColumnNames");
            final var identifierColumnNames = (String[]) getIdentifierColumNamesMethod.invoke(abstractEntityPersister);
            // ---------------------------------------------------------------------------------------------------------
            final var getPropertyNamesMethod = abstractEntityPersisterClass.getMethod("getPropertyNames");
            final var propertyNames = (String[]) getPropertyNamesMethod.invoke(abstractEntityPersister);
            // ---------------------------------------------------------------------------------------------------------
            final var toColumnsMethod = abstractEntityPersisterClass.getMethod("toColumns", String.class);
            final var propertyColumnNames = Arrays.stream(propertyNames).flatMap(pn -> {
                try {
                    return Arrays.stream((String[]) toColumnsMethod.invoke(abstractEntityPersister, pn));
                } catch (final ReflectiveOperationException e) {
                    throw new RuntimeException(
                            "failed to invoke " + toColumnsMethod + " with " + abstractEntityPersister + " and " + pn,
                            e
                    );
                }
            }).toArray(String[]::new);
            // ---------------------------------------------------------------------------------------------------------
            return Stream.concat(
                            Arrays.stream(identifierColumnNames),
                            Arrays.stream(propertyColumnNames)
                    )
                    .distinct()
                    .toList();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to get entity column names for " + entityClass, roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private JinahyaHibernateUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
