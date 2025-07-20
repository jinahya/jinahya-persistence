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

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __AttributeEnumTestUtils {

    static <E extends Enum<E> & __AttributeEnum<E, ?>, R>
    R applyEnumConstantStream(@Nonnull final Class<E> enumClass,
                              @Nonnull final Function<? super Stream<E>, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<E>builder();
        for (final E enumConstant : enumClass.getEnumConstants()) {
            builder.add(enumConstant);
        }
        return function.apply(builder.build());
    }

    static <E extends Enum<E> & __AttributeEnum<E, ?>>
    void acceptEnumConstantStream(@Nonnull final Class<E> enumClass,
                                  @Nonnull final Consumer<? super Stream<E>> consumer) {
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
    static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE, R>
    R applyAttributeValueStream(@Nonnull final Class<E> enumClass,
                                @Nonnull final Function<? super Stream<ATTRIBUTE>, ? extends R> function) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ATTRIBUTE>builder();
        for (final E enumConstant : enumClass.getEnumConstants()) {
            builder.add(enumConstant.attributeValue());
        }
        return function.apply(builder.build());
    }

    static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE, R>
    void acceptAttributeValueStream(@Nonnull final Class<E> enumClass,
                                    @Nonnull final Consumer<? super Stream<ATTRIBUTE>> consumer) {
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
    static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE>
    void acceptEachEnumConstantAndAttributeValue(@Nonnull final Class<E> enumClass,
                                                 @Nonnull final BiConsumer<? super E, ? super ATTRIBUTE> consumer) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        for (final E enumConstant : enumClass.getEnumConstants()) {
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
     * @param <E>       enum type parameter
     * @param <R>       result type parameter
     * @return the result of the {@code function}; {@code null} if the {@code enumClass} doesn't have any constant.
     * @see #getRandomEnumConstant(Class)
     */
    @Nullable
    public static <E extends Enum<E> & __AttributeEnum<E, ?>, R>
    R applyRandomEnumConstant(@Nonnull final Class<E> enumClass,
                              @Nonnull final Function<? super E, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEnumConstantStream(enumClass, s -> {
            final List<E> list = s.toList();
            if (list.isEmpty()) {
                return null;
            }
            final var index = ThreadLocalRandom.current().nextInt(list.size());
            final var value = list.get(index);
            return function.apply(value);
        });
    }

    /**
     * Returns a random enum constant of the specified enum class.
     *
     * @param enumClass the enum class.
     * @param <E>       enum type parameter
     * @return a random enum constant of the {@code enumClass}; {@code null} if the {@code enumClass} doesn't have any
     *         constant.
     * @see #applyRandomEnumConstant(Class, Function)
     */
    @Nullable
    public static <E extends Enum<E> & __AttributeEnum<E, ?>>
    E getRandomEnumConstant(@Nonnull final Class<E> enumClass) {
        return applyRandomEnumConstant(enumClass, Function.identity());
    }

    /**
     * Returns the result of the specified function, applied with a randomly selected attribute value of the specified
     * enum class.
     *
     * @param enumClass the enum class.
     * @param function  the function.
     * @param <E>       enum type parameter
     * @param <R>       result type parameter
     * @return the result of the {@code function}; {@code null} if the {@code enumClass} doesn't have any constant.
     * @see #getRandomAttributeValue(Class)
     */
    @Nullable
    public static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE, R>
    R applyRandomAttributeValue(@Nonnull final Class<E> enumClass,
                                @Nonnull final Function<? super ATTRIBUTE, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyRandomEnumConstant(enumClass, e -> function.apply(e.attributeValue()));
    }

    /**
     * Returns a random attribute value of the specified enum class.
     *
     * @param enumClass the enum class.
     * @param <E>       enum type parameter
     * @return a random attribute value of the {@code enumClass}; {@code null} if the {@code enumClass} doesn't have any
     *         constant.
     * @see #applyRandomAttributeValue(Class, Function)
     */
    @Nullable
    public static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE>
    ATTRIBUTE getRandomAttributeValue(@Nonnull final Class<E> enumClass) {
        return applyRandomAttributeValue(enumClass, Function.identity());
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __AttributeEnumTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
