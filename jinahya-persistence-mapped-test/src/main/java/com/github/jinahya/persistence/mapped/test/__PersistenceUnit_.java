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
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
abstract class __PersistenceUnit_ {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    __PersistenceUnit_() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        logger.log(Level.INFO, "entityManagerFactory: {0}", entityManagerFactory);
        entityManagerFactory.getProperties().forEach((k, v) -> {
            logger.log(Level.DEBUG, "{0}: {1}", k, v);
        });
        catalog = __PersistenceUnitUtils.getDefaultCatalog(entityManagerFactory).orElseThrow();
        schema = __PersistenceUnitUtils.getDefaultSchema(entityManagerFactory).orElseThrow();
        types = __PersistenceUnitUtils.getDefaultTypes(entityManagerFactory).orElse(null);
    }

    // https://stackoverflow.com/a/72628439/330457
    private void onStartup(@Observes final Startup startup) {
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("print database info")
    @Test
    @SuppressWarnings({
            "java:S100" // Method names should comply with a naming convention
    })
    protected void printDatabaseInfo__() {
        acceptEntityManager(em -> {
            ___JakartaPersistenceTestUtils.acceptConnectionInTransaction(
                    em,
                    c -> {
                        try {
                            ___JavaSqlTestUtils.printDatabaseInfo(c);
                        } catch (final SQLException sqle) {
                            throw new RuntimeException("failed to print database info", sqle);
                        }
                    }
            );
        });
        acceptEntityManager(em -> {
            ___JakartaPersistenceTestUtils.acceptConnectionInTransaction(
                    em,
                    c -> {
                        try {
                            try (var tableTypes = c.getMetaData().getTableTypes()) {
                                while (tableTypes.next()) {
                                    final var tableType = tableTypes.getString("TABLE_TYPE");
                                    logger.log(Level.INFO, "TABLE_TYPE: {0}", tableType);
                                }
                            }
                        } catch (final SQLException sqle) {
                            throw new RuntimeException("failed to print table types", sqle);
                        }
                    }
            );
        });
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory

    /**
     * Applies an injected instance of {@link EntityManagerFactory} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManagerFactory(Consumer)
     */
    protected final <R> R applyEntityManagerFactory(
            @Nonnull final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(entityManagerFactory);
    }

    /**
     * Accepts an injected instance of {@link EntityManagerFactory} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManagerFactory(Function)
     */
    protected final void acceptEntityManagerFactory(@Nonnull final Consumer<? super EntityManagerFactory> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerFactory(emf -> {
            consumer.accept(emf);
            return null;
        });
    }

    /**
     * Applies an injected instance of {@link EntityManager} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManager(Consumer)
     */
    protected final <R> R applyEntityManager(@Nonnull final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerFactory(emf -> {
            try (final var entityManager = emf.createEntityManager()) {
                return function.apply(entityManager);
            }
        });
    }

    /**
     * Accepts an injected instance of {@link EntityManager} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManager(Function)
     */
    protected final void acceptEntityManager(@Nonnull final Consumer<? super EntityManager> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected String catalog() {
        return catalog;
    }

    protected String schema() {
        return schema;
    }

    protected String[] types() {
        return types;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.Integration__
    @Inject
    private EntityManagerFactory entityManagerFactory;

//    @Deprecated(forRemoval = true)
//    @__PersistenceProducer.Integration__
//    @Inject
//    private EntityManager entityManager;

    private String catalog;

    private String schema;

    private String[] types;
}
