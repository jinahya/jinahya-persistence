package com.github.jinahya.persistence.test.util;

import com.github.jinahya.persistence.util.JinahyaPersistenceUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Utilities for testing persistence.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaPersistenceTestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    // the factories opened so far, by the names of their persistence units
    private static final Map<String, EntityManagerFactory> FACTORIES = new ConcurrentHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FACTORIES.values().forEach(f -> {
            try {
                if (f.isOpen()) {
                    f.close();
                }
            } catch (final RuntimeException re) {
                // the provider, or the database, may well be shutting down as well
                logger.log(System.Logger.Level.DEBUG, "failed to close " + f, re);
            }
        })));
    }

    /**
     * Applies the factory of the persistence unit of the specified name to the specified function, opening the factory
     * only for the first call of the unit.
     * <p>
     * Unlike {@link JinahyaPersistenceUtils#applyEntityManagerFactory(String, Function)}, which opens a new factory at
     * each call and closes it afterwards, this keeps the factory open, and applies the same one to each call for the
     * same unit, so that the cost of opening it &mdash; the provider reads the descriptor, builds the metamodel,
     * connects to the database, and generates the schema &mdash; is paid once for the whole run. The factories are
     * closed when the virtual machine shuts down.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to be applied with the factory.
     * @param <R>                 result type parameter
     * @return the result of the {@code function}.
     * @throws NullPointerException when either argument is {@code null}.
     * @apiNote The factory is shared, so the {@code function} should not close it; a factory found closed is
     *         opened again, as a new one, at the next call. What the {@code function} leaves in the database, and
     *         anything it adds to the factory, such as a named query, is seen by every later call for the same unit.
     * @see JinahyaPersistenceUtils#applyEntityManagerFactory(String, Function)
     * @see Persistence#createEntityManagerFactory(String)
     */
    public static <R> R applyCachedEntityManagerFactory(
            final String persistenceUnitName,
            final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(persistenceUnitName, "persistenceUnitName is null");
        Objects.requireNonNull(function, "function is null");
        final var factory = FACTORIES.compute(
                persistenceUnitName,
                (k, v) -> v != null && v.isOpen() ? v : Persistence.createEntityManagerFactory(k)
        );
        return function.apply(factory);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
