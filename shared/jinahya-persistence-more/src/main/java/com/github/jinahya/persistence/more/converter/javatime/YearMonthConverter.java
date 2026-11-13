package com.github.jinahya.persistence.more.converter.javatime;

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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.invoke.MethodHandles;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Converters for an entity attribute of {@link YearMonth}, one per column type it is kept in.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __TemporalAccessorStringAttributeConverter
 */
public final class YearMonthConverter {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link YearMonth}, stored as text.
     *
     * @implSpec A value is stored as {@link YearMonth#toString()}, e.g. {@code "2026-09"}.
     */
    @Converter(autoApply = false)
    public static class OfString extends __TemporalAccessorStringAttributeConverter<YearMonth>
            implements AttributeConverter<YearMonth, String> {

        /**
         * Creates a new instance.
         */
        public OfString() {
            super(YearMonth.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfString(final DateTimeFormatter formatter) {
            super(YearMonth.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    private YearMonthConverter() {
        throw new AssertionError("instantiation is not allowed");
    }
}
