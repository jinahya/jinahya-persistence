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
import jakarta.persistence.PersistenceConfiguration;

import java.util.Objects;

/**
 * A class for providing persistence resources.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __PersistenceProducer_TestUtils {

    private static final String VALUE_SCHEMAGEN_DATABASE_ACTION_NONE = "none";

    private static final String KEY_ECLIPSELINK_DDL_GENERATION = "eclipselink.ddl-generation";

    private static final String VALUE_ECLIPSELINK_DDL_GENERATION_NONE = "none";

    private static final String KEY_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

    private static final String VALUE_HIBERNATE_HBM2DDL_AUTO_NONE = "none";

    /**
     * Asserts that {@value PersistenceConfiguration#SCHEMAGEN_DATABASE_ACTION} property of the specified entity manager
     * factory is {@value #VALUE_SCHEMAGEN_DATABASE_ACTION_NONE}.
     *
     * @param entityManagerFactory the entity manager factory.
     * @return given {@code entityManagerFactory}.
     * @see <a
     *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a12384">8.2.1.11.
     *         properties</a>
     */
    @Nonnull
    public static EntityManagerFactory assertSchemagenDatabaseActionNone(
            final @Nonnull EntityManagerFactory entityManagerFactory) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        final var properties = entityManagerFactory.getProperties();
        final var value = properties.getOrDefault(
                PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION,
                VALUE_SCHEMAGEN_DATABASE_ACTION_NONE
        );
        if (!Objects.equals(value, VALUE_SCHEMAGEN_DATABASE_ACTION_NONE)) {
            throw new RuntimeException(
                    "invalid value of '" + PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION + "': " + value
            );
        }
        return entityManagerFactory;
    }

    /**
     * Asserts that {@value #KEY_ECLIPSELINK_DDL_GENERATION} property of the specified entity manager factory is
     * {@value #VALUE_ECLIPSELINK_DDL_GENERATION_NONE}.
     *
     * @param entityManagerFactory the entity manager factory.
     * @return given {@code entityManagerFactory}.
     * @see <a
     *         href="https://eclipse.dev/eclipselink/documentation/4.0/jpa/extensions/jpa-extensions.html#ddl-generation">ddl-generation</a>
     */
    @Nonnull
    public static EntityManagerFactory assertEclipselinkDdlGenerationNone(
            final @Nonnull EntityManagerFactory entityManagerFactory) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        final var properties = entityManagerFactory.getProperties();
        final var value = properties.getOrDefault(
                KEY_ECLIPSELINK_DDL_GENERATION,
                VALUE_ECLIPSELINK_DDL_GENERATION_NONE
        );
        if (!Objects.equals(value, VALUE_ECLIPSELINK_DDL_GENERATION_NONE)) {
            throw new RuntimeException("invalid value of '" + KEY_ECLIPSELINK_DDL_GENERATION + "': " + value);
        }
        return entityManagerFactory;
    }

    /**
     * Asserts that {@value #KEY_HIBERNATE_HBM2DDL_AUTO} property of the specified entity manager factory is
     * {@value #VALUE_HIBERNATE_HBM2DDL_AUTO_NONE}.
     *
     * @param entityManagerFactory the entity manager factory.
     * @return given {@code entityManagerFactory}.
     * @see <a
     *         href="https://docs.jboss.org/hibernate/orm/7.1/userguide/html_single/Hibernate_User_Guide.html#settings-hibernate.hbm2ddl.auto">A.17.12.
     *         hibernate.hbm2ddl.auto</a>
     */
    @Nonnull
    public static EntityManagerFactory assertHibernateHbm2ddlAutoNone(
            final @Nonnull EntityManagerFactory entityManagerFactory) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        final var properties = entityManagerFactory.getProperties();
        final var value = properties.getOrDefault(
                KEY_HIBERNATE_HBM2DDL_AUTO,
                VALUE_HIBERNATE_HBM2DDL_AUTO_NONE
        );
        if (!Objects.equals(value, VALUE_HIBERNATE_HBM2DDL_AUTO_NONE)) {
            throw new RuntimeException("invalid value of '" + KEY_HIBERNATE_HBM2DDL_AUTO + "': " + value);
        }
        return entityManagerFactory;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __PersistenceProducer_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
