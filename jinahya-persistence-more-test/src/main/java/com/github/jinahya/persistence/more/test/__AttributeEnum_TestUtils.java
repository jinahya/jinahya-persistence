package com.github.jinahya.persistence.more.test;

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
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utilities for testing {@link __AttributeEnum}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __AttributeEnum_TestUtils {

    /**
     * Applies a stream of all constants of the specified enum class to the specified function, and returns the result.
     *
     * @param enumClass the enum class whose constants are streamed.
     * @param function  the function to apply the stream to.
     * @param <ENUM>    enum type parameter
     * @param <R>       result type parameter
     * @return the result of the {@code function}.
     */
    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ?>, R extends @Nullable Object>
    R applyEnumConstantStream(final Class<ENUM> enumClass,
                              final Function<? super Stream<ENUM>, ? extends R> function) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ENUM>builder();
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            builder.add(enumConstant);
        }
        return function.apply(builder.build());
    }

    /**
     * Accepts a stream of all constants of the specified enum class to the specified consumer.
     *
     * @param enumClass the enum class whose constants are streamed.
     * @param consumer  the consumer to accept the stream.
     * @param <E>       enum type parameter
     * @see #applyEnumConstantStream(Class, Function)
     */
    static <E extends Enum<E> & __AttributeEnum<E, ?>>
    void acceptEnumConstantStream(final Class<E> enumClass,
                                  final Consumer<? super Stream<E>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        __AttributeEnum_TestUtils.<E, @Nullable Void>applyEnumConstantStream(
                enumClass,
                s -> {
                    consumer.accept(s);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies a stream of the attribute values of all constants of the specified enum class to the specified function,
     * and returns the result.
     *
     * @param enumClass   the enum class whose attribute values are streamed.
     * @param function    the function to apply the stream to.
     * @param <ENUM>      enum type parameter
     * @param <ATTRIBUTE> attribute type parameter
     * @param <R>         result type parameter
     * @return the result of the {@code function}.
     */
    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE, R extends @Nullable Object>
    R applyAttributeValueStream(final Class<ENUM> enumClass,
                                final Function<? super Stream<ATTRIBUTE>, ? extends R> function) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ATTRIBUTE>builder();
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            builder.add(enumConstant.attributeValue());
        }
        return function.apply(builder.build());
    }

    /**
     * Accepts a stream of the attribute values of all constants of the specified enum class to the specified consumer.
     *
     * @param enumClass   the enum class whose attribute values are streamed.
     * @param consumer    the consumer to accept the stream.
     * @param <ENUM>      enum type parameter
     * @param <ATTRIBUTE> attribute type parameter
     * @param <R>         unused result type parameter
     * @see #applyAttributeValueStream(Class, Function)
     */
    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE, R>
    void acceptAttributeValueStream(final Class<ENUM> enumClass,
                                    final Consumer<? super Stream<ATTRIBUTE>> consumer) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        __AttributeEnum_TestUtils.<ENUM, ATTRIBUTE, @Nullable Void>applyAttributeValueStream(
                enumClass,
                s -> {
                    consumer.accept(s);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Accepts each constant of the specified enum class, paired with its attribute value, to the specified consumer.
     *
     * @param enumClass   the enum class whose constants are iterated.
     * @param consumer    the consumer to accept each constant and its attribute value.
     * @param <ENUM>      enum type parameter
     * @param <ATTRIBUTE> attribute type parameter
     */
    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
    void acceptEachEnumConstantAndAttributeValue(final Class<ENUM> enumClass,
                                                 final BiConsumer<? super ENUM, ? super ATTRIBUTE> consumer) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            final var attributeValue = enumConstant.attributeValue();
            consumer.accept(enumConstant, attributeValue);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the result of the specified function, applied with a random enum constant of the specified enum class.
     *
     * @param enumClass the enum class.
     * @param function  the function.
     * @param <ENUM>    enum type parameter
     * @param <R>       result type parameter
     * @return the result of the {@code function}; {@code null} if the {@code enumClass} doesn't have any constant.
     * @see #getRandomEnumConstant(Class)
     */
    // NullAway cannot infer a @Nullable type argument for the generic call below from the lambda body;
    // the enclosing method is declared @Nullable and documents the null result.
    @SuppressWarnings("NullAway")
    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ?>, R extends @Nullable Object>
    @Nullable R applyRandomEnumConstant(final Class<ENUM> enumClass,
                                        final Function<? super ENUM, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEnumConstantStream(
                enumClass,
                s -> {
                    final List<ENUM> list = s.toList();
                    if (list.isEmpty()) {
                        return null;
                    }
                    final var index = ThreadLocalRandom.current().nextInt(list.size());
                    final var value = list.get(index);
                    return function.apply(value);
                }
        );
    }

    /**
     * Returns a random enum constant of the specified enum class.
     *
     * @param enumClass the enum class.
     * @param <ENUM>    enum type parameter
     * @return a random enum constant of the {@code enumClass}; {@code null} if the {@code enumClass} doesn't have any
     *         constant.
     * @see #applyRandomEnumConstant(Class, Function)
     */
    @Nullable
    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ?>>
    ENUM getRandomEnumConstant(final Class<ENUM> enumClass) {
        return applyRandomEnumConstant(enumClass, Function.identity());
    }

    /**
     * Returns the result of the specified function, applied with a randomly selected attribute value of the specified
     * enum class.
     *
     * @param enumClass the enum class.
     * @param function  the function.
     * @param <ENUM>    enum type parameter
     * @param <R>       result type parameter
     * @return the result of the {@code function}; {@code null} if the {@code enumClass} doesn't have any constant.
     * @see #getRandomAttributeValue(Class)
     */
    @Nullable
    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE, R>
    R applyRandomAttributeValue(final Class<ENUM> enumClass,
                                final Function<? super ATTRIBUTE, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomEnumConstant(enumClass, e -> function.apply(e.attributeValue()));
    }

    /**
     * Returns a random attribute value of the specified enum class.
     *
     * @param enumClass the enum class.
     * @param <ENUM>    enum type parameter
     * @return a random attribute value of the {@code enumClass}; {@code null} if the {@code enumClass} doesn't have any
     *         constant.
     * @see #applyRandomAttributeValue(Class, Function)
     */
    @Nullable
    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
    ATTRIBUTE getRandomAttributeValue(final Class<ENUM> enumClass) {
        return applyRandomAttributeValue(enumClass, Function.identity());
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __AttributeEnum_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
