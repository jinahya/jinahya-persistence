package com.github.jinahya.persistence.test;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities, internal to this package, for locating and instantiating classes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __InstantiatorLocator#STANDARD
 * @see __RandomizerLocator#STANDARD
 * @see __PersisterLocator#STANDARD
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
final class ___Utils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Returns the specified class, only when it extends the specified supertype.
     *
     * @param clazz     the class to check; may be {@code null}.
     * @param supertype the supertype.
     * @return the {@code clazz}; {@code null} when the {@code clazz} is {@code null}, or does not extend the
     *         {@code supertype}.
     * @apiNote A class which does not extend the {@code supertype} is logged, at
     *         {@link System.Logger.Level#WARNING WARNING}, as it usually indicates a misconfiguration.
     */
    @Nullable
    static Class<?> classForSupertype(final @Nullable Class<?> clazz, final @Nonnull Class<?> supertype) {
        Objects.requireNonNull(supertype, "supertype is null");
        if (clazz == null) {
            return null;
        }
        if (!supertype.isAssignableFrom(clazz)) {
            logger.log(System.Logger.Level.WARNING, "{0} does not extend {1}", clazz, supertype);
            return null;
        }
        return clazz;
    }

    /**
     * Finds a sibling class of the specified type, which (optionally) extends the specified supertype and has any of
     * the specified postfixes.
     *
     * @param type      the type.
     * @param supertype the supertype; {@code null} to ignore.
     * @param postfixes the postfix candidates, in the order they are probed.
     * @return the sibling class meets given conditions; {@code null} when not found.
     * @apiNote Every probed class name is logged at {@link System.Logger.Level#TRACE TRACE}, so that a class which is
     *         not located, due to a name not following the convention, can be diagnosed.
     */
    @Nullable
    static Class<?> siblingClassForPostfix(final @Nonnull Class<?> type,
                                           final @Nullable Class<?> supertype,
                                           final @Nonnull String... postfixes) {
        Objects.requireNonNull(type, "type is null");
        if (Objects.requireNonNull(postfixes, "postfixes is null").length == 0) {
            throw new IllegalArgumentException("postfixes is empty");
        }
        // the class loader of the type, rather than the one of this class, for the type may have been loaded by an
        // other class loader
        final var classLoader = Optional.ofNullable(type.getClassLoader())
                .orElseGet(___Utils.class::getClassLoader);
        final String typeName = type.getName();
        for (final String postfix : postfixes) {
            if (postfix == null || postfix.strip().isBlank()) {
                continue;
            }
            final String className = typeName + postfix.strip();
            final Class<?> clazz;
            try {
                // no initialization; the class is merely being probed
                clazz = Class.forName(className, false, classLoader);
            } catch (final ClassNotFoundException cnfe) {
                logger.log(System.Logger.Level.TRACE, "no class named {0}", className);
                continue;
            }
            if (supertype != null && classForSupertype(clazz, supertype) == null) {
                continue;
            }
            logger.log(System.Logger.Level.TRACE, "located {0} for {1}", clazz, type);
            return clazz;
        }
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of the specified class, using its no-argument constructor.
     *
     * @param clazz the class to be instantiated.
     * @param <T>   class type parameter
     * @return a new instance of the {@code clazz}.
     * @see Class#getDeclaredConstructor(Class[])
     * @see Constructor#newInstance(Object...)
     */
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
    static <T> @Nonnull T newInstance(final @Nonnull Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        final Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (final NoSuchMethodException nsme) {
            throw new RuntimeException("no no-arg constructor of " + clazz, nsme);
        }
        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate " + clazz, roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
