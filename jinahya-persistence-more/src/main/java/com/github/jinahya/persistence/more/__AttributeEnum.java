package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import jakarta.validation.constraints.NotNull;

/**
 * An interface for defining enum constants with a specific type of attribute values.
 *
 * @param <SELF>      self type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S114", // Interface names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
public interface __AttributeEnum<SELF extends Enum<SELF> & __AttributeEnum<SELF, ATTRIBUTE>, ATTRIBUTE> {

    /**
     * An interface for defining enum constants with attribute values of string attribute values.
     *
     * @param <SELF> self type parameter
     */
    @SuppressWarnings({
            "java:S114" // Interface names should comply with a naming convention
    })
    interface __OfString<SELF extends Enum<SELF> & __OfString<SELF>> extends __AttributeEnum<SELF, String> {

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @apiNote The {@code attributeValue()} method of {@code __OfString} class returns
         *         {@link Enum#name() this.name()}.
         */
        @Override
        @SuppressWarnings({"unchecked"})
        default String attributeValue() {
            return ((SELF) this).name();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the attribute value of this enum constant.
     *
     * @return the attribute value of this enum constant.
     */
    @NotNull
    ATTRIBUTE attributeValue();
}
