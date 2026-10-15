package com.github.jinahya.persistence.encryption;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.function.Function;

/**
 * Helpers shared by the tests which run against a persistence unit.
 */
final class PersistenceTests {

    /**
     * Applies the specified function to an entity manager of the specified factory, in a transaction.
     */
    static <R> R apply(final EntityManagerFactory factory,
                       final Function<? super EntityManager, ? extends R> function) {
        try (final var em = factory.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            try {
                final var result = function.apply(em);
                tx.commit();
                return result;
            } catch (final RuntimeException re) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw re;
            }
        }
    }

    /**
     * Returns the data type the database reports for the specified column, in upper case.
     */
    static String columnType(final EntityManagerFactory factory, final String table, final String column) {
        return apply(factory, em -> (String) em.createNativeQuery(
                        "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table
                        + "' AND COLUMN_NAME = '" + column + "'")
                .getSingleResult()).toUpperCase();
    }

    private PersistenceTests() {
        throw new AssertionError("instantiation is not allowed");
    }
}
