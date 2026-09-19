package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
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

/**
 * An interface for defining enum constants with a specific type of attribute values.
 * <p>
 * An enum implementing this interface carries, for each constant, the value which is actually stored in the database,
 * so that the persisted form does not depend on {@link Enum#name() name()} or, worse, on
 * {@link Enum#ordinal() ordinal()}. Constants can then be renamed or reordered without a migration.
 *
 * @param <SELF>      self type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.persistence.more.converter.__AttributeEnumConverter
 * @see __AttributeEnumUtils
 */
@SuppressWarnings({
        "java:S114", // Interface names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
public interface __AttributeEnum<SELF extends Enum<SELF> & __AttributeEnum<SELF, ATTRIBUTE>, ATTRIBUTE> {

    /**
     * An interface for defining enum constants with attribute values of string attribute values.
     * <p>
     * Note that the default {@link #attributeValue()} returns {@link Enum#name() name()}, which re-couples the
     * persisted value to the constant's name — the very coupling {@link __AttributeEnum} exists to break. Override it
     * in every constant whose stored value should survive a rename.
     *
     * @param <SELF> self type parameter
     */
    @SuppressWarnings({
            "java:S114" // Interface names should comply with a naming convention
    })
    interface __OfString<SELF extends Enum<SELF> & __OfString<SELF>> extends __AttributeEnum<SELF, String> {

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @apiNote The {@code attributeValue()} method of {@code __OfString} class returns
         *         {@link Enum#name() this.name()}.
         */
        @Override
        @SuppressWarnings({"unchecked"})
        default String attributeValue() {
            return ((SELF) this).name();
        }
    }

    /**
     * An interface for defining enum constants with attribute values of a specific subtype of {@link Number}.
     *
     * @param <SELF>   self type parameter
     * @param <NUMBER> number type parameter
     * @apiNote This interface defines no default {@link #attributeValue()}, and cannot: the
     *         {@link Enum#name() name()} of a constant is a {@link String}, and the only numeric candidate,
     *         {@link Enum#ordinal() ordinal()}, is exactly the coupling {@link __AttributeEnum} exists to break — it
     *         changes whenever constants are reordered. Every constant therefore has to declare its own value.
     * @apiNote The named subtypes stop at {@link __OfInteger} and {@link __OfLong} on purpose. A constant is
     *         looked up by its attribute value through {@link Object#equals(Object) equals} and
     *         {@link Object#hashCode() hashCode()}, and the remaining common numeric types do not behave under that
     *         contract the way a discriminator has to: {@link java.math.BigDecimal#equals(Object) BigDecimal.equals} is
     *         scale-sensitive, so {@code 1.0} and {@code 1.00} are different keys, which makes a lookup depend on the
     *         scale the column and the driver happen to produce; and {@link Double} and {@link Float} distinguish
     *         {@code 0.0} from {@code -0.0}, report {@code NaN} equal to itself, and need not round-trip a column bit
     *         for bit. This interface stays generic over {@code NUMBER} regardless, so an enum with a genuine need can
     *         still implement it with any {@link Number} &mdash; it simply does so knowingly, and without the
     *         convenience the named subtypes add.
     * @see __OfString
     */
    @SuppressWarnings({
            "java:S114", // Interface names should comply with a naming convention
            "java:S119"  // Type parameter names should comply with a naming convention
    })
    interface __OfNumber<SELF extends Enum<SELF> & __OfNumber<SELF, NUMBER>, NUMBER extends Number>
            extends __AttributeEnum<SELF, NUMBER> {

    }

    /**
     * An interface for defining enum constants with {@link Integer} attribute values.
     *
     * @param <SELF> self type parameter
     * @see com.github.jinahya.persistence.more.converter.__AttributeEnumConverter.__OfInteger
     */
    @SuppressWarnings({
            "java:S114" // Interface names should comply with a naming convention
    })
    interface __OfInteger<SELF extends Enum<SELF> & __OfInteger<SELF>> extends __OfNumber<SELF, Integer> {

        /**
         * Finds the constant, of the specified enum class, whose attribute value equals the specified value.
         *
         * @param enumClass      the enum class to search.
         * @param attributeValue the attribute value to look for.
         * @param <E>            enum type parameter
         * @return the enum constant, of the {@code enumClass}, that has the specified attribute value.
         * @throws IllegalArgumentException when no constant of the {@code enumClass} carries the
         *                                  {@code attributeValue}.
         * @apiNote Taking an {@code int} keeps the value boxed as an {@link Integer}; a {@link Long} of the
         *         same numeric value does not match a constant of this interface.
         * @see __AttributeEnumUtils#valueOf(Class, Object)
         */
        static <E extends Enum<E> & __OfInteger<E>> E valueOfAttributeValue(final Class<E> enumClass,
                                                                            final int attributeValue) {
            return __AttributeEnumUtils.valueOf(enumClass, attributeValue);
        }

        /**
         * Returns the attribute value of this enum constant, unboxed.
         *
         * @return the {@link #attributeValue() attributeValue} of this enum constant, as an {@code int}.
         * @throws NullPointerException when this constant's {@link #attributeValue() attributeValue} is {@code null}.
         * @implSpec The conversion is exact; unlike {@link Number#intValue()} on an arbitrary number, nothing
         *         is narrowed or rounded here. A {@code null} {@code attributeValue} is a declaration error &mdash;
         *         {@link __AttributeEnumUtils} rejects one outright &mdash; but this method does not go through that
         *         map, so it names the offending constant rather than unboxing into a bare
         *         {@link NullPointerException}.
         */
        default int intValue() {
            return java.util.Objects.requireNonNull(
                    attributeValue(), () -> "null attributeValue of " + this);
        }
    }

    /**
     * An interface for defining enum constants with {@link Long} attribute values.
     *
     * @param <SELF> self type parameter
     * @see com.github.jinahya.persistence.more.converter.__AttributeEnumConverter.__OfLong
     */
    @SuppressWarnings({
            "java:S114" // Interface names should comply with a naming convention
    })
    interface __OfLong<SELF extends Enum<SELF> & __OfLong<SELF>> extends __OfNumber<SELF, Long> {

        /**
         * Finds the constant, of the specified enum class, whose attribute value equals the specified value.
         *
         * @param enumClass      the enum class to search.
         * @param attributeValue the attribute value to look for.
         * @param <E>            enum type parameter
         * @return the enum constant, of the {@code enumClass}, that has the specified attribute value.
         * @throws IllegalArgumentException when no constant of the {@code enumClass} carries the
         *                                  {@code attributeValue}.
         * @apiNote Taking a {@code long} keeps the value boxed as a {@link Long}; an {@link Integer} of the
         *         same numeric value does not match a constant of this interface.
         * @see __AttributeEnumUtils#valueOf(Class, Object)
         */
        static <E extends Enum<E> & __OfLong<E>> E valueOfAttributeValue(final Class<E> enumClass,
                                                                         final long attributeValue) {
            return __AttributeEnumUtils.valueOf(enumClass, attributeValue);
        }

        /**
         * Returns the attribute value of this enum constant, unboxed.
         *
         * @return the {@link #attributeValue() attributeValue} of this enum constant, as a {@code long}.
         * @throws NullPointerException when this constant's {@link #attributeValue() attributeValue} is {@code null}.
         * @implSpec The conversion is exact; unlike {@link Number#longValue()} on an arbitrary number, nothing
         *         is narrowed or rounded here. A {@code null} {@code attributeValue} is a declaration error &mdash;
         *         {@link __AttributeEnumUtils} rejects one outright &mdash; but this method does not go through that
         *         map, so it names the offending constant rather than unboxing into a bare
         *         {@link NullPointerException}.
         */
        default long longValue() {
            return java.util.Objects.requireNonNull(
                    attributeValue(), () -> "null attributeValue of " + this);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the attribute value of this enum constant.
     *
     * @return the attribute value of this enum constant.
     */
    ATTRIBUTE attributeValue();
}
