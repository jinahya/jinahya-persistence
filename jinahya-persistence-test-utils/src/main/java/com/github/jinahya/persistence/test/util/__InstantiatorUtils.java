package com.github.jinahya.persistence.test.util;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Utilities for {@link __Instantiator}.
 * <p>
 * An instantiator is found for a target class by the {@link #locateStandard(Class) naming convention}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Instantiator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __InstantiatorUtils {

    /**
     * Locates, by the naming convention, the instantiator class of the specified target class.
     * <p>
     * The instantiator class is a sibling of the {@code target} -- declared in the same package, beside it -- which
     * extends {@link __Instantiator} and has a postfix of either {@code "Instantiator"} or {@code "_Instantiator"}. For
     * a target class {@code Foo}, that is {@code FooInstantiator}, and then {@code Foo_Instantiator}.
     * <p>
     * The convention spans source sets: a {@code Foo} declared in {@code main} and a {@code FooInstantiator} declared
     * in {@code test} are the same package, and both are on the test classpath.
     *
     * @param target the target class whose instantiator class is located.
     * @return the instantiator class of the {@code target}; {@code null} when not found.
     * @implNote A class which is not a sibling of the {@code target} is never located: neither a local nor an
     *         anonymous class, which can not carry the required name, nor a class nested inside the {@code target},
     *         which would have to be declared in the source of the {@code target} itself. A class nested in a target
     *         class of {@code main} therefore has to be declared as a top-level class to be randomizable here.
     */
    @Nullable
    static Class<?> locateStandard(final Class<?> target) {
        assert target != null;
        return ___Utils.siblingClassForPostfixes(target, "Instantiator", "_Instantiator");
    }

    /**
     * Instantiates a new instance of the specified class, using the instantiator located for it by the
     * {@link #locateStandard(Class) naming convention}.
     * <p>
     * When no instantiator is located, the {@code target} itself is instantiated, using its no-argument constructor.
     * When one <em>is</em> located, it is used, and anything wrong with it -- it does not extend
     * {@link __Instantiator}, it can not be instantiated, or it produces something which is not an instance of the
     * {@code target} -- fails, rather than falling back: a class named by the convention is one the developer meant to
     * be used.
     *
     * @param target the class to be instantiated.
     * @param <T>    the target class type parameter.
     * @return a new instance of the {@code target}.
     * @throws NullPointerException when the {@code target} is {@code null}.
     * @throws RuntimeException     when the located instantiator is unusable, or produces nothing, or produces
     *                              something which is not a {@code target}; and when no instantiator is located and the
     *                              {@code target} declares no no-argument constructor.
     * @see #locateStandard(Class)
     * @see ___Utils#produced(Class, Object, Object)
     */
    public static <T> T newInstantiatedInstanceOf(final Class<T> target) {
        Objects.requireNonNull(target, "target is null");
        final Class<?> located = locateStandard(target);
        if (located == null) {
            // none is provided; the target instantiates itself, through its own no-argument constructor
            return ___Utils.newInstance(target);
        }
        // one is provided, so it is meant to be used; from here, anything wrong with it is a fault
        ___Utils.requireSubtype(located, __Instantiator.class, target);
        // and we can test 'targetClass' field for result compatibility?
        final var instantiator = (__Instantiator<?>) ___Utils.newInstance(located);
        return ___Utils.produced(target, instantiator, instantiator.get());
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private __InstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
