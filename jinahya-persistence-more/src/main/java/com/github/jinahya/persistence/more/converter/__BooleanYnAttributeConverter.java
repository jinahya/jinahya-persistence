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

/**
 * An interface for converting a {@link Boolean} entity attribute to a yes-or-no flag column, and vice versa.
 * <p>
 * The flag is the {@code 'Y'}/{@code 'N'} pair a {@code CHAR(1)} column has carried since long before a database had a
 * boolean type. Schemas written that way are still in service, and a column of theirs is not a boolean column with an
 * odd encoding &mdash; it is its own thing, queried by hand as {@code where active = 'Y'} and read by systems which
 * know nothing of this application. This interface is what marks a converter as speaking that convention, so that
 * "converts a {@code Boolean}" and "converts a legacy yes-or-no flag" are not the same statement.
 *
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An interface, carrying nothing: an implementation spends no inheritance on it, and is free to extend
 *         whatever it likes. {@link __BooleanYnAttributeConverters} holds the implementations &mdash; one per column
 *         type, each in a strict and a lenient reading.
 * @see __BooleanAttributeConverter
 * @see __BooleanYnAttributeConverters
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __BooleanYnAttributeConverter<Y> extends __BooleanAttributeConverter<Y> {

}
