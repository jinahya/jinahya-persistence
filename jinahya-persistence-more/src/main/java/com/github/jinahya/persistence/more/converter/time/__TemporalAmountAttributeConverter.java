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

import java.time.temporal.TemporalAmount;

/**
 * An interface for converting an entity attribute of a subtype of {@link TemporalAmount} to a database column value,
 * and vice versa.
 * <p>
 * This is the attribute axis of this package for an amount of time: it fixes what the attribute is, and leaves the
 * column open, because the column a {@link java.time.Duration Duration} or a {@link java.time.Period Period} is kept
 * in is a schema's decision and not the amount's &mdash; its ISO-8601 text, or the one count it is measured by.
 * {@link com.github.jinahya.persistence.more.converter.__StringAttributeConverter __StringAttributeConverter} and
 * {@link com.github.jinahya.persistence.more.converter.__LongAttributeConverter __LongAttributeConverter} are the
 * other axis, fixing the column and leaving the attribute open; {@link __TemporalAmountStringAttributeConverter} and
 * {@link __TemporalAmountLongAttributeConverter} are what sit on both.
 *
 * @param <X> temporal amount type parameter
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An interface, carrying nothing: an implementation spends no inheritance on it, and is free to extend
 *         whatever it likes.
 * @see __TemporalAccessorAttributeConverter
 * @see __TemporalAmountStringAttributeConverter
 * @see __TemporalAmountLongAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __TemporalAmountAttributeConverter<X extends TemporalAmount, Y> extends AttributeConverter<X, Y> {

}
