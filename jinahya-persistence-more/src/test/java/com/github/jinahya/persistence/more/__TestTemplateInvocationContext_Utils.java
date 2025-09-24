package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

final class __TestTemplateInvocationContext_Utils {

    static TestTemplateInvocationContext of(
            @Nullable final IntFunction<String> getDisplayNameFunction,
            @Nonnull final Supplier<? extends List<Extension>> getAdditionalExtensionsSupplier,
            @Nullable final Consumer<? super ExtensionContext> prepareInvocationConsumer) {
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

    private __TestTemplateInvocationContext_Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
