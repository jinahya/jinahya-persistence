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
 * An interface for converting an entity attribute to a {@code String} db data, and vice versa.
 * <p>
 * This is the column axis of this package: it fixes what the column is, and says nothing about what the attribute is.
 * The attribute axis is the other one &mdash; {@link __NumberAttributeConverter}, {@link __BooleanAttributeConverter}
 * &mdash; and a converter which is pinned on both implements one of each.
 *
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An interface, carrying nothing: an implementation spends no inheritance on it, and is free to extend
 *         whatever it likes. {@link __NumberStringAttributeConverter},
 *         {@link __TemporalAccessorStringAttributeConverter}, {@link __TemporalAmountStringAttributeConverter} and
 *         {@link __JoinedStringAttributeConverter} are the abstract classes built on it.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __StringAttributeConverter<X> extends AttributeConverter<X, String> {

}
