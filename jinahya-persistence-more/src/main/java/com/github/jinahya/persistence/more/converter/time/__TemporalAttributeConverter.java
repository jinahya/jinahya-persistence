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
import java.time.temporal.Temporal;

/**
 * An interface for converting an entity attribute of a subtype of {@link Temporal} to a database column value, and
 * vice versa.
 * <p>
 * A refinement of {@link __TemporalAccessorAttributeConverter} for the values which can be <em>moved</em> &mdash;
 * {@link java.time.LocalDate LocalDate}, {@link java.time.Instant Instant}, {@link java.time.Year Year} &mdash; as
 * against the two which can only be read, {@link java.time.DayOfWeek DayOfWeek} and {@link java.time.Month Month}.
 * The abstract converters of this package bound their attribute on {@link java.time.temporal.TemporalAccessor
 * TemporalAccessor}, which every {@code Temporal} is, so a converter for a {@code Temporal} may pin itself here on top
 * of extending one of them.
 *
 * @param <X> temporal type parameter
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An interface, carrying nothing: an implementation spends no inheritance on it, and is free to extend
 *         whatever it likes.
 * @see __TemporalAccessorAttributeConverter
 * @see __TemporalAdjusterAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __TemporalAttributeConverter<X extends Temporal, Y>
        extends __TemporalAccessorAttributeConverter<X, Y> {

}
