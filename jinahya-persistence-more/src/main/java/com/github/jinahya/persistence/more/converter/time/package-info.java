/**
 * Attribute converters for {@code java.time}: dates, times, amounts of time, and the day and month names.
 *
 * <h2>Two axes, as in the parent package</h2>
 * The column axis is the parent's &mdash;
 * {@link com.github.jinahya.persistence.more.converter.__StringAttributeConverter} and
 * {@link com.github.jinahya.persistence.more.converter.__LongAttributeConverter}. The attribute axis is this package's:
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAccessorAttributeConverter} for a date or a time,
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAmountAttributeConverter} for an amount of one,
 * and, refining the first along {@code java.time.temporal}'s own lines,
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAttributeConverter} for what can be moved and
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAdjusterAttributeConverter} for what adjusts.
 * Every abstract converter here sits on one of each.
 *
 * <h2>As text</h2>
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAccessorStringAttributeConverter} stores a value
 * as the ISO-8601 text its own type reads back, or through a {@link java.time.format.DateTimeFormatter} given to it;
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAmountStringAttributeConverter} does the same for
 * a {@link java.time.Duration} or a {@link java.time.Period}. The concrete converters of each live in
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAccessorStringAttributeConverters} and
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAmountStringAttributeConverters}.
 * <p>
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAccessorTextStringAttributeConverter} stores a
 * {@link java.time.DayOfWeek} or a {@link java.time.Month} as its <em>name</em> in a locale it is given &mdash; never
 * the default one &mdash; and reads it back regardless of case; its concrete converters, which pin English, are in
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAccessorTextStringAttributeConverters}.
 * <p>
 * {@link com.github.jinahya.persistence.more.converter.time.ZoneIdStringAttributeConverter} stores a
 * {@link java.time.ZoneId} as its identifier. A zone is no {@link java.time.temporal.TemporalAccessor}, so the
 * converter sits on the column axis alone, and is concrete itself rather than one of a holder's. Nested in it,
 * {@link com.github.jinahya.persistence.more.converter.time.ZoneIdStringAttributeConverter.TimeZoneStringAttributeConverter}
 * stores a {@link java.util.TimeZone} through it.
 *
 * <h2>As a number</h2>
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAccessorLongAttributeConverter} stores a value as
 * the one integral coordinate its type is measured by &mdash; a nanosecond of the day, an epoch day &mdash; which is
 * compact, exact, and the same column on every database where a {@code TIME} or {@code TIMESTAMP} is not. Its
 * concrete converters are in
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAccessorLongAttributeConverters}, and there are
 * only two: the encoding has to be increasing and lossless, and few types have one.
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAmountLongAttributeConverter} does the same for
 * an amount, through
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAmountLongAttributeConverters}.
 *
 * <h2>Registration</h2>
 * Only a concrete converter carries {@link jakarta.persistence.Converter @Converter}, and every one of them declares
 * {@code autoApply = false}: Jakarta Persistence already maps most of these types to real date/time columns, and
 * nothing here changes that by accident. Naming a converter in {@link jakarta.persistence.Convert @Convert}, or in
 * {@code persistence.xml}, is what puts it to work.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
package com.github.jinahya.persistence.more.converter.time;

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
