package com.github.jinahya.persistence.test.util;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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
    static Class<?> classForSupertype(final @Nullable Class<?> clazz, final Class<?> supertype) {
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
     * @apiNote Every probed class name is logged at {@link System.Logger.Level#TRACE TRACE}, so that a class
     *         which is not located, due to a name not following the convention, can be diagnosed.
     */
    @Nullable
    static Class<?> siblingClassForPostfix(final Class<?> type,
                                           final @Nullable Class<?> supertype,
                                           final String... postfixes) {
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
            // isBlank() already ignores surrounding whitespace, so stripping first was redundant here;
            // the strip() below is not -- it normalizes the postfix actually used to build the name
            if (postfix == null || postfix.isBlank()) {
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
            {
                // a candidate which can never be instantiated, such as an abstract base shared by several concrete
                // subclasses, does not end the probe; a later postfix may still yield a usable class
                final var reason = reasonNotInstantiable(clazz);
                if (reason != null) {
                    logger.log(System.Logger.Level.TRACE, "{0} is {1}; probing further", clazz, reason);
                    continue;
                }
            }
            logger.log(System.Logger.Level.TRACE, "located {0} for {1}", clazz, type);
            return clazz;
        }
        return null;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Returns whether a located producer, declared for the specified class, produces instances usable as the specified
     * target class.
     *
     * @param target   the class the caller requires instances of.
     * @param declared the class the {@code located} producer is declared for.
     * @param located  the located producer; for diagnostics only.
     * @return {@code true} when the {@code declared} class is the {@code target} class, or a subclass of it;
     *         {@code false} otherwise.
     * @apiNote A producer, such as an {@link __Instantiator} or a {@link __Randomizer}, is <em>covariant</em>
     *         in the class it is declared for; one declared for a subclass of the {@code target} still produces
     *         instances of the {@code target}, while one declared for a superclass, or for an unrelated class, does
     *         not.
     * @implNote An incompatible producer is logged, at {@link System.Logger.Level#WARNING WARNING}, and
     *         rejected; the caller then proceeds as if nothing had been located.
     * @see #canConsume(Class, Class, Object)
     */
    static boolean canProduce(final Class<?> target, final Class<?> declared,
                              final Object located) {
        assert target != null;
        assert declared != null;
        if (target.isAssignableFrom(declared)) {
            return true;
        }
        logger.log(System.Logger.Level.WARNING, "{0}, located for {1}, produces {2}; rejected", located, target,
                   declared);
        return false;
    }

    /**
     * Returns whether a located consumer, declared for the specified class, accepts instances of the specified target
     * class.
     *
     * @param target   the class the caller has instances of.
     * @param declared the class the {@code located} consumer is declared for.
     * @param located  the located consumer; for diagnostics only.
     * @return {@code true} when the {@code declared} class is the {@code target} class, or a superclass of it;
     *         {@code false} otherwise.
     * @apiNote A consumer, such as a {@link __Persister}, is <em>contravariant</em> in the class it is declared
     *         for; one declared for a superclass of the {@code target} accepts instances of the {@code target}, while
     *         one declared for a subclass, or for an unrelated class, does not. Note that this is the opposite of the
     *         rule applied to producers.
     * @implNote An incompatible consumer is logged, at {@link System.Logger.Level#WARNING WARNING}, and
     *         rejected; the caller then proceeds as if nothing had been located.
     * @see #canProduce(Class, Class, Object)
     */
    static boolean canConsume(final Class<?> target, final Class<?> declared,
                              final Object located) {
        assert target != null;
        assert declared != null;
        if (declared.isAssignableFrom(target)) {
            return true;
        }
        logger.log(System.Logger.Level.WARNING, "{0}, located for {1}, accepts only {2}; rejected", located, target,
                   declared);
        return false;
    }

    /**
     * Returns whether a located class can be instantiated, using a no-argument constructor.
     *
     * @param located the located class.
     * @param target  the class the {@code located} class was located for; for diagnostics only.
     * @return {@code true} when the {@code located} class may declare a usable no-argument constructor; {@code false}
     *         otherwise.
     * @implNote A class which can not be instantiated is logged, at
     *         {@link System.Logger.Level#WARNING WARNING}, and rejected; the caller then proceeds as if nothing had
     *         been located, rather than failing with a reflective error raised deep inside
     *         {@link #newInstance(Class)}.
     * @see #reasonNotInstantiable(Class)
     */
    static boolean canInstantiate(final Class<?> located, final Class<?> target) {
        assert located != null;
        assert target != null;
        final var reason = reasonNotInstantiable(located);
        if (reason == null) {
            return true;
        }
        logger.log(System.Logger.Level.WARNING, "{0}, located for {1}, is {2}; rejected", located, target, reason);
        return false;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Returns the reason why the specified class can not possibly declare a usable no-argument constructor.
     *
     * @param clazz the class to check.
     * @return the reason, as a predicate phrase; {@code null} when the {@code clazz} may declare one.
     * @apiNote This method recognizes only the shapes which are non-instantiable by construction, so that
     *         {@link #newInstance(Class)} can name the actual problem; the remaining failures, such as a class which
     *         simply declares no no-argument constructor, are left to reflection to report.
     * @see #newInstance(Class)
     */
    @Nullable
    static String reasonNotInstantiable(final Class<?> clazz) {
        assert clazz != null;
        if (clazz.isPrimitive()) {
            return "a primitive type";
        }
        if (clazz.isArray()) {
            return "an array type";
        }
        if (clazz.isInterface()) {
            return "an interface";
        }
        if (clazz.isEnum()) {
            return "an enum";
        }
        if (clazz.isRecord()) {
            return "a record, which declares no no-argument constructor";
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return "abstract";
        }
        // a member class which is not static; its constructors all take the enclosing instance
        if (clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers())) {
            return "an inner class; declare it 'static'";
        }
        return null;
    }

    /**
     * Creates a new instance of the specified class, using its no-argument constructor.
     *
     * @param clazz the class to be instantiated.
     * @param <T>   the type of the instance to create.
     * @return a new instance of the {@code clazz}.
     * @throws IllegalArgumentException when the {@code clazz} is of a shape which can not declare a no-argument
     *                                  constructor, such as an interface, an enum, a record, an abstract class, or an
     *                                  inner class.
     * @throws RuntimeException         when the {@code clazz} declares no no-argument constructor, or when that
     *                                  constructor is inaccessible or throws.
     * @implNote The no-argument constructor is made
     *         {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) accessible} when required, so that a
     *         class may keep it {@code private} and still be instantiated here.
     * @see #reasonNotInstantiable(Class)
     * @see Class#getDeclaredConstructor(Class[])
     * @see Constructor#newInstance(Object...)
     */
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
    static <T> T newInstance(final Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        {
            final var reason = reasonNotInstantiable(clazz);
            if (reason != null) {
                throw new IllegalArgumentException("unable to instantiate " + clazz + "; it is " + reason);
            }
        }
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

    // ---------------------------------------------------------------------------------------------------------------------
    private ___Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
