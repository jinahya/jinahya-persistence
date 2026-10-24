package com.github.jinahya.persistence.more.converter.postgresql;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
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

import com.github.jinahya.persistence.more.converter.__AttributeConverterUtils;
import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Concrete {@link __ArrayAttributeConverter}s, one per element type whose text the parent package already converts.
 * <p>
 * Each one pairs the array literal with an element converter from
 * {@link com.github.jinahya.persistence.more.converter the parent package}: a {@code text[]} needs none, and an
 * {@code integer[]} or a {@code bigint[]} takes the decimal converter of its element type. An array of anything else
 * is a subclass of {@link __ArrayAttributeConverter} handing the constructor the element converter it wants.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __ArrayAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __ArrayAttributeConverters {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of a {@link List} of {@link String}s, for a {@code text[]} or a
     * {@code varchar[]} column.
     *
     * @implSpec Each element is written as it is, quoted when the literal syntax requires it, and read back
     *         unchanged. A {@code null} element becomes {@code NULL} and back.
     */
    @Converter(autoApply = false)
    public static class OfStrings extends __ArrayAttributeConverter<String>
            implements AttributeConverter<List<String>, Object> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor. It also
         *         lets a caller use this converter directly, without subclassing a leaf class.
         */
        public OfStrings() {
            super(__AttributeConverterUtils.using(UnaryOperator.identity(), UnaryOperator.identity()));
        }
    }

    /**
     * A converter for an entity attribute of a {@link List} of {@link Integer}s, for an {@code integer[]} column.
     *
     * @implSpec Each element goes through {@link __NumberStringAttributeConverters.OfInteger}.
     */
    @Converter(autoApply = false)
    public static class OfIntegers extends __ArrayAttributeConverter<Integer>
            implements AttributeConverter<List<Integer>, Object> {

        /**
         * Creates a new instance.
         */
        public OfIntegers() {
            super(new __NumberStringAttributeConverters.OfInteger());
        }
    }

    /**
     * A converter for an entity attribute of a {@link List} of {@link Long}s, for a {@code bigint[]} column.
     *
     * @implSpec Each element goes through {@link __NumberStringAttributeConverters.OfLong}.
     */
    @Converter(autoApply = false)
    public static class OfLongs extends __ArrayAttributeConverter<Long>
            implements AttributeConverter<List<Long>, Object> {

        /**
         * Creates a new instance.
         */
        public OfLongs() {
            super(new __NumberStringAttributeConverters.OfLong());
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __ArrayAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
