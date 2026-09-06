/**
 * Utilities for working with the core Jakarta Persistence types.
 * <p>
 * Each class here is a final, non-instantiable holder of {@code static} methods for a single Jakarta Persistence type:
 * <dl>
 *   <dt>{@link com.github.jinahya.persistence.__EntityManagerFactoryUtils}</dt>
 *   <dd>Reads the identifier of an entity, and reaches the
 *       {@link jakarta.persistence.PersistenceUnitUtil persistenceUnitUtil} and the
 *       {@link jakarta.persistence.metamodel.Metamodel metamodel} of a factory.</dd>
 *   <dt>{@link com.github.jinahya.persistence.__EntityManagerUtils}</dt>
 *   <dd>Runs a {@link java.util.function.Supplier supplier} or a {@link java.util.function.Function function} inside a
 *       resource-level transaction, either committing or rolling back, and unwraps a {@link java.sql.Connection} from
 *       an {@link jakarta.persistence.EntityManager entityManager}.</dd>
 * </dl>
 * The {@code ...AndRollback} variants always roll the transaction back, which makes them convenient for tests that
 * should leave no trace in the database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence;
