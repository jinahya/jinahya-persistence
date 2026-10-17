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
import jakarta.persistence.AttributeConverter;

import java.time.temporal.TemporalAdjuster;

/**
 * An interface for converting an entity attribute of a subtype of {@link TemporalAdjuster} to a database column value,
 * and vice versa.
 * <p>
 * This is the attribute axis of this package for a value which <em>adjusts</em> a date or a time: it fixes what the
 * attribute is, and leaves the column open. Most {@code java.time} values are adjusters as well as accessors &mdash;
 * a {@link java.time.LocalDate LocalDate} sets the date of whatever it is applied to &mdash; but the axis earns its
 * place for the two which are adjusters and accessors and nothing more: {@link java.time.DayOfWeek DayOfWeek} and
 * {@link java.time.Month Month}, each a single named field and neither a {@link java.time.temporal.Temporal Temporal}.
 * {@link __TemporalAccessorTextStringAttributeConverter} sits on this axis together with
 * {@link __TemporalAccessorAttributeConverter}, by bounding its attribute on both.
 *
 * @param <X> temporal adjuster type parameter
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An interface, carrying nothing: an implementation spends no inheritance on it, and is free to extend
 *         whatever it likes.
 * @see __TemporalAccessorAttributeConverter
 * @see __TemporalAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __TemporalAdjusterAttributeConverter<X extends TemporalAdjuster, Y> extends AttributeConverter<X, Y> {

}
