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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract class for testing persistence unit of
 * {@link __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_IT_PU}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __PersistenceUnitTest
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __PersistenceUnitIT extends __PersistenceUnit_ {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected __PersistenceUnitIT() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests that all database tables are mapped to entities.
     *
     * @see #_Empty_RemainingDatabaseTableNames(List)
     */
    @DisplayName("check all database tables are mapped")
    @Test
    protected void _Mapped_AllDatabaseTableNames() {
        final var databaseTableNames =
                applyEntityManager(em -> {
                    return ___JakartaPersistenceTestUtils.applyConnectionInTransaction(
                            em,
                            c -> {
                                try {
                                    return ___JavaSqlTestUtils.addAllTableNames(
                                            c,
                                            catalog(),
                                            schema(),
                                            types(),
                                            new ArrayList<>()
                                    );
                                } catch (final SQLException sqle) {
                                    throw new RuntimeException("failed to get database table names", sqle);
                                }
                            }
                    );
                });
        final var entityTableNames = Collections.unmodifiableList(
                applyEntityManagerFactory(
                        emf -> ___JakartaPersistenceTestUtils.addAllEntityTableNames(
                                emf,
                                new ArrayList<>()
                        )
                )
        );
        logger.log(Level.DEBUG, "all database table names: {0}", databaseTableNames);
        logger.log(Level.DEBUG, "all entity table names: {0}", entityTableNames);
        databaseTableNames.removeAll(entityTableNames);
        _Empty_RemainingDatabaseTableNames(databaseTableNames);
    }

    /**
     * Verifies that the specified collection of remaining database table names, from which all entity table names are
     * removed, is empty.
     *
     * @param remainingDatabaseTableNames the collection of remaining database table names which should be empty.
     * @see #_Mapped_AllDatabaseTableNames()
     */
    protected void _Empty_RemainingDatabaseTableNames(@Nonnull final List<String> remainingDatabaseTableNames) {
        remainingDatabaseTableNames.removeIf(tn -> {
            return tn.startsWith("HTE_"); // H2 temporary tables
        });
        remainingDatabaseTableNames.remove("SEQUENCE");
        remainingDatabaseTableNames.forEach(tn -> logger.log(Level.WARNING, "remaining database table name: {0}", tn));
        assertThat(remainingDatabaseTableNames)
                .as("remaining database table names")
                .isEmpty();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests that all database tables are mapped to entities.
     *
     * @see #_Empty_RemainingEntityTableNames(List)
     */
    @DisplayName("check all entity tables are known")
    @Test
    protected void _Mapped_AllEntityTableNames() {
        final var databaseTableNames = Collections.unmodifiableList(
                applyEntityManager(
                        em -> ___JakartaPersistenceTestUtils.applyConnectionInTransaction(
                                em,
                                c -> {
                                    try {
                                        return ___JavaSqlTestUtils.addAllTableNames(
                                                c,
                                                catalog(),
                                                schema(),
                                                types(),
                                                new ArrayList<>()
                                        );
                                    } catch (final
                                    SQLException sqle) {
                                        throw new RuntimeException(
                                                "failed to get database table names",
                                                sqle);
                                    }
                                }
                        )
                )
        );
        final var entityTableNames = applyEntityManagerFactory(
                emf -> ___JakartaPersistenceTestUtils.addAllEntityTableNames(
                        emf,
                        new ArrayList<>()
                )
        );
        entityTableNames.removeAll(databaseTableNames);
        _Empty_RemainingEntityTableNames(entityTableNames);
    }

    /**
     * Verifies that the specified collection of remaining entity table names, from which all database table names are
     * removed, is empty.
     *
     * @param remainingEntityTableNames the entity table names from which all database table names are removed.
     * @see #_Mapped_AllEntityTableNames()
     */
    protected void _Empty_RemainingEntityTableNames(@Nonnull final List<String> remainingEntityTableNames) {
        remainingEntityTableNames.forEach(tn -> logger.log(Level.WARNING, "remaining entity table name: {0}", tn));
        assertThat(remainingEntityTableNames)
                .as("remaining entity table names")
                .isEmpty();
    }

//    // -------------------------------------------------------------------------------------------- entityManagerFactory
//
//    /**
//     * Applies an injected instance of {@link EntityManagerFactory} to the specified function, and returns the result.
//     *
//     * @param function the function.
//     * @param <R>      result type parameter
//     * @return the result of the {@code function}.
//     * @see #acceptEntityManagerFactory(Consumer)
//     */
//    @Deprecated(forRemoval = true)
//    protected final <R> R applyEntityManagerFactory(
//            @Nonnull final Function<? super EntityManagerFactory, ? extends R> function) {
//        Objects.requireNonNull(function, "function is null");
//        return function.apply(entityManagerFactory);
//    }
//
//    /**
//     * Accepts an injected instance of {@link EntityManagerFactory} to the specified consumer.
//     *
//     * @param consumer the consumer.
//     * @see #applyEntityManagerFactory(Function)
//     */
//    @Deprecated(forRemoval = true)
//    protected final void acceptEntityManagerFactory(@Nonnull final Consumer<? super EntityManagerFactory> consumer) {
//        Objects.requireNonNull(consumer, "consumer is null");
//        applyEntityManagerFactory(emf -> {
//            consumer.accept(emf);
//            return null;
//        });
//    }
//
//    /**
//     * Applies an injected instance of {@link EntityManager} to the specified function, and returns the result.
//     *
//     * @param function the function.
//     * @param <R>      result type parameter
//     * @return the result of the {@code function}.
//     * @see #acceptEntityManager(Consumer)
//     */
//    protected final <R> R applyEntityManager(@Nonnull final Function<? super EntityManager, ? extends R> function) {
//        Objects.requireNonNull(function, "function is null");
//        return applyEntityManagerFactory(emf -> {
//            try (final var entityManager = emf.createEntityManager()) {
//                return function.apply(entityManager);
//            }
//        });
//    }
//
//    /**
//     * Accepts an injected instance of {@link EntityManager} to the specified consumer.
//     *
//     * @param consumer the consumer.
//     * @see #applyEntityManager(Function)
//     */
//    protected final void acceptEntityManager(@Nonnull final Consumer<? super EntityManager> consumer) {
//        Objects.requireNonNull(consumer, "consumer is null");
//        applyEntityManager(em -> {
//            consumer.accept(em);
//            return null;
//        });
//    }

//    // -----------------------------------------------------------------------------------------------------------------
//    @__PersistenceProducer.Integration__
//    @Inject
//    private EntityManagerFactory entityManagerFactory;
//
//    @Deprecated(forRemoval = true)
//    @__PersistenceProducer.Integration__
//    @Inject
//    private EntityManager entityManager;
//
//    private String catalog;
//
//    private String schema;
//
//    private String[] types;
}
