package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangUtils;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utilities for {@link ___Instantiator}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___Instantiator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___InstantiatorUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static <T> Optional<Class<?>> getInstantiatorClassOf(final Class<T> type) {
        return Optional.ofNullable(
                __JavaLangUtils.forAnyPostfixes(type, ___Instantiator.class, "Instantiator", "_Instantiator")
        ).map(ic -> {
            final var typeClass = __JavaLangReflectUtils.getActualTypeParameter(ic, ___Instantiator.class, 0);
            assertThat(typeClass)
                    .as("type class of the instantiator class: %s", ic)
                    .isNotNull()
                    .isAssignableTo(type);
            return ic;
        });
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Instantiator<T>> newInstantiatorInstanceOf(final Class<T> type) {
        return getInstantiatorClassOf(type)
                .map(__JavaLangReflectUtils::newInstanceOf)
                .map(i -> (___Instantiator<T>) i);
    }

    /**
     * Creates a new instance of the specified class, which implements {@link ___Instantiator}, and has a postfix of
     * either {@code "Instantiator"} or {@code "_Instantiator"}, after the fully qualified name of the specified class.
     * <p>
     * {@snippet lang = "java":
     * class MyPojo {
     * }
     *
     * class MyPojoInstantiator implements Instantiator<MyPojo> {
     *
     *     @Override
     *     public MyPojo get() {
     *         return new MyPojo();
     *     }
     * }
     *}
     *
     * @param type the class to be initialized.
     * @param <T>  class type parameter
     * @return an optional of the initialized instance; {@link Optional#empty() empty} when no instantiator found.
     */
    public static <T> Optional<T> newInitializedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return newInstantiatorInstanceOf(type)
                .map(___Instantiator::get);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___InstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
