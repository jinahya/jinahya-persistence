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
 * An abstract class for converting {@code String} db data to an entity attribute of a specific subtype of
 * {@link Number}, and vice versa.
 * <p>
 * It sits on both of this package's axes at once: the column is a {@code String}, which is what
 * {@link __StringAttributeConverter} says, and the attribute is a {@link Number}, which is what
 * {@link __NumberAttributeConverter} says. Neither is the primary one, which is why both are implemented rather than
 * one of them extended.
 *
 * @param <X> number type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __NumberStringAttributeConverters
 * @see __StringAttributeConverter
 * @see __NumberAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __NumberStringAttributeConverter<X extends Number>
        implements __StringAttributeConverter<X>, __NumberAttributeConverter<X, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __NumberStringAttributeConverter() {
        super();
    }
}
