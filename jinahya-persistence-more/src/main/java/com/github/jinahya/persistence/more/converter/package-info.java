/**
 * Abstract classes and utilities for writing a Jakarta Persistence
 * {@link jakarta.persistence.AttributeConverter AttributeConverter}.
 *
 * <h2>Two axes</h2>
 * A converter is pinned by what the column is and by what the attribute is, and the two are separate.
 * {@link com.github.jinahya.persistence.more.converter.__StringAttributeConverter} is the column axis &mdash; an
 * attribute, whatever it is, in a {@code String} column. The attribute axis is
 * {@link com.github.jinahya.persistence.more.converter.__NumberAttributeConverter} and
 * {@link com.github.jinahya.persistence.more.converter.__BooleanAttributeConverter} &mdash; a number, or a boolean, in
 * whatever column. Both are interfaces carrying nothing, so a converter pinned on both axes implements one of each, as
 * {@link com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverter} does, and still has its
 * inheritance to spend elsewhere.
 *
 * <h2>Storing a value as a {@code String}</h2>
 * {@link com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters} holds the ready-made
 * converters for the {@link java.lang.Number} types, and
 * {@link com.github.jinahya.persistence.more.converter.__JoinedStringAttributeConverter} stores a whole
 * {@link java.util.List} as one delimited column.
 *
 * <h2>Composing converters</h2>
 * A converter need not be written from scratch.
 * {@link com.github.jinahya.persistence.more.converter.__ChainingAttributeConverter} builds one out of two, through an
 * intermediate type, and {@link com.github.jinahya.persistence.more.converter.__AttributeConverterUtils} builds one out
 * of a pair of functions.
 *
 * <h2>Dates, times and amounts of time</h2>
 * {@link com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverter} stores a
 * {@code java.time} value as the ISO-8601 text its own type reads back, or through a
 * {@link java.time.format.DateTimeFormatter} given to it;
 * {@link com.github.jinahya.persistence.more.converter.__TemporalAmountStringAttributeConverter} does the same for a
 * {@link java.time.Duration} or a {@link java.time.Period}. The concrete converters of each live in
 * {@link com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters} and
 * {@link com.github.jinahya.persistence.more.converter.__TemporalAmountStringAttributeConverters}.
 * <p>
 * {@link com.github.jinahya.persistence.more.converter.__TemporalAccessorLongAttributeConverter} stores such a value as
 * the one integral coordinate its type is measured by instead &mdash; a nanosecond of the day, an epoch day &mdash;
 * which is compact, exact, and the same column on every database where a {@code TIME} or {@code TIMESTAMP} is not. Its
 * concrete converters are in
 * {@link com.github.jinahya.persistence.more.converter.__TemporalAccessorLongAttributeConverters}, and there are only
 * two: the encoding has to be increasing and lossless, and few types have one.
 *
 * <h2>Yes-or-no flags</h2>
 * {@link com.github.jinahya.persistence.more.converter.__BooleanYnAttributeConverters} holds four converters for a
 * {@link java.lang.Boolean} stored as a {@code 'Y'}/{@code 'N'} flag, crossing the column type
 * ({@link java.lang.Character} or {@link java.lang.String}) with what becomes of a value which is neither &mdash;
 * refused, or read as {@link java.lang.Boolean#FALSE}.
 *
 * <h2>Enums with stable persisted values</h2>
 * {@link com.github.jinahya.persistence.more.converter.__AttributeEnumConverter} is the converter for an
 * {@link com.github.jinahya.persistence.more.__AttributeEnum}, whose constants declare the value actually written to
 * the database. The enum side of that pairing lives in {@link com.github.jinahya.persistence.more the parent package}.
 *
 * <h2>Registration</h2>
 * Only a concrete converter carries {@link jakarta.persistence.Converter @Converter}, and every one of them declares
 * {@code autoApply = false}: naming it in {@link jakarta.persistence.Convert @Convert}, or in {@code persistence.xml},
 * is what puts it to work. No abstract class here is annotated, and neither is what the factory methods return &mdash;
 * those are plain objects, for composing programmatically and delegating to from a class which <em>is</em> registered.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.converter;

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
