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
 * An interface for converting a {@link Boolean} entity attribute to a database column value, and vice versa.
 * <p>
 * This is the attribute axis of this package: it fixes what the attribute is, and leaves the column open, because a
 * boolean has been spelled in a column in every way a column can be spelled &mdash; a character, a string, a number, a
 * real boolean where the database has one. {@link __StringAttributeConverter} is the other axis, fixing the column and
 * leaving the attribute open.
 *
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An interface, carrying nothing: an implementation spends no inheritance on it, and is free to extend
 *         whatever it likes.
 * @see __BooleanYnAttributeConverter
 * @see __StringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __BooleanAttributeConverter<Y> extends AttributeConverter<Boolean, Y> {

}
