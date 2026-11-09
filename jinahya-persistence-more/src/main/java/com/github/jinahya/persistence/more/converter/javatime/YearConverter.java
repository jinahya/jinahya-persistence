package com.github.jinahya.persistence.more.converter.javatime;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
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
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Converters for an entity attribute of {@link Year}, one per column type a year is kept in.
 * <p>
 * {@link OfString} stores the text of the year. {@link OfNumber} is the intermediate class for a numeric column, and
 * {@link OfShort} and {@link OfInteger} are its concrete converters.
 * <p>
 * Each concrete converter implements {@link AttributeConverter AttributeConverter&lt;Year, Y&gt;} itself, not only
 * through its parent: EclipseLink takes a converter's column type from the type arguments of the interface the class
 * names, as {@link ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote MySQL's {@code YEAR} column is a numeric one, and {@link OfShort} is the converter for it. MySQL
 *         Connector/J, by default ({@code yearIsDateType=true}), returns the column from {@code getObject} as a
 *         {@link java.sql.Date} of January the first of the year, while {@code getShort} and {@code setShort} work
 *         either way. A provider which reads the column with {@code getShort} needs no option; EclipseLink reads it
 *         with {@code getObject}, and needs {@code yearIsDateType=false} in the URL, with which {@code getObject}
 *         returns a {@link Short}.
 *         <p>
 *         There is no converter to a {@link java.sql.Date}: Connector/J's client-side prepared statement, the default,
 *         sends {@code setDate} as a string such as {@code '2026-01-01'}, which MySQL's strict mode refuses for a
 *         {@code YEAR} column ({@code Data truncated}); only {@code useServerPrepStmts=true} makes it work.
 */
public final class YearConverter {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link Year}, stored as text.
     *
     * @implSpec A value is stored as {@link Year#toString()}, which is the plain number for the four-digit
     *         years and a signed, wider form outside them &mdash; {@code "+12345"}, {@code "-0001"}.
     */
    @Converter(autoApply = false)
    public static class OfString extends __TemporalAccessorStringAttributeConverter<Year>
            implements AttributeConverter<Year, String> {

        /**
         * Creates a new instance.
         */
        public OfString() {
            super(Year.class);
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
            super(Year.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An abstract converter for an entity attribute of {@link Year}, stored as its {@link Year#getValue() value} in a
     * numeric column.
     * <p>
     * The column holds the proleptic year itself &mdash; {@code 2026} for 2026, {@code 0} for 1 BC &mdash; so the
     * encoding is increasing, and the round trip is exact for every year the column type can hold.
     *
     * @param <Y> the numeric column type parameter
     */
    public abstract static class OfNumber<Y extends Number> implements AttributeConverter<Year, Y> {

        /**
         * Creates a new instance with the specified function narrowing a year's value to the column type.
         *
         * @param encoder the function which narrows a year's value to the number written to the column; it throws
         *                an {@link IllegalArgumentException} for a value the column type cannot hold.
         * @throws NullPointerException when {@code encoder} is {@code null}.
         */
        protected OfNumber(final IntFunction<? extends Y> encoder) {
            super();
            this.encoder = Objects.requireNonNull(encoder, "encoder is null");
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the value of the {@code attribute}, narrowed to the column type; {@code null} when the
         *         {@code attribute} is {@code null}.
         * @throws IllegalArgumentException when the value of the {@code attribute} does not fit the column type.
         */
        @Override
        public Y convertToDatabaseColumn(final Year attribute) {
            if (attribute == null) {
                return null;
            }
            return encoder.apply(attribute.getValue());
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the year of the {@code dbData}; {@code null} when the {@code dbData} is {@code null}.
         * @throws java.time.DateTimeException when the {@code dbData} is outside the range of {@link Year}.
         */
        @Override
        public Year convertToEntityAttribute(final Y dbData) {
            if (dbData == null) {
                return null;
            }
            return Year.of(dbData.intValue());
        }

        /**
         * The function which narrows a year's value to the number written to the column.
         */
        protected final transient IntFunction<? extends Y> encoder;
    }

    /**
     * A converter for an entity attribute of {@link Year}, stored as a {@link Short}.
     *
     * @implSpec A year outside {@value Short#MIN_VALUE} to {@value Short#MAX_VALUE} is refused with an
     *         {@link IllegalArgumentException}, not truncated.
     * @apiNote This is the converter for MySQL's {@code YEAR} column; on EclipseLink, the URL needs
     *         {@code yearIsDateType=false}, as {@link YearConverter} explains.
     */
    @Converter(autoApply = false)
    public static class OfShort extends OfNumber<Short>
            implements AttributeConverter<Year, Short> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfShort() {
            super(v -> {
                if (v < Short.MIN_VALUE || v > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("year out of range of a short: " + v);
                }
                return (short) v;
            });
        }
    }

    /**
     * A converter for an entity attribute of {@link Year}, stored as an {@link Integer}.
     *
     * @implSpec Every {@link Year} fits an {@code int}, so nothing is refused on the way in.
     */
    @Converter(autoApply = false)
    public static class OfInteger extends OfNumber<Integer>
            implements AttributeConverter<Year, Integer> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfInteger() {
            super(Integer::valueOf);
        }
    }

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    private YearConverter() {
        throw new AssertionError("instantiation is not allowed");
    }
}
