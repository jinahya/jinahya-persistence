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

import java.util.List;
import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___EclipseLinkTestUtils {

    /**
     * Returns an unmodifiable list of the specified entity class's column names.
     *
     * @param entityManagerFactory an entity manager factory.
     * @param entityClass          the entity class.
     * @return an unmodifiable list of {@code entityClass}'s column names.
     */
    static List<String> getEntityColumnNames(@Nonnull final EntityManagerFactory entityManagerFactory,
                                             @Nonnull final Class<?> entityClass) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        try {
            final var entityManagerFactoryImplClass =
                    Class.forName("org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl");
            final var entityManagerFactoryImpl = entityManagerFactoryImplClass.cast(entityManagerFactory);
            final var jpaEntityManagerFactoryClass =
                    Class.forName("org.eclipse.persistence.jpa.JpaEntityManagerFactory");
            final var getDatabaseSessionMethod = jpaEntityManagerFactoryClass.getMethod("getDatabaseSession");
            final var databaseSession = getDatabaseSessionMethod.invoke(entityManagerFactoryImpl);

//            final var abstractSessionClass = Class.forName("org.eclipse.persistence.sessions.AbstractSession");
//            final var getDescriptorMethod = abstractSessionClass.getMethod("getDescriptor", Class.class);
            final var sessionClass = Class.forName("org.eclipse.persistence.sessions.Session");
            final var getDescriptorMethod = sessionClass.getMethod("getDescriptor", Class.class);
            final var descriptor = getDescriptorMethod.invoke(databaseSession, entityClass);

            final var classDescriptorClass = Class.forName("org.eclipse.persistence.descriptors.ClassDescriptor");
            final var getMappingsMethod = classDescriptorClass.getMethod("getMappings");
            final var mappings = getMappingsMethod.invoke(descriptor);

            final var databaseMappingClass = Class.forName("org.eclipse.persistence.mappings.DatabaseMapping");

            final var getFieldsMethod = databaseMappingClass.getMethod("getFields");
            final var databaseFieldClass = Class.forName("org.eclipse.persistence.internal.helper.DatabaseField");
            final var getNameMethod = databaseFieldClass.getMethod("getName");
            return ((List<Object>) mappings).stream().flatMap(m -> {
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
            }).toList();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to get entity column names for " + entityClass, roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___EclipseLinkTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
