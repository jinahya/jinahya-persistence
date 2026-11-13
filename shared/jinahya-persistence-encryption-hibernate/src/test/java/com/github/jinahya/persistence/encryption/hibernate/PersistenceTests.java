package com.github.jinahya.persistence.encryption.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.function.Function;

final class PersistenceTests {

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

    static String columnType(final EntityManagerFactory factory, final String table, final String column) {
        return apply(factory, em -> (String) em.createNativeQuery(
                        "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table
                        + "' AND COLUMN_NAME = '" + column + "'")
                .getSingleResult()).toUpperCase();
    }

    static long columnLength(final EntityManagerFactory factory, final String table, final String column) {
        return ((Number) apply(factory, em -> em.createNativeQuery(
                "SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table
                + "' AND COLUMN_NAME = '" + column + "'")
                .getSingleResult())).longValue();
    }

    private PersistenceTests() {
        throw new AssertionError("instantiation is not allowed");
    }
}
