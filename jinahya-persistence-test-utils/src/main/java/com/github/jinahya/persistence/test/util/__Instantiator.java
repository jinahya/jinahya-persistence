package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An abstract class for initializing a new instance of a specific class.
 * <p>
 * The default {@link #get() get()} method instantiates the {@link #targetClass} using its no-argument constructor;
 * override it for a class which has none, or which requires constructor arguments to be in a usable state.
 * <p>
 * An instantiator is located, for its target class, by an {@link __InstantiatorLocator}, and is applied by
 * {@link __InstantiatorUtils}; it is also what a {@link __Randomizer} uses to obtain the instance it then populates.
 *
 * @param <T> the type of the instances to instantiate.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __InstantiatorLocator#STANDARD
 * @see __InstantiatorUtils#newInstantiatedInstanceOf(Class)
 * @see __Randomizer#newTargetInstance()
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __Instantiator<T> implements Supplier<T> {

//SEP:CONSTRUCTORS

    /**
     * Creates a new instance for instantiating the specified class.
     *
     * @param targetClass the class to be instantiated.
     * @throws NullPointerException when the {@code targetClass} is {@code null}.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies the {@code targetClass}, for
     *         that is how a located instantiator class is instantiated.
     * @see #targetClass
     */
    protected __Instantiator(final @Nonnull Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Instantiates {@link #targetClass}, using its no-argument constructor.
     *
     * @return a new instance of {@link #targetClass}.
     * @throws RuntimeException when the {@link #targetClass} declares no no-argument constructor, or when that
     *                          constructor is inaccessible, abstract, or throws.
     * @implSpec The default implementation returns {@code ___Utils.newInstance(targetClass)}; the constructor is made
     *         accessible when required, so that a {@code private} no-argument constructor is enough.
     * @see ___Utils#newInstance(Class)
     */
    @Nonnull
    @Override
    public T get() {
        return ___Utils.newInstance(targetClass);
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * The target type to instantiate.
     */
    protected final Class<T> targetClass;
}
