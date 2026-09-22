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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract base class for testing {@link __AttributeEnum} implementations.
 * <p>
 * Extending it gives an enum two checks for free: that no constant carries a {@code null} attribute value, and that no
 * two constants carry the same one — the second of which is what keeps a converter's lookup unambiguous.
 *
 * @param <ENUM>      entity type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __AttributeEnum_TestUtils
 * @see com.github.jinahya.persistence.more.converter.test.__AttributeEnumConverter_Test
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnum_Test<ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE> {

    /**
     * An abstract base class for testing {@link __AttributeEnum.__OfString} implementations.
     *
     * @param <E> enum type parameter
     */
    public abstract static class __OfStringTest<E extends Enum<E> & __AttributeEnum.__OfString<E>>
            extends __AttributeEnum_Test<E, String> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected __OfStringTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    /**
     * An abstract base class for testing {@link __AttributeEnum.__OfNumber} implementations.
     *
     * @param <E> enum type parameter
     * @param <N> number type parameter
     */
    public abstract static class __OfNumberTest<E extends Enum<E> & __AttributeEnum.__OfNumber<E, N>,
            N extends Number>
            extends __AttributeEnum_Test<E, N> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected __OfNumberTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    /**
     * An abstract base class for testing {@link __AttributeEnum.__OfInteger} implementations.
     *
     * @param <E> enum type parameter
     */
    public abstract static class __OfIntegerTest<E extends Enum<E> & __AttributeEnum.__OfInteger<E>>
            extends __OfNumberTest<E, Integer> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected __OfIntegerTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    /**
     * An abstract base class for testing {@link __AttributeEnum.__OfLong} implementations.
     *
     * @param <E> enum type parameter
     */
    public abstract static class __OfLongTest<E extends Enum<E> & __AttributeEnum.__OfLong<E>>
            extends __OfNumberTest<E, Long> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected __OfLongTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specific enum types.
     *
     * @param enumClass the enum class to test.
     */
    protected __AttributeEnum_Test(final Class<ENUM> enumClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies that each enum constant has a non-null attribute value.
     */
    @DisplayName("no null attribute value")
    @Test
    protected void _NoNull_AttributeValue() {
        __AttributeEnum_TestUtils.acceptEachEnumConstantAndAttributeValue(
                enumClass,
                (ec, av) -> {
                    assertNotNull(av, () -> "null attribute value of " + ec);
                }
        );
    }

    /**
     * Verifies that each attribute value is unique among enum constants.
     */
    @DisplayName("no duplicates of attribute values")
    @Test
    protected void _NoDuplicates_AttributeValues() {
        __AttributeEnum_TestUtils.acceptAttributeValueStream(
                enumClass,
                s -> {
                    // AssertJ's doesNotHaveDuplicates() is the one assertion here with no counterpart in
                    // org.junit.jupiter.api.Assertions; naming the offending values matters more than the
                    // three lines it costs to find them
                    final var seen = new HashSet<>();
                    final var duplicates = new LinkedHashSet<>();
                    s.forEach(av -> {
                        if (!seen.add(av)) {
                            duplicates.add(av);
                        }
                    });
                    assertTrue(
                            duplicates.isEmpty(),
                            () -> "duplicate attribute values of " + enumClass + ": " + duplicates
                    );
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Accepts each pair of enum constant and attribute value to the specified consumer.
     *
     * @param consumer the consumer.
     */
    protected void acceptEachEnumConstantAndAttributeValue(
            final BiConsumer<? super ENUM, ? super ATTRIBUTE> consumer) {
        __AttributeEnum_TestUtils.acceptEachEnumConstantAndAttributeValue(enumClass, consumer);
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
    protected final <R> R applyEnumConstantStream(final Function<? super Stream<ENUM>, ? extends R> function) {
        return __AttributeEnum_TestUtils.applyEnumConstantStream(enumClass, function);
    }

    /**
     * Accepts a stream of all enum constants, of {@link #enumClass}, to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEnumConstantStream(Function)
     */
    protected final void acceptEnumConstantStream(final Consumer<? super Stream<ENUM>> consumer) {
        __AttributeEnum_TestUtils.acceptEnumConstantStream(enumClass, consumer);
    }

    /**
     * Applies a stream of all attribute values, of {@link #enumClass}, to the specified function, and returns the
     * result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptAttributeValueStream(Consumer)
     */
    protected final <R> R applyAttributeValueStream(
            final Function<? super Stream<ATTRIBUTE>, ? extends R> function) {
        return __AttributeEnum_TestUtils.applyAttributeValueStream(enumClass, function);
    }

    /**
     * Accepts a stream of all attribute values, of {@link #enumClass}, to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyAttributeValueStream(Function)
     */
    protected final void acceptAttributeValueStream(final Consumer<? super Stream<ATTRIBUTE>> consumer) {
        __AttributeEnum_TestUtils.acceptAttributeValueStream(enumClass, consumer);
    }

    // ------------------------------------------------------------------------------------------------------- enumClass

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENUM} type.
     */
    protected final Class<ENUM> enumClass;
}
