package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.___Mapped;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

/**
 * A class for testing subclass of {@link ___Mapped}.
 *
 * @param <T> entity type parameter
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
public abstract class ___MappedTest<T extends ___Mapped<T>> {

    /**
     * Creates a new instance to test the specified entity class.
     *
     * @param typeClass the entity class to test.
     * @see #typeClass
     */
    protected ___MappedTest(final Class<T> typeClass) {
        super();
        this.typeClass = Objects.requireNonNull(typeClass, "typeClass is null");
    }

    // ----------------------------------------------------------------------------------------------------- typeClass

    /**
     * Returns a new instance of the {@link #typeClass}.
     *
     * @return a new instance of the {@link #typeClass}.
     */
    protected T newTypeInstance() {
        return __JavaLangReflectUtils.newInstanceOf(typeClass);
    }

    /**
     * Returns a apy of a new instance of {@link #typeClass}.
     *
     * @return a apy of a new spied instance of {@link #typeClass}.
     * @see #newTypeInstance()
     * @see Mockito#spy(Object)
     */
    protected T newTypeInstanceSpy() {
        return Mockito.spy(newTypeInstance());
    }

    /**
     * Returns a new randomized instance of the {@link #typeClass}.
     *
     * @return a new randomized instance of the {@link #typeClass}.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    protected Optional<T> newRandomizedTypeInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(typeClass);
    }

    /**
     * Returns a spy of anew randomized instance of the {@link #typeClass}.
     *
     * @return a apy of a new randomized instance of the {@link #typeClass}.
     * @see #newRandomizedTypeInstance()
     * @see Mockito#spy(Object)
     */
    protected Optional<T> newRandomizedTypeInstanceSpy() {
        return newRandomizedTypeInstance().map(Mockito::spy);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The type class to test.
     */
    protected final Class<T> typeClass;
}
