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
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ____EclipseLinkTestUtils {

    // https://github.com/eclipse-ee4j/eclipselink/blob/master/jpa/org.eclipse.persistence.jpa/src/main/java/org/eclipse/persistence/internal/jpa/EntityManagerFactoryImpl.java
// https://github.com/eclipse-ee4j/eclipselink/blob/master/foundation/org.eclipse.persistence.core/src/main/java/org/eclipse/persistence/internal/sessions/DatabaseSessionImpl.java
// https://javadoc.io/doc/org.eclipse.persistence/eclipselink/latest/eclipselink/org/eclipse/persistence/sessions/Session.html#getDescriptor(java.lang.Class)
// https://javadoc.io/doc/org.eclipse.persistence/eclipselink/latest/eclipselink/org/eclipse/persistence/descriptors/ClassDescriptor.html
// https://github.com/eclipse-ee4j/eclipselink/blob/master/foundation/org.eclipse.persistence.core/src/main/java/org/eclipse/persistence/internal/helper/DatabaseField.java
    @SuppressWarnings("unchecked")
    static <C extends Collection<? super String>>
    @Nonnull C addAllEntityColumnNames(final @Nonnull EntityManagerFactory entityManagerFactory,
                                       final @Nonnull Class<?> entityClass, final @Nonnull C collection) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        // EntityManagerFactoryImpl entityManagerFactoryImpl = (EntityManagerFactoryImpl) entityManagerFactory;
        // DatabaseSession databaseSession = entityManagerFactoryImpl.getDatabaseSession();
        // ClassDescriptor descriptor = databaseSession.getDescriptor(entityClass);
        // List<DatabaseMapping> mappings = descriptor.getMappings();
        // String[] columnNames = mappings.stream().flatMap(m -> m.getFields().stream().map(f -> f.getName())).toArray(String[]::new);
        try {
            final var entityManagerFactoryImplClass =
                    Class.forName("org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl");
            final var entityManagerFactoryImpl = entityManagerFactoryImplClass.cast(entityManagerFactory);
            // ---------------------------------------------------------------------------------------------------------
            final var jpaEntityManagerFactoryClass =
                    Class.forName("org.eclipse.persistence.jpa.JpaEntityManagerFactory");
            final var getDatabaseSessionMethod = jpaEntityManagerFactoryClass.getMethod("getDatabaseSession");
            final var databaseSession = getDatabaseSessionMethod.invoke(entityManagerFactoryImpl);
            // ---------------------------------------------------------------------------------------------------------
            final var sessionClass = Class.forName("org.eclipse.persistence.sessions.Session");
            final var getDescriptorMethod = sessionClass.getMethod("getDescriptor", Class.class);
            final var descriptor = getDescriptorMethod.invoke(databaseSession, entityClass);
            // ---------------------------------------------------------------------------------------------------------
            final var classDescriptorClass = Class.forName("org.eclipse.persistence.descriptors.ClassDescriptor");
            final var getMappingsMethod = classDescriptorClass.getMethod("getMappings");
            final var mappings = getMappingsMethod.invoke(descriptor);
            // ---------------------------------------------------------------------------------------------------------
            final var databaseMappingClass = Class.forName("org.eclipse.persistence.mappings.DatabaseMapping");
            final var getFieldsMethod = databaseMappingClass.getMethod("getFields");
            final var databaseFieldClass = Class.forName("org.eclipse.persistence.internal.helper.DatabaseField");
            final var getNameMethod = databaseFieldClass.getMethod("getName");
            ((List<Object>) mappings).stream().flatMap(m -> {
                final var databaseMapping = databaseMappingClass.cast(m);
                try {
                    final var fields = (List<Object>) getFieldsMethod.invoke(databaseMapping);
                    return fields.stream().map(f -> {
                        final var databaseField = databaseFieldClass.cast(f);
                        try {
                            return (String) getNameMethod.invoke(databaseField);
                        } catch (final ReflectiveOperationException roe) {
                            throw new RuntimeException(roe);
                        }
                    });
                } catch (final ReflectiveOperationException roe) {
                    throw new RuntimeException(roe);
                }
            }).forEach(collection::add);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to get entity column names for " + entityClass, roe);
        }
        return collection;
    }

    /**
     * Returns an unmodifiable list of the specified entity class's column names.
     *
     * @param entityManagerFactory an entity manager factory.
     * @param entityClass          the entity class.
     * @return an unmodifiable list of {@code entityClass}'s column names.
     */
    @SuppressWarnings("unchecked")
    static List<String> getEntityColumnNames(final @Nonnull EntityManagerFactory entityManagerFactory,
                                             final @Nonnull Class<?> entityClass) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return addAllEntityColumnNames(entityManagerFactory, entityClass, new ArrayList<>());
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ____EclipseLinkTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
