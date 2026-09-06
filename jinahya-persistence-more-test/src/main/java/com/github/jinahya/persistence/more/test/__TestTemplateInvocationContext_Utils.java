package com.github.jinahya.persistence.more.test;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * A utility class for {@link TestTemplateInvocationContext}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __TestTemplateInvocationContext_Utils {

    /**
     * Creates a new {@link TestTemplateInvocationContext} out of the specified functions, each of which may be
     * {@code null} for the default behavior of the interface.
     *
     * @param getDisplayNameFunction          a function for
     *                                        {@link TestTemplateInvocationContext#getDisplayName(int)
     *                                        getDisplayName(int)}; may be {@code null}.
     * @param getAdditionalExtensionsSupplier a supplier for
     *                                        {@link TestTemplateInvocationContext#getAdditionalExtensions()
     *                                        getAdditionalExtensions()}.
     * @param prepareInvocationConsumer       a consumer for
     *                                        {@link TestTemplateInvocationContext#prepareInvocation(ExtensionContext)
     *                                        prepareInvocation(ExtensionContext)}; may be {@code null}.
     * @return a new invocation context.
     */
    static TestTemplateInvocationContext of(
            final @Nullable IntFunction<String> getDisplayNameFunction,
            final Supplier<? extends List<Extension>> getAdditionalExtensionsSupplier,
            final @Nullable Consumer<? super ExtensionContext> prepareInvocationConsumer) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                if (getDisplayNameFunction != null) {
                    return getDisplayNameFunction.apply(invocationIndex);
                }
                return TestTemplateInvocationContext.super.getDisplayName(invocationIndex);
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return getAdditionalExtensionsSupplier.get();
            }

            @Override
            public void prepareInvocation(final ExtensionContext context) {
                if (prepareInvocationConsumer != null) {
                    prepareInvocationConsumer.accept(context);
                    return;
                }
                TestTemplateInvocationContext.super.prepareInvocation(context);
            }
        };
    }

    /**
     * Creates a new instance, which is not allowed.
     */
    private __TestTemplateInvocationContext_Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
