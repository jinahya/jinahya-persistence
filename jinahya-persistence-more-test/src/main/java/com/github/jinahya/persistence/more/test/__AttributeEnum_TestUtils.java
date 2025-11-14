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
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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

    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ?>, R>
    R applyEnumConstantStream(final @Nonnull Class<ENUM> enumClass,
                              final @Nonnull Function<? super Stream<ENUM>, ? extends R> function) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ENUM>builder();
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            builder.add(enumConstant);
        }
        return function.apply(builder.build());
    }

    static <E extends Enum<E> & __AttributeEnum<E, ?>>
    void acceptEnumConstantStream(final @Nonnull Class<E> enumClass,
                                  final @Nonnull Consumer<? super Stream<E>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEnumConstantStream(
                enumClass,
                s -> {
                    consumer.accept(s);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE, R>
    R applyAttributeValueStream(final @Nonnull Class<ENUM> enumClass,
                                final @Nonnull Function<? super Stream<ATTRIBUTE>, ? extends R> function) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ATTRIBUTE>builder();
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            builder.add(enumConstant.attributeValue());
        }
        return function.apply(builder.build());
    }

    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE, R>
    void acceptAttributeValueStream(final @Nonnull Class<ENUM> enumClass,
                                    final @Nonnull Consumer<? super Stream<ATTRIBUTE>> consumer) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        applyAttributeValueStream(
                enumClass,
                s -> {
                    consumer.accept(s);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
    void acceptEachEnumConstantAndAttributeValue(final @Nonnull Class<ENUM> enumClass,
                                                 final @Nonnull BiConsumer<? super ENUM, ? super ATTRIBUTE> consumer) {
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
    @Nullable
    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ?>, R>
    R applyRandomEnumConstant(final @Nonnull Class<ENUM> enumClass,
                              final @Nonnull Function<? super ENUM, ? extends R> function) {
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
    ENUM getRandomEnumConstant(final @Nonnull Class<ENUM> enumClass) {
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
    R applyRandomAttributeValue(final @Nonnull Class<ENUM> enumClass,
                                final @Nonnull Function<? super ATTRIBUTE, ? extends R> function) {
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
    ATTRIBUTE getRandomAttributeValue(final @Nonnull Class<ENUM> enumClass) {
        return applyRandomAttributeValue(enumClass, Function.identity());
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __AttributeEnum_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
