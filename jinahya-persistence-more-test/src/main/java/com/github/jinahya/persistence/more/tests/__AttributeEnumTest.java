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
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract base class for testing {@link __AttributeEnum} implementations.
 *
 * @param <E>         entity type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @see __AttributeEnumTestUtils
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnumTest<E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE> {

    /**
     * An abstract base class for testing {@link __AttributeEnum.__OfString} implementations.
     *
     * @param <E> enum type parameter
     */
    public abstract static class __OfStringTest<E extends Enum<E> & __AttributeEnum.__OfString<E>>
            extends __AttributeEnumTest<E, String> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected __OfStringTest(final Class<E> enumClass) {
            super(enumClass, String.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specific enum types.
     *
     * @param enumClass      the enum class to test.
     * @param attributeClass the type of entity attribute.
     */
    protected __AttributeEnumTest(final Class<E> enumClass, final Class<ATTRIBUTE> attributeClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    protected void _NoNullAttributeValue_() {
        __AttributeEnumTestUtils.acceptEachEnumConstantAndAttributeValue(
                enumClass,
                (ec, av) -> {
                    assertThat(av)
                            .as("attribute values of %s", ec)
                            .isNotNull();
                }
        );
    }

    @Test
    protected void _NoDuplicateAttributeValue_() {
        __AttributeEnumTestUtils.acceptAttributeValueStream(
                enumClass,
                s -> {
                    assertThat(s)
                            .as("attribute values of %s", enumClass)
                            .doesNotHaveDuplicates();
                }
        );
    }

//    // -----------------------------------------------------------------------------------------------------------------
//
//    /**
//     * Accepts each pair of enum constant and attribute value to the specified consumer.
//     *
//     * @param consumer the consumer.
//     */
//    protected void acceptEachEnumConstantAndAttributeValue(final BiConsumer<? super E, ? super ATTRIBUTE> consumer) {
//        __AttributeEnumTestUtils.acceptEachEnumConstantAndAttributeValue(enumClass, consumer);
//    }
//
//    /**
//     * Applies a stream of all enum constants, of {@link #enumClass}, to the specified function, and returns the
//     * result.
//     *
//     * @param function the function.
//     * @param <R>      result type parameter
//     * @return the result of the {@code function}.
//     * @see #acceptEnumConstantStream(Consumer)
//     */
//    protected final <R> R applyEnumConstantStream(final Function<? super Stream<E>, ? extends R> function) {
//        return __AttributeEnumTestUtils.applyEnumConstantStream(enumClass, function);
//    }
//
//    /**
//     * Accepts a stream of all enum constants, of {@link #enumClass}, to the specified consumer.
//     *
//     * @param consumer the consumer.
//     * @see #applyEnumConstantStream(Function)
//     */
//    protected final void acceptEnumConstantStream(final Consumer<? super Stream<E>> consumer) {
//        __AttributeEnumTestUtils.acceptEnumConstantStream(enumClass, consumer);
//    }
//
//    /**
//     * Applies a stream of all attribute values, of {@link #enumClass}, to the specified function, and returns the
//     * result.
//     *
//     * @param function the function.
//     * @see #acceptAttributeValueStream(Consumer)
//     */
//    protected final <R> R applyAttributeValueStream(final Function<? super Stream<ATTRIBUTE>, ? extends R> function) {
//        return __AttributeEnumTestUtils.applyAttributeValueStream(enumClass, function);
//    }
//
//    /**
//     * Accepts a stream of all attribute values, of {@link #enumClass}, to the specified consumer.
//     *
//     * @param consumer the consumer.
//     * @see #applyAttributeValueStream(Function)
//     */
//    protected final void acceptAttributeValueStream(final Consumer<? super Stream<ATTRIBUTE>> consumer) {
//        __AttributeEnumTestUtils.acceptAttributeValueStream(enumClass, consumer);
//    }

    // ------------------------------------------------------------------------------------------------------- enumClass

    // -------------------------------------------------------------------------------------------------- attributeClass

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link E} type.
     */
    protected final Class<E> enumClass;

    /**
     * The class of {@link ATTRIBUTE} type.
     */
    protected final Class<ATTRIBUTE> attributeClass;
}
