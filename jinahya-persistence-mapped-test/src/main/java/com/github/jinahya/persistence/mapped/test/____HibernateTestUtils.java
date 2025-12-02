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
import jakarta.persistence.EntityManagerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ____HibernateTestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Return the result of the specified function applied to a connection unwrapped from the specified entity manager.
     *
     * @param entityManager the entity manager from which the connection is unwrapped.
     * @param function      the function to be applied with the connection.
     * @param <R>           result type parameter
     * @return the result of the {@code function}.
     */
    @SuppressWarnings({"unchecked"})
    // https://stackoverflow.com/a/44214469/330457
    static <R> R applyConnection(final @Nonnull EntityManager entityManager,
                                 final @Nonnull Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        try {
            final Class<?> sessionClass = Class.forName("org.hibernate.Session");
            final Object sessionInstance = entityManager.unwrap(sessionClass);
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
    private static void addColumnNamed(final @Nonnull EntityManagerFactory entityManagerFactory,
                                       final @Nonnull Class<?> type,
                                       final @Nonnull Collection<String> columnNames) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(type, "type is null");
        try {
            // > final var mappingMetamodel = (MappingMatamodel) entityManagerFactory.getMetamodel();
            // > final var entityDescriptor = mappingMetamodel.getEntityDescriptor(type);
            // > final var entityPersister = EntityMappingType.getEntityPersister(entityDescriptor);
            // > final var abstractEntityPersister = (AbstractEntityPersister) entityPersister;
            // > final String[] identifierColumnNames = abstractEntityPersister.getIdentifierColumnNames();
            // > String[] propertyNames = abstractEntityPersister.getPropertyNames()
            // > final String[] columnNames = abstractEntityPersister.toColumns(propertyName)
            // ---------------------------------------------------------------------------------------------------------
            final var mappingMetamodelClass = Class.forName("org.hibernate.metamodel.MappingMetamodel");
            final var mappingMetamodel = mappingMetamodelClass.cast(entityManagerFactory.getMetamodel());
            // ---------------------------------------------------------------------------------------------------------
            final var getEntityDescriptorMethod = mappingMetamodelClass.getMethod("getEntityDescriptor", Class.class);
            final var entityDescriptor = getEntityDescriptorMethod.invoke(mappingMetamodel, type);
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
                    final var columns = (String[]) toColumnsMethod.invoke(abstractEntityPersister, pn);
                    return Arrays.stream(columns);
                } catch (final ReflectiveOperationException e) {
                    throw new RuntimeException(
                            "failed to invoke " + toColumnsMethod + " with " + abstractEntityPersister + " and " + pn,
                            e
                    );
                }
            }).toArray(String[]::new);
            columnNames.addAll(Arrays.asList(identifierColumnNames));
            columnNames.addAll(Arrays.asList(propertyColumnNames));
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to get entity column names for " + type, roe);
        }
    }

    // https://docs.hibernate.org/orm/current/javadocs/org/hibernate/persister/entity/AbstractEntityPersister.html
    static List<String> getEntityColumnNames(final @Nonnull EntityManagerFactory entityManagerFactory,
                                             final @Nonnull Class<?> entityClass) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        try {
            // > final var mappingMetamodel = (MappingMatamodel) entityManagerFactory.getMetamodel();
            // > final var entityDescriptor = mappingMetamodel.getEntityDescriptor(entityClass);
            // > final var entityPersister = EntityMappingType.getEntityPersister(entityDescriptor);
            // > final var abstractEntityPersister = (AbstractEntityPersister) entityPersister;
            // > final String[] identifierColumnNames = abstractEntityPersister.getIdentifierColumnNames();
            // > String[] propertyNames = abstractEntityPersister.getPropertyNames()
            // > final String[] columnNames = abstractEntityPersister.toColumns(propertyName)
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
                    final var columnNames = (String[]) toColumnsMethod.invoke(abstractEntityPersister, pn);
                    return Arrays.stream(columnNames);
                } catch (final ReflectiveOperationException e) {
                    throw new RuntimeException(
                            "failed to invoke " + toColumnsMethod + " with " + abstractEntityPersister + " and " + pn,
                            e
                    );
                }
            }).toArray(String[]::new);
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
    private ____HibernateTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
