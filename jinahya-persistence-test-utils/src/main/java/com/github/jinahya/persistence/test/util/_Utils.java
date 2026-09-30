package com.github.jinahya.persistence.test.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utilities, internal to this package, for instantiating classes reflectively.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityPersisterUtils
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S112", // Generic exceptions should never be thrown
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
final class _Utils {

    /**
     * Creates a new instance of the specified class, using its no-argument constructor.
     *
     * @param clazz the class to be instantiated.
     * @param <T>   the type of the instance to create.
     * @return a new instance of the {@code clazz}.
     * @throws RuntimeException when the {@code clazz} declares no no-argument constructor, or when that constructor is
     *                          inaccessible or throws; and when the {@code clazz} is of a shape which can not be
     *                          instantiated at all, such as an interface, an enum, an abstract class, or an inner
     *                          class.
     * @apiNote The shape of the {@code clazz} is not examined beforehand. Every shape which can not be
     *         instantiated fails here anyway -- on the constructor lookup, or on the instantiation itself -- and
     *         reflection names the failure; a check ahead of it would only duplicate that.
     * @implNote The no-argument constructor is made
     *         {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) accessible} when required, so that a
     *         class may keep it {@code private} and still be instantiated here.
     * @see Class#getDeclaredConstructor(Class[])
     * @see Constructor#newInstance(Object...)
     */
    static <T> T newInstance(final Class<T> clazz) {
        assert clazz != null;
        final Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (final NoSuchMethodException nsme) {
            // an interface, an enum, and an inner class all land here: none of them declares a constructor which
            // takes no argument at all
            throw new RuntimeException("no no-arg constructor; class: " + clazz, nsme);
        }
        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance();
        } catch (final InvocationTargetException ite) {
            // the constructor itself threw; wrap what it threw, rather than the reflective wrapper around it, so
            // that the cause of the failure is one getCause() away, as it is for every other failure here
            throw new RuntimeException("failed to instantiate; class: " + clazz, ite.getCause());
        } catch (final ReflectiveOperationException roe) {
            // an abstract class lands here: it may well declare a no-argument constructor, which simply can not be
            // invoked on its own
            throw new RuntimeException("failed to instantiate; class: " + clazz, roe);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private _Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
