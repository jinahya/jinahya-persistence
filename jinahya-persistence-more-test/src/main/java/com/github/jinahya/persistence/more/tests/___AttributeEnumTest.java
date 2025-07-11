package com.github.jinahya.persistence.more.tests;

/*-
 * #%L
 * jinahya-persistence-more-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.persistence.more.__AttributeEnum;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A class for testing subclass of {@link __AttributeEnum}.
 *
 * @param <ENUM>      enum type parameter
 * @param <ATTRIBUTE> attribute type parameter
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
abstract class ___AttributeEnumTest<ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    ___AttributeEnumTest(final Class<ENUM> enumClass, final Class<ATTRIBUTE> attributeClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Accepts each pair of enum constant and attribute value to the specified consumer.
     *
     * @param consumer the consumer.
     */
    protected void acceptEnumConstantAndAttributeValue(final BiConsumer<? super ENUM, ? super ATTRIBUTE> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            final var attributeValue = enumConstant.attributeValue();
            consumer.accept(enumConstant, attributeValue);
        }
    }

    /**
     * Applies a stream of all enum constants, of {@link #enumClass}, to the specified function, and returns the
     * result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEnumConstantStream(Consumer)
     */
    protected <R> R applyEnumConstantStream(final Function<? super Stream<ENUM>, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ENUM>builder();
        acceptEnumConstantAndAttributeValue((ec, av) -> builder.add(ec));
        return function.apply(builder.build());
    }

    /**
     * Accepts a stream of all enum constants, of {@link #enumClass}, to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEnumConstantStream(Function)
     */
    protected void acceptEnumConstantStream(final Consumer<? super Stream<ENUM>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEnumConstantStream(s -> {
            consumer.accept(s);
            return null;
        });
    }

    /**
     * Applies a stream of all attribute values, of {@link #enumClass}, to the specified function, and returns the
     * result.
     *
     * @param function the function.
     * @see #acceptAttributeValueStream(Consumer)
     */
    protected <R> R applyAttributeValueStream(final Function<? super Stream<ATTRIBUTE>, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ATTRIBUTE>builder();
        acceptEnumConstantAndAttributeValue((ec, av) -> builder.add(av));
        return function.apply(builder.build());
    }

    /**
     * Accepts a stream of all attribute values, of {@link #enumClass}, to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyAttributeValueStream(Function)
     */
    protected void acceptAttributeValueStream(final Consumer<? super Stream<ATTRIBUTE>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyAttributeValueStream(s -> {
            consumer.accept(s);
            return null;
        });
    }

    // ------------------------------------------------------------------------------------------------------- enumClass

    // -------------------------------------------------------------------------------------------------- attributeClass

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENUM} type.
     */
    protected final Class<ENUM> enumClass;

    /**
     * The class of {@link ATTRIBUTE} type.
     */
    protected final Class<ATTRIBUTE> attributeClass;
}
