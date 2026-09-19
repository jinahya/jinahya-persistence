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

/**
 * Utilities for working with the core Jakarta Persistence types.
 * <p>
 * Each class here is a final, non-instantiable holder of {@code static} methods for a single Jakarta Persistence type:
 * <dl>
 *   <dt>{@link com.github.jinahya.persistence.__EntityManagerFactoryUtils}</dt>
 *   <dd>Reads the identifier and the version of an entity, and reaches the
 *       {@link jakarta.persistence.PersistenceUnitUtil persistenceUnitUtil} and the
 *       {@link jakarta.persistence.metamodel.Metamodel metamodel} of a factory.</dd>
 *   <dt>{@link com.github.jinahya.persistence.__EntityManagerUtils}</dt>
 *   <dd>Runs a {@link java.lang.Runnable runnable}, a {@link java.util.function.Supplier supplier}, a
 *       {@link java.util.function.Consumer consumer} or a {@link java.util.function.Function function} inside a
 *       resource-level transaction, either committing or rolling back, and reaches the {@link java.sql.Connection}
 *       underlying an {@link jakarta.persistence.EntityManager entityManager}.</dd>
 * </dl>
 * The {@code ...AndRollback} variants always roll the transaction back, which makes them convenient for tests that
 * should leave no trace in the database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence;
