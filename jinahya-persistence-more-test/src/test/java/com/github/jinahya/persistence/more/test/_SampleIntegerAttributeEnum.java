package com.github.jinahya.persistence.more.test;

/*-
 * #%L
 * jinahya-persistence-more-test
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

enum _SampleIntegerAttributeEnum implements __SampleIntegerAttributeEnum<_SampleIntegerAttributeEnum> {

    A(0),

    B(1);

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _SampleIntegerAttributeEnum(final Integer attributeValue) {
        this.attributeValue = attributeValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Integer attributeValue() {
        return attributeValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Integer attributeValue;
}
