package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
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

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __PersistenceProducerConstants {

    // -----------------------------------------------------------------------------------------------------------------
    static final String PERSISTENCE_UNIT_PROPERTY_DEFAULT_CATALOG = "jinahya.persistence.default_catalog";

    static final String PERSISTENCE_UNIT_PROPERTY_DEFAULT_SCHEMA = "jinahya.persistence.default_schema";

    // -----------------------------------------------------------------------------------------------------------------
    private __PersistenceProducerConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
