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

import com.github.jinahya.persistence.more.AttributeEnum;
import com.github.jinahya.persistence.more.AttributeEnumUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract base class for testing {@link AttributeEnum} implementations.
 * <p>
 * Extending it gives an enum three checks for free: that no constant carries a {@code null} attribute value, that no
 * two constants carry the same one — the second of which is what keeps a converter's lookup unambiguous — and that
 * {@link AttributeEnumUtils#valueOf(Class, Object)} finds each constant by its own attribute value. A subclass may also
 * override {@link #expectedAttributeValues()} to pin the attribute values down to, e.g., what a column's constraint
 * allows.
 *
 * @param <ENUM>      entity type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AttributeEnum_TestUtils
 * @see com.github.jinahya.persistence.more.converter.test.AttributeEnumConverter_Test
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class AttributeEnum_Test<ENUM extends Enum<ENUM> & AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * An abstract base class for testing {@link AttributeEnum.OfString} implementations.
     *
     * @param <E> enum type parameter
     */
    public abstract static class OfStringTest<E extends Enum<E> & AttributeEnum.OfString<E>>
            extends AttributeEnum_Test<E, String> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected OfStringTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    /**
     * An abstract base class for testing {@link AttributeEnum.OfNumber} implementations.
     *
     * @param <E> enum type parameter
     * @param <N> number type parameter
     */
    public abstract static class OfNumberTest<
            E extends Enum<E> & AttributeEnum.OfNumber<E, N>,
            N extends Number
            >
            extends AttributeEnum_Test<E, N> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected OfNumberTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    /**
     * An abstract base class for testing {@link AttributeEnum.OfInteger} implementations.
     *
     * @param <E> enum type parameter
     */
    public abstract static class OfIntegerTest<E extends Enum<E> & AttributeEnum.OfInteger<E>>
            extends OfNumberTest<E, Integer> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected OfIntegerTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    /**
     * An abstract base class for testing {@link AttributeEnum.OfLong} implementations.
     *
     * @param <E> enum type parameter
     */
    public abstract static class OfLongTest<E extends Enum<E> & AttributeEnum.OfLong<E>>
            extends OfNumberTest<E, Long> {

        /**
         * Creates a new instance for testing specific enum types.
         *
         * @param enumClass the enum class to test.
         */
        protected OfLongTest(final Class<E> enumClass) {
            super(enumClass);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specific enum types.
     *
     * @param enumClass the enum class to test.
     */
    protected AttributeEnum_Test(final Class<ENUM> enumClass) {
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
        AttributeEnum_TestUtils.acceptEachEnumConstantAndAttributeValue(
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
        AttributeEnum_TestUtils.acceptAttributeValueStream(
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

    /**
     * Verifies that {@link AttributeEnumUtils#valueOf(Class, Object)} finds each constant by its own attribute value.
     */
    @DisplayName("valueOf(enumClass, attributeValue) -> the constant")
    @Test
    protected void _RoundTrip_ValueOf() {
        acceptEachEnumConstantAndAttributeValue((ec, av) -> {
            assertSame(ec, AttributeEnumUtils.valueOf(enumClass, av));
        });
    }

    /**
     * Verifies that the attribute values are exactly the {@link #expectedAttributeValues() expected ones}, when they
     * are specified.
     */
    @DisplayName("attribute values are the expected ones")
    @Test
    protected void _Expected_AttributeValues() {
        final var expected = expectedAttributeValues();
        if (expected == null) {
            return;
        }
        final var actual = applyAttributeValueStream(s -> s.collect(Collectors.toSet()));
        assertEquals(expected, actual, () -> "attribute values of " + enumClass);
    }

    /**
     * Returns the attribute values the constants of {@link #enumClass} are expected to carry, e.g. those the column's
     * constraint allows. The default implementation returns {@code null}, which skips the check.
     *
     * @return a set of expected attribute values; {@code null} not to check.
     * @see #_Expected_AttributeValues()
     */
    protected Set<ATTRIBUTE> expectedAttributeValues() {
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Accepts each pair of enum constant and attribute value to the specified consumer.
     *
     * @param consumer the consumer.
     */
    protected void acceptEachEnumConstantAndAttributeValue(
            final BiConsumer<? super ENUM, ? super ATTRIBUTE> consumer) {
        AttributeEnum_TestUtils.acceptEachEnumConstantAndAttributeValue(enumClass, consumer);
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
        return AttributeEnum_TestUtils.applyEnumConstantStream(enumClass, function);
    }

    /**
     * Accepts a stream of all enum constants, of {@link #enumClass}, to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEnumConstantStream(Function)
     */
    protected final void acceptEnumConstantStream(final Consumer<? super Stream<ENUM>> consumer) {
        AttributeEnum_TestUtils.acceptEnumConstantStream(enumClass, consumer);
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
        return AttributeEnum_TestUtils.applyAttributeValueStream(enumClass, function);
    }

    /**
     * Accepts a stream of all attribute values, of {@link #enumClass}, to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyAttributeValueStream(Function)
     */
    protected final void acceptAttributeValueStream(final Consumer<? super Stream<ATTRIBUTE>> consumer) {
        AttributeEnum_TestUtils.acceptAttributeValueStream(enumClass, consumer);
    }

    // ------------------------------------------------------------------------------------------------------- enumClass

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENUM} type.
     */
    protected final Class<ENUM> enumClass;
}
