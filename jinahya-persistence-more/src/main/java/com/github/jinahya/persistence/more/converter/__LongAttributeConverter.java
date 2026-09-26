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

import jakarta.persistence.AttributeConverter;

/**
 * An interface for converting an entity attribute to a {@code Long} db data, and vice versa.
 * <p>
 * This is a column axis of this package, alongside {@link __StringAttributeConverter}: it fixes what the column is
 * &mdash; one integral number, a {@code BIGINT} or whatever the database calls it &mdash; and says nothing about what
 * the attribute is.
 *
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An interface, carrying nothing: an implementation spends no inheritance on it, and is free to extend
 *         whatever it likes. {@link __TemporalAccessorLongAttributeConverter} is the abstract class built on it.
 * @apiNote {@code Long} rather than {@code long}: an {@link AttributeConverter AttributeConverter} carries the
 *         column type as a type argument, which cannot be a primitive, and a nullable column needs the reference type
 *         in any case.
 * @see __StringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __LongAttributeConverter<X> extends AttributeConverter<X, Long> {

}
